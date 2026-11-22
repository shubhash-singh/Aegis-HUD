package dev.ragnar.aegis.ui.home.visualization

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Closed 3D brain shell, built once from [BrainMeshData].
 *
 * Unit space: x = front(-1) to back(+1), y = screen-down, z = toward the viewer.
 * Each patch (cerebrum, cerebellum, brainstem) gets a near surface (z > 0) and a mirrored far
 * surface (z < 0). Outline nodes sit at z = 0 and are shared, so the two sides are stitched
 * together along the rim.
 */
class NeuralMesh3D private constructor(
    val px: FloatArray,
    val py: FloatArray,
    val pz: FloatArray,
    val nx: FloatArray,
    val ny: FloatArray,
    val nz: FloatArray,
    val edgeA: IntArray,
    val edgeB: IntArray,
    val edgeRim: BooleanArray,
) {
    val count: Int get() = px.size
    val edgeCount: Int get() = edgeA.size

    companion object {
        fun build(): NeuralMesh3D {
            val parts = BrainMeshData.parts

            var total = 0
            for (p in parts) total += p.nodeCount + p.boundary.count { !it }

            val xs = FloatArray(total)
            val ys = FloatArray(total)
            val zs = FloatArray(total)
            val nxs = FloatArray(total)
            val nys = FloatArray(total)
            val nzs = FloatArray(total)
            val rimByEdge = LinkedHashMap<Long, Boolean>()
            val triangles = ArrayList<IntArray>() // a, b, c, side(+1 near / -1 far)

            fun addEdge(a: Int, b: Int, rim: Boolean) {
                val key = min(a, b).toLong() * total + max(a, b)
                rimByEdge[key] = (rimByEdge[key] ?: false) || rim
            }

            var off = 0
            for (p in parts) {
                val n = p.nodeCount
                val far = IntArray(n)
                var next = off + n
                for (i in 0 until n) {
                    val z = surfaceZ(p.u[i], p.v[i], p.t[i], p.h)
                    xs[off + i] = p.u[i]; ys[off + i] = p.v[i]; zs[off + i] = z
                    if (p.boundary[i]) {
                        far[i] = off + i
                    } else {
                        far[i] = next
                        xs[next] = p.u[i]; ys[next] = p.v[i]; zs[next] = -z
                        next++
                    }
                }
                var k = 0
                while (k < p.tris.size) {
                    val a = p.tris[k]; val b = p.tris[k + 1]; val c = p.tris[k + 2]
                    triangles.add(intArrayOf(off + a, off + b, off + c, 1))
                    triangles.add(intArrayOf(far[a], far[b], far[c], -1))
                    k += 3
                }
                k = 0
                while (k < p.edges.size) {
                    val a = p.edges[k]; val b = p.edges[k + 1]; val rim = p.edges[k + 2] == 1
                    addEdge(off + a, off + b, rim)
                    addEdge(far[a], far[b], rim)
                    k += 3
                }
                off = next
            }

            // Vertex normals: area-weighted, each triangle oriented away from the mid-plane.
            for (t in triangles) {
                val a = t[0]; val b = t[1]; val c = t[2]
                val ux = xs[b] - xs[a]; val uy = ys[b] - ys[a]; val uz = zs[b] - zs[a]
                val vx = xs[c] - xs[a]; val vy = ys[c] - ys[a]; val vz = zs[c] - zs[a]
                var cx = uy * vz - uz * vy
                var cy = uz * vx - ux * vz
                var cz = ux * vy - uy * vx
                if (cz * t[3] < 0f) { cx = -cx; cy = -cy; cz = -cz }
                for (idx in intArrayOf(a, b, c)) {
                    nxs[idx] += cx; nys[idx] += cy; nzs[idx] += cz
                }
            }
            for (i in 0 until total) {
                val len = max(sqrt(nxs[i] * nxs[i] + nys[i] * nys[i] + nzs[i] * nzs[i]), 1e-6f)
                nxs[i] /= len; nys[i] /= len; nzs[i] /= len
            }

            val m = rimByEdge.size
            val ea = IntArray(m)
            val eb = IntArray(m)
            val er = BooleanArray(m)
            var e = 0
            for ((key, rim) in rimByEdge) {
                ea[e] = (key / total).toInt()
                eb[e] = (key % total).toInt()
                er[e] = rim
                e++
            }
            return NeuralMesh3D(xs, ys, zs, nxs, nys, nzs, ea, eb, er)
        }

        /** Dome profile + a little gyri-like undulation. t = 0 on the outline, 1 deep inside. */
        private fun surfaceZ(u: Float, v: Float, t: Float, h: Float): Float {
            val prof = sqrt(max(0f, 1f - (1f - t) * (1f - t)))
            val taper = 1f - 0.18f * ((-u - 0.25f) / 0.75f).coerceIn(0f, 1f)
            val gyri = 0.035f * prof * sin(13f * u + 4f * v + 1f) * cos(10f * v - 3f * u)
            return h * prof * taper + gyri
        }
    }
}

private fun smooth01(x: Float): Float {
    val c = x.coerceIn(0f, 1f)
    return c * c * (3f - 2f * c)
}

/**
 * Rotates (yaw, then pitch) and perspective-projects every node, and computes a 0..1
 * brightness per node from its rotated normal (facing the viewer + a bright rim).
 * [z2] receives the rotated depth (larger = nearer) for back-to-front sorting.
 */
internal fun projectNeuralMesh(
    mesh: NeuralMesh3D,
    yaw: Float,
    pitch: Float,
    focal: Float,
    scale: Float,
    centerX: Float,
    centerY: Float,
    sx: FloatArray,
    sy: FloatArray,
    persp: FloatArray,
    w: FloatArray,
    z2: FloatArray,
) {
    val cY = cos(yaw); val sY = sin(yaw)
    val cP = cos(pitch); val sP = sin(pitch)
    for (i in 0 until mesh.count) {
        val x = mesh.px[i]; val y = mesh.py[i]; val z = mesh.pz[i]
        val x1 = x * cY + z * sY
        val z1 = -x * sY + z * cY
        val y2 = y * cP + z1 * sP
        val zr = -y * sP + z1 * cP
        val pr = focal / (focal - zr)
        sx[i] = centerX + x1 * scale * pr
        sy[i] = centerY + y2 * scale * pr
        persp[i] = pr
        z2[i] = zr

        val nz1 = -mesh.nx[i] * sY + mesh.nz[i] * cY
        val nzr = -mesh.ny[i] * sP + nz1 * cP
        val front = smooth01((nzr + 0.10f) / 0.7f)
        val rimT = 1f - abs(nzr)
        val rim = rimT * rimT * rimT
        w[i] = (0.05f + 0.55f * front + 0.40f * rim * (0.3f + 0.7f * front)).coerceIn(0f, 1f)
    }
}
