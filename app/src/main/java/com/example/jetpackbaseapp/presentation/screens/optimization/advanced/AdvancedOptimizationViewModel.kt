package com.example.jetpackbaseapp.presentation.screens.optimization.advanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
     * ==================== VALUE CLASSES ====================
     *
     * Value Class = Zero-cost wrapper
     * - Compile-time: Có type safety (UserId ≠ ProductId)
     * - Runtime: Compiler inline thành primitive type, không tạo object
     *
     * Demo: So sánh tạo 100,000 ID wrappers
     *
     * Regular Class RegularUserId(val value: Int):
     * - Tạo 100,000 objects trên heap
     * - Mỗi object = 16 bytes (header + int + padding)
     * - Memory overhead: 1.6 MB
     *
     * Value Class UserId(val value: Int):
     * - Compiler inline thành int nguyên thuỷ
     * - Không tạo object
     * - Memory overhead: 0 bytes
     *
     * → Tiết kiệm 100% memory cho wrapper!
     */
    fun testValueClass() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = "")

            try {
                val result = withContext(Dispatchers.Default) {
                    // Force GC để có baseline sạch
                    System.gc()
                    Thread.sleep(100)

                    val iterations = 100_000

                    // ==================== TEST VALUE CLASS ====================
                    // UserId(i) → Compiler inline thành int, không tạo object
                    val memBeforeValue = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

                    var sum1 = 0L
                    val timeValue = measureNanoTime {
                        repeat(iterations) { i ->
                            val id = UserId(i)  // Không tạo object! Chỉ dùng int = i
                            sum1 += id.value
                        }
                    }

                    val memAfterValue = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

                    // Force GC giữa 2 tests
                    System.gc()
                    Thread.sleep(100)

                    // ==================== TEST REGULAR CLASS ====================
                    // RegularUserId(i) → Tạo object mới trên heap
                    val memBeforeRegular = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

                    var sum2 = 0L
                    val timeRegular = measureNanoTime {
                        repeat(iterations) { i ->
                            val id = RegularUserId(i)  // Tạo object! Allocate trên heap
                            sum2 += id.value
                        }
                    }

                    val memAfterRegular = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

                    ValueClassResult(
                        testName = "ID Wrapper Creation",
                        valueClassTime = timeValue,
                        valueClassMemory = memBeforeValue,
                        valueClassMemoryAfter = memAfterValue,
                        regularClassTime = timeRegular,
                        regularClassMemory = memBeforeRegular,
                        regularClassMemoryAfter = memAfterRegular,
                        iterations = iterations
                    )
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    valueClassResult = result
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
}

