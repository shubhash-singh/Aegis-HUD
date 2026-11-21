package dev.ragnar.aegis.ui.home.visualization

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.ragnar.aegis.ui.theme.AegisColorScheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

// ---- Tuning knobs ----
private const val BRAIN_SCALE = 1.0f   // 1f = brain is ~58% of the shorter screen side; lower it to clear the side cards
private const val FOCAL = 3.2f         // lower = stronger perspective
private const val BASE_YAW = 0.20f     // + turns the front toward the viewer
private const val SWAY = 0.10f         // radians of left/right drift (0f = static)
private const val PITCH = 0.10f        // look-down angle (0 = pure lateral view)
private const val BUCKETS = 4          // brightness buckets -> one Path (and a couple of draw calls) each
private const val TWO_PI = 2f * PI.toFloat()

private fun sq(v: Float) = v * v
private fun smooth01(t: Float): Float { val c = t.coerceIn(0f, 1f); return c * c * (3f - 2f * c) }

/**
 * A subtle, closed-loop camera pose for the hologram.
 *
 * The second harmonic prevents the mesh from reading as a metronomic pendulum while
 * integer frequencies keep the first and last frame identical when the phase restarts.
 */
internal data class NeuralMeshPose(val yaw: Float, val pitch: Float)

internal fun organicMeshPose(phase: Float): NeuralMeshPose {
    val cycle = phase - floor(phase)
    val angle = TWO_PI * cycle
    return NeuralMeshPose(
        yaw = BASE_YAW + SWAY * sin(angle) + 0.035f * sin(2f * angle + 0.8f),
        pitch = PITCH + 0.018f * sin(angle + 1.35f),
    )
}

/**
 * 3D brain mesh, built once.
 * Unit space: x = front(-1) -> back(+1), y = screen-down, z = toward viewer.
 *
 * Construction (see [MeshBuilder]):
 *  - Cerebrum: a hand-traced lateral silhouette (frontal + parietal bumps, temporal lobe, occipital pole) is
 *    filled with Poisson-spaced points, Delaunay-triangulated, and inflated into a two-sided shell.
 *    The Sylvian fissure is a row of "hot" nodes with a groove in the shell.
 *  - Cerebellum: same pipeline on a small oval, with row-aligned points so the folia read as horizontal stripes.
 *  - Brainstem: a short tapered tube.
 *
 * Rim nodes (the silhouette) are shared by the near and far surfaces, so the outline stays closed at any yaw.
 */
class NeuralMesh3D internal constructor(
    val px: FloatArray, val py: FloatArray, val pz: FloatArray,
    val nx: FloatArray, val ny: FloatArray, val nz: FloatArray,
    /** 0 = plain dot, 1 = small star, 2 = hub star with glint. */
    val star: IntArray,
    /** Extra brightness 0..1 (fissure nodes). */
    val lift: FloatArray,
    val edgeA: IntArray, val edgeB: IntArray,
    /** True for silhouette-loop edges, which are drawn as a separate glowing outline. */
    val rimEdge: BooleanArray,
) {
    val count: Int get() = px.size

    companion object {
        fun build(): NeuralMesh3D {
            val rng = Rng(20260622)
            val b = MeshBuilder()
            b.addCerebrum(rng)
            b.addCerebellum(rng)
            b.addStem()
            return b.finish()
        }
    }
}

// =====================================================================================================
// Mesh construction (pure Kotlin, runs once)
// =====================================================================================================

/** Deterministic xorshift32 -> [0,1). Same mesh on every launch. */
private class Rng(seed: Int) {
    private var s = if (seed == 0) 1 else seed
    fun next(): Float {
        s = s xor (s shl 13)
        s = s xor (s ushr 17)
        s = s xor (s shl 5)
        return (s ushr 8) / 16777216f
    }
    fun range(a: Float, b: Float): Float = a + (b - a) * next()
}

private class Poly(val x: FloatArray, val y: FloatArray) {
    val n: Int get() = x.size

    /** Even-odd point-in-polygon (closed). */
    fun contains(px: Float, py: Float): Boolean {
        var c = false
        var j = n - 1
        for (i in 0 until n) {
            val xi = x[i]; val yi = y[i]; val xj = x[j]; val yj = y[j]
            if ((yi > py) != (yj > py) && px < (xj - xi) * (py - yi) / (yj - yi) + xi) c = !c
            j = i
        }
        return c
    }

