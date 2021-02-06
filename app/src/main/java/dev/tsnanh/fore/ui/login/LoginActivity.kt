package dev.tsnanh.fore.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.tsnanh.fore.MainActivity
import dev.tsnanh.fore.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private val signInResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                val credential =
                    Identity.getSignInClient(this).getSignInCredentialFromIntent(result.data)

                firebaseAuthWithGoogle(credential.googleIdToken!!)
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }

    private fun firebaseAuthWithGoogle(googleIdToken: String) {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    Snackbar
                        .make(binding.root, "Authentication failed", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val user = auth.currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.login.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email.isBlank() || password.isBlank()) return@setOnClickListener
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        Snackbar
                            .make(binding.root, "Authentication failed", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
        }
        binding.google.setOnClickListener {
            val request = GetSignInIntentRequest.builder()
                .setServerClientId("34951888279-ttce5utqan1fv063tu0kcj0t7qm7nj9a.apps.googleusercontent.com")
                .build()
            Identity.getSignInClient(this)
                .getSignInIntent(request)
                .addOnSuccessListener {
                    signInResult.launch(IntentSenderRequest.Builder(it.intentSender).build())
                }
                .addOnFailureListener {
                    Snackbar
                        .make(binding.root, "Authentication failed", Snackbar.LENGTH_SHORT)
                        .show()
                }
        }
    }


}
