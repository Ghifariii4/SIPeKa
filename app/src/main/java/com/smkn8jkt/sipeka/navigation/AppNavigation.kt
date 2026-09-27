package com.smkn8jkt.sipeka.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smkn8jkt.sipeka.ApiTestScreen
import com.smkn8jkt.sipeka.MainViewModel
import com.smkn8jkt.sipeka.data.remote.TokenManager
import com.smkn8jkt.sipeka.ui.components.PrimaryButton
import com.smkn8jkt.sipeka.ui.screens.auth.AuthViewModel
import com.smkn8jkt.sipeka.ui.screens.auth.LoginScreen
import com.smkn8jkt.sipeka.ui.screens.auth.RegisterScreen
import com.smkn8jkt.sipeka.ui.screens.pos.KasirHomeScreen
import com.smkn8jkt.sipeka.ui.screens.pos.PosViewModel
import com.smkn8jkt.sipeka.ui.screens.pos.ProfilKasirScreen
import com.smkn8jkt.sipeka.ui.screens.pos.RiwayatScreen
import com.smkn8jkt.sipeka.ui.screens.splash.SplashScreen
import com.smkn8jkt.sipeka.ui.theme.BackgroundNeutral
import com.smkn8jkt.sipeka.ui.theme.SecondaryBrown

import com.smkn8jkt.sipeka.ui.screens.pos.PosViewModelFactory

object Screen {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val History = "history"
    const val Profile = "profile"
    const val AccessDenied = "access_denied"
    const val DebugDashboard = "debug_dashboard"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    tokenManager: TokenManager? = null,
    navController: NavHostController = rememberNavController(),
    posViewModel: PosViewModel = viewModel(factory = PosViewModelFactory(tokenManager))
) {
    val currentRole by tokenManager?.roleFlow?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val authUserRole by authViewModel.userRole.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash
    ) {
        composable(Screen.Splash) {
            SplashScreen(
                tokenManager = tokenManager,
                onNavigateToMain = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    val userRole = authViewModel.userRole.value?.lowercase() ?: currentRole?.lowercase() ?: "admin"
                    if (userRole == "kasir" || userRole == "admin") {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.AccessDenied) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
                }
            )
        }

        composable(Screen.Register) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home) {
            val role = currentRole?.lowercase() ?: authUserRole?.lowercase()
            if (role != null && role != "kasir" && role != "admin") {
                AccessDeniedScreen(
                    userRole = role,
                    onBackToLogin = {
                        authViewModel.resetState()
                        navController.navigate(Screen.Login) {
                            popUpTo(Screen.Home) { inclusive = true }
                        }
                    }
                )
            } else {
                KasirHomeScreen(
                    navController = navController,
                    viewModel = posViewModel
                )
            }
        }

        composable(Screen.History) {
            RiwayatScreen(
                navController = navController,
                viewModel = posViewModel
            )
        }

        composable(Screen.Profile) {
            ProfilKasirScreen(
                navController = navController,
                viewModel = posViewModel,
                authViewModel = authViewModel,
                tokenManager = tokenManager
            )
        }

        composable(Screen.AccessDenied) {
            AccessDeniedScreen(
                userRole = currentRole ?: authUserRole,
                onBackToLogin = {
                    authViewModel.resetState()
                    navController.navigate(Screen.AccessDenied) {
                        popUpTo(Screen.AccessDenied) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DebugDashboard) {
            ApiTestScreen(
                viewModel = mainViewModel
            )
        }
    }
}

@Composable
fun AccessDeniedScreen(
    userRole: String?,
    onBackToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundNeutral),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Akses Ditolak",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Akses Ditolak",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBrown
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Peran Anda (${userRole ?: "pembeli"}) tidak memiliki akses ke Dashboard Kasir. Hanya akun dengan peran 'kasir' atau 'admin' yang dapat mengakses aplikasi ini.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryButton(
                    text = "Kembali ke Login",
                    onClick = onBackToLogin
                )
            }
        }
    }
}