    fun distance(px: Float, py: Float, closed: Boolean): Float {
        val m = if (closed) n else n - 1
        var best = Float.MAX_VALUE
        for (i in 0 until m) {
            val k = (i + 1) % n
            best = min(best, segDist(px, py, x[i], y[i], x[k], y[k]))
        }
        return best
    }
}

private fun poly(vararg xy: Float): Poly {
    val n = xy.size / 2
    return Poly(FloatArray(n) { xy[2 * it] }, FloatArray(n) { xy[2 * it + 1] })
}

private fun segDist(px: Float, py: Float, ax: Float, ay: Float, bx: Float, by: Float): Float {
    val dx = bx - ax; val dy = by - ay
    val l2 = dx * dx + dy * dy
    val t = if (l2 < 1e-12f) 0f else (((px - ax) * dx + (py - ay) * dy) / l2).coerceIn(0f, 1f)
    val qx = px - (ax + t * dx); val qy = py - (ay + t * dy)
    return sqrt(qx * qx + qy * qy)
}

private fun catmull(p0: Float, p1: Float, p2: Float, p3: Float, t: Float): Float {
    val t2 = t * t; val t3 = t2 * t
    return 0.5f * (2f * p1 + (-p0 + p2) * t + (2f * p0 - 5f * p1 + 4f * p2 - p3) * t2 + (-p0 + 3f * p1 - 3f * p2 + p3) * t3)
}

private fun smoothClosed(c: Poly, per: Int): Poly {
    val n = c.n
    val ox = FloatArray(n * per); val oy = FloatArray(n * per)
    var o = 0
    for (i in 0 until n) {
        val a = (i - 1 + n) % n; val d = (i + 1) % n; val e = (i + 2) % n
        for (k in 0 until per) {
            val t = k / per.toFloat()
            ox[o] = catmull(c.x[a], c.x[i], c.x[d], c.x[e], t)
            oy[o] = catmull(c.y[a], c.y[i], c.y[d], c.y[e], t)
            o++
        }
    }
    return Poly(ox, oy)
}

private fun smoothOpen(c: Poly, per: Int): Poly {
    val n = c.n
    val ox = FloatArray((n - 1) * per + 1); val oy = FloatArray((n - 1) * per + 1)
    var o = 0
    for (i in 0 until n - 1) {
        val a = max(i - 1, 0); val d = i + 1; val e = min(i + 2, n - 1)
        for (k in 0 until per) {
            val t = k / per.toFloat()
            ox[o] = catmull(c.x[a], c.x[i], c.x[d], c.x[e], t)
            oy[o] = catmull(c.y[a], c.y[i], c.y[d], c.y[e], t)
            o++
        }
    }
    ox[o] = c.x[n - 1]; oy[o] = c.y[n - 1]
    return Poly(ox, oy)
}

/** Equal arc-length resampling of a closed polyline. */
private fun resampleClosed(d: Poly, spacing: Float): Poly {
    val n = d.n
    val seg = FloatArray(n)
    var total = 0f
    for (i in 0 until n) {
        val k = (i + 1) % n
        seg[i] = hypot(d.x[k] - d.x[i], d.y[k] - d.y[i])
        total += seg[i]
    }
    val cnt = max(3, (total / spacing).roundToInt())
    val step = total / cnt
    val ox = FloatArray(cnt); val oy = FloatArray(cnt)
    var i = 0
    var acc = 0f
    for (k in 0 until cnt) {
        val target = k * step
        while (acc + seg[i] < target && i < n - 1) { acc += seg[i]; i++ }
        val j = (i + 1) % n
        val f = (target - acc) / max(seg[i], 1e-9f)
        ox[k] = d.x[i] + (d.x[j] - d.x[i]) * f
        oy[k] = d.y[i] + (d.y[j] - d.y[i]) * f
    }
    return Poly(ox, oy)
}

/** Equal arc-length resampling of an open polyline (both end points included). */
private fun resampleOpen(d: Poly, spacing: Float): Poly {
    val n = d.n
    val seg = FloatArray(n - 1)
    var total = 0f
    for (i in 0 until n - 1) {
        seg[i] = hypot(d.x[i + 1] - d.x[i], d.y[i + 1] - d.y[i])
        total += seg[i]
    }
    val cnt = max(1, (total / spacing).roundToInt())
    val step = total / cnt
    val ox = FloatArray(cnt + 1); val oy = FloatArray(cnt + 1)
    var i = 0
    var acc = 0f
    for (k in 0..cnt) {
        val target = k * step
        while (acc + seg[i] < target && i < n - 2) { acc += seg[i]; i++ }
        val f = (target - acc) / max(seg[i], 1e-9f)
        ox[k] = d.x[i] + (d.x[i + 1] - d.x[i]) * f
        oy[k] = d.y[i] + (d.y[i + 1] - d.y[i]) * f
    }
    return Poly(ox, oy)
}

