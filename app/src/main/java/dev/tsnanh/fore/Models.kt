package dev.tsnanh.fore

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Post(
    var postId: String = "",
    val postTitle: String = "",
    val postDescription: String = "",
    var photos: List<FoodPair> = emptyList(),
    val latitude: Float = 0F,
    val longitude: Float = 0F,
    val foodTags: List<FoodPair> = emptyList(),
    val address: String = "",
    @ServerTimestamp
    val datePost: Date = Date(),
    val dateExpired: Long = 0L,
    @field:JvmField
    val isExpired: Boolean = false
)

data class FoodPair(
    val index: Int = 0,
    val data: String = ""
)

infix fun Int.fud(url: String) = FoodPair(this, url)

sealed class State<T> {
    class Loading<T> : State<T>()
    data class Error<T>(val error: String) : State<T>()
    data class Success<T>(val data: T) : State<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> error(error: String) = Error<T>(error)
        fun <T> success(data: T) = Success(data)
    }
}