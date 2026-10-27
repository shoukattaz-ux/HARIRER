package com.example.data.model

import android.graphics.Bitmap

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
