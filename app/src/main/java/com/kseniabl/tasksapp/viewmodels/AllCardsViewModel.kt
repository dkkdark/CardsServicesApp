package com.kseniabl.tasksapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllCardsViewModel @Inject constructor(): ViewModel() {

    private val _adapterValue = MutableLiveData<AllCardsAdapterInterface>()
    val adapterValue: LiveData<AllCardsAdapterInterface> = _adapterValue

    fun changeAdapter(adapter: AllCardsAdapterInterface) {
        _adapterValue.value = adapter
    }
}