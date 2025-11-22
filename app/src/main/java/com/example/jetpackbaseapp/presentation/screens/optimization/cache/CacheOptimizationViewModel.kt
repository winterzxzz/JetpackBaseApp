package com.example.jetpackbaseapp.presentation.screens.optimization.cache

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackbaseapp.data.local.dao.CacheDemoDao
import com.example.jetpackbaseapp.data.local.entity.CacheDemoEntity
import com.example.jetpackbaseapp.domain.model.CacheDemo
import com.example.jetpackbaseapp.domain.model.CacheSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.measureNanoTime

@HiltViewModel
class CacheOptimizationViewModel @Inject constructor(
    private val cacheDemoDao: CacheDemoDao
) : ViewModel() {

    private val _state = MutableStateFlow(CacheState())
    val state: StateFlow<CacheState> = _state.asStateFlow()

    private val memoryCache = mutableMapOf<Int, CacheDemoEntity>()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val diskSize = withContext(Dispatchers.IO) {
                try {
                    cacheDemoDao.getAllCached().firstOrNull()?.size ?: 0
                } catch (e: Exception) {
                    0 // Return 0 if db fails
                }
            }
            _state.value = _state.value.copy(
                memoryCacheSize = memoryCache.size,
                diskCacheSize = diskSize
            )
        }
    }

    private suspend fun fetchFromNetwork(id: Int): CacheDemoEntity {
        delay(1500) // Simulate network delay
        return CacheDemoEntity(
            id = id,
            title = "Item #$id",
            data = "Sample data for item #$id."
        )
    }

    fun loadDataWithCache(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = "")
            try {
                // --- Level 1: Memory Cache ---
                var result: CacheDemo? = null
                val memoryTime = measureNanoTime {
                    memoryCache[id]?.let { entity ->
                        result = CacheDemo(entity.id, entity.title, entity.data, CacheSource.MEMORY_CACHE, 0)
                    }
                }
                if (result != null) {
                    updateUiWithResult(result!!.copy(loadTimeNs = memoryTime))
                    return@launch
                }

                // --- Level 2: Disk Cache ---
                var diskEntity: CacheDemoEntity? = null
                val diskTime = measureNanoTime {
                    diskEntity = withContext(Dispatchers.IO) { cacheDemoDao.getCachedById(id) }
                }
                diskEntity?.let { entity ->
                    memoryCache[id] = entity // Update memory cache
                    val diskResult = CacheDemo(entity.id, entity.title, entity.data, CacheSource.DISK_CACHE, diskTime)
                    updateUiWithResult(diskResult)
                    return@launch
                }

                // --- Level 3: Network ---
                var networkEntity: CacheDemoEntity? = null
                val networkTime = measureNanoTime {
                    networkEntity = withContext(Dispatchers.IO) { fetchFromNetwork(id) }
                }
                networkEntity?.let { entity ->
                    // Save to caches *after* measurement
                    withContext(Dispatchers.IO) { cacheDemoDao.insertCache(entity) }
                    memoryCache[id] = entity
                    val networkResult = CacheDemo(entity.id, entity.title, entity.data, CacheSource.NETWORK, networkTime)
                    updateUiWithResult(networkResult)
                    return@launch
                }

                // --- Not Found ---
                _state.value = _state.value.copy(isLoading = false, error = "Could not load data for id=$id")

            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Error: ${e.message}")
            }
        }
    }

    private fun updateUiWithResult(result: CacheDemo) {
        _state.value = _state.value.copy(
            isLoading = false,
            currentData = result,
            loadHistory = listOf(result) + _state.value.loadHistory.take(4) // Prepend and limit history
        )
        loadStats() // Refresh cache stats
    }

    fun loadDataWithoutCache(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = "")
            try {
                var networkEntity: CacheDemoEntity? = null
                val networkTime = measureNanoTime {
                    networkEntity = fetchFromNetwork(id)
                }

                networkEntity?.let { entity ->
                    val finalResult = CacheDemo(
                        id = entity.id,
                        title = entity.title,
                        data = entity.data,
                        source = CacheSource.NETWORK,
                        loadTimeNs = networkTime
                    )
                    updateUiWithResult(finalResult)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Error: ${e.message}")
            }
        }
    }

    fun clearMemoryCache() {
        memoryCache.clear()
        loadStats()
    }

    fun clearDiskCache() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cacheDemoDao.clearAllCache()
            }
            // Also clear memory cache for consistency
            memoryCache.clear()
            loadStats()
        }
    }

    fun clearAllCaches() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cacheDemoDao.clearAllCache()
            }
            memoryCache.clear()
            _state.value = CacheState() // Reset to initial state
        }
    }

    fun preloadCache(count: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val items = (1..count).map { id ->
                    CacheDemoEntity(
                        id = id,
                        title = "Item #$id",
                        data = "Sample preloaded data for item $id."
                    )
                }
                withContext(Dispatchers.IO) {
                    cacheDemoDao.insertAllCache(items)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Error preloading: ${e.message}")
            }
            loadStats()
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}

data class CacheState(
    val isLoading: Boolean = false,
    val currentData: CacheDemo? = null,
    val loadHistory: List<CacheDemo> = emptyList(),
    val memoryCacheSize: Int = 0,
    val diskCacheSize: Int = 0,
    val error: String = ""
)
