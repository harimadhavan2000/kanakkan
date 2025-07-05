package com.upitracker.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.upitracker.presentation.components.TransactionItem
import com.upitracker.presentation.components.SearchBar
import com.upitracker.presentation.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Notification permission state
    val notificationPermissionState = rememberPermissionState(
        android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
    )
    
    var showPermissionDialog by remember { mutableStateOf(!notificationPermissionState.status.isGranted) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("UPI Tracker") },
                actions = {
                    IconButton(onClick = { navController.navigate("categories") }) {
                        Icon(Icons.Default.List, contentDescription = "Categories")
                    }
                    IconButton(onClick = { navController.navigate("reports") }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Reports")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Open camera for bill scanning */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Scan Bill")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Spent",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "₹${uiState.totalSpent}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Received",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "₹${uiState.totalReceived}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Transaction List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = {
                                navController.navigate("transaction/${transaction.id}")
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Permission Dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Enable Notification Access") },
            text = { 
                Text("To automatically track UPI transactions, please enable notification access for this app in Settings.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            // Open notification access settings
                            val intent = android.content.Intent(
                                android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                            )
                            context.startActivity(intent)
                        }
                        showPermissionDialog = false
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Later")
                }
            }
        )
    }
}