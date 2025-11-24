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
     * ==================== PHƯƠNG PHÁP 1: ĐỆ QUY THUẦN TÚY ====================
     * Tính Fibonacci bằng đệ quy (KHÔNG TỐI ƯU)
     *
     * Ví dụ: fib(5)
     * fib(5)
     * ├── fib(4) + fib(3)
     * │   ├── fib(3) + fib(2)    (tính lại!)
     * │   └── fib(2) + fib(1)    (tính lại!)
     * └── fib(3) + fib(2)        (tính lại fib(3)!)
     *
     * Vấn đề: Tính lại cùng giá trị nhiều lần
     * - fib(3) được tính 2 lần
     * - fib(2) được tính 3 lần
     * - Số lệnh gọi = O(2^n) → Nhanh chóng tăng lên
     *
     * Ví dụ: fib(40) cần ~300 triệu lệnh gọi!
     *
     * Time complexity: O(2^n) - Exponential (CHẬM)
     * Space complexity: O(n) - Gọi stack (TỐN)
     */
    private fun fibonacciRecursive(n: Int): Long {
        if (n <= 1) return n.toLong()
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2)
    }

    /**
     * ==================== PHƯƠNG PHÁP 2: LẶP (ITERATION) ====================
     * Tính Fibonacci bằng vòng lặp (TỐI ƯU NHẤT)
     *
     * Ví dụ: fib(5)
     * Step 1: prev=0, current=1, next=1
     * Step 2: prev=1, current=1, next=2
     * Step 3: prev=1, current=2, next=3
     * Step 4: prev=2, current=3, next=5
     * Step 5: prev=3, current=5, next=8 → return 5
     *
     * Ưu điểm:
     * - Tính từ dưới lên (bottom-up)
     * - Mỗi giá trị tính đúng 1 lần
     * - Không cần stack
     * - Chỉ cần 2 biến: prev, current
     *
     * Time complexity: O(n) - Linear (NHANH)
     * Space complexity: O(1) - Hằng số (TIẾT KIỆM)
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
     * ==================== PHƯƠNG PHÁP 3: ĐỆ QUY + MEMOIZATION ====================
     * Tính Fibonacci bằng đệ quy có lưu cache (CÁCH TÍNH TỐI ƯU)
     *
     * Ý tưởng: Lưu kết quả đã tính để không tính lại
     *
     * Ví dụ: fib(5)
     * fib(5)
     * ├── fib(4) + fib(3)
     * │   ├── fib(3) + fib(2)
     * │   │   ├── fib(2) + fib(1) → memo[2] = 1
     * │   │   └── return memo[2] = 1 (lấy từ cache!)
     * │   └── return memo[3] = 2
     * └── return memo[4] = 3
     *
     * Ưu điểm:
     * - Vẫn dùng đệ quy (dễ hiểu)
     * - Lưu kết quả đã tính (memoization)
     * - Không tính lại giá trị
     * - fib(40) chỉ cần ~80 lệnh gọi (thay vì 300 triệu!)
     *
     * Time complexity: O(n) - Linear (NHANH)
     * Space complexity: O(n) - Lưu cache (TỐN BỘ NHỚ)
     */
    private fun fibonacciMemoization(n: Int, memo: MutableMap<Int, Long> = mutableMapOf()): Long {
        if (n <= 1) return n.toLong()

        // Nếu đã tính trước đó, lấy từ cache thay vì tính lại
        memo[n]?.let { return it }

        // Tính kết quả
        val result = fibonacciMemoization(n - 1, memo) + fibonacciMemoization(n - 2, memo)
        // Lưu vào cache để lần sau không tính lại
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

                    // ==================== TEST 1: ITERATIVE ====================
                    // Tính fib(n) bằng vòng lặp
                    // Cách: Tính từ dưới lên (fib(0) → fib(1) → fib(2) → ... → fib(n))
                    // Mỗi giá trị tính 1 lần → Nhanh O(n)
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

                    // ==================== TEST 2: MEMOIZATION ====================
                    // Tính fib(n) bằng đệ quy + cache
                    // Cách: Gọi đệ quy, nhưng lưu kết quả đã tính
                    // Lần sau cần fib(k) → lấy từ cache, không tính lại
                    // Tương tự Iterative: O(n), nhưng dùng thêm bộ nhớ để lưu cache
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

                    // ==================== TEST 3: RECURSIVE (CHỈ VỚI N NHỎ) ====================
                    // Tính fib(n) bằng đệ quy thuần túy (KHÔNG CACHE)
                    // Cách: Gọi đệ quy, tính lại cùng giá trị nhiều lần
                    // Số lệnh gọi = O(2^n) → Nhanh chóng tăng exponential
                    // Ví dụ:
                    // - fib(30) → ~2 triệu lệnh gọi
                    // - fib(40) → ~330 triệu lệnh gọi (chậm!)
                    // - fib(50) → ~40 tỷ lệnh gọi (TIMEOUT!)
                    // Vì thế chỉ test với n nhỏ để không bị StackOverflow
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
