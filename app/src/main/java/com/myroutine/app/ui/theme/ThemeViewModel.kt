package com.myroutine.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myroutine.app.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val defaultTheme = AppThemeConfig(
        primary = Color(0xFF00E676),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E)
    )

    private val _themeConfig = MutableStateFlow(defaultTheme)
    val themeConfig: StateFlow<AppThemeConfig> = _themeConfig

    init {
        viewModelScope.launch {
            combine(
                settingsDataStore.primaryColor,
                settingsDataStore.backgroundColor,
                settingsDataStore.surfaceColor
            ) { primary, background, surface ->

                AppThemeConfig(
                    primary = Color(primary),
                    background = Color(background),
                    surface = Color(surface)
                )
            }.collect {
                _themeConfig.value = it
            }
        }
    }

    fun updatePrimary(color: Color) {
        _themeConfig.value = _themeConfig.value.copy(primary = color)

        viewModelScope.launch {
            settingsDataStore.savePrimaryColor(color.toArgb().toLong())
        }
    }

    fun updateBackground(color: Color) {
        _themeConfig.value = _themeConfig.value.copy(background = color)

        viewModelScope.launch {
            settingsDataStore.saveBackgroundColor(color.toArgb().toLong())
        }
    }

    fun updateSurface(color: Color) {
        _themeConfig.value = _themeConfig.value.copy(surface = color)

        viewModelScope.launch {
            settingsDataStore.saveSurfaceColor(color.toArgb().toLong())
        }
    }
}