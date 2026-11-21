package dev.ragnar.aegis.domain.widget

/**
 * Represents an individual third-party app widget hosted on the AEGIS HUD.
 *
 * @property appWidgetId The unique widget allocation ID assigned by AppWidgetHost.
 * @property providerPackage The package name of the widget provider.
 * @property providerClass The class name of the widget provider.
 * @property label The display label of the widget.
 * @property heightDp The display height in DP allocated to this widget container.
 */
data class HostedWidget(
    val appWidgetId: Int,
    val providerPackage: String,
    val providerClass: String,
    val label: String,
    val heightDp: Int = DEFAULT_HEIGHT_DP,
) {
    companion object {
        const val DEFAULT_HEIGHT_DP = 180
        val ALLOWED_HEIGHTS_DP = listOf(140, 200, 280)

        fun nextHeight(currentHeight: Int): Int {
            val index = ALLOWED_HEIGHTS_DP.indexOf(currentHeight)
            return if (index != -1 && index + 1 < ALLOWED_HEIGHTS_DP.size) {
                ALLOWED_HEIGHTS_DP[index + 1]
            } else {
                ALLOWED_HEIGHTS_DP.first()
            }
        }
    }
}

/**
 * Summary metadata describing an installed widget provider available for docking.
 */
data class WidgetProviderSummary(
    val providerPackage: String,
    val providerClass: String,
    val label: String,
    val appName: String,
    val minWidthDp: Int,
    val minHeightDp: Int,
    val configureClass: String? = null,
)

/**
 * UI state for the Widget Hosting subsystem.
 */
data class WidgetHostUiState(
    val hostedWidgets: List<HostedWidget> = emptyList(),
    val availableProviders: List<WidgetProviderSummary> = emptyList(),
    val filteredProviders: List<WidgetProviderSummary> = emptyList(),
    val searchQuery: String = "",
    val isDeckVisible: Boolean = false,
    val isPickerVisible: Boolean = false,
    val statusMessage: String? = null,
)
