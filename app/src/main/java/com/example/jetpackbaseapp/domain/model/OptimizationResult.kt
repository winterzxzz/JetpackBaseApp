package com.example.jetpackbaseapp.domain.model

/**
 * Model để lưu kết quả so sánh performance của các kỹ thuật tối ưu
 */
data class OptimizationResult(
    val technique: String,
    val executionTimeMs: Long,
    val memoryUsedKb: Long = 0,
    val isOptimized: Boolean = false,
    val result: String = "",
    val description: String = ""
)

/**
 * Model cho demo Fibonacci
 */
data class FibonacciResult(
    val value: Long,
    val executionTimeNs: Long, // Changed from Ms to Ns
    val method: String, // "Recursive" or "Iterative"
    val stackOverflow: Boolean = false
)

/**
 * Model cho demo Cache
 */
data class CacheDemo(
    val id: Int,
    val title: String,
    val data: String,
    val source: CacheSource,
    val loadTimeNs: Long // Changed from Ms to Ns
)

enum class CacheSource {
    MEMORY_CACHE,
    DISK_CACHE,
    NETWORK
}

/**
 * Model cho demo Data Type
 */
data class DataTypeComparison(
    val typeName: String,
    val memoryBytes: Long,
    val operationTimeNs: Long,
    val isPrimitive: Boolean,
    val description: String
)