private fun superEllipse(cx: Float, cy: Float, rx: Float, ry: Float, n: Int, e: Float): Poly {
    val xs = FloatArray(n); val ys = FloatArray(n)
    for (k in 0 until n) {
        val t = 2f * PI.toFloat() * k / n
        val c = cos(t); val s = sin(t)
        xs[k] = cx + rx * sign(c) * abs(c).pow(2f / e)
        ys[k] = cy + ry * sign(s) * abs(s).pow(2f / e)
    }
    return Poly(xs, ys)
}

// ---- Delaunay (Bowyer-Watson, O(n^2) is plenty for a few hundred points) ----

private class Tri(val a: Int, val b: Int, val c: Int, val cx: Double, val cy: Double, val r2: Double)

private fun makeTri(xs: DoubleArray, ys: DoubleArray, a: Int, b: Int, c: Int): Tri {
    val ax = xs[a]; val ay = ys[a]; val bx = xs[b]; val by = ys[b]; val cx = xs[c]; val cy = ys[c]
    val d = 2.0 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by))
    if (abs(d) < 1e-14) return Tri(a, b, c, 0.0, 0.0, 1e30)
    val a2 = ax * ax + ay * ay; val b2 = bx * bx + by * by; val c2 = cx * cx + cy * cy
    val ux = (a2 * (by - cy) + b2 * (cy - ay) + c2 * (ay - by)) / d
    val uy = (a2 * (cx - bx) + b2 * (ax - cx) + c2 * (bx - ax)) / d
    return Tri(a, b, c, ux, uy, (ax - ux) * (ax - ux) + (ay - uy) * (ay - uy))
}

/** Returns triangles as a flat index list (a, b, c, a, b, c, ...). */
private fun delaunay(px: FloatArray, py: FloatArray, n: Int): IntArray {
    var minX = Double.MAX_VALUE; var maxX = -Double.MAX_VALUE
    var minY = Double.MAX_VALUE; var maxY = -Double.MAX_VALUE
    val xs = DoubleArray(n + 3); val ys = DoubleArray(n + 3)
    for (i in 0 until n) {
        xs[i] = px[i].toDouble(); ys[i] = py[i].toDouble()
        minX = min(minX, xs[i]); maxX = max(maxX, xs[i])
        minY = min(minY, ys[i]); maxY = max(maxY, ys[i])
    }
    val d = max(maxX - minX, maxY - minY) * 20.0
    val mx = (minX + maxX) / 2.0; val my = (minY + maxY) / 2.0
    xs[n] = mx - d; ys[n] = my - d
    xs[n + 1] = mx; ys[n + 1] = my + d
    xs[n + 2] = mx + d; ys[n + 2] = my - d

    var tris = ArrayList<Tri>()
    tris.add(makeTri(xs, ys, n, n + 1, n + 2))
    for (i in 0 until n) {
        val x = xs[i]; val y = ys[i]
        val bad = ArrayList<Tri>()
        val keep = ArrayList<Tri>()
        for (t in tris) {
            val dx = x - t.cx; val dy = y - t.cy
            if (dx * dx + dy * dy < t.r2) bad.add(t) else keep.add(t)
        }
        // The hole's boundary is every edge used by exactly one bad triangle.
        val eu = ArrayList<Int>(); val ev = ArrayList<Int>()
        for (t in bad) {
            eu.add(t.a); ev.add(t.b)
            eu.add(t.b); ev.add(t.c)
            eu.add(t.c); ev.add(t.a)
        }
        for (e in eu.indices) {
            var shared = 0
            for (f in eu.indices) {
                if ((eu[e] == eu[f] && ev[e] == ev[f]) || (eu[e] == ev[f] && ev[e] == eu[f])) shared++
            }
            if (shared == 1) keep.add(makeTri(xs, ys, eu[e], ev[e], i))
        }
        tris = keep
    }
    val out = ArrayList<Int>()
    for (t in tris) {
        if (t.a < n && t.b < n && t.c < n) { out.add(t.a); out.add(t.b); out.add(t.c) }
    }
    return out.toIntArray()
}

// ---- Shell: a lateral outline turned into an inflated, two-sided triangulated surface ----

