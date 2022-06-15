package com.produze.sistemas.vendasnow.vendasnowpremium

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.model.LoginUser
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.ApiInterface
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.RetrofitInstance
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(){
//    private lateinit var auth: FirebaseAuth
//    private lateinit var googleSignInClient: GoogleSignInClient
//    private val reqCode:Int=123
//    private lateinit var cardViewSignGoogle: CardView
    private lateinit var imageViewGoogle: ImageView
    private lateinit var textViewGoogle: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarRegister: ProgressBar
    private lateinit var cardViewLogin: CardView
    private lateinit var linearLayoutLogin: LinearLayout
    private lateinit var linearLayoutRegister: LinearLayout
//    private val TAG = "LoginActivity"
    private lateinit var editTextEmail: EditText
    private lateinit var editTextSecret: EditText
    private var datasource: DataSourceUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        auth = FirebaseAuth.getInstance()
//        if (auth.currentUser != null) {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }

//        cardViewSignGoogle = findViewById(R.id.cardViewSignGoogle);
        cardViewLogin = findViewById(R.id.cardViewLogin)
        linearLayoutLogin = findViewById(R.id.linearLayoutLogin)
        linearLayoutRegister = findViewById(R.id.linearLayoutRegister)

        imageViewGoogle = findViewById(R.id.imageViewGoogle)
        textViewGoogle = findViewById(R.id.textViewGoogle)
        progressBar = findViewById(R.id.progressBar)
        progressBarRegister = findViewById(R.id.progressBarRegister)

        imageViewGoogle.visibility = View.VISIBLE
        textViewGoogle.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        linearLayoutRegister.visibility = View.GONE

//        cardViewSignGoogle.setOnClickListener{
//            try {
//            imageViewGoogle.visibility = View.GONE
//            textViewGoogle.visibility = View.GONE
//            progressBar.visibility = View.VISIBLE
//            signInGoogle()
//                } catch (e: ApiException) {
//                imageViewGoogle.visibility = View.VISIBLE
//                textViewGoogle.visibility = View.VISIBLE
//                progressBar.visibility = View.GONE
//                    MainUtils.snack(it, e.message.toString(), Snackbar.LENGTH_LONG)
//                }
//        }

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSecret = findViewById(R.id.editTextSecret);
        datasource = DataSourceUser(this)
        var token = datasource?.get()!!
        if (token.token != "") {
            changeActivity()
            finish()
        }
        imageViewGoogle.visibility = View.VISIBLE
        textViewGoogle.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        cardViewLogin.setOnClickListener{

            if (this?.let { it1 -> MainUtils.isOnline(it1) }!!) {

                if ((editTextEmail.text.toString() == "") || (editTextSecret.text.toString() == "")) {
                    MainUtils.snack(
                        it,
                        this.resources.getString(R.string.validation_login),
                        Snackbar.LENGTH_LONG
                    )
                } else {

                    imageViewGoogle.visibility = View.GONE
                    textViewGoogle.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    onLogin(it, editTextEmail.text.toString(), editTextSecret.text.toString())
                }
            } else {
                MainUtils.snack(it, this.resources.getString(R.string.validation_connection), Snackbar.LENGTH_LONG)
            }
        }

    }

    override fun onStart() {
        super.onStart()
//        if (auth.currentUser != null) {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 123) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            val account = task.result
//            if (account != null) {
//                firebaseAuthWithGoogle(account)
//            }
//        }
//
//    }
//
//    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        imageViewGoogle.visibility = View.VISIBLE
//                        textViewGoogle.visibility = View.VISIBLE
//                        progressBar.visibility = View.GONE
//                        startActivity(Intent(this, MainActivity::class.java))
//                        finish()
//                    } else {
//                        MainUtils.snack(
//                            window.decorView.findViewById(android.R.id.content),
//                            this.resources.getString(R.string.msg_error_authentication_failure),
//                            Snackbar.LENGTH_LONG)
//                    }
//                }
//    }
//
//    private  fun signInGoogle(){
//        val signInIntent:Intent=googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, reqCode)
//    }

    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 123
    }

    private fun onLogin(view: View, email: String, secret: String){

        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val loginUser = LoginUser(email, secret)
        retIn.login(loginUser).enqueue(object : Callback<Token> {
            override fun onFailure(call: Call<Token>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
                imageViewGoogle.visibility = View.VISIBLE
                textViewGoogle.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.code() == 200) {
                    imageViewGoogle.visibility = View.VISIBLE
                    textViewGoogle.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE

                    try {
                        var token = MutableLiveData<Token>()
                        token.value = response.body()
                        datasource?.deleteAll()
                        token.value?.let { datasource?.insert(it) }
                        changeActivity()
                    } catch (e: SecurityException) {
                        e.message?.let { MainUtils.snack(view, it, Snackbar.LENGTH_LONG) }
                    }
                }
                if (response.code() == 400) {
                    MainUtils.snack(view, response.errorBody()!!.string(), Snackbar.LENGTH_LONG)
                    imageViewGoogle.visibility = View.VISIBLE
                    textViewGoogle.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        })

    }

    private fun changeActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}