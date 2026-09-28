package com.smkn8jkt.sipeka.ui.screens.auth

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
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
import com.smkn8jkt.sipeka.ui.components.SipekaDropdownField
import com.smkn8jkt.sipeka.ui.components.SipekaPasswordField
import com.smkn8jkt.sipeka.ui.components.SipekaTextField
import com.smkn8jkt.sipeka.ui.theme.BgLightCanvas
import com.smkn8jkt.sipeka.ui.theme.BorderStitch
import com.smkn8jkt.sipeka.ui.theme.BtnDarkChocolate
import com.smkn8jkt.sipeka.ui.theme.BtnMocha
import com.smkn8jkt.sipeka.ui.theme.CardCreamWhite
import com.smkn8jkt.sipeka.ui.theme.SipekaTheme
import com.smkn8jkt.sipeka.ui.theme.TextDark
import com.smkn8jkt.sipeka.ui.theme.TextMuted
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            Toast.makeText(context, "Pendaftaran Akun Berhasil! Mohon tunggu persetujuan (ACC) dari Admin / Guru Pembina sebelum login.", Toast.LENGTH_LONG).show()
            viewModel.resetState()
            onNavigateToLogin()
        }
    }

    RegisterScreenContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRegisterClick = { nisnNip, name, password, confirmPassword, role ->
            viewModel.register(
                nisnNip = nisnNip,
                name = name,
                password = password,
                confirmPassword = confirmPassword,
                role = role
            )
        },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterScreenContent(
    isLoading: Boolean,
    errorMessage: String?,
    onRegisterClick: (String, String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nisnNip by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("pembeli") }

    val roleOptions = remember { listOf("pembeli", "penitip") }

    // Spring Entrance Animation
    val cardScale = remember { Animatable(0.85f) }
    val cardAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            cardScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            cardAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 500)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLightCanvas),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .scale(cardScale.value)
                .alpha(cardAlpha.value)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardCreamWhite
                ),
                border = BorderStroke(1.dp, BorderStitch),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo Container - Pure Crisp White & Elevated (Tidak Samar!)
                    Surface(
                        modifier = Modifier.size(84.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 6.dp,
                        border = BorderStroke(2.dp, BorderStitch)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.loading_sipeka),
                                contentDescription = "Logo SIPeKa",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Daftar Akun Baru",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Lengkapi data diri Anda untuk bergabung di Sistem PKK",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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
                                imageVector = Icons.Default.Badge,
                                contentDescription = "NISN/NIP Icon",
                                tint = BtnMocha
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SipekaTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nama Lengkap",
                        placeholder = "Masukkan Nama Lengkap Anda",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name Icon",
                                tint = BtnMocha
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SipekaDropdownField(
                        options = roleOptions,
                        selectedOption = selectedRole,
                        onOptionSelected = { selectedRole = it },
                        label = "Daftar Sebagai"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SipekaPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "Masukkan Password",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = BtnMocha
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SipekaPasswordField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Konfirmasi Password",
                        placeholder = "Ulangi Password",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = "Confirm Password Icon",
                                tint = BtnMocha
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Sudah punya akun? ",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "Masuk",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BtnDarkChocolate
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
                        text = "DAFTAR SEKARANG",
                        onClick = {
                            onRegisterClick(
                                nisnNip,
                                name,
                                password,
                                confirmPassword,
                                selectedRole
                            )
                        },
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    SipekaTheme {
        RegisterScreenContent(
            isLoading = false,
            errorMessage = null,
            onRegisterClick = { _, _, _, _, _ -> },
            onNavigateToLogin = {}
        )
    }
}
