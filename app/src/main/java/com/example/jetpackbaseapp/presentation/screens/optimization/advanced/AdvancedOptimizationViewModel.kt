package com.example.jetpackbaseapp.presentation.screens.optimization.advanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackbaseapp.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.measureNanoTime

@HiltViewModel
class AdvancedOptimizationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AdvancedOptimizationState())
    val state: StateFlow<AdvancedOptimizationState> = _state.asStateFlow()

    /**
     * Test Value Classes vs Regular Classes
     */
    fun testValueClasses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = "")

            try {
                val results = withContext(Dispatchers.Default) {
                    listOf(
                        testValueClassWrapping(),
                        testValueClassCollections(),
                        testValueClassParameters()
                    )
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    valueClassResults = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    private fun testValueClassWrapping(): ValueClassComparison {
        val iterations = 100_000
        var sum1 = 0L
        var sum2 = 0

        // With Value Class - inlined, no object allocation
        val timeWithValueClass = measureNanoTime {
            repeat(iterations) { i ->
                val id = UserId(i) // No object created!
                sum1 += id.value
            }
        }

        // Without Value Class - creates wrapper object
        val timeWithoutValueClass = measureNanoTime {
            repeat(iterations) { i ->
                val id = RegularUserId(i) // Object created
                sum2 += id.value
            }
        }

        return ValueClassComparison(
            testName = "ID Wrapping (${iterations}x)",
            withValueClass = PerformanceMetrics(
                executionTimeNs = timeWithValueClass,
                objectsAllocated = 0, // No objects!
                memoryUsedBytes = 0
            ),
            withoutValueClass = PerformanceMetrics(
                executionTimeNs = timeWithoutValueClass,
                objectsAllocated = iterations.toLong(),
                memoryUsedBytes = iterations * 16L // Approx object overhead
            ),
            memoryReduction = 100.0 // 100% memory saved
        )
    }

    private fun testValueClassCollections(): ValueClassComparison {
        val count = 10_000
        
        val timeWithValueClass = measureNanoTime {
            val ids = List(count) { UserId(it) }
            ids.sumOf { it.value }
        }

        val timeWithoutValueClass = measureNanoTime {
            val ids = List(count) { RegularUserId(it) }
            ids.sumOf { it.value }
        }

        return ValueClassComparison(
            testName = "List Operations ($count items)",
            withValueClass = PerformanceMetrics(
                executionTimeNs = timeWithValueClass,
                objectsAllocated = count.toLong(),
                memoryUsedBytes = count * 4L // Just Ints
            ),
            withoutValueClass = PerformanceMetrics(
                executionTimeNs = timeWithoutValueClass,
                objectsAllocated = count * 2L, // List + wrappers
                memoryUsedBytes = count * 20L // Objects + Ints
            ),
            memoryReduction = 80.0
        )
    }

    private fun testValueClassParameters(): ValueClassComparison {
        val iterations = 100_000

        val timeWithValueClass = measureNanoTime {
            repeat(iterations) { i ->
                processWithValueClass(UserId(i))
            }
        }

        val timeWithoutValueClass = measureNanoTime {
            repeat(iterations) { i ->
                processWithoutValueClass(RegularUserId(i))
            }
        }

        return ValueClassComparison(
            testName = "Function Parameters (${iterations}x)",
            withValueClass = PerformanceMetrics(
                executionTimeNs = timeWithValueClass,
                objectsAllocated = 0,
                memoryUsedBytes = 0
            ),
            withoutValueClass = PerformanceMetrics(
                executionTimeNs = timeWithoutValueClass,
                objectsAllocated = iterations.toLong(),
                memoryUsedBytes = iterations * 16L
            ),
            memoryReduction = 100.0
        )
    }

    /**
     * Test Inline Functions vs Regular Functions
     */
    fun testInlineFunctions() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = "")

            try {
                val results = withContext(Dispatchers.Default) {
                    listOf(
                        testInlineHigherOrderFunction(),
                        testInlineMeasurement(),
                        testInlineReified()
                    )
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    inlineFunctionResults = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    private fun testInlineHigherOrderFunction(): InlineFunctionComparison {
        val iterations = 50_000

        // Inline - no Function object created
        val timeInline = measureNanoTime {
            repeat(iterations) { i ->
                inlineRepeat(10) { 
                    // Lambda is inlined, no object
                }
            }
        }

        // Regular - creates Function object
        val timeRegular = measureNanoTime {
            repeat(iterations) { i ->
                regularRepeat(10) { 
                    // Lambda creates Function object
                }
            }
        }

        return InlineFunctionComparison(
            testName = "Higher-Order Functions (${iterations}x)",
            withInline = PerformanceMetrics(
                executionTimeNs = timeInline,
                objectsAllocated = 0,
                memoryUsedBytes = 0
            ),
            withoutInline = PerformanceMetrics(
                executionTimeNs = timeRegular,
                objectsAllocated = iterations.toLong(),
                memoryUsedBytes = iterations * 24L // Function object
            ),
            speedup = timeRegular.toDouble() / timeInline.coerceAtLeast(1)
        )
    }

    private fun testInlineMeasurement(): InlineFunctionComparison {
        val iterations = 100_000

        val timeInline = measureNanoTime {
            repeat(iterations) {
                val (result, _) = measureExecutionTime {
                    it * 2
                }
            }
        }

        val timeRegular = measureNanoTime {
            repeat(iterations) {
                val (result, _) = measureExecutionTimeRegular {
                    it * 2
                }
            }
        }

        return InlineFunctionComparison(
            testName = "Measurement Utils (${iterations}x)",
            withInline = PerformanceMetrics(
                executionTimeNs = timeInline,
                objectsAllocated = iterations.toLong(), // Only Pairs
                memoryUsedBytes = iterations * 24L
            ),
            withoutInline = PerformanceMetrics(
                executionTimeNs = timeRegular,
                objectsAllocated = iterations * 2L, // Pairs + Functions
                memoryUsedBytes = iterations * 48L
            ),
            speedup = timeRegular.toDouble() / timeInline.coerceAtLeast(1)
        )
    }

    private fun testInlineReified(): InlineFunctionComparison {
        val iterations = 50_000
        val testValue: Any = "test"

        val timeInline = measureNanoTime {
            repeat(iterations) {
                checkType<String>(testValue) // Reified inline
            }
        }

        val timeRegular = measureNanoTime {
            repeat(iterations) {
                checkTypeRegular(testValue, String::class.java) // Regular with Class<T>
            }
        }

        return InlineFunctionComparison(
            testName = "Reified Type Check (${iterations}x)",
            withInline = PerformanceMetrics(
                executionTimeNs = timeInline,
                objectsAllocated = 0,
                memoryUsedBytes = 0
            ),
            withoutInline = PerformanceMetrics(
                executionTimeNs = timeRegular,
                objectsAllocated = 0,
                memoryUsedBytes = 0
            ),
            speedup = timeRegular.toDouble() / timeInline.coerceAtLeast(1)
        )
    }

    // Helper functions for comparison
    private inline fun inlineRepeat(times: Int, action: (Int) -> Unit) {
        for (i in 0 until times) action(i)
    }

    private fun regularRepeat(times: Int, action: (Int) -> Unit) {
        for (i in 0 until times) action(i)
    }

    private fun <T> measureExecutionTimeRegular(block: () -> T): Pair<T, Long> {
        val start = System.nanoTime()
        val result = block()
        return result to (System.nanoTime() - start)
    }

    private fun <T> checkTypeRegular(value: Any, clazz: Class<T>): Boolean {
        return clazz.isInstance(value)
    }

    private fun processWithValueClass(id: UserId): Int = id.value * 2
    private fun processWithoutValueClass(id: RegularUserId): Int = id.value * 2
}

// Regular class for comparison
data class RegularUserId(val value: Int)