private class Shell(
    val poly: Poly,
    val px: FloatArray, val py: FloatArray,
    /** 1 for nodes that sit on the fissure line. */
    val hot: FloatArray,
    /** Nodes [0, nb) are the silhouette loop. */
    val nb: Int,
    /** Packed edges: min * 65536 + max. */
    val edges: LongArray,
    /** Inflation radius: distance from the rim at which the surface reaches full thickness. */
    val radius: Float,
    /** Half-thickness at full inflation. */
    val zmax: Float,
    val groove: Float,
    val gyri: Float,
    val fissure: Poly?,
) {
    /** Half-thickness of the shell at (x, y); 0 on and outside the rim. */
    fun height(x: Float, y: Float): Float {
        if (!poly.contains(x, y)) return 0f
        val d = poly.distance(x, y, true)
        val u = 1f - min(d, radius) / radius
        var h = zmax * sqrt(max(0f, 1f - u * u))      // rolling-ball profile -> rounded cross-section
        val g = gyri * (sin(11f * x + 3f * y + 1.3f) * sin(9f * y - 4f * x) +
                0.6f * sin(17f * x + 2f) * cos(13f * y))
        h *= 1f + g
        val f = fissure
        if (f != null && groove > 0f) {
            val df = f.distance(x, y, false)
            h *= 1f - groove * exp(-sq(df / 0.05f))
        }
        return h
    }
}

private fun buildShell(
    ctrl: Poly, rng: Rng,
    bspace: Float, rint: Float, radius: Float, zmax: Float, maxEdge: Float, jitter: Float,
    fissureCtrl: Poly? = null, fspace: Float = 0.12f, groove: Float = 0f, gyri: Float = 0f,
    rowDy: Float = 0f, colDx: Float = 0f, relaxIters: Int = 3,
): Shell {
    // 1. Outline: smooth spline -> even spacing -> small normal jitter (gives the polygonal, scalloped rim).
    val rim0 = resampleClosed(smoothClosed(ctrl, 14), bspace)
    val nb = rim0.n
    val bx = FloatArray(nb); val by = FloatArray(nb)
    for (i in 0 until nb) {
        val p = (i - 1 + nb) % nb; val q = (i + 1) % nb
        val tx = rim0.x[q] - rim0.x[p]; val ty = rim0.y[q] - rim0.y[p]
        val l = hypot(tx, ty).let { if (it == 0f) 1f else it }
        val j = jitter * (rng.next() * 2f - 1f)
        bx[i] = rim0.x[i] + (ty / l) * j
        by[i] = rim0.y[i] + (-tx / l) * j
    }
    val poly = Poly(bx, by)

    // 2. Point set: rim, then the fissure line, then interior fill.
    val cap = nb + 700
    val ptx = FloatArray(cap); val pty = FloatArray(cap); val hot = FloatArray(cap)
    var cnt = 0
    for (i in 0 until nb) { ptx[cnt] = bx[i]; pty[cnt] = by[i]; cnt++ }

    val fis = fissureCtrl?.let { smoothOpen(it, 10) }
    if (fis != null) {
        val fs = resampleOpen(fis, fspace)
        for (i in 0 until fs.n) {
            if (poly.contains(fs.x[i], fs.y[i])) { ptx[cnt] = fs.x[i]; pty[cnt] = fs.y[i]; hot[cnt] = 1f; cnt++ }
        }
    }

    var x0 = Float.MAX_VALUE; var x1 = -Float.MAX_VALUE
    var y0 = Float.MAX_VALUE; var y1 = -Float.MAX_VALUE
    for (i in 0 until nb) {
        x0 = min(x0, bx[i]); x1 = max(x1, bx[i]); y0 = min(y0, by[i]); y1 = max(y1, by[i])
    }

    if (rowDy <= 0f) {
        // Poisson-disc fill: sparse in the interior, dense toward the rim.
        for (attempt in 0 until 6000) {
            val x = rng.range(x0, x1); val y = rng.range(y0, y1)
            if (cnt >= cap || !poly.contains(x, y)) continue
            val d = poly.distance(x, y, true)
            val r = bspace + (rint - bspace) * smooth01(d / 0.32f)
            val r2 = r * r
            var ok = true
            var q = 0
            while (q < cnt) {
                val dx = ptx[q] - x; val dy = pty[q] - y
                if (dx * dx + dy * dy < r2) { ok = false; break }
                q++
            }
            if (ok) { ptx[cnt] = x; pty[cnt] = y; cnt++ }
        }
    } else {
        // Staggered rows (folia): wide spacing along x, tight along y -> long horizontal edges.
        var row = 0
        var y = y0 + rowDy
        while (y < y1) {
            var x = x0 + (if (row % 2 == 1) colDx * 0.5f else 0f) + colDx * 0.5f
            while (x < x1) {
                val xx = x + rng.range(-0.18f, 0.18f) * colDx
                val yy = y + rng.range(-0.2f, 0.2f) * rowDy
                if (cnt < cap && poly.contains(xx, yy) && poly.distance(xx, yy, true) > bspace * 0.55f) {
                    ptx[cnt] = xx; pty[cnt] = yy; cnt++
                }
                x += colDx
            }
            y += rowDy
            row++
        }
    }

    // 3. Triangulate; keep triangles that lie inside the outline and aren't stretched across a concavity.
    val tri = delaunay(ptx, pty, cnt)
    fun key(u: Int, v: Int): Long = min(u, v).toLong() * 65536L + max(u, v)
    val es = HashSet<Long>()
    var k = 0
    while (k < tri.size) {
        val a = tri[k]; val b = tri[k + 1]; val c = tri[k + 2]
        k += 3
        val cx = (ptx[a] + ptx[b] + ptx[c]) / 3f
        val cy = (pty[a] + pty[b] + pty[c]) / 3f
        if (!poly.contains(cx, cy)) continue
        if (hypot(ptx[a] - ptx[b], pty[a] - pty[b]) > maxEdge) continue
        if (hypot(ptx[b] - ptx[c], pty[b] - pty[c]) > maxEdge) continue
        if (hypot(ptx[c] - ptx[a], pty[c] - pty[a]) > maxEdge) continue
        es.add(key(a, b)); es.add(key(b, c)); es.add(key(c, a))
    }
    val keys = es.toLongArray()
    keys.sort()

    // 4. Relax free interior points toward their neighbours' centroid (topology fixed) -> even triangles.
    val nbrs = Array(cnt) { ArrayList<Int>() }
    for (e in keys) {
        val u = (e / 65536L).toInt(); val v = (e % 65536L).toInt()
        nbrs[u].add(v); nbrs[v].add(u)
    }
    val tmpX = FloatArray(cnt); val tmpY = FloatArray(cnt); val moved = BooleanArray(cnt)
    for (iter in 0 until relaxIters) {
        for (i in nb until cnt) {
            moved[i] = false
            if (hot[i] != 0f || nbrs[i].size < 3) continue
            var mx = 0f; var my = 0f
            for (j in nbrs[i]) { mx += ptx[j]; my += pty[j] }
            mx /= nbrs[i].size; my /= nbrs[i].size
            val qx = ptx[i] * 0.5f + mx * 0.5f
            val qy = pty[i] * 0.5f + my * 0.5f
            if (poly.contains(qx, qy)) { tmpX[i] = qx; tmpY[i] = qy; moved[i] = true }
        }
        for (i in nb until cnt) if (moved[i]) { ptx[i] = tmpX[i]; pty[i] = tmpY[i] }
    }

    return Shell(poly, ptx.copyOf(cnt), pty.copyOf(cnt), hot.copyOf(cnt), nb, keys, radius, zmax, groove, gyri, fis)
}

