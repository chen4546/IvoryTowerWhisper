package com.chen.ivorytowerwhisper.data.local

import android.content.Context
import com.chen.ivorytowerwhisper.model.EmotionHistory
import com.chen.ivorytowerwhisper.model.SavedHistory
import com.chen.ivorytowerwhisper.model.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalStorage {
    private const val PREFS_NAME = "IvoryTowerPrefs"
    private const val KEY_USER_PREFS = "user_preferences"
    private const val KEY_HISTORY = "emotion_history"

    // 保存用户偏好
    fun saveUserPreferences(context: Context, prefs: UserPreferences) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(prefs)
        sharedPref.edit().putString(KEY_USER_PREFS, json).apply()
    }

    // 获取用户偏好
    fun getUserPreferences(context: Context): UserPreferences? {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPref.getString(KEY_USER_PREFS, null)
        return json?.let { Gson().fromJson(it, UserPreferences::class.java) }
    }

    // 保存历史记录
    fun saveHistory(context: Context, history: List<EmotionHistory>) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(SavedHistory(history))
        sharedPref.edit().putString(KEY_HISTORY, json).apply()
    }

    // 获取历史记录
    fun getHistory(context: Context): List<EmotionHistory> {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPref.getString(KEY_HISTORY, null) ?: return emptyList()

        val type = object : TypeToken<SavedHistory>() {}.type
        return try {
            Gson().fromJson<SavedHistory>(json, type).items
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 清除用户偏好
    fun clearUserPreferences(context: Context) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().remove(KEY_USER_PREFS).apply()
    }
    // 添加清除所有数据的方法
    fun clearAllData(context: Context) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}