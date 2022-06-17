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
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.ActivityLoginBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.LoginUser
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.ApiInterface
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.RetrofitInstance
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(){
    private var datasource: DataSourceUser? = null
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        datasource = DataSourceUser(this)
        var token = datasource?.get()!!
        if (token.token != "") {
            changeActivity()
            finish()
        }
        binding.cardViewLogin.setOnClickListener{

            if (this?.let { it1 -> MainUtils.isOnline(it1) }!!) {

                if ((binding.editTextEmail.text.toString() == "") || (binding.editTextSecret.text.toString() == "")) {
                    MainUtils.snack(
                        it,
                        this.resources.getString(R.string.validation_login),
                        Snackbar.LENGTH_LONG
                    )
                } else {

                    binding.imageViewLogin.visibility = View.GONE
                    binding.textViewLogin.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    onLogin(it, binding.editTextEmail.text.toString(), binding.editTextSecret.text.toString())
                }
            } else {
                MainUtils.snack(it, this.resources.getString(R.string.validation_connection), Snackbar.LENGTH_LONG)
            }
        }

        binding.cardViewSignUp.setOnClickListener{
            binding.linearLayoutRegister.visibility = View.VISIBLE
            binding.linearLayoutLogin.visibility = View.GONE
           }

        binding.cardViewBack.setOnClickListener{
            binding.linearLayoutRegister.visibility = View.GONE
            binding.linearLayoutLogin.visibility = View.VISIBLE
        }

        binding.cardViewRegister.setOnClickListener{

            if (this?.let { it1 -> MainUtils.isOnline(it1) }!!) {

                if ((binding.editTextEmailRegister.text.toString() == "") || (binding.editTextSecretRegister.text.toString() == "")) {
                    MainUtils.snack(
                        it,
                        this.resources.getString(R.string.validation_login),
                        Snackbar.LENGTH_LONG
                    )
                } else {

                    binding.imageViewRegister.visibility = View.GONE
                    binding.textViewRegister.visibility = View.GONE
                    binding.progressBarRegister.visibility = View.VISIBLE
                    onRegister(it, binding.editTextEmailRegister.text.toString(), binding.editTextSecretRegister.text.toString())
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
                binding.imageViewLogin.visibility = View.VISIBLE
                binding.textViewLogin.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.code() == 200) {
                    binding.imageViewLogin.visibility = View.VISIBLE
                    binding.textViewLogin.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE

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
                    binding.imageViewLogin.visibility = View.VISIBLE
                    binding.textViewLogin.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

    }

    private fun onRegister(view: View, email: String, secret: String){

        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val loginUser = LoginUser(email, secret)
        retIn.registerUser(loginUser).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
                binding.imageViewRegister.visibility = View.VISIBLE
                binding.textViewRegister.visibility = View.VISIBLE
                binding.progressBarRegister.visibility = View.GONE
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    binding.imageViewRegister.visibility = View.VISIBLE
                    binding.textViewRegister.visibility = View.VISIBLE
                    binding.progressBarRegister.visibility = View.GONE
                    MainUtils.snack(view, response.body()!!.string(), Snackbar.LENGTH_LONG)
                }
                if (response.code() == 400) {
                    MainUtils.snack(view, response.errorBody()!!.string(), Snackbar.LENGTH_LONG)
                    binding.imageViewRegister.visibility = View.VISIBLE
                    binding.textViewRegister.visibility = View.VISIBLE
                    binding.progressBarRegister.visibility = View.GONE
                }
            }
        })

    }

    private fun changeActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}