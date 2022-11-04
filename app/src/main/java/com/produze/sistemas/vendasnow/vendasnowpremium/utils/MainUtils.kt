package com.produze.sistemas.vendasnow.vendasnowpremium.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64.URL_SAFE
import android.util.Base64.decode
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


object MainUtils {



    val currentDate: Date
        get() = Calendar.getInstance().time
    val currentOnlyDateStr: String
        get() {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            return sdf.format(Date())
        }

    fun formatHour(date: Date?): String {
        val df = SimpleDateFormat("HH:mm")
        return df.format(date)
    }

    fun formatDate(date: Date?): String {
        val df = SimpleDateFormat("dd/MM/yyyy")
        return df.format(date)
    }

    fun formatDateGlobal(date: Date?): String {
        val df = SimpleDateFormat("yyyy-MM-dd")
        return df.format(date)
    }

    fun getMonth(mes: Int): String {
        var nomeMes = ""
        nomeMes = when (mes) {
            1 -> "Jan"
            2 -> "Fev"
            3 -> "Mar"
            4 -> "Abr"
            5 -> "Mai"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Ago"
            9 -> "Set"
            10 -> "Out"
            11 -> "Nov"
            12 -> "Dez"
            else -> "Mês inválido"
        }
        return nomeMes
    }

    fun getMonthInt(mes: String?): Int {
        var nomeMes = 0
        nomeMes = when (mes) {
            "Jan" -> 1
            "Fev" -> 2
            "Mar" -> 3
            "Abr" -> 4
            "Mai" -> 5
            "Jun" -> 6
            "Jul" -> 7
            "Ago" -> 8
            "Set" -> 9
            "Out" -> 10
            "Nov" -> 11
            "Dez" -> 12
            else -> 0
        }
        return nomeMes
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun snack(view: View, message: String, duration: Int) {
        val snack: Snackbar = view?.let {
            Snackbar
                .make(it, message, duration)
        }!!
        snack.show()
    }

    fun snackInTop(view: View, message: String, duration: Int) {
        val snack: Snackbar = view?.let {
            Snackbar
                .make(it, message, duration)
        }!!
        val viewS = snack.view
        val params = viewS.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        viewS.layoutParams = params
        snack.show()
    }

    fun getStatusName(status: Int, view: View) : String{
        var str = ""
        val arrayStatus = view.resources.getStringArray(R.array.ArrayStatus)

        arrayStatus.forEach {
            val s = it.split(",")
            if (status === s[0].toInt()) {
                str = s[1]
            }
        }
        return str
    }

    fun checkSubscription(subscriptionDate: Date): Boolean {
        var check: Boolean = false
        if (Date().after(subscriptionDate)) {
            check = true
        }

        return check
    }
}