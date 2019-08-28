package com.abaz.twitterish.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.DateTime
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
class DateTimeDeserializer : JsonDeserializer<Date> {

//    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date? {
        return try {
            Date(json!!.asLong)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }
}
//class DateTimeDeserializer : JsonDeserializer<DateTime> {
//
//    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//
//    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DateTime? {
//        return try {
////            DateTime(df.parse(json?.asString))
//
//            DateTime(json?.asLong)
//
//        } catch (e: java.text.ParseException) {
//            e.printStackTrace()
//            null
//        }
//    }
//}