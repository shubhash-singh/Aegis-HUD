package dev.ragnar.aegis.data.apps

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.core.content.ContextCompat
import dev.ragnar.aegis.domain.apps.AppInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Android adapter for package discovery, change broadcasts, icons, and launching. */
class AndroidAppRepository(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AppRepository {

    private val appContext = context.applicationContext
    private val packageManager: PackageManager = appContext.packageManager
    private val iconCache = LruCache<String, Drawable>(48)

    override fun observeLaunchableApps(): Flow<List<AppInfo>> = callbackFlow {
        fun refresh() {
            launch {
                trySend(loadLaunchableApps())
            }
        }

        val packageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                refresh()
            }
        }
        val packageFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }

        ContextCompat.registerReceiver(
            appContext,
            packageReceiver,
            packageFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        refresh()

        awaitClose {
            runCatching {
                appContext.unregisterReceiver(packageReceiver)
            }
        }
    }.flowOn(ioDispatcher)

    override fun launch(app: AppInfo): Result<Unit> = runCatching {
        val component = ComponentName.unflattenFromString(app.componentName)
            ?: error("Invalid launch component")
        val launchIntent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(component)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(launchIntent)
    }

    /** Returns a cached activity icon or null when the activity is no longer available. */
    fun iconFor(app: AppInfo): Drawable? {
        iconCache.get(app.componentName)?.let { return it }
        val component = ComponentName.unflattenFromString(app.componentName) ?: return null
        return runCatching { packageManager.getActivityIcon(component) }
            .getOrNull()
            ?.also { iconCache.put(app.componentName, it) }
    }

    private fun loadLaunchableApps(): List<AppInfo> {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val launchableApps = packageManager.queryIntentActivities(launcherIntent, 0)
            .mapNotNull { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                if (activityInfo.packageName == appContext.packageName) return@mapNotNull null

                val label = resolveInfo.loadLabel(packageManager)
                    ?.toString()
                    ?.takeIf(String::isNotBlank)
                    ?: activityInfo.name
                val installTime = runCatching {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        packageManager.getPackageInfo(
                            activityInfo.packageName,
                            PackageManager.PackageInfoFlags.of(0),
                        ).firstInstallTime
                    } else {
                        @Suppress("DEPRECATION")
                        packageManager.getPackageInfo(activityInfo.packageName, 0).firstInstallTime
                    }
                }.getOrDefault(0L)

                AppInfo(
                    label = label,
                    packageName = activityInfo.packageName,
                    componentName = ComponentName(
                        activityInfo.packageName,
                        activityInfo.name,
                    ).flattenToString(),
                    installTimeMillis = installTime,
                )
            }
        return AppListNormalizer.normalize(launchableApps)
    }
}
