package com.example

import com.example.data.model.GridTileState
import com.example.data.model.TryOnError
import com.example.data.repository.AiTryOnResult
import com.example.data.repository.HairstyleRepository
import com.example.data.repository.StyleGridGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

/**
 * Unit tests for the "My 4 AI Looks" grid generation rules.
 */
class StyleGridGeneratorTest {

    private val styles = HairstyleRepository.getAllHairstyles().take(4)

    private fun runGrid(
        allowed: Int,
        indices: List<Int> = styles.indices.toList(),
        maxConcurrency: Int = StyleGridGenerator.DEFAULT_MAX_CONCURRENCY,
        generate: suspend () -> AiTryOnResult
    ): Pair<Map<Int, GridTileState>, Int> {
        val tiles = mutableMapOf<Int, GridTileState>()
        val calls = AtomicInteger(0)
        runBlocking {
            StyleGridGenerator.generate(
                styles = styles,
                allowed = allowed,
                indices = indices,
                maxConcurrency = maxConcurrency,
                generate = {
                    calls.incrementAndGet()
                    generate()
                },
                onTileUpdate = { index, state -> tiles[index] = state }
            )
        }
        return tiles to calls.get()
    }

    @Test
    fun tilesBeyondFreeAllowance_areLockedAndNeverRequested() {
        val (tiles, calls) = runGrid(allowed = 2) { AiTryOnResult.Error(TryOnError.ProcessingFailed) }
        assertEquals(2, calls)
        assertTrue(tiles[0] is GridTileState.Failed)
        assertTrue(tiles[1] is GridTileState.Failed)
        assertEquals(GridTileState.Locked, tiles[2])
        assertEquals(GridTileState.Locked, tiles[3])
    }

    @Test
    fun zeroAllowance_locksEverything() {
        val (tiles, calls) = runGrid(allowed = 0) { AiTryOnResult.Error(TryOnError.ProcessingFailed) }
        assertEquals(0, calls)
        assertTrue(tiles.values.all { it == GridTileState.Locked })
    }

    @Test
    fun rateLimit_stopsRemainingRequests() {
        val (tiles, calls) = runGrid(allowed = 4, maxConcurrency = 1) {
            AiTryOnResult.Error(TryOnError.RateLimitExceeded, "Free AI preview limit reached.")
        }
        assertEquals("Only the first request should hit the API", 1, calls)
        assertEquals(4, tiles.size)
        tiles.values.forEach {
            assertTrue(it is GridTileState.Failed)
            assertEquals(TryOnError.RateLimitExceeded, (it as GridTileState.Failed).error)
        }
    }

    @Test
    fun ordinaryFailure_doesNotStopOtherTiles() {
        val (_, calls) = runGrid(allowed = 4, maxConcurrency = 1) { AiTryOnResult.Error(TryOnError.AiGenerationFailed) }
        assertEquals(4, calls)
    }

    @Test
    fun concurrency_isCapped() {
        val active = AtomicInteger(0)
        val peak = AtomicInteger(0)
        val (_, calls) = runGrid(allowed = 4, maxConcurrency = 2) {
            val now = active.incrementAndGet()
            peak.updateAndGet { maxOf(it, now) }
            delay(30)
            active.decrementAndGet()
            AiTryOnResult.Error(TryOnError.ProcessingFailed)
        }
        assertEquals(4, calls)
        assertEquals(2, peak.get())
    }

    @Test
    fun retryOfSingleTile_onlyTouchesThatTile() {
        val (tiles, calls) = runGrid(allowed = 1, indices = listOf(3)) { AiTryOnResult.Error(TryOnError.ProcessingFailed) }
        assertEquals(1, calls)
        assertEquals(setOf(3), tiles.keys)
    }
}
