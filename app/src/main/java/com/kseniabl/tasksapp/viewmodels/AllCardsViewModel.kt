package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.models.FreelancerModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllCardsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
): ViewModel() {

    private val _adapterValue = MutableLiveData<AllCardsAdapterInterface>()
    val adapterValue: LiveData<AllCardsAdapterInterface> = _adapterValue

    private val _adapterTasksList = MutableStateFlow(listOf<CardModel>())
    val adapterTasksList: StateFlow<List<CardModel>> = _adapterTasksList

    private val _adapterFreelancersList = MutableStateFlow(listOf<FreelancerModel>())
    val adapterFreelancersList: StateFlow<List<FreelancerModel>> = _adapterFreelancersList

    private val _dialogsTrigger = MutableSharedFlow<String>()
    val dialogsTrigger: SharedFlow<String> = _dialogsTrigger

    fun changeAdapter(adapter: AllCardsAdapterInterface) {
        _adapterValue.value = adapter
    }

    fun getCardsData() {
        auth.currentUser?.uid?.let {
            database.child("cards").orderByChild("active")
                .equalTo(true).limitToFirst(20).get()
                .addOnSuccessListener { data ->
                    val list = arrayListOf<CardModel>()
                    data.children.forEach { child ->
                        val card = child.getValue(CardModel::class.java) as CardModel
                        list.add(card)
                    }
                    _adapterTasksList.value = list.sortedBy { it.createTime }.reversed()
                }
                .addOnFailureListener {
                    Log.e("qqq", "getCardsData ${it.message}")
                    viewModelScope.launch {
                        _dialogsTrigger.emit("We couldn't load your cards. Please check internet connection")
                    }
                }

        }
    }
}