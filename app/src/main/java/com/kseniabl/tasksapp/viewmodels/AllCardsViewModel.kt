package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllCardsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
): ViewModel() {

    private val _adapterValue = MutableLiveData<AllCardsAdapterInterface>()
    val adapterValue: LiveData<AllCardsAdapterInterface> = _adapterValue

    fun changeAdapter(adapter: AllCardsAdapterInterface) {
        _adapterValue.value = adapter
    }

    fun getData(view: View) {
        auth.currentUser?.uid?.let {
            val query = database.child("cards").orderByChild("createTime").limitToFirst(20)
            query.get()
                .addOnSuccessListener { data ->
                    // TODO: listener on list and in fragment set list to recycler
                }
                .addOnFailureListener { Snackbar.make(view, "We couldn't load your cards. Please check internet connection",
                    Snackbar.LENGTH_SHORT).show() }

        }
    }
}