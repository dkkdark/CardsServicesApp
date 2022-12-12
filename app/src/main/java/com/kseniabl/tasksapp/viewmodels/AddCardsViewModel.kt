package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.Resource
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

    private val _dialogTrigger = MutableSharedFlow<Resource<CardModel?>>()
    val dialogTrigger = _dialogTrigger.asSharedFlow()

    private val _cards = MutableStateFlow<ArrayList<CardModel>?>(null)
    val cards = _cards.asStateFlow()

    fun changeList(active: Boolean) {
        _adapterList.value = active
    }

    fun isUserCreator() {
        viewModelScope.launch {
            val user = userDataStore.readUser.first()
            Log.e("qqq", "user loaded $user")
            if (user.userInfo?.creator == true)
                _dialogTrigger.emit(Resource.Success(null))
            else
                _dialogTrigger.emit(Resource.Error("You must became a creator to add card"))
        }
    }

    fun getCards() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                val cards = repository.getUsersCards(token).body()
                _cards.emit(cards)
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
                Log.e("qqq", "Update card was unsuccessful: ${exception.message}")
            }
        }
    }

    override fun onAddItemClick(item: CardModel) {
        viewModelScope.launch {
            _dialogTrigger.emit(Resource.Success(item))
        }
    }
}