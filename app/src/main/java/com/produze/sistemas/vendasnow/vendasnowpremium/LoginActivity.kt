package com.produze.sistemas.vendasnow.vendasnowpremium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val reqCode:Int=123
    private lateinit var cardViewSignGoogle: CardView
    private lateinit var imageViewGoogle: ImageView
    private lateinit var textViewGoogle: TextView
    private lateinit var progressBar: ProgressBar

    private val TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        cardViewSignGoogle = findViewById(R.id.cardViewSignGoogle);
        imageViewGoogle = findViewById(R.id.imageViewGoogle);
        textViewGoogle = findViewById(R.id.textViewGoogle);
        progressBar = findViewById(R.id.progressBar);
        imageViewGoogle.visibility = View.VISIBLE
        textViewGoogle.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        cardViewSignGoogle.setOnClickListener{
            imageViewGoogle.visibility = View.GONE
            textViewGoogle.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            signInGoogle()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 123) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.result
                Log.d(TAG, "firebaseAuthWithGoogle:" + account)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        imageViewGoogle.visibility = View.VISIBLE
                        textViewGoogle.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, R.string.msg_error_authentication_failure,
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private  fun signInGoogle(){
        val signInIntent:Intent=googleSignInClient.signInIntent
        startActivityForResult(signInIntent, reqCode)
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 123
    }

}