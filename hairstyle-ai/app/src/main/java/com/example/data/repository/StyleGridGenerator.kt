package com.example.data.repository

import com.example.data.model.GridTileState
import com.example.data.model.HairstyleItem
import com.example.data.model.TryOnError
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.atomic.AtomicReference

/**
 * Generates several hairstyle previews for the "4 AI Looks" grid.
 *
 * Cost & rate-limit protections:
 * - Only the first [allowed] styles are generated; the rest are marked [GridTileState.Locked]
 * - At most [maxConcurrency] requests run at once to stay under per-minute API limits
 * - No automatic retries; a key/quota/rate-limit error stops the remaining queued tiles
 */
object StyleGridGenerator {

    const val GRID_SIZE = 4
    const val DEFAULT_MAX_CONCURRENCY = 2

    /** Errors after which further requests would fail the same way. */
    private val abortingErrors = setOf(
        TryOnError.MissingApiKey,
        TryOnError.QuotaExceeded,
        TryOnError.RateLimitExceeded
    )

    suspend fun generate(
        styles: List<HairstyleItem>,
        allowed: Int,
        generate: suspend (HairstyleItem) -> AiTryOnResult,
        onTileUpdate: (index: Int, state: GridTileState) -> Unit,
        indices: List<Int> = styles.indices.toList(),
        maxConcurrency: Int = DEFAULT_MAX_CONCURRENCY
    ) {
        val toRun = indices.take(allowed.coerceAtLeast(0))
        indices.drop(toRun.size).forEach { onTileUpdate(it, GridTileState.Locked) }
        toRun.forEach { onTileUpdate(it, GridTileState.Queued) }

        val semaphore = Semaphore(maxConcurrency.coerceAtLeast(1))
        val abortedWith = AtomicReference<GridTileState.Failed?>(null)

        coroutineScope {
            toRun.forEach { index ->
                launch {
                    semaphore.withPermit {
                        abortedWith.get()?.let {
                            onTileUpdate(index, it)
                            return@withPermit
                        }
                        onTileUpdate(index, GridTileState.Generating)
                        val tile = when (val result = generate(styles[index])) {
                            is AiTryOnResult.Success -> GridTileState.Ready(result.resultBitmap, result.isAiGenerated)
                            is AiTryOnResult.Error -> {
                                val failed = GridTileState.Failed(
                                    result.error,
                                    result.detailedMessage ?: result.error.message
                                )
                                if (result.error in abortingErrors) abortedWith.compareAndSet(null, failed)
                                failed
                            }
                        }
                        onTileUpdate(index, tile)
                    }
                }
            }
        }
    }
}
