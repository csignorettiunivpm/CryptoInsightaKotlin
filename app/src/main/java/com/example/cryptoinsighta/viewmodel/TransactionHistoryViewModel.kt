package com.example.cryptoinsighta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.example.cryptoinsighta.model.Transaction
import com.example.cryptoinsighta.model.TransactionWithTicker
import com.example.cryptoinsighta.repository.AssetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AssetRepository(application)
    private val _listaTransazioni = MutableStateFlow<List<TransactionWithTicker>>(emptyList())
    val listaTransazioni: StateFlow<List<TransactionWithTicker>> = _listaTransazioni

    fun loadTransazioni(){
        viewModelScope.launch {
            _listaTransazioni.value = repository.getAllTransactionsWithTicker()
        }
    }

    fun eliminaTransazione(transaction: Transaction) {
        viewModelScope.launch {
            repository.eliminaTransazione(transaction)
            loadTransazioni()
        }
    }


}