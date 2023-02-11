package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardsViewModel @Inject constructor(
    private val repository: Repository,
    private val userDataStore: UserDataStore,
    private val userTokenDataStore: UserTokenDataStore
    ): ViewModel(), AddTasksAdapter.Listener {

    private val _adapterList = MutableStateFlow(true)
    val adapterList: StateFlow<Boolean> = _adapterList

    private val _actionsTrigger = MutableSharedFlow<UIActions>()
    val actionsTrigger = _actionsTrigger.asSharedFlow()

    private val _cards = MutableStateFlow<ArrayList<CardModel>?>(null)
    val cards = _cards.asStateFlow()

    fun changeList(active: Boolean) {
        _adapterList.value = active
    }

    fun isUserCreator() {
        viewModelScope.launch {
            val user = userDataStore.readUser.first()
            if (user.userInfo?.creator == true)
                _actionsTrigger.emit(UIActions.GoToDialog(null))
            else
                _actionsTrigger.emit(UIActions.ShowSnackbar("You must became a creator to add card"))
        }
    }

    fun getCards() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                val cards = repository.getUsersCards(token).body()
                _cards.value = cards
            } catch (exception: Exception) {
                Log.e("qqq", "load users cards error: ${exception.message}")
            }
        }
    }

    fun updateCard(card: CardModel) {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                repository.updateCard(token, card)
                getCards()
            } catch (exception: Exception) {
                _actionsTrigger.emit(UIActions.ShowSnackbar("Update card was unsuccessful"))
                Log.e("qqq", "Update card was unsuccessful: ${exception.message}")
            }
        }
    }

    override fun onAddItemClick(item: CardModel) {
        viewModelScope.launch {
            _actionsTrigger.emit(UIActions.GoToDialog(item))
        }
    }

    sealed class UIActions {
        data class ShowSnackbar(val message: String): UIActions()
        data class GoToDialog(val card: CardModel?): UIActions()
    }
}