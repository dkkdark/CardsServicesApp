package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookedCardsViewModel @Inject constructor(
    private val repository: Repository,
    private val userDataStore: UserDataStore,
    private val userTokenDataStore: UserTokenDataStore
): ViewModel() {

    private val _cards = MutableStateFlow<ArrayList<CardModel>?>(null)
    val cards = _cards.asStateFlow()

    fun getCards() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                val cards = repository.getBookedCards(token).body()
                _cards.emit(cards)
            } catch (exception: Exception) {
                Log.e("qqq", "load users cards error: ${exception.message}")
            }
        }
    }

}