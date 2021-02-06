package dev.tsnanh.fore.ui.addfood

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import dev.tsnanh.fore.Post
import dev.tsnanh.fore.State
import dev.tsnanh.fore.base.BaseViewModel
import dev.tsnanh.fore.repository.FoReRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddFoodViewModel(
    private val repository: FoReRepository,
) : BaseViewModel() {
    fun addAllPhotos(photos: List<Uri>) {
        _photos.value = _photos.value + photos
    }

    private val _photos = MutableStateFlow(listOf<Uri>())
    val photos: StateFlow<List<Uri>>
        get() = _photos

    val addPost: (Post, List<Uri>) -> Flow<State<DocumentReference>> = { post, photos ->
        repository.addNewPost(post, photos)
    }
}