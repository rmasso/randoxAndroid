package com.demit.certifly.Extras

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Shared(var context: Context) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor
    fun setInt(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun setString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getInt(key: String?): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences.edit()
    }
}