// ---- Builder ----

private class MeshBuilder {
    private val v = ArrayList<FloatArray>()
    private val st = ArrayList<Int>()
    private val lf = ArrayList<Float>()
    private val ea = ArrayList<Int>()
    private val eb = ArrayList<Int>()
    private val er = ArrayList<Boolean>()

    fun node(x: Float, y: Float, z: Float, nx: Float, ny: Float, nz: Float, star: Int = 0, lift: Float = 0f): Int {
        val l = sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(1e-5f)
        v.add(floatArrayOf(x, y, z, nx / l, ny / l, nz / l))
        st.add(star)
        lf.add(lift)
        return v.size - 1
    }

    fun edge(a: Int, b: Int, rim: Boolean = false) { ea.add(a); eb.add(b); er.add(rim) }

    /** Connects closed rings with quads split by alternating diagonals (triangulated skin). */
    private fun skin(rings: List<IntArray>) {
        for (r in rings.indices) {
            val m = rings[r].size
            for (k in 0 until m) {
                val a = rings[r][k]
                val a2 = rings[r][(k + 1) % m]
                edge(a, a2)
                if (r + 1 < rings.size) {
                    val b = rings[r + 1][k]
                    val b2 = rings[r + 1][(k + 1) % m]
                    edge(a, b)
                    if ((k + r) % 2 == 0) edge(a, b2) else edge(a2, b)
                }
            }
        }
    }

