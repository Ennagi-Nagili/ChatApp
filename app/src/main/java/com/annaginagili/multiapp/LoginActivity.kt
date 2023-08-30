package com.annaginagili.multiapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.annaginagili.multiapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var login: SignInButton
    lateinit var auth: FirebaseAuth
    lateinit var signInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    private val code = 40
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        login = binding.login
        auth = FirebaseAuth.getInstance()
        preferences = getSharedPreferences("MultiApp", MODE_PRIVATE)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.api_key)).requestEmail().build()
        signInClient = GoogleSignIn.getClient(this, gso)

        login.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val intent = signInClient.signInIntent
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == code) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuth(account.idToken)
            } catch (e: ApiException) {
                Log.e("hello", e.toString())
            }
        }
    }

    private fun firebaseAuth(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                preferences.edit().putString("token", idToken).apply()
                startActivity(Intent(this,  MainActivity::class.java))
                FirebaseDatabase.getInstance().reference.child(auth.currentUser!!.uid).get()
                    .addOnSuccessListener { dataSnapshot ->
                        val email = GoogleSignIn.getLastSignedInAccount(this)!!.email
                        Log.e("hello", email.toString())
                        if (!dataSnapshot.exists()) {
                            FirebaseDatabase.getInstance().reference.child("users")
                                .setValue(auth.currentUser!!.uid, email)
                        }
                    }
            }
            else {
                Log.e("hello", it.exception.toString())
            }
        }
    }
}