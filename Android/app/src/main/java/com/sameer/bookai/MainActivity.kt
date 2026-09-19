package com.sameer.bookai

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sameer.bookai.auth.AuthNavigation
import com.sameer.bookai.auth.AuthViewModel
import com.sameer.bookai.ui.theme.BookAITheme

private val BookAIBackground = Color(0xFFFCFCFE)
private val BookAINavy = Color(0xFF12132A)
private val BookAISecondary = Color(0xFF687083)
private val BookAIPrimary = Color(0xFF6658D9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BookAITheme {
                AuthNavigation()
            }
        }
    }
}

@Composable
fun SignInScreen(
    viewModel: AuthViewModel = viewModel(),
    onSignUpClicked: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // we are using LaunchedEffect to observe changes in the isLoggedIn state.
    // When it becomes true, we call onLoginSuccess to navigate to the home screen.
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BookAIBackground)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(120.dp))

        // Branding
        Text(
            text = "📖",
            fontSize = 48.sp
        )

        Text(
            text = "BookAI",
            fontSize = 39.sp,
            fontWeight = FontWeight.Bold,
            color = BookAINavy
        )

        Text(
            text = "Discover. Track. Grow.",
            fontSize = 16.sp,
            color = BookAISecondary
        )

        Spacer(modifier = Modifier.height(65.dp))

        // Title
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sign In",
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                color = BookAINavy
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome back! Sign in to continue your reading journey.",
                fontSize = 16.sp,
                color = BookAISecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Email")
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (uiState.errorMessage != null) {
                    viewModel.setValidationError("")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Password")
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(18.dp)
        )

        // Conditional text field that shows login errors
        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Forgot password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot password?",
                color = BookAIPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Sign in button
        Button(
            onClick = {
                viewModel.signIn(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(19.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BookAIPrimary
            )
        ) {
            Text(
                text = if (uiState.isLoading) "Signing In..." else "Sign In",
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))

            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 14.dp),
                color = BookAISecondary,
                fontSize = 13.sp
            )

            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign up
        Row {
            Text(
                text = "Don't have an account? ",
                color = BookAISecondary,
                fontSize = 15.sp
            )

            Text(
                text = "Sign Up",
                color = BookAIPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.clickable{
                    onSignUpClicked()
                }
            )
        }
    }
}

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = viewModel(),
    onSignInClick: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onSignUpSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BookAIBackground)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(120.dp))

        // Branding
        Text(
            text = "📖",
            fontSize = 48.sp
        )

        Text(
            text = "BookAI",
            fontSize = 39.sp,
            fontWeight = FontWeight.Bold,
            color = BookAINavy
        )

        Text(
            text = "Discover. Track. Grow.",
            fontSize = 16.sp,
            color = BookAISecondary
        )

        Spacer(modifier = Modifier.height(65.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Create Account",
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                color = BookAINavy
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create your account and start your reading journey.",
                fontSize = 16.sp,
                color = BookAISecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Email")
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Password")
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (uiState.errorMessage != null) {
                    viewModel.setValidationError("")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Confirm Password")
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = {
                when {
                    password.isBlank() -> {
                        viewModel.setValidationError("Password cannot be empty.")
                    }
                    password != confirmPassword -> {
                        viewModel.setValidationError("Passwords do not match.")
                    }
                    else -> {
                        viewModel.signUp(email, password)
                    }
                }
            },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(19.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BookAIPrimary
            )
        ) {
            Text(
                text = if (uiState.isLoading) {
                    "Creating Account..."
                } else {
                    "Create Account"
                },
                fontSize = 17.sp
            )
        }

        // Conditional text field that shows Sign up errors
        uiState.errorMessage?.takeIf { it.isNotBlank() }?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                color = BookAISecondary,
                fontSize = 15.sp
            )

            Text(
                text = "Sign In",
                color = BookAIPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.clickable {
                    onSignInClick()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    BookAITheme {
        //SignInScreen()
    }
}