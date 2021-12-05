package com.produze.sistemas.vendasnow.vendasnowpremium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*


class LoginActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var btnLoginGoogle: CardView
    lateinit var facebookSignInButton: LoginButton
    val Req_Code:Int=123
    private lateinit var callbackManager : CallbackManager
    lateinit var google_sign_in_button: CardView
    private val TAG = "LoginActivity"
    private val RC_SIGN_IN = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        if (auth.getCurrentUser() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        FacebookSdk.sdkInitialize(getApplicationContext())

        callbackManager = CallbackManager.Factory.create();
        facebookSignInButton = findViewById(R.id.facebook_sign_in_button)
        facebookSignInButton.setReadPermissions("email", "public_profile")

// Callback registration
        facebookSignInButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                handleFacebookAccessToken(loginResult.accessToken);
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })

//        btnLoginGoogle = findViewById(R.id.btnLoginGoogle)
        google_sign_in_button = findViewById(R.id.google_sign_in_button);
        google_sign_in_button.setOnClickListener{ view: View? ->
            signInGoogle()
        }

//        setGooglePlusButtonText(google_sign_in_button)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


    }

    override fun onStart() {
        super.onStart()
        if (auth.getCurrentUser() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
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
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, R.string.msg_error_authentication_failure,
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + token)

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth!!.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            Toast.makeText(this, R.string.msg_error_authentication_failure,
                                    Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, R.string.msg_error_email_exist_google,
                                Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthUserCollisionException) {

                            Toast.makeText(this, R.string.msg_error_authentication_failure,
                                    Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthException) {
                            Toast.makeText(this, R.string.msg_error_authentication_failure,
                                    Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this, R.string.msg_error_authentication_failure,
                                    Toast.LENGTH_SHORT).show()
                        }
                        FirebaseAuth.getInstance().signOut()
                        LoginManager.getInstance().logOut();
                    }
                }
    }

    private  fun signInGoogle(){
        val signInIntent:Intent=googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 123
    }

    protected fun setGooglePlusButtonText(signInButton: SignInButton) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (i in 0 until signInButton.childCount) {
            val v = signInButton.getChildAt(i)
            if (v is TextView) {
                v.text = "Login com Google"
                return
            }
        }
    }
}