package com.myroutine.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.myroutine.app.ui.navigation.NavGraph
import com.myroutine.app.ui.theme.MyRoutineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyRoutineTheme {
                NavGraph()
            }
        }
    }
}