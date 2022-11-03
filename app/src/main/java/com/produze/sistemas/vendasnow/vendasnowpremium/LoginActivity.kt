package com.produze.sistemas.vendasnow.vendasnowpremium

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.ActivityLoginBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity(){
    private var datasource: DataSourceUser? = null
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModelLogin: LoginViewModel

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
        viewModelLogin = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.cardViewLogin.setOnClickListener{

            if (this?.let { it1 -> MainUtils.isOnline(it1) }!!) {

                if ((binding.editTextEmail.text.toString() == "") || (binding.editTextSecret.text.toString() == "")) {
                    MainUtils.snackInTop(
                        it,
                        this.resources.getString(R.string.validation_login),
                        Snackbar.LENGTH_LONG
                    )
                } else {
                    onLogin(binding.editTextEmail.text.toString(), binding.editTextSecret.text.toString())
                }
            } else {
                MainUtils.snackInTop(it, this.resources.getString(R.string.validation_connection), Snackbar.LENGTH_LONG)
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
                    MainUtils.snackInTop(
                        it,
                        this.resources.getString(R.string.validation_login),
                        Snackbar.LENGTH_LONG
                    )
                } else {
                    onRegister(binding.editTextEmailRegister.text.toString(), binding.editTextSecretRegister.text.toString())
                }
            } else {
                MainUtils.snackInTop(it, this.resources.getString(R.string.validation_connection), Snackbar.LENGTH_LONG)
            }
        }

        binding.cardViewForgotPassword.setOnClickListener{
            binding.linearLayoutForgot.visibility = View.VISIBLE
            binding.linearLayoutLogin.visibility = View.GONE
        }

        binding.cardViewForgot.setOnClickListener{
            if (this?.let { it1 -> MainUtils.isOnline(it1) }!!) {
                if ((binding.editTextEmailForgot.text.toString() == "") || (binding.editTextSecretForgot.text.toString() == "")) {
                    MainUtils.snackInTop(
                        it,
                        this.resources.getString(R.string.validation_login),
                        Snackbar.LENGTH_LONG
                    )
                } else {
                    onForgot(binding.editTextEmailForgot.text.toString(), binding.editTextSecretForgot.text.toString())
                }
            } else {
                MainUtils.snackInTop(it, this.resources.getString(R.string.validation_connection), Snackbar.LENGTH_LONG)
            }
        }

        binding.cardViewBackForgot.setOnClickListener{
            binding.linearLayoutForgot.visibility = View.GONE
            binding.linearLayoutLogin.visibility = View.VISIBLE
        }

        viewModelLogin.errorMessage.observe(this) {
            MainUtils.snackInTop(window.decorView.findViewById(android.R.id.content),
                it.message, Snackbar.LENGTH_LONG)
            if (it.code == 401) {
                changeActivity()
            }

            if (it.code == 600) {
                startActivity(Intent(this, SubscriptionActivity::class.java))
            }

            if (it.code == 503) {
                MainUtils.snackInTop(window.decorView.findViewById(android.R.id.content),
                    this.resources.getString(R.string.error_503), Snackbar.LENGTH_LONG)
            }

            binding.imageViewLogin.visibility = View.VISIBLE
            binding.textViewLogin.visibility = View.VISIBLE
        }

        viewModelLogin.loading.observe(this, {
            if (it) {
                binding.imageViewLogin.visibility = View.GONE
                binding.textViewLogin.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.imageViewLogin.visibility = View.VISIBLE
                binding.textViewLogin.visibility = View.VISIBLE
            }
        })

        viewModelLogin.loadingRegister.observe(this, {
            if (it) {
                binding.imageViewRegister.visibility = View.GONE
                binding.textViewRegister.visibility = View.GONE
                binding.progressBarRegister.visibility = View.VISIBLE
            } else {
                binding.progressBarRegister.visibility = View.GONE
                binding.imageViewRegister.visibility = View.VISIBLE
                binding.textViewRegister.visibility = View.VISIBLE
            }
        })

        viewModelLogin.token.observe(this, {
            datasource?.deleteAll()
            datasource?.insert(it)
            changeActivity()
        })

        viewModelLogin.msg.observe(this, {
            MainUtils.snackInTop(window.decorView.findViewById(android.R.id.content),
                it, Snackbar.LENGTH_LONG)
        })

    }


    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 123
    }

    private fun onLogin(email: String, secret: String){
        viewModelLogin.login(email, secret)
    }

    private fun onRegister(email: String, secret: String){
        viewModelLogin.register(email, secret)
    }

    private fun onForgot(email: String, secret: String){
        viewModelLogin.forgot(email, secret)
    }

    private fun changeActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}