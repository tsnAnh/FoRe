package dev.tsnanh.fore.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.tsnanh.fore.repository.FoReRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val repository: FoReRepository
) : ViewModel() {
    private val _navigateToDetail = MutableLiveData<String?>(null)
    val navigateToDetail: LiveData<String?>
        get() = _navigateToDetail
    fun onNavigateToDetail(postId: String) {
        _navigateToDetail.value = postId
    }

    fun onNavigatedToDetail() {
        _navigateToDetail.value = null
    }

    val posts = repository.fetchPosts()
}