package com.produze.sistemas.vendasnow.vendasnowpremium.utils

import java.text.SimpleDateFormat
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
}