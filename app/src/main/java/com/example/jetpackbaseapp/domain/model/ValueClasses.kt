package com.example.jetpackbaseapp.domain.model

/**
 * Value Classes - Zero-cost wrappers that are inlined at compile time
 * Không tạo object wrapper, chỉ dùng giá trị primitive bên trong
 */

@JvmInline
value class UserId(val value: Int)

@JvmInline
value class CacheId(val value: Int)

@JvmInline
value class Timestamp(val value: Long)

@JvmInline
value class DataSize(val bytes: Long) {
    val kiloBytes: Double get() = bytes / 1024.0
    val megaBytes: Double get() = bytes / (1024.0 * 1024.0)
}

/**
 * Inline functions - Function calls are replaced with function body at compile time
 * Không có overhead của function call, tối ưu cho HOF (Higher-Order Functions)
 */

/**
 * Inline function cho performance measurement
 * Không tạo Function object cho lambda
 */
inline fun <T> measureExecutionTime(block: () -> T): Pair<T, Long> {
    val startTime = System.nanoTime()
    val result = block()
    val endTime = System.nanoTime()
    return result to (endTime - startTime)
}

/**
 * Inline function cho safe null operations
 */
inline fun <T, R> T?.ifNotNull(block: (T) -> R): R? {
    return if (this != null) block(this) else null
}

/**
 * Inline function cho repeat with index
 */
inline fun repeatIndexed(times: Int, action: (index: Int) -> Unit) {
    for (index in 0 until times) {
        action(index)
    }
}

/**
 * Inline reified function - có thể access type parameter tại runtime
 */
inline fun <reified T> checkType(value: Any): Boolean {
    return value is T
}
