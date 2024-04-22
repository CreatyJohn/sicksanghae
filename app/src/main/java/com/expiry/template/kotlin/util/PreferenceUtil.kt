package com.expiry.template.kotlin.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceUtil(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }

    fun setInt(key: String, int: Int) {
        prefs.edit().putInt(key, int).apply()
    }

    fun setStringArray(key: String, values: ArrayList<String>) {
        val gson = Gson()
        val json = gson.toJson(values)
        val editor = prefs.edit()
        editor.putString(key, json)
        editor.apply()
    }

    fun getStringArray(key: String): ArrayList<String> {
        val json = prefs.getString(key, null)
        val gson = Gson()

        return gson.fromJson(
            json,
            object : TypeToken<ArrayList<String?>>() {}.type
        )
    }

    fun destroyData() {
        prefs.edit().clear().apply()
    }

}