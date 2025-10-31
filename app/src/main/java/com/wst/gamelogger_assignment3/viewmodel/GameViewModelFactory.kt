package com.wst.gamelogger_assignment3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wst.gamelogger_assignment3.repository.GameRepository

class GameViewModelFactory(private val repo: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(repo) as T
    }
}