package dev.ragnar.aegis.ui.home.visualization

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Encapsulates all animation values for [AICoreVisualization].
 *
 * Created once inside [rememberNeuralAnimationState] and passed to the Canvas draw functions.
 * This keeps all InfiniteTransition state in one place, allowing [AICoreVisualization] to
 * be isolated from floating panel data updates — panel recompositions do not touch this.
 *
 * Animation specs follow UI_UX_SPECIFICATION.md Section 7.
 * Decision: DEC-014 — animation state isolated from panel data.
 *
 * When [isPaused] is true all animation values freeze at their current position.
 */
@Stable
class NeuralAnimationState(
    /** Inner circular indicator rotation: 0 → 360 in 6s, clockwise. */
    val innerRotation: State<Float>,
    /** Outer circular indicator rotation: 360 → 0 in 10s, counter-clockwise. */
    val outerRotation: State<Float>,
    /** Brain pulse scale: 1.0 → 1.05 → 1.0, 3s. */
    val brainPulse: State<Float>,
    /** Vertical beam alpha: 0.3 → 1.0 → 0.3, 2s. */
    val beamAlpha: State<Float>,
    /** Data pulse travel: 0 → 1 (fraction along each edge), 2s, offset per edge. */
    val pulsePhase: State<Float>,
    /** Particle drift phase: 0 → 1, 6s — drives vertical + alpha drift. */
    val particlePhase: State<Float>,
    /** Scanning ring expansion: 0 → 1 → 0, 4s. */
    val ringPhase: State<Float>,
    /** Waveform phase shift: 0 → 2π, 4s. */
    val wavePhase: State<Float>,
)

/**
 * Creates and remembers [NeuralAnimationState]. All transitions are started here.
 *
 * @param isPaused When true, all animations freeze (battery-saver / user setting).
 * @param speedMultiplier 1f = normal, 0.5f = slow, 2f = fast.
 */
@Composable
fun rememberNeuralAnimationState(
    isPaused: Boolean = false,
    speedMultiplier: Float = 1f,
): NeuralAnimationState {
    if (isPaused) {
        return remember {
            NeuralAnimationState(
                innerRotation = mutableStateOf(0f),
                outerRotation = mutableStateOf(360f),
                brainPulse = mutableStateOf(1f),
                beamAlpha = mutableStateOf(0.6f),
                pulsePhase = mutableStateOf(0f),
                particlePhase = mutableStateOf(0f),
                ringPhase = mutableStateOf(0f),
                wavePhase = mutableStateOf(0f),
            )
        }
    }

    // Duration helpers — scaled by speedMultiplier
    fun ms(base: Int) = (base / speedMultiplier.coerceIn(0.25f, 4f)).toInt()

    val transition = rememberInfiniteTransition(label = "NeuralCore")

    val innerRotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(ms(6000), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "InnerRot",
    )

    val outerRotation = transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(ms(10000), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "OuterRot",
    )

    val brainPulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(ms(3000), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "BrainPulse",
    )

    val beamAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(ms(2000), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "BeamAlpha",
    )

    val pulsePhase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(ms(2000), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "PulsePhase",
    )

    val particlePhase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(ms(6000), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ParticlePhase",
    )

    val ringPhase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(ms(4000), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "RingPhase",
    )

    val wavePhase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(ms(4000), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "WavePhase",
    )

    return remember(isPaused, speedMultiplier) {
        NeuralAnimationState(
            innerRotation = innerRotation,
            outerRotation = outerRotation,
            brainPulse = mutableStateOf(brainPulse),
            beamAlpha = mutableStateOf(beamAlpha),
            pulsePhase = pulsePhase,
            particlePhase = particlePhase,
            ringPhase = ringPhase,
            wavePhase = wavePhase,
        )
    }
}
