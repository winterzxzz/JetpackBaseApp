package com.example.jetpackbaseapp.presentation.screens.optimization.advanced

import androidx.compose.runtime.Stable

/**
 * Models cho Advanced Optimizations Demo
 */

@Stable
data class AdvancedOptimizationState(
    val isLoading: Boolean = false,
    val valueClassResult: ValueClassResult? = null,
    val error: String = ""
)

/**
 * Kết quả so sánh Value Class vs Regular Class
 */
data class ValueClassResult(
    val testName: String,
    
    // Value Class metrics
    val valueClassTime: Long,        // nanoseconds
    val valueClassMemory: Long,      // bytes trước test
    val valueClassMemoryAfter: Long, // bytes sau test
    
    // Regular Class metrics  
    val regularClassTime: Long,        // nanoseconds
    val regularClassMemory: Long,      // bytes trước test
    val regularClassMemoryAfter: Long, // bytes sau test
    
    // Iterations để tính objects
    val iterations: Int
)

/**
 * ==================== VALUE CLASS ====================
 * @JvmInline = Compiler inline thành primitive type
 * Zero-cost wrapper: Có type safety compile-time, nhưng không tốn memory runtime
 */
@JvmInline
value class UserId(val value: Int)

/**
 * Regular class để so sánh
 * Tạo object mới mỗi lần khởi tạo
 */
data class RegularUserId(val value: Int)


