package dev.ragnar.aegis

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import dev.ragnar.aegis.data.widget.AegisAppWidgetHost
import dev.ragnar.aegis.domain.widget.HostedWidget
import dev.ragnar.aegis.ui.home.HomeScreen
import dev.ragnar.aegis.ui.theme.AegisTheme
import dev.ragnar.aegis.ui.widget.LocalWidgetHostController
import dev.ragnar.aegis.ui.widget.WidgetHostController

/**
 * Main launcher activity for AEGIS HUD.
 *
 * Registered as an Android home-screen launcher via manifest intent-filter.
 * Integrates AppWidgetHost lifecycle, widget binding, and configuration contracts (DEC-018).
 * Uses singleTask launch mode to prevent stacked instances (DEC-010).
 * Uses stateNotNeeded=true because the launcher rebuilds from live data (DEC-010).
 * Uses excludeFromRecents=true to stay out of the Recents screen (DEC-010).
 */
class MainActivity : ComponentActivity(), WidgetHostController {

    override val appWidgetHost: AppWidgetHost by lazy {
        AegisAppWidgetHost(applicationContext)
    }

    override val appWidgetManager: AppWidgetManager by lazy {
        AppWidgetManager.getInstance(applicationContext)
    }

    private var pendingBindSuccess: ((HostedWidget) -> Unit)? = null
    private var pendingBindError: ((String) -> Unit)? = null
    private var pendingWidgetId: Int? = null
    private var pendingProvider: AppWidgetProviderInfo? = null

    private val bindWidgetLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val widgetId = pendingWidgetId
            val provider = pendingProvider
            if (widgetId != null && provider != null) {
                if (result.resultCode == Activity.RESULT_OK) {
                    checkAndLaunchConfigure(widgetId, provider)
                } else {
                    runCatching { appWidgetHost.deleteAppWidgetId(widgetId) }
                    pendingBindError?.invoke("Widget binding permission was denied by user")
                    clearPending()
                }
            } else {
                clearPending()
            }
        }

    private val configureWidgetLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val widgetId = pendingWidgetId
            val provider = pendingProvider
            if (widgetId != null && provider != null) {
                if (result.resultCode == Activity.RESULT_OK) {
                    dispatchSuccess(widgetId, provider)
                } else {
                    runCatching { appWidgetHost.deleteAppWidgetId(widgetId) }
                    pendingBindError?.invoke("Widget configuration was canceled")
                    clearPending()
                }
            } else {
                clearPending()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalWidgetHostController provides this) {
                AegisTheme {
                    HomeScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        runCatching {
            appWidgetHost.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        runCatching {
            appWidgetHost.stopListening()
        }
    }

    override fun requestAddWidget(
        provider: AppWidgetProviderInfo,
        onSuccess: (HostedWidget) -> Unit,
        onError: (String) -> Unit,
    ) {
        val widgetId = appWidgetHost.allocateAppWidgetId()
        pendingWidgetId = widgetId
        pendingProvider = provider
        pendingBindSuccess = onSuccess
        pendingBindError = onError

        val canBind = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                appWidgetManager.bindAppWidgetIdIfAllowed(
                    widgetId,
                    provider.profile,
                    provider.provider,
                    null,
                )
            } else {
                @Suppress("DEPRECATION")
                appWidgetManager.bindAppWidgetIdIfAllowed(widgetId, provider.provider)
            }
        }.getOrDefault(false)

        if (canBind) {
            checkAndLaunchConfigure(widgetId, provider)
        } else {
            val bindIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE, provider.profile)
                }
            }
            bindWidgetLauncher.launch(bindIntent)
        }
    }

    private fun checkAndLaunchConfigure(widgetId: Int, provider: AppWidgetProviderInfo) {
        if (provider.configure != null) {
            val configIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                component = provider.configure
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            configureWidgetLauncher.launch(configIntent)
        } else {
            dispatchSuccess(widgetId, provider)
        }
    }

    private fun dispatchSuccess(widgetId: Int, provider: AppWidgetProviderInfo) {
        val label = runCatching { provider.loadLabel(packageManager) }.getOrDefault(provider.provider.shortClassName)
        val hosted = HostedWidget(
            appWidgetId = widgetId,
            providerPackage = provider.provider.packageName,
            providerClass = provider.provider.className,
            label = label,
            heightDp = HostedWidget.DEFAULT_HEIGHT_DP,
        )
        pendingBindSuccess?.invoke(hosted)
        clearPending()
    }

    private fun clearPending() {
        pendingWidgetId = null
        pendingProvider = null
        pendingBindSuccess = null
        pendingBindError = null
    }

    override fun deleteWidget(appWidgetId: Int) {
        runCatching {
            appWidgetHost.deleteAppWidgetId(appWidgetId)
        }
    }
}
