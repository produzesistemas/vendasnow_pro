package com.produze.sistemas.vendasnow.vendasnowpremium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.produze.sistemas.vendasnow.vendasnowpremium.services.NotificationUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import android.view.ViewGroup





class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModelMain: ViewModelMain

    private val mNotificationTime =  GregorianCalendar.getInstance().timeInMillis + 5000 //Set after 5 seconds from the current time.
    private var mNotified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            val mTitle: TextView = toolbar.findViewById(R.id.toolbar_title);
            setSupportActionBar(toolbar)
            mTitle.setText(toolbar.getTitle())
            getSupportActionBar()?.setDisplayShowTitleEnabled(false)
            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            val navView: NavigationView = findViewById(R.id.nav_view)
            val navController = findNavController(R.id.nav_host_fragment)
            appBarConfiguration = AppBarConfiguration(setOf(
                    R.id.nav_home, R.id.nav_client, R.id.nav_product, R.id.nav_service, R.id.nav_sale, R.id.nav_graphic, R.id.nav_account_receivable), drawerLayout)
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            navView.getMenu().findItem(R.id.navigation_logout).setOnMenuItemClickListener { menuItem ->
                signOut()
                true
            }

            navView.getMenu().findItem(R.id.navigation_sair_do_app).setOnMenuItemClickListener { menuItem ->
                onBackPressed()
                true
            }

            auth = FirebaseAuth.getInstance()
            val user = auth.getCurrentUser()
            if (user == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                val header: View = navView.getHeaderView(0)
                val textView: TextView = header.findViewById(R.id.textViewEmail)
                val profileImage: CircleImageView = header.findViewById(R.id.profile_image)
                textView.text = user.displayName
                Picasso.get().load(user.photoUrl).into(profileImage);
            }

            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
            viewModelMain.title.observe(this, Observer {
                mTitle.setText(it)
            })
        } catch (e: SecurityException) {
            e.message?.let { Log.e("Exception: %s", it) }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun signOut() {
        auth.signOut()
        onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onBackPressed() {
        finishAffinity()
    }


}