package com.dicoding.asclepius.view

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.HistoryRepository
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import kotlinx.coroutines.launch

class ResultViewModel(application: Application) : ViewModel() {
    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)
    fun insertHistory(history: HistoryEntity) {
        viewModelScope.launch {
            mHistoryRepository.insertHistory(history)
        }
    }
}