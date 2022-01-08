package com.example.greenroadtest.storage

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ShardPref(val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("GreenRoadTestPref", Context.MODE_PRIVATE)


    private inline fun <reified T> SharedPreferences.asLiveData(key: String): LiveData<T> {
        return object : LiveData<T>(), SharedPreferences.OnSharedPreferenceChangeListener {

            private var isInitialized = false
            private fun readValue(): T? {
                return try {
                    when (val storedValue = sharedPreferences.all[key]) {
                        is String -> Gson().fromJson<T?>(storedValue, object : TypeToken<T>() {}.type)
                        is T -> storedValue
                        else -> null
                    }
                } catch (t: Throwable) {
                    null
                }
            }

            override fun onSharedPreferenceChanged(
                prefs: SharedPreferences, propertyName: String?
            ) {
                if (propertyName == key) {
                    value = readValue()
                }
            }

            override fun onActive() {
                val newValue = readValue()
                if (value != newValue || !isInitialized) {
                    value = newValue
                    isInitialized = true
                }
                registerOnSharedPreferenceChangeListener(this)
            }

            override fun onInactive() {
                unregisterOnSharedPreferenceChangeListener(this)
            }
        }
    }

}
