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
                // ==================== CẤP ĐỘ 1: MEMORY CACHE ====================
                // ID: Định danh duy nhất của item (ví dụ: Item #1, Item #2, ...)
                // Dùng để: Tìm kiếm item có id này trong cache/database/network
                //
                // Ví dụ: id = 5 → Tìm "Item #5"
                //
                // Lưu trữ: RAM (bộ nhớ chính)
                // Tốc độ: NHANH NHẤT (~ns)
                // Dung lượng: NHỎ (chỉ vài MB)
                // Sống bao lâu: Khi ứng dụng đóng → mất dữ liệu
                //
                // Ưu điểm:
                // - Truy cập cực nhanh (trong cùng process)
                // - Phù hợp dữ liệu thường dùng (session, user data)
                //
                // Nhược điểm:
                // - Bộ nhớ hạn chế
                // - Bị xóa khi app đóng
                var result: CacheDemo? = null
                val memoryTime = measureNanoTime {
                    // Tìm item có id này trong memory cache
                    memoryCache[id]?.let { entity ->
                        result = CacheDemo(entity.id, entity.title, entity.data, CacheSource.MEMORY_CACHE, 0)
                    }
                }
                if (result != null) {
                    updateUiWithResult(result!!.copy(loadTimeNs = memoryTime))
                    return@launch
                }

                // ==================== CẤP ĐỘ 2: DISK CACHE (DATABASE) ====================
                // ID: Dùng để query database tìm item có id này
                //
                // Ví dụ: SELECT * FROM cache_table WHERE id = 5
                //
                // Lưu trữ: Ổ cứng / Flash storage (SQLite, Room)
                // Tốc độ: CHẬM (~ms)
                // Dung lượng: LỚN (vài MB đến GB)
                // Sống bao lâu: Persistent (lâu bền cho đến khi xóa)
                //
                // Ưu điểm:
                // - Dung lượng lớn (lưu nhiều data)
                // - Tồn tại lâu dài (persistent)
                // - Có thể là database, SharedPreferences, file
                //
                // Nhược điểm:
                // - Truy cập chậm hơn memory
                // - I/O blocking (phải dùng background thread)
                var diskEntity: CacheDemoEntity? = null
                val diskTime = measureNanoTime {
                    // Query database tìm item có id này
                    diskEntity = withContext(Dispatchers.IO) { cacheDemoDao.getCachedById(id) }
                }
                diskEntity?.let { entity ->
                    // Cập nhật memory cache để lần sau nhanh hơn
                    memoryCache[id] = entity
                    val diskResult = CacheDemo(entity.id, entity.title, entity.data, CacheSource.DISK_CACHE, diskTime)
                    updateUiWithResult(diskResult)
                    return@launch
                }

                // ==================== CẤP ĐỘ 3: NETWORK ====================
                // ID: Dùng để gửi request lên server tìm item có id này
                //
                // Ví dụ: GET /api/items/5 → Server trả về Item #5 từ database
                //
                // Lưu trữ: Server (Internet)
                // Tốc độ: CHẬM NHẤT (~s)
                // Dung lượng: VÔ HẠN (cloud server)
                // Sống bao lâu: Persistent (lâu bền)
                //
                // Ưu điểm:
                // - Dung lượng vô hạn
                // - Dữ liệu mới nhất
                // - Có thể share với thiết bị khác
                //
                // Nhược điểm:
                // - Chậm nhất (phải fetch từ internet)
                // - Cần kết nối mạng
                // - Tốn data
                //
                // STRATEGY: Khi lấy từ network, lưu vào:
                // - Disk cache (để lần sau nhanh)
                // - Memory cache (để lần sau cực nhanh)
                var networkEntity: CacheDemoEntity? = null
                val networkTime = measureNanoTime {
                    // Gọi API với id để lấy dữ liệu từ server
                    networkEntity = withContext(Dispatchers.IO) { fetchFromNetwork(id) }
                }
                networkEntity?.let { entity ->
                    // Lưu vào disk cache để lần sau không phải fetch network
                    withContext(Dispatchers.IO) { cacheDemoDao.insertCache(entity) }
                    // Lưu vào memory cache để lần sau cực nhanh
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
                // ==================== KHÔNG DÙNG CACHE ====================
                // Luôn fetch từ network, bỏ qua memory cache và disk cache
                //
                // Kịch bản: Người dùng muốn dữ liệu mới nhất (refresh)
                // hoặc test xem network tốn bao lâu
                //
                // Kết quả:
                // - Thời gian CHẬM (phải fetch network ~1500ms)
                // - Không lợi dụng cache đã lưu trước đó
                // - Lãng phí bandwidth/data (fetch lại dữ liệu cũ)
                //
                // So sánh:
                // - Lần 1 với cache: Memory ❌ → Disk ❌ → Network ✅ (~1500ms)
                // - Lần 2 với cache: Memory ✅ (~ns) - RẤT NHANH!
                // - Lần 2 không cache: Network ✅ (~1500ms) - CHẬM!
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
