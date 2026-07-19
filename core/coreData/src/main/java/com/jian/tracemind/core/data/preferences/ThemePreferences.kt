package com.jian.tracemind.core.data.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _themeMode = MutableStateFlow(getThemeModeFromPrefs())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    private fun getThemeModeFromPrefs(): ThemeMode {
        val savedMode = prefs.getString("theme_mode", ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(savedMode ?: ThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }
}
