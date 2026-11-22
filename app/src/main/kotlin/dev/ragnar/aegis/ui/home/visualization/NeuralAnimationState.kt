package dev.ragnar.aegis.ui.home.visualization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos

/**
 * Encapsulates all animation values for [AICoreVisualization].
 *
 * Created once inside [rememberNeuralAnimationState] and passed to the Canvas draw functions.
 * This keeps all animation state in one place, allowing [AICoreVisualization] to
 * be isolated from floating panel data updates — panel recompositions do not touch this.
 *
 * Animation specs follow UI_UX_SPECIFICATION.md Section 7.
 * Decision: DEC-014 — animation state isolated from panel data.
 *
 * When [isPaused] is true all animation values freeze at their current position. Updates are
 * intentionally capped at 30 FPS because this full-screen Canvas is CPU-bound on target hardware.
 */
@Stable
class NeuralAnimationState(
    /** Inner circular indicator rotation: 0 → 360 in 6s, clockwise. */
    val innerRotation: State<Float>,
    /** Outer circular indicator rotation: 360 → 0 in 10s, counter-clockwise. */
    val outerRotation: State<Float>,
    /** Brain pulse scale: 1.0 → 1.018 → 1.0, 7s. */
    val brainPulse: State<Float>,
    /** Vertical beam alpha: 0.3 → 1.0 → 0.3, 2s. */
    val beamAlpha: State<Float>,
    /** Data pulse travel: 0 → 1 (fraction along each edge), 3s, offset per edge. */
    val pulsePhase: State<Float>,
    /** Particle drift phase: 0 → 1, 6s — drives vertical + alpha drift. */
    val particlePhase: State<Float>,
    /** Scanning ring expansion: 0 → 1 → 0, 4s. */
    val ringPhase: State<Float>,
    /** Waveform phase shift: 0 → 2π, 4s. */
    val wavePhase: State<Float>,
    /** Slow 0 → 1 phase used exclusively for the neural mesh's organic camera drift. */
    val meshDriftPhase: State<Float>,
    /** Slow 0 → 1 phase used by the faint application-source code rain. */
    val codeRainPhase: State<Float>,
)

/**
 * Creates and remembers [NeuralAnimationState]. One capped animation clock drives every value.
 *
 * @param isPaused When true, all animations freeze (battery-saver / user setting).
 * @param speedMultiplier 1f = normal, 0.5f = slow, 2f = fast.
 */
@Composable
fun rememberNeuralAnimationState(
    isPaused: Boolean = false,
    speedMultiplier: Float = 1f,
): NeuralAnimationState {
    val elapsedSeconds = remember { mutableFloatStateOf(0f) }
    val boundedSpeedMultiplier = speedMultiplier.coerceIn(0.25f, 4f)

    LaunchedEffect(isPaused, boundedSpeedMultiplier) {
        if (isPaused) return@LaunchedEffect

        val phaseAtStart = elapsedSeconds.floatValue
        val startNanos = withFrameNanos { it }
        while (isActive) {
            val frameNanos = withFrameNanos { it }
            val elapsedNanos = frameNanos - startNanos
            elapsedSeconds.floatValue = phaseAtStart +
                (elapsedNanos / NANOS_PER_SECOND.toFloat()) * boundedSpeedMultiplier
            delay(FRAME_INTERVAL_MILLIS)
        }
    }

    return remember {
        NeuralAnimationState(
            innerRotation = derivedStateOf { positiveModulo(elapsedSeconds.floatValue * 60f, 360f) },
            outerRotation = derivedStateOf { 360f - positiveModulo(elapsedSeconds.floatValue * 36f, 360f) },
            brainPulse = derivedStateOf {
                1.009f - 0.009f * cos(2f * PI.toFloat() * elapsedSeconds.floatValue / 7f)
            },
            beamAlpha = derivedStateOf {
                0.65f - 0.35f * cos(PI.toFloat() * elapsedSeconds.floatValue / 2f)
            },
            pulsePhase = derivedStateOf { positiveModulo(elapsedSeconds.floatValue / 3f, 1f) },
            particlePhase = derivedStateOf { positiveModulo(elapsedSeconds.floatValue / 6f, 1f) },
            ringPhase = derivedStateOf { positiveModulo(elapsedSeconds.floatValue / 4f, 1f) },
            wavePhase = derivedStateOf { positiveModulo(elapsedSeconds.floatValue / 4f, 1f) },
            meshDriftPhase = derivedStateOf { positiveModulo(elapsedSeconds.floatValue / 26f, 1f) },
            codeRainPhase = derivedStateOf { positiveModulo(elapsedSeconds.floatValue / 18f, 1f) },
        )
    }
}

private const val FRAME_INTERVAL_MILLIS = 33L
private const val NANOS_PER_SECOND = 1_000_000_000L

private fun positiveModulo(value: Float, modulus: Float): Float {
    val remainder = value % modulus
    return if (remainder < 0f) remainder + modulus else remainder
}