    /** Turns a [Shell] into a near surface (+z) and a far surface (-z) that share the rim nodes. */
    private fun addShell(sh: Shell, rng: Rng) {
        val n = sh.px.size
        val near = IntArray(n); val far = IntArray(n)
        val eps = 0.012f
        for (i in 0 until n) {
            val x = sh.px[i]; val y = sh.py[i]
            val hx = (sh.height(x + eps, y) - sh.height(x - eps, y)) / (2f * eps)
            val hy = (sh.height(x, y + eps) - sh.height(x, y - eps)) / (2f * eps)
            if (i < sh.nb) {
                // Silhouette: z = 0, normal lies in the screen plane pointing outward -> lit as a rim.
                val l = hypot(hx, hy)
                val nx = if (l < 1e-6f) 0f else -hx / l
                val ny = if (l < 1e-6f) -1f else -hy / l
                val id = node(x, y, 0f, nx, ny, 0f)
                near[i] = id; far[i] = id
            } else {
                val h = sh.height(x, y)
                val r = rng.next()
                val star = when {
                    sh.hot[i] > 0f -> 1
                    r < 0.10f -> 2
                    r < 0.34f -> 1
                    else -> 0
                }
                near[i] = node(x, y, h, -hx, -hy, 1f, star, sh.hot[i])
                far[i] = node(x, y, -h, -hx, -hy, -1f, 0, sh.hot[i])
            }
        }
        val nb = sh.nb
        for (i in 0 until nb) edge(near[i], near[(i + 1) % nb], rim = true)
        for (e in sh.edges) {
            val a = (e / 65536L).toInt(); val b = (e % 65536L).toInt()
            if (b < nb && (b - a == 1 || (a == 0 && b == nb - 1))) continue   // rim loop edge, already added
            edge(near[a], near[b])
            if (!(far[a] == near[a] && far[b] == near[b])) edge(far[a], far[b])
        }
    }

    // ---------- Cerebrum (traced from the reference: x front -> back, y screen-down) ----------
    fun addCerebrum(rng: Rng) {
        val outline = poly(
            -1.00f, 0.14f, -0.98f, -0.02f, -0.92f, -0.14f, -0.78f, -0.31f, -0.57f, -0.49f, -0.33f, -0.60f,
            -0.12f, -0.66f, 0.02f, -0.665f, 0.17f, -0.60f, 0.33f, -0.63f, 0.59f, -0.49f, 0.78f, -0.31f,
            0.92f, -0.12f, 1.00f, 0.08f, 1.00f, 0.27f, 0.95f, 0.41f, 0.87f, 0.50f, 0.64f, 0.49f, 0.42f, 0.49f,
            0.20f, 0.56f, 0.02f, 0.62f, -0.08f, 0.655f, -0.25f, 0.667f, -0.42f, 0.63f, -0.52f, 0.55f,
            -0.535f, 0.47f, -0.58f, 0.425f, -0.68f, 0.415f, -0.81f, 0.40f, -0.93f, 0.32f, -0.99f, 0.22f,
        )
        // Sylvian fissure: rises from the temporal pole toward the parietal region.
        val fissure = poly(-0.46f, 0.39f, -0.25f, 0.22f, 0.05f, 0.12f, 0.35f, 0.07f, 0.60f, 0.10f)
        addShell(
            buildShell(
                outline, rng,
                bspace = 0.085f, rint = 0.128f, radius = 0.45f, zmax = 0.52f, maxEdge = 0.30f, jitter = 0.010f,
                fissureCtrl = fissure, fspace = 0.12f, groove = 0.30f, gyri = 0.05f,
            ),
            rng,
        )
    }

    // ---------- Cerebellum (lower back, horizontal folia) ----------
    fun addCerebellum(rng: Rng) {
        addShell(
            buildShell(
                superEllipse(0.46f, 0.665f, 0.335f, 0.17f, 12, 2.3f), rng,
                bspace = 0.085f, rint = 0.12f, radius = 0.25f, zmax = 0.30f, maxEdge = 0.24f, jitter = 0.006f,
                rowDy = 0.058f, colDx = 0.12f,
            ),
            rng,
        )
    }

