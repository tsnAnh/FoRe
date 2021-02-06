package dev.tsnanh.fore.repository

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.tsnanh.fore.Post
import dev.tsnanh.fore.State
import dev.tsnanh.fore.fud
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import okio.ByteString.Companion.readByteString
import java.lang.Exception
import java.util.*

interface FoReRepository {
    fun fetchPosts(): Flow<State<List<Post>>>
    fun addNewPost(post: Post, photos: List<Uri>): Flow<State<DocumentReference>>
    fun getPost(postId: String): Flow<State<Post>>
}

class FoReRepositoryImpl : FoReRepository {
    private val firestore = Firebase.firestore.apply {
        firestoreSettings = firestoreSettings { isPersistenceEnabled = false }
    }
    private val storage = Firebase.storage

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchPosts() = callbackFlow {
        offer(State.loading<List<Post>>())

        try {
            firestore
                .collection("posts")
                .orderBy("datePost")
                .addSnapshotListener { value, error ->
                    println(value)
                    println(error)
                    if (error == null) {
                        offer(State.success(value!!.toObjects<Post>()))
                    } else {
                        offer(State.error<List<Post>>("FirebaseFirestoreException occurs"))
                    }
                }
        } catch (e: Exception) {
            offer(State.error<List<Post>>(e.localizedMessage ?: ""))
        }

        awaitClose()
    }.flowOn(Dispatchers.IO)

    override fun addNewPost(post: Post, photos: List<Uri>) = flow {
        emit(State.loading())

        val doc = firestore.collection("posts")
            .document()

        val uploaded = photos.mapIndexed { i, e ->
            val ref = storage
                .reference
                .child("${
                    Firebase.auth.currentUser?.uid
                }/${
                    Calendar.getInstance().timeInMillis
                }-${
                    e.lastPathSegment
                }")
            val uploadTask = ref.putFile(e)
            i fud uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { e ->
                        println(e)
                        throw e
                    }
                }
                ref.downloadUrl
            }.await().toString()
        }

        post.apply {
            this.photos = uploaded
            postId = doc.id
        }
        doc.set(post).await()
        emit(State.success(doc))
    }.catch {
        emit(State.error(it.localizedMessage ?: ""))
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPost(postId: String) = callbackFlow {
        offer(State.loading())

        try {
            firestore.collection("posts")
                .document(postId)
                .addSnapshotListener { value, error ->
                    if (error == null) {
                        offer(State.success(value!!.toObject<Post>()!!))
                    } else {
                        offer(State.error<Post>("error"))
                    }
                }
        } catch (e: Exception) {
            offer(State.error<Post>(e.localizedMessage ?: ""))
        }

        awaitClose()
    }.catch {
        emit(State.error(it.localizedMessage ?: ""))
    }.flowOn(Dispatchers.IO)
}