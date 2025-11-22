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
            _state.value = _state.value.copy(isLoading = true)

            try {
                val results = withContext(Dispatchers.Default) {
                    val iterations = 1_000_000
                    val comparisons = mutableListOf<DataTypeComparison>()

                    // Test int (primitive)
                    var intSum = 0
                    val intTime = measureNanoTime {
                        repeat(iterations) { i ->
                            intSum += i
                        }
                    }
                    comparisons.add(
                        DataTypeComparison(
                            typeName = "int (primitive)",
                            memoryBytes = 4,
                            operationTimeNs = intTime,
                            isPrimitive = true,
                            description = "4 bytes, lưu trên stack, cực nhanh"
                        )
                    )

                    // Test Integer (boxed)
                    var integerSum = 0
                    val integerTime = measureNanoTime {
                        repeat(iterations) { i ->
                            integerSum += Integer.valueOf(i) // Boxing
                        }
                    }
                    comparisons.add(
                        DataTypeComparison(
                            typeName = "Integer (boxed)",
                            memoryBytes = 16, // Object header + int value
                            operationTimeNs = integerTime,
                            isPrimitive = false,
                            description = "16 bytes, lưu trên heap, chậm hơn do boxing/unboxing"
                        )
                    )

                    comparisons
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    primitiveComparisons = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    /**
     * So sánh các kiểu collection
     */
    fun compareCollections() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val results = withContext(Dispatchers.Default) {
                    val size = 10_000
                    val comparisons = mutableListOf<DataTypeComparison>()

                    // ArrayList
                    val arrayListTime = measureNanoTime {
                        val list = ArrayList<Int>(size) // Pre-sized
                        repeat(size) { i ->
                            list.add(i)
                        }
                        list.forEach { _ -> }
                    }
                    comparisons.add(
                        DataTypeComparison(
                            typeName = "ArrayList (pre-sized)",
                            memoryBytes = size * 4L,
                            operationTimeNs = arrayListTime,
                            isPrimitive = false,
                            description = "Tối ưu: khởi tạo với capacity, tránh resize"
                        )
                    )

                    // ArrayList without pre-sizing
                    val arrayListNoSizeTime = measureNanoTime {
                        val list = ArrayList<Int>() // No pre-size
                        repeat(size) { i ->
                            list.add(i)
                        }
                        list.forEach { _ -> }
                    }
                    comparisons.add(
                        DataTypeComparison(
                            typeName = "ArrayList (no pre-size)",
                            memoryBytes = size * 4L * 2, // Resizing overhead
                            operationTimeNs = arrayListNoSizeTime,
                            isPrimitive = false,
                            description = "Không tối ưu: phải resize nhiều lần"
                        )
                    )

                    // Array (primitive)
                    val arrayTime = measureNanoTime {
                        val array = IntArray(size)
                        repeat(size) { i ->
                            array[i] = i
                        }
                        array.forEach { _ -> }
                    }
                    comparisons.add(
                        DataTypeComparison(
                            typeName = "IntArray (primitive)",
                            memoryBytes = size * 4L,
                            operationTimeNs = arrayTime,
                            isPrimitive = true,
                            description = "Nhanh nhất: không boxing, liên tục trong memory"
                        )
                    )

                    comparisons
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    collectionComparisons = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    /**
     * So sánh String operations
     */
    fun compareStringOperations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val results = withContext(Dispatchers.Default) {
                    val iterations = 1000
                    val comparisons = mutableListOf<DataTypeComparison>()

                    // String concatenation (BAD)
                    val concatTime = measureNanoTime {
                        var result = ""
                        repeat(iterations) { i ->
                            result += "Item $i, " // Creates new String object each time
                        }
                    }
                    comparisons.add(
                        DataTypeComparison(
                            typeName = "String concatenation (+)",
                            memoryBytes = iterations * 50L, // Approximate
                            operationTimeNs = concatTime,
                            isPrimitive = false,
                            description = "❌ RẤT CHẬM: tạo String object mới mỗi lần"
                        )
                    )

                    // StringBuilder (GOOD)
                    val stringBuilderTime = measureNanoTime {
                        val builder = StringBuilder(iterations * 20)
                        repeat(iterations) { i ->
                            builder.append("Item ").append(i).append(", ")
                        }
                        val result = builder.toString()
                    }
                    comparisons.add(
                        DataTypeComparison(
                            typeName = "StringBuilder",
                            memoryBytes = iterations * 20L,
                            operationTimeNs = stringBuilderTime,
                            isPrimitive = false,
                            description = "✅ TỐI ƯU: sử dụng mutable buffer"
                        )
                    )

                    comparisons
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    stringComparisons = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    /**
     * Demo val vs var, data class optimization
     */
    fun compareKotlinOptimizations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val results = withContext(Dispatchers.Default) {
                    val iterations = 100_000
                    val comparisons = mutableListOf<DataTypeComparison>()

                    // Regular class
                    class RegularUser(var name: String, var age: Int, var email: String)

                    val regularTime = measureNanoTime {
                        repeat(iterations) {
                            val user = RegularUser("John", 30, "john@example.com")
                            val name = user.name
                        }
                    }
                    comparisons.add(
                        DataTypeComparison(
                            typeName = "Regular Class",
                            memoryBytes = 48, // Object header + fields
                            operationTimeNs = regularTime,
                            isPrimitive = false,
                            description = "Class thông thường: nhiều boilerplate"
                        )
                    )

                    // Data class (optimized)
                    data class DataUser(val name: String, val age: Int, val email: String)

                    val dataClassTime = measureNanoTime {
                        repeat(iterations) {
                            val user = DataUser("John", 30, "john@example.com")
                            val name = user.name
                        }
                    }
                    comparisons.add(
                        DataTypeComparison(
                            typeName = "Data Class (val)",
                            memoryBytes = 48,
                            operationTimeNs = dataClassTime,
                            isPrimitive = false,
                            description = "✅ Tối ưu: immutable, có equals/hashCode/copy tự động"
                        )
                    )

                    comparisons
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    kotlinComparisons = results
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun runAllComparisons() {
        comparePrimitiveVsBoxed()
        compareCollections()
        compareStringOperations()
        compareKotlinOptimizations()
    }

    fun clearResults() {
        _state.value = DataTypeState()
    }
}

data class DataTypeState(
    val isLoading: Boolean = false,
    val primitiveComparisons: List<DataTypeComparison> = emptyList(),
    val collectionComparisons: List<DataTypeComparison> = emptyList(),
    val stringComparisons: List<DataTypeComparison> = emptyList(),
    val kotlinComparisons: List<DataTypeComparison> = emptyList(),
    val error: String = ""
)
