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

    private val _themeConfig = MutableStateFlow(defaultThemeConfig)
    val themeConfig: StateFlow<AppThemeConfig> = _themeConfig

    // Hasta leer los colores guardados no se dibuja la app, para no mostrar un instante el tema por defecto
    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded

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
                _isLoaded.value = true
            }
        }
    }

    fun applyPreset(config: AppThemeConfig) {
        _themeConfig.value = config

        viewModelScope.launch {
            settingsDataStore.savePrimaryColor(config.primary.toArgb().toLong())
            settingsDataStore.saveBackgroundColor(config.background.toArgb().toLong())
            settingsDataStore.saveSurfaceColor(config.surface.toArgb().toLong())
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