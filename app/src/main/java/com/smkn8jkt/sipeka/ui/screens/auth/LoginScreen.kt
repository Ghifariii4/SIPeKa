package com.smkn8jkt.sipeka.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smkn8jkt.sipeka.R
import com.smkn8jkt.sipeka.ui.components.PrimaryButton
import com.smkn8jkt.sipeka.ui.components.SipekaPasswordField
import com.smkn8jkt.sipeka.ui.components.SipekaTextField
import com.smkn8jkt.sipeka.ui.theme.BackgroundNeutral
import com.smkn8jkt.sipeka.ui.theme.PrimaryBrown
import com.smkn8jkt.sipeka.ui.theme.SecondaryBrown
import com.smkn8jkt.sipeka.ui.theme.SipekaTheme

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }

    LoginScreenContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        onLoginClick = { nisnNip, password ->
            viewModel.login(nisnNip, password)
        },
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun LoginScreenContent(
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var nisnNip by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundNeutral),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.loading_sipeka),
                        contentDescription = "Logo SIPeKa",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(65.dp)
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Selamat Datang",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryBrown,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Silakan login ke akun Anda",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SipekaTextField(
                        value = nisnNip,
                        onValueChange = { input ->
                            nisnNip = input.filter { char -> char.isDigit() }
                        },
                        label = "NISN / NIP",
                        placeholder = "Masukkan NISN atau NIP",
                        keyboardType = KeyboardType.Number,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "NISN/NIP Icon",
                                tint = PrimaryBrown
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SipekaPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "Masukkan Password",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = PrimaryBrown
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Belum punya akun? ",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Register",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryBrown
                            )
                        }
                    }

                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    PrimaryButton(
                        text = "LOGIN",
                        onClick = { onLoginClick(nisnNip, password) },
                        isLoading = isLoading
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = {
                            Toast.makeText(context, "Silakan hubungi Admin Sekolah untuk reset password", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Lupa Password?",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SipekaTheme {
        LoginScreenContent(
            isLoading = false,
            errorMessage = null,
            onLoginClick = { _, _ -> },
            onNavigateToRegister = {}
        )
    }
}
