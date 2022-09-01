package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import com.kseniabl.tasksapp.db.TasksRepository
import com.kseniabl.tasksapp.db.TasksRepositoryInterface
import com.kseniabl.tasksapp.models.CardModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardsViewModel @Inject constructor(private val repository: TasksRepositoryInterface): ViewModel(), AddTasksAdapter.Listener {

    private val _adapterList = MutableStateFlow(true)
    val adapterList: StateFlow<Boolean> = _adapterList

    private val _dialogTrigger = MutableSharedFlow<CardModel>()
    val dialogTrigger: SharedFlow<CardModel> = _dialogTrigger

    val cards = repository.getAddCards()

    fun changeList(active: Boolean) {
        _adapterList.value = active
    }

    fun insertCard(card: CardModel) {
        viewModelScope.launch {
            repository.insertAddCard(card)
        }
    }

    fun changeCard(card: CardModel) {
        viewModelScope.launch { repository.changeAddProdCard(card) }
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