package com.example.data.model

import android.graphics.Bitmap
import com.example.data.api.LifestyleScene
import com.example.data.api.SceneRenderStyle

/**
 * State of one tile in the 2x2 "4 AI Looks" grid.
 */
sealed class GridTileState {
    /** Not generated because the free preview quota does not cover it. */
    object Locked : GridTileState()

    /** Waiting for a free generation slot. */
    object Queued : GridTileState()

    object Generating : GridTileState()

    data class Ready(val bitmap: Bitmap, val isAiGenerated: Boolean) : GridTileState()

    data class Failed(val error: TryOnError, val message: String) : GridTileState()
}

data class StyleGridState(
    val styles: List<HairstyleItem> = emptyList(),
    val tiles: List<GridTileState> = emptyList(),
    val isRunning: Boolean = false
) {
    val readyCount: Int
        get() = tiles.count { it is GridTileState.Ready }

    val lockedCount: Int
        get() = tiles.count { it is GridTileState.Locked }
}

/**
 * Lifestyle scenes generated for one try-on look on the Style Score screen.
 * [lookId] identifies the look the scenes belong to; a new look starts with no scenes.
 */
data class SceneStudioState(
    val lookId: Int = 0,
    val scenes: Map<String, GridTileState> = emptyMap(),
    val isGenerating: Boolean = false
) {
    companion object {
        fun key(scene: LifestyleScene, renderStyle: SceneRenderStyle): String = "${scene.name}_${renderStyle.name}"
    }
}
