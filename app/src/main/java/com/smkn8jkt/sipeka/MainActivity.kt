package com.smkn8jkt.sipeka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.enableEdgeToEdge
import com.smkn8jkt.sipeka.data.remote.ApiClient
import com.smkn8jkt.sipeka.data.remote.TokenManager
import com.smkn8jkt.sipeka.navigation.AppNavigation
import com.smkn8jkt.sipeka.ui.screens.auth.AuthViewModel
import com.smkn8jkt.sipeka.ui.screens.auth.AuthViewModelFactory
import com.smkn8jkt.sipeka.ui.theme.SipekaTheme

class MainActivity : ComponentActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(applicationContext)
        ApiClient.init(tokenManager)

        val authViewModel: AuthViewModel by viewModels {
            AuthViewModelFactory(tokenManager)
        }

        val mainViewModel: MainViewModel by viewModels {
            MainViewModelFactory(tokenManager)
        }

        setContent {
            SipekaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        authViewModel = authViewModel,
                        mainViewModel = mainViewModel,
                        tokenManager = tokenManager
                    )
                }
            }
        }
    }
}

@Composable
fun ApiTestScreen(viewModel: MainViewModel) {
    val apiLog by viewModel.apiLog.collectAsState()
    val currentToken by viewModel.currentToken.collectAsState()

    ApiTestScreenContent(
        apiLog = apiLog,
        currentToken = currentToken,
        currentBaseUrl = "http://47.129.118.194/api/v1/",
        onUpdateBaseUrl = { },
        onClearToken = { viewModel.clearToken() },
        onClearLog = { viewModel.clearLog() },
        onRegister = { viewModel.register() },
        onLogin = { viewModel.login() },
        onGetProducts = { viewModel.getProducts() },
        onGetProductById = { viewModel.getProductById() },
        onGetUsers = { viewModel.getUsers() },
        onCreateUser = { viewModel.createUser() },
        onApproveUser = { viewModel.approveUser() },
        onClockIn = { viewModel.clockIn() },
        onGetCurrentShift = { viewModel.getCurrentShift() },
        onCreateTransaction = { viewModel.createTransaction() },
        onScanQrCode = { viewModel.scanQrCode() },
        onGetOrders = { viewModel.getOrders() },
        onGetOrderById = { viewModel.getOrderById() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTestScreenContent(
    apiLog: String,
    currentToken: String?,
    currentBaseUrl: String,
    onUpdateBaseUrl: (String) -> Unit,
    onClearToken: () -> Unit,
    onClearLog: () -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit,
    onGetProducts: () -> Unit,
    onGetProductById: () -> Unit,
    onGetUsers: () -> Unit,
    onCreateUser: () -> Unit,
    onApproveUser: () -> Unit,
    onClockIn: () -> Unit,
    onGetCurrentShift: () -> Unit,
    onCreateTransaction: () -> Unit,
    onScanQrCode: () -> Unit,
    onGetOrders: () -> Unit,
    onGetOrderById: () -> Unit
) {
    var urlInput by remember(currentBaseUrl) { mutableStateOf(currentBaseUrl) }
    var showUrlEdit by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SIPeKa REST API Debugger",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            // Section 1: Server Config & Saved Token
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Base URL:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        OutlinedButton(
                            onClick = { showUrlEdit = !showUrlEdit },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(if (showUrlEdit) "Tutup" else "Ubah IP Server", fontSize = 11.sp)
                        }
                    }

                    if (showUrlEdit) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                label = { Text("Base URL / IP", fontSize = 10.sp) }
                            )
                            Button(
                                onClick = {
                                    onUpdateBaseUrl(urlInput)
                                    showUrlEdit = false
                                }
                            ) {
                                Text("Set", fontSize = 11.sp)
                            }
                        }
                        Text(
                            text = "Tip HP Fisik: Pakai 'http://localhost:8081/api/v1/' setelah jalankan 'adb reverse tcp:8081 tcp:8081'",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Text(
                            text = currentBaseUrl,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "JWT Token:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val displayToken = currentToken?.takeIf { it.isNotBlank() } ?: "(Belum Login)"
                        Text(
                            text = displayToken,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            color = if (!currentToken.isNullOrBlank()) MaterialTheme.colorScheme.secondary else Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        if (!currentToken.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = onClearToken,
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Clear", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Section 2: API Request Buttons
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Daftar Endpoint API",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // Group 1: Auth & Register
                    EndpointCategoryGroup(title = "1. Auth & Register") {
                        ApiButton("POST Register", onRegister)
                        ApiButton("POST Login", onLogin)
                    }

                    // Group 2: Products
                    EndpointCategoryGroup(title = "2. Products (Katalog)") {
                        ApiButton("GET Products", onGetProducts)
                        ApiButton("GET Product By ID", onGetProductById)
                    }

                    // Group 3: Admin
                    EndpointCategoryGroup(title = "3. Admin (User Management)") {
                        ApiButton("GET Users", onGetUsers)
                        ApiButton("POST Create User", onCreateUser)
                        ApiButton("PUT Approve User", onApproveUser)
                    }

                    // Group 4: Shifts
                    EndpointCategoryGroup(title = "4. Shifts (Kasir)") {
                        ApiButton("POST Clock-In", onClockIn)
                        ApiButton("GET Current Shift", onGetCurrentShift)
                    }

                    // Group 5: POS & Orders
                    EndpointCategoryGroup(title = "5. POS & Orders (Transaksi)") {
                        ApiButton("POST Transaction", onCreateTransaction)
                        ApiButton("PUT Scan QR", onScanQrCode)
                        ApiButton("GET Orders", onGetOrders)
                        ApiButton("GET Order By ID", onGetOrderById)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Section 3: Response Log Output
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E1E)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Raw Response Log",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        OutlinedButton(
                            onClick = onClearLog,
                            modifier = Modifier.height(30.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.LightGray
                            )
                        ) {
                            Text("Clear Log", fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF121212), shape = RoundedCornerShape(6.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        SelectionContainer {
                            Text(
                                text = apiLog,
                                color = Color(0xFF00FF66),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EndpointCategoryGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ApiButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(text = label, fontSize = 11.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ApiTestScreenPreview() {
    SipekaTheme {
        ApiTestScreenContent(
            apiLog = "[10:15:30] === ENDPOINT: POST auth/login ===\nHTTP Status: 200 OK\n\nRESPONSE BODY:\n{\n  \"code\": 200,\n  \"status\": \"success\",\n  \"message\": \"Login berhasil\",\n  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"\n}",
            currentToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            currentBaseUrl = "http://localhost:8081/api/v1/",
            onUpdateBaseUrl = {},
            onClearToken = {},
            onClearLog = {},
            onRegister = {},
            onLogin = {},
            onGetProducts = {},
            onGetProductById = {},
            onGetUsers = {},
            onCreateUser = {},
            onApproveUser = {},
            onClockIn = {},
            onGetCurrentShift = {},
            onCreateTransaction = {},
            onScanQrCode = {},
            onGetOrders = {},
            onGetOrderById = {}
        )
    }
}
