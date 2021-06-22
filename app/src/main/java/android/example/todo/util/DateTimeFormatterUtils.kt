package android.example.todo.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormatterUtils {

    fun convertDateTimeToMillis(myDate: String): Long {

        //val myDate = "2014/10/29 18:10:45"
        val sdf = SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault())
        val date = sdf.parse(myDate)

        return date.time
    }

    fun convertMillisToDateTime(milliseconds: Long): String {

        val date: Date = Date(milliseconds)
        val sdf = SimpleDateFormat("d MMM yyyy h:mm a", Locale.getDefault())

        return sdf.format(date)
    }
}