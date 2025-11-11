package com.example.jetpackbaseapp.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackbaseapp.domain.model.Post
import com.example.jetpackbaseapp.domain.repository.PostRepository
import com.example.jetpackbaseapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    fun loadPost(postId: Int) {
        viewModelScope.launch {
            postRepository.getPostById(postId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = DetailState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = DetailState(post = result.data)
                    }
                    is Resource.Error -> {
                        _state.value = DetailState(error = result.message ?: "Unknown error")
                    }
                }
            }
        }
    }
}

data class DetailState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val error: String = ""
)
