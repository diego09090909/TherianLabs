package cl.therionlabs.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.therionlabs.myapplication.navigation.NavigationEvent
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.ui.HomeScreen
import cl.therionlabs.myapplication.ui.ListScreen
import cl.therionlabs.myapplication.ui.RegisterScreen
import cl.therionlabs.myapplication.ui.theme.MyApplicationTheme
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import cl.therionlabs.myapplication.ui.RegistroCaptadorScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo ->
                                navController.navigate(event.screen.route)

                            NavigationEvent.PopBackStack ->
                                navController.popBackStack()
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(viewModel = viewModel)
                    }
                    composable(Screen.Register.route) {
                        RegisterScreen(mainViewModel = viewModel)
                    }
                    composable(Screen.Lista.route) {
                        ListScreen(mainViewModel = viewModel)
                    }
                    composable("registro_captador") {
                        RegistroCaptadorScreen(
                            onVolverLogin = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
