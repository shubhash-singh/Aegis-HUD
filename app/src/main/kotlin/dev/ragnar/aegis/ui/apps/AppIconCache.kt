package dev.ragnar.aegis.ui.apps

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import dev.ragnar.aegis.domain.apps.AppInfo

/**
 * Global high-performance in-memory cache for rendered Compose application icons.
 *
 * Solves the LazyGrid scroll hitching by:
 * - Pre-rendering platform [Drawable]s into optimized 128x128 ARGB_8888 [ImageBitmap]s
 * - Caching up to 256 app icons in an in-memory LRU cache
 * - Completely replacing heavy [AndroidView(ImageView)] with native Compose [Image]
 */
object AppIconCache {
    private const val CACHE_SIZE = 256
    private const val ICON_SIZE_PX = 128

    private val memoryCache = object : LruCache<String, ImageBitmap>(CACHE_SIZE) {}
    private var placeholderBitmap: ImageBitmap? = null

    fun getOrLoad(context: Context, app: AppInfo): ImageBitmap {
        memoryCache.get(app.componentName)?.let { return it }

        val loaded = loadIconBitmap(context, app)
        val result = loaded ?: getPlaceholder()
        memoryCache.put(app.componentName, result)
        return result
    }

    private fun loadIconBitmap(context: Context, app: AppInfo): ImageBitmap? {
        val component = ComponentName.unflattenFromString(app.componentName) ?: return null
        val pm = context.packageManager
        val drawable = runCatching { pm.getActivityIcon(component) }.getOrNull() ?: return null
        return drawableToImageBitmap(drawable)
    }

    fun drawableToImageBitmap(drawable: Drawable): ImageBitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null && !drawable.bitmap.isRecycled) {
            val bmp = drawable.bitmap
            if (bmp.width == ICON_SIZE_PX && bmp.height == ICON_SIZE_PX) {
                return bmp.asImageBitmap()
            }
        }

        val bitmap = Bitmap.createBitmap(ICON_SIZE_PX, ICON_SIZE_PX, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, ICON_SIZE_PX, ICON_SIZE_PX)
        drawable.draw(canvas)
        return bitmap.asImageBitmap()
    }

    private fun getPlaceholder(): ImageBitmap {
        placeholderBitmap?.let { return it }
        val bitmap = Bitmap.createBitmap(ICON_SIZE_PX, ICON_SIZE_PX, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = 0xFF0A2238.toInt()
            isAntiAlias = true
        }
        canvas.drawCircle(ICON_SIZE_PX / 2f, ICON_SIZE_PX / 2f, ICON_SIZE_PX / 2f - 4, paint)
        val imageBitmap = bitmap.asImageBitmap()
        placeholderBitmap = imageBitmap
        return imageBitmap
    }

    fun clear() {
        memoryCache.evictAll()
    }
}
