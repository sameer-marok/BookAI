package com.sameer.bookai.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.sameer.bookai.HomeScreen
import com.sameer.bookai.SignInScreen
import com.sameer.bookai.SignUpScreen
import com.sameer.bookai.SplashScreen

@Composable
fun AuthNavigation() {

    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        //Splash screen
        composable("splash") {
            SplashScreen(
                onSessionChecked = { isLoggedIn ->
                    if (isLoggedIn) {
                        navController.navigate("home") {
                            popUpTo("splash") {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate("sign_in") {
                            popUpTo("splash") {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

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
            HomeScreen(
                onSignOutClicked = {
                    navController.navigate("sign_in") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
