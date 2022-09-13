package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import com.kseniabl.tasksapp.db.TasksRepository
import com.kseniabl.tasksapp.db.TasksRepositoryInterface
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.models.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardsViewModel @Inject constructor(
    private val repository: TasksRepositoryInterface,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
    ): ViewModel(), AddTasksAdapter.Listener {

    private val _adapterList = MutableStateFlow(true)
    val adapterList: StateFlow<Boolean> = _adapterList

    private val _dialogTrigger = MutableSharedFlow<CardModel>()
    val dialogTrigger: SharedFlow<CardModel> = _dialogTrigger

    val cards = repository.getAddCards()

    fun changeList(active: Boolean) {
        _adapterList.value = active
    }

    fun insertCard(card: CardModel) {
        viewModelScope.launch { repository.insertAddCard(card) }
        setCardToDatabase(card)
    }

    fun changeCard(card: CardModel) {
        viewModelScope.launch { repository.changeAddProdCard(card) }
        setCardToDatabase(card)
    }

    private fun setCardToDatabase(card: CardModel) {
        auth.currentUser?.uid?.let {
            database.child("cards").child(card.id).setValue(card)
        }
    }

    fun getList(): List<CardModel> {
        return repository.allAddCards()
    }

    override fun onAddItemClick(item: CardModel) {
        viewModelScope.launch {
            _dialogTrigger.emit(item)
        }
    }
}