package com.myroutine.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.myroutine.app.data.local.AppDatabase
import com.myroutine.app.data.repositories.TrainingHistoryRepository
import com.myroutine.app.ui.screens.home.HomeScreen
import com.myroutine.app.ui.screens.calendar.CalendarScreen
import com.myroutine.app.ui.screens.calendar.CalendarViewModel

sealed class Screen(val route: String){
    object Home : Screen("home")
    object Calendar : Screen("calendar")
}

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Inicializar base de datos y repositorio
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { TrainingHistoryRepository(database.trainingHistoryDao()) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ){

        composable(Screen.Home.route) {
            HomeScreen(
                onOpenCalendar = {
                    navController.navigate(Screen.Calendar.route)
                }
            )
        }

        composable(Screen.Calendar.route) {
            val calendarViewModel: CalendarViewModel = viewModel(
                factory = CalendarViewModel.Factory(repository)
            )
            CalendarScreen(
                onBack = {
                    navController.popBackStack()
                },
                viewModel = calendarViewModel
            )
        }
    }
}

