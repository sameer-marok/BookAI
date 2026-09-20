package com.sameer.bookai.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sameer.bookai.SignInScreen
import com.sameer.bookai.SignUpScreen

@Composable
fun AuthNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "sign_in"
    ) {
        // Sign in screen
        composable("sign_in") {
            SignInScreen(
                onSignUpClicked = {
                    navController.navigate("sign_up")
                },
                onLoginSuccess = {
                    navController.navigate("home") {
                        // removing login screen from backstack
                        popUpTo("sign_in") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        // Sign Up screen
        composable("sign_up") {
            SignUpScreen(
                onSignInClick = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate("home") {
                        // removing everything up to login screen from backstack
                        // so both login and sign up screens are removed from backstack
                        popUpTo("sign_in") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        // Home screen
        composable("home") {
            Text("Home")
        }
    }
}
