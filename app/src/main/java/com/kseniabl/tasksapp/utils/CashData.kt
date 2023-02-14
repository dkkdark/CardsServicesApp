package com.kseniabl.tasksapp.utils

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.Response

inline fun <R> cashData(
    crossinline apiCall: suspend () -> ArrayList<R>?,
    crossinline dbCall: () -> Flow<List<R>>,
    crossinline saveData: (ArrayList<R>?) -> Unit
) = flow {
    val dbData = dbCall().first()
    val apiData = apiCall()

    emit(dbData)

    if (dbData.toMutableList() != apiData?.toMutableList() && apiData != null) {
        saveData(apiData)
        emit(apiData)
    }
}