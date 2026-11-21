package dev.ragnar.aegis.data.apps

import dev.ragnar.aegis.domain.apps.AppInfo
import kotlinx.coroutines.flow.Flow

/** Source of launcher activities and their launch action. */
interface AppRepository {
    /** Emits the current list and a replacement after relevant package broadcasts. */
    fun observeLaunchableApps(): Flow<List<AppInfo>>

    /** Starts [app], returning a failure rather than allowing the launcher UI to crash. */
    fun launch(app: AppInfo): Result<Unit>
}