    // ---------- Brainstem: tapered tube sweeping down and back ----------
    fun addStem() {
        val nr = 6
        val m = 7
        val pi = PI.toFloat()
        val rings = ArrayList<IntArray>()
        for (i in 0 until nr) {
            val t = i / (nr - 1).toFloat()
            val u = 1f - t
            // quadratic Bezier centre line
            val cx = u * u * 0.02f + 2f * u * t * 0.20f + t * t * 0.36f
            val cy = u * u * 0.58f + 2f * u * t * 0.73f + t * t * 1.02f
            val tx = 2f * u * (0.20f - 0.02f) + 2f * t * (0.36f - 0.20f)
            val ty = 2f * u * (0.73f - 0.58f) + 2f * t * (1.02f - 0.73f)
            val tl = hypot(tx, ty)
            val ux = ty / tl; val uy = -tx / tl                      // in-plane perpendicular
            val r = 0.112f - 0.03f * t + 0.012f * sin(pi * min(1f, t * 2.2f))
            rings.add(IntArray(m) { j ->
                val be = 2f * pi * j / m
                val c = cos(be); val s = sin(be)
                node(cx + r * c * ux, cy + r * c * uy, r * s * 0.9f, c * ux, c * uy, s)
            })
        }
        skin(rings)
    }

    fun finish(): NeuralMesh3D {
        val n = v.size
        var minX = Float.MAX_VALUE; var maxX = -Float.MAX_VALUE
        var minY = Float.MAX_VALUE; var maxY = -Float.MAX_VALUE
        for (p in v) {
            minX = min(minX, p[0]); maxX = max(maxX, p[0])
            minY = min(minY, p[1]); maxY = max(maxY, p[1])
        }
        // Centre the whole brain (stem included) on the origin so geo.center really is its centre.
        val ox = (minX + maxX) / 2f
        val oy = (minY + maxY) / 2f
        return NeuralMesh3D(
            FloatArray(n) { v[it][0] - ox }, FloatArray(n) { v[it][1] - oy }, FloatArray(n) { v[it][2] },
            FloatArray(n) { v[it][3] }, FloatArray(n) { v[it][4] }, FloatArray(n) { v[it][5] },
            IntArray(n) { st[it] }, FloatArray(n) { lf[it] },
            ea.toIntArray(), eb.toIntArray(), BooleanArray(ea.size) { er[it] },
        )
    }
}

// =====================================================================================================
// Per-frame projection + drawing
// =====================================================================================================

/** Per-frame buffers, allocated once and reused. */
class NeuralProjection(n: Int) {
    val sx = FloatArray(n)
    val sy = FloatArray(n)
    val scale = FloatArray(n)
    val w = FloatArray(n)                       // brightness 0..1 (facing + rim + lift)
    val paths = Array(BUCKETS) { Path() }
    val rimPath = Path()
}

