package dev.tsnanh.fore.ui.detail

import androidx.lifecycle.ViewModel
import dev.tsnanh.fore.repository.FoReRepository

class DetailViewModel(
    private val repository: FoReRepository
) : ViewModel() {
    fun getPost(postId: String) = repository.getPost(postId)
}