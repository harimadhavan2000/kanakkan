package com.upitracker.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upitracker.data.database.TransactionDatabase
import com.upitracker.data.models.Transaction
import com.upitracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class HomeUiState(
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val totalSpent: Double = 0.0,
    val totalReceived: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = TransactionDatabase.getDatabase(application)
    private val repository = TransactionRepository(
        database.transactionDao(),
        database.categoryDao()
    )
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val searchQuery = MutableStateFlow("")
    
    init {
        // Observe transactions based on search query
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .flatMapLatest { query ->
                    if (query.isEmpty()) {
                        repository.getAllTransactions()
                    } else {
                        repository.searchTransactions(query)
                    }
                }
                .collect { transactions ->
                    _uiState.update { state ->
                        state.copy(transactions = transactions)
                    }
                }
        }
        
        // Calculate totals for current month
        viewModelScope.launch {
            val now = LocalDateTime.now()
            val startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            val endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59)
            
            val totalSpent = repository.getTotalSpent(startOfMonth, endOfMonth)
            val totalReceived = repository.getTotalReceived(startOfMonth, endOfMonth)
            
            _uiState.update { state ->
                state.copy(
                    totalSpent = totalSpent,
                    totalReceived = totalReceived
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Recalculate totals
                val now = LocalDateTime.now()
                val startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                val endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59)
                
                val totalSpent = repository.getTotalSpent(startOfMonth, endOfMonth)
                val totalReceived = repository.getTotalReceived(startOfMonth, endOfMonth)
                
                _uiState.update { state ->
                    state.copy(
                        totalSpent = totalSpent,
                        totalReceived = totalReceived,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}