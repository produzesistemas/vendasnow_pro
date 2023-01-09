package com.produze.sistemas.vendasnow.vendasnowpremium

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
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
import com.produze.sistemas.vendasnow.vendasnowpremium.services.NotificationUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import de.hdodenhof.circleimageview.CircleImageView
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.services.AlarmReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
private var datasource: DataSourceUser? = null
    private lateinit var viewModelMain: ViewModelMain
//    var requestPermissionsResultCallback: (() -> Unit)? = null
//
//    private val PERMISSION_REQUEST_CODE = 34
//    private lateinit var afterPermissionFunc: (Map<String, Int>) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        checkAndRequestPermission(arrayOf(Manifest.permission.INTERNET)) { permissionResults ->
//            if (permissionResults.none { it.value != PackageManager.PERMISSION_GRANTED })
//                Toast.makeText(
//                    this@MainActivity,
//                    "Permission Granted",
//                    Toast.LENGTH_SHORT
//                ).show()
//            else
//                Toast.makeText(
//                    this@MainActivity,
//                    "Permission Denied",
//                    Toast.LENGTH_SHORT
//                ).show()
//        }

        try {

                    val receiver = ComponentName(applicationContext, AlarmReceiver::class.java)

        applicationContext.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

            val toolbar: Toolbar = findViewById(R.id.toolbar)
            val mTitle: TextView = toolbar.findViewById(R.id.toolbar_title);
            setSupportActionBar(toolbar)
            mTitle.setText(toolbar.getTitle())
            getSupportActionBar()?.setDisplayShowTitleEnabled(false)
            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            val navView: NavigationView = findViewById(R.id.nav_view)
            val navController = findNavController(R.id.nav_host_fragment)
            appBarConfiguration = AppBarConfiguration(setOf(
                    R.id.nav_home, R.id.nav_client, R.id.nav_product, R.id.nav_sale, R.id.nav_graphic, R.id.nav_account_receivable, R.id.nav_about, R.id.nav_subscription, R.id.nav_my_account), drawerLayout)
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

            datasource = DataSourceUser(this)
            var token = datasource?.get()!!
            if (token.token == "") {
                changeActivity()
                finish()
            } else {
                val header: View = navView.getHeaderView(0)
                val textView: TextView = header.findViewById(R.id.textViewEmail)
                val profileImage: CircleImageView = header.findViewById(R.id.profile_image)
                textView.text = token.email
//                Picasso.get().load(user.photoUrl).into(profileImage);
            }

            startNotification()

            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
            viewModelMain.title.observe(this, Observer {
                mTitle.text = it
            })

        } catch (e: SecurityException) {
            e.message?.let { Log.e("Exception: %s", it) }
        }

    }

//    private fun checkAndRequestPermission(permissions: Array<String>, param: (Map<String, Int>) -> Unit) {
//        requestPermissions(permissions, PERMISSION_REQUEST_CODE)
//        afterPermissionFunc = param
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun changeActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun signOut() {
        datasource?.deleteAll()
        onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun startNotification() {
        NotificationUtils().setAlarmManager(this)
    }

}