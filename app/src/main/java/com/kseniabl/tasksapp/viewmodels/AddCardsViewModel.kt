package com.kseniabl.tasksapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import com.kseniabl.tasksapp.models.CardModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddCardsViewModel @Inject constructor(): ViewModel() {

    private val _adapterList = MutableLiveData<Boolean>()
    val adapterList: LiveData<Boolean> = _adapterList

    init {
        _adapterList.value = true
    }

    fun changeList(active: Boolean) {
        _adapterList.value = active
    }
}