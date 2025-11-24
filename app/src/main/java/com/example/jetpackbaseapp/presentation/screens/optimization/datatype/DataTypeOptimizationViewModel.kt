package com.example.jetpackbaseapp.presentation.screens.optimization.datatype

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackbaseapp.domain.model.DataTypeComparison
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
class DataTypeOptimizationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(DataTypeState())
    val state: StateFlow<DataTypeState> = _state.asStateFlow()

    /**
     * So sánh Primitive vs Boxed types
     */
    fun comparePrimitiveVsBoxed() {
        viewModelScope.launch {
            _state.value = _state.value.copy(primitiveComparisons = emptyList())

            try {
                val results = withContext(Dispatchers.Default) {
                    val iterations = 10_000_000
                    val comparisons = mutableListOf<DataTypeComparison>()
                    val runtime = Runtime.getRuntime()

                    // ==================== Test 1: INT (PRIMITIVE) ====================
                    // Cùng cấp phát 1 mảng IntArray, cùng chạy 1 vòng lặp gán giá trị
                    // Chạy 3 lần để lấy trung bình (tránh fluctuation)
                    var intTimeSum = 0L
                    var intMemorySum = 0L
                    repeat(3) {
                        System.gc()
                        Thread.sleep(100)
                        val memBefore = runtime.totalMemory() - runtime.freeMemory()

                        val intTime = measureNanoTime {
                            // Cấp phát: IntArray(size) - mảng primitive
                            val intArray = IntArray(iterations / 100)
                            // Chạy vòng lặp: gán int vào array
                            repeat(intArray.size) { i ->
                                intArray[i] = i // Gán primitive int - NHANH
                            }
                        }

                        val memAfter = runtime.totalMemory() - runtime.freeMemory()
                        intTimeSum += intTime
                        intMemorySum += (memAfter - memBefore).coerceAtLeast(0)
                    }
                    val avgIntTime = intTimeSum / 3
                    val avgIntMemory = intMemorySum / 3

                    comparisons.add(
                        DataTypeComparison(
                            typeName = "int (primitive)",
                            memoryBytes = avgIntMemory,
                            operationTimeNs = avgIntTime,
                            isPrimitive = true,
                            description = ""
                        )
                    )

                    // ==================== Test 2: INTEGER (BOXED) ====================
                    // Cùng cấp phát 1 mảng Array<Integer>, cùng chạy 1 vòng lặp gán giá trị
                    // Nhưng dùng Integer.valueOf() để boxing
                    // Chạy 3 lần để lấy trung bình
                    var integerTimeSum = 0L
                    var integerMemorySum = 0L
                    repeat(3) {
                        System.gc()
                        Thread.sleep(100)
                        val memBefore = runtime.totalMemory() - runtime.freeMemory()

                        val integerTime = measureNanoTime {
                            // Cấp phát: Array<Integer>(size) - mảng object
                            val intArray = Array(iterations / 100) { 0 }
                            // Chạy vòng lặp: gán Integer vào array
                            repeat(intArray.size) { i ->
                                intArray[i] = Integer.valueOf(i) // Boxing mỗi lần - CHẬM, tốn bộ nhớ
                            }
                        }

                        val memAfter = runtime.totalMemory() - runtime.freeMemory()
                        integerTimeSum += integerTime
                        integerMemorySum += (memAfter - memBefore).coerceAtLeast(0)
                    }
                    val avgIntegerTime = integerTimeSum / 3
                    val avgIntegerMemory = integerMemorySum / 3

                    comparisons.add(
                        DataTypeComparison(
                            typeName = "Integer (boxed)",
                            memoryBytes = avgIntegerMemory,
                            operationTimeNs = avgIntegerTime,
                            isPrimitive = false,
                            description = ""
                        )
                    )

                    comparisons
                }

                _state.value = _state.value.copy(
                    primitiveComparisons = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Primitive test error: ${e.message}"
                )
            }
        }
    }

    /**
     * So sánh các kiểu collection
     */
    fun compareCollections() {
        viewModelScope.launch {
            _state.value = _state.value.copy(collectionComparisons = emptyList())

            try {
                val results = withContext(Dispatchers.Default) {
                    val size = 10_000
                    val comparisons = mutableListOf<DataTypeComparison>()
                    val runtime = Runtime.getRuntime()

                    // ==================== Test 1: ArrayList (PRE-SIZED) ====================
                    // Cấp phát ArrayList với capacity từ đầu
                    // Add 10_000 phần tử vào - không phải resize
                    System.gc()
                    Thread.sleep(50)
                    val memBefore1 = runtime.totalMemory() - runtime.freeMemory()
                    val arrayListTime = measureNanoTime {
                        val list = ArrayList<Int>(size) // Cấp phát capacity = size từ đầu
                        repeat(size) { i ->
                            list.add(i) // Thêm phần tử - chỉ tăng size, không resize mảng
                        }
                        list.forEach { _ -> } // Duyệt hết
                    }
                    val memAfter1 = runtime.totalMemory() - runtime.freeMemory()
                    val memUsed1 = (memAfter1 - memBefore1).coerceAtLeast(0)

                    comparisons.add(
                        DataTypeComparison(
                            typeName = "ArrayList (pre-sized)",
                            memoryBytes = memUsed1,
                            operationTimeNs = arrayListTime,
                            isPrimitive = false,
                            description = ""
                        )
                    )

                    // ==================== Test 2: ArrayList (NO PRE-SIZE) ====================
                    // Cấp phát ArrayList không biết capacity
                    // Add 10_000 phần tử vào - phải resize nhiều lần
                    System.gc()
                    Thread.sleep(50)
                    val memBefore2 = runtime.totalMemory() - runtime.freeMemory()
                    val arrayListNoSizeTime = measureNanoTime {
                        val list = ArrayList<Int>() // Không cấp phát capacity, mặc định = 10
                        repeat(size) { i ->
                            list.add(i) // Mỗi khi vượt capacity → phải resize (copy array, tốn thời gian)
                        }
                        list.forEach { _ -> } // Duyệt hết
                    }
                    val memAfter2 = runtime.totalMemory() - runtime.freeMemory()
                    val memUsed2 = (memAfter2 - memBefore2).coerceAtLeast(0)

                    comparisons.add(
                        DataTypeComparison(
                            typeName = "ArrayList (no pre-size)",
                            memoryBytes = memUsed2,
                            operationTimeNs = arrayListNoSizeTime,
                            isPrimitive = false,
                            description = ""
                        )
                    )

                    // ==================== Test 3: IntArray (PRIMITIVE) ====================
                    // Cấp phát mảng primitive - kích thước cố định
                    // Gán 10_000 int vào - không có resize, không có boxing
                    System.gc()
                    Thread.sleep(50)
                    val memBefore3 = runtime.totalMemory() - runtime.freeMemory()
                    val arrayTime = measureNanoTime {
                        val array = IntArray(size) // Cấp phát IntArray size = 10_000
                        repeat(size) { i ->
                            array[i] = i // Gán primitive int - không boxing, chỉ ghi giá trị
                        }
                        array.forEach { _ -> } // Duyệt hết
                    }
                    val memAfter3 = runtime.totalMemory() - runtime.freeMemory()
                    val memUsed3 = (memAfter3 - memBefore3).coerceAtLeast(0)

                    comparisons.add(
                        DataTypeComparison(
                            typeName = "IntArray (primitive)",
                            memoryBytes = memUsed3,
                            operationTimeNs = arrayTime,
                            isPrimitive = true,
                            description = ""
                        )
                    )

                    comparisons
                }

                _state.value = _state.value.copy(
                    collectionComparisons = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Collection test error: ${e.message}"
                )
            }
        }
    }

    /**
     * So sánh String operations
     */
    fun compareStringOperations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(stringComparisons = emptyList())

            try {
                val results = withContext(Dispatchers.Default) {
                    val iterations = 1000
                    val comparisons = mutableListOf<DataTypeComparison>()
                    val runtime = Runtime.getRuntime()

                    // ==================== Test 1: String Concatenation (+) ====================
                    // Dùng toán tử + để nối string
                    // Mỗi lần result += "..." → Tạo String object mới, copy dữ liệu cũ, thêm dữ liệu mới
                    // Chạy 1000 lần → 1000 String objects được tạo ra
                    System.gc()
                    Thread.sleep(50)
                    val memBefore1 = runtime.totalMemory() - runtime.freeMemory()
                    val concatTime = measureNanoTime {
                        var result = ""
                        repeat(iterations) { i ->
                            result += "Item $i, " // Mỗi lần += tạo String object mới (CHẬM)
                        }
                    }
                    val memAfter1 = runtime.totalMemory() - runtime.freeMemory()
                    val memUsed1 = (memAfter1 - memBefore1).coerceAtLeast(0)

                    comparisons.add(
                        DataTypeComparison(
                            typeName = "String concatenation (+)",
                            memoryBytes = memUsed1,
                            operationTimeNs = concatTime,
                            isPrimitive = false,
                            description = ""
                        )
                    )

                    // ==================== Test 2: StringBuilder ====================
                    // Dùng StringBuilder (mutable buffer)
                    // Cấp phát buffer, append từng chuỗi nhỏ vào buffer
                    // Cuối cùng gọi toString() 1 lần để tạo String
                    // Chạy 1000 lần → chỉ 1 String object được tạo
                    System.gc()
                    Thread.sleep(50)
                    val memBefore2 = runtime.totalMemory() - runtime.freeMemory()
                    val stringBuilderTime = measureNanoTime {
                        val builder = StringBuilder(iterations * 20) // Cấp phát buffer từ đầu
                        repeat(iterations) { i ->
                            builder.append("Item ").append(i).append(", ") // Append vào buffer - NHANH
                        }
                        val result = builder.toString() // Tạo String cuối cùng (1 lần duy nhất)
                    }
                    val memAfter2 = runtime.totalMemory() - runtime.freeMemory()
                    val memUsed2 = (memAfter2 - memBefore2).coerceAtLeast(0)

                    comparisons.add(
                        DataTypeComparison(
                            typeName = "StringBuilder",
                            memoryBytes = memUsed2,
                            operationTimeNs = stringBuilderTime,
                            isPrimitive = false,
                            description = ""
                        )
                    )

                    comparisons
                }

                _state.value = _state.value.copy(
                    stringComparisons = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "String test error: ${e.message}"
                )
            }
        }
    }
}

data class DataTypeState(
    val primitiveComparisons: List<DataTypeComparison> = emptyList(),
    val collectionComparisons: List<DataTypeComparison> = emptyList(),
    val stringComparisons: List<DataTypeComparison> = emptyList(),
    val error: String = ""
)
