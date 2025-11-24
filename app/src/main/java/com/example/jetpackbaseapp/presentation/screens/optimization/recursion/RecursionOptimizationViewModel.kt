package com.example.jetpackbaseapp.presentation.screens.optimization.recursion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackbaseapp.domain.model.FibonacciResult
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
class RecursionOptimizationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(RecursionState())
    val state: StateFlow<RecursionState> = _state.asStateFlow()

    /**
     * Tính Fibonacci bằng đệ quy
     * Time complexity: O(2^n)
     * Space complexity: O(n) - call stack
     */
    private fun fibonacciRecursive(n: Int): Long {
        if (n <= 1) return n.toLong()
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2)
    }

    /**
     * Tính Fibonacci bằng iteration (TỐI ƯU)
     * Time complexity: O(n)
     * Space complexity: O(1)
     */
    private fun fibonacciIterative(n: Int): Long {
        if (n <= 1) return n.toLong()

        var prev = 0L
        var current = 1L

        repeat(n - 1) {
            val next = prev + current
            prev = current
            current = next
        }

        return current
    }

    /**
     * Tính Fibonacci bằng đệ quy có memoization
     * Time complexity: O(n)
     * Space complexity: O(n)
     */
    private fun fibonacciMemoization(n: Int, memo: MutableMap<Int, Long> = mutableMapOf()): Long {
        if (n <= 1) return n.toLong()

        memo[n]?.let { return it }

        val result = fibonacciMemoization(n - 1, memo) + fibonacciMemoization(n - 2, memo)
        memo[n] = result
        return result
    }

    fun calculateFibonacci(n: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = ""
            )

            try {
                val results = withContext(Dispatchers.Default) {
                    val resultsList = mutableListOf<FibonacciResult>()

                    // Test Iterative (luôn chạy được)
                    val iterativeValue: Long
                    val iterativeTime = measureNanoTime {
                        iterativeValue = fibonacciIterative(n)
                    }
                    resultsList.add(
                        FibonacciResult(
                            value = iterativeValue,
                            executionTimeNs = iterativeTime,
                            method = "Iterative"
                        )
                    )

                    // Test Memoization
                    val memoValue: Long
                    val memoTime = measureNanoTime {
                        memoValue = fibonacciMemoization(n)
                    }
                    resultsList.add(
                        FibonacciResult(
                            value = memoValue,
                            executionTimeNs = memoTime,
                            method = "Memoization"
                        )
                    )

                    // Test Recursive (chỉ với n nhỏ)
                    val recursiveValue: Long
                    val recursiveTime = measureNanoTime {
                        recursiveValue = fibonacciRecursive(n)
                    }
                    resultsList.add(
                        FibonacciResult(
                            value = recursiveValue,
                            executionTimeNs = recursiveTime,
                            method = "Recursive"
                        )
                    )

                    resultsList
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    results = results,
                    selectedN = n
                )
            } catch (e: StackOverflowError) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Stack Overflow! Số quá lớn cho đệ quy."
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearResults() {
        _state.value = RecursionState()
    }
}

data class RecursionState(
    val isLoading: Boolean = false,
    val results: List<FibonacciResult> = emptyList(),
    val selectedN: Int = 0,
    val error: String = ""
)
