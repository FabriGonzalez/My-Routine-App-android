package com.myroutine.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.myroutine.app.ui.navigation.NavGraph
import com.myroutine.app.ui.theme.MyRoutineTheme
import com.myroutine.app.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeConfig by themeViewModel.themeConfig.collectAsState()

            MyRoutineTheme(themeConfig = themeConfig) {
                NavGraph(themeViewModel)
            }
        }
    }
}