internal fun DrawScope.drawBrainMesh3D(
    mesh: NeuralMesh3D,
    f: NeuralProjection,
    geo: NeuralGeometry,
    brainPulse: Float,
    pulsePhase: Float,
    driftPhase: Float,
    colors: AegisColorScheme,
) {
    val s = geo.brainWidth * 0.5f * BRAIN_SCALE * brainPulse
    val cx = geo.centerX
    val cy = geo.centerY
    val dp = 1.dp.toPx()
    val cyan = colors.cyan
    val pose = organicMeshPose(driftPhase)
    val cosY = cos(pose.yaw); val sinY = sin(pose.yaw)
    val cosP = cos(pose.pitch); val sinP = sin(pose.pitch)

    // 1. rotate + perspective project, and compute per-node brightness from the rotated normal
    for (i in 0 until mesh.count) {
        val x = mesh.px[i]; val y = mesh.py[i]; val z = mesh.pz[i]
        val x1 = x * cosY + z * sinY
        val z1 = -x * sinY + z * cosY
        val y2 = y * cosP + z1 * sinP
        val z2 = -y * sinP + z1 * cosP
        val persp = FOCAL / (FOCAL - z2)
        f.sx[i] = cx + x1 * s * persp
        f.sy[i] = cy + y2 * s * persp
        f.scale[i] = persp

        val nz1 = -mesh.nx[i] * sinY + mesh.nz[i] * cosY
        val nzr = -mesh.ny[i] * sinP + nz1 * cosP
        val front = smooth01((nzr + 0.25f) / 0.8f)
        val rim = (1f - abs(nzr)).let { it * it * it }
        f.w[i] = (0.10f + 0.50f * front + 0.42f * rim + 0.30f * mesh.lift[i]).coerceIn(0f, 1f)
    }

    // 2. bucket edges by brightness into paths; the silhouette loop gets its own path
    for (p in f.paths) p.rewind()
    f.rimPath.rewind()
    var rimW = 0f
    var rimN = 0
    for (e in mesh.edgeA.indices) {
        val a = mesh.edgeA[e]; val b = mesh.edgeB[e]
        val w = (f.w[a] + f.w[b]) * 0.5f
        val p: Path
        if (mesh.rimEdge[e]) {
            p = f.rimPath
            rimW += w; rimN++
        } else {
            p = f.paths[(w * BUCKETS).toInt().coerceIn(0, BUCKETS - 1)]
        }
        p.moveTo(f.sx[a], f.sy[a])
        p.lineTo(f.sx[b], f.sy[b])
    }

    val base = 0.75f * dp
    for (bi in 0 until BUCKETS) {
        val path = f.paths[bi]
        if (path.isEmpty) continue
        val w = (bi + 0.5f) / BUCKETS
        val alpha = 0.07f + 0.62f * w
        val stroke = base * (0.45f + 0.75f * w)
        if (bi >= BUCKETS - 2) {   // soft glow on the brightest lines
            drawPath(path, cyan.copy(alpha = alpha * 0.22f),
                style = Stroke(width = stroke * 3.4f, cap = StrokeCap.Round))
        }
        drawPath(path, cyan.copy(alpha = alpha), style = Stroke(width = stroke, cap = StrokeCap.Round))
    }
    if (!f.rimPath.isEmpty) {
        val rw = if (rimN > 0) rimW / rimN else 0.5f
        drawPath(f.rimPath, cyan.copy(alpha = 0.10f + 0.22f * rw),
            style = Stroke(width = base * 3.6f, cap = StrokeCap.Round))
        drawPath(f.rimPath, cyan.copy(alpha = 0.30f + 0.55f * rw),
            style = Stroke(width = base * 1.35f, cap = StrokeCap.Round))
    }

    // 3. Sparse bidirectional activity pulses. A deterministic golden-ratio offset avoids a visible wave
    // of identical dots while preserving a stable image across recompositions.
    for (e in 0 until mesh.edgeA.size step 13) {
        val a = mesh.edgeA[e]; val b = mesh.edgeB[e]
        val phaseOffset = (e * 0.61803399f) % 1f
        val forward = ((e / 13) and 1) == 0
        val progress = (pulsePhase + phaseOffset) % 1f
        val t = if (forward) progress else 1f - progress
        val w = f.w[a] + (f.w[b] - f.w[a]) * t
        if (w < 0.3f) continue
        val alpha = (sin(t * PI.toFloat()) * (0.25f + 0.75f * w)).coerceIn(0f, 1f)
        if (alpha > 0.1f) {
            val c = Offset(f.sx[a] + (f.sx[b] - f.sx[a]) * t, f.sy[a] + (f.sy[b] - f.sy[a]) * t)
            val r = 1.8f * dp * (0.6f + 0.8f * w)
            drawCircle(cyan.copy(alpha = alpha * 0.6f), r * 2f, c)
            drawCircle(Color.White.copy(alpha = alpha), r, c)
        }
    }

    // 4. nodes: crisp dots, small stars, and hub stars with a soft radial halo + glint. Back-facing nodes skipped.
    for (i in 0 until mesh.count) {
        val w = f.w[i]
        if (w < 0.22f) continue
        val sc = f.scale[i]
        val c = Offset(f.sx[i], f.sy[i])
        val kind = mesh.star[i]
        if (kind == 0) {
            drawCircle(cyan.copy(alpha = 0.25f + 0.6f * w), 1.3f * dp * sc * (0.7f + 0.6f * w), c)
            continue
        }
        val hub = kind == 2
        val tw = 0.84f + 0.16f * sin(TWO_PI * (pulsePhase + i * 0.137f))
        val a = (w * tw).coerceIn(0f, 1f)
        val r = 2.4f * dp * sc * (if (hub) 1f else 0.62f)
        if (hub) {
            val halo = r * 4.6f
            // Layered solid circles retain the holographic halo without allocating a gradient each frame.
            drawCircle(cyan.copy(alpha = a * 0.10f), halo, c)
            drawCircle(cyan.copy(alpha = a * 0.22f), halo * 0.55f, c)
        } else {
            drawCircle(cyan.copy(alpha = a * 0.16f), r * 3f, c)
        }
        drawCircle(cyan.copy(alpha = a * 0.5f), r * 1.4f, c)
        drawCircle(Color.White.copy(alpha = a * 0.95f), r * 0.5f, c)
        if (hub) {
            val len = r * 4.5f
            val glint = Color.White.copy(alpha = a * 0.4f)
            drawLine(glint, Offset(c.x - len, c.y), Offset(c.x + len, c.y), strokeWidth = 0.7f * dp)
            drawLine(glint, Offset(c.x, c.y - len), Offset(c.x, c.y + len), strokeWidth = 0.7f * dp)
        }
    }
}
