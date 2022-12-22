package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserTokenDataStoreInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    val repository: Repository,
    val userTokenDataStore: UserTokenDataStoreInterface,
): ViewModel() {

    private val _user = MutableStateFlow<Resource<UserModel>?>(null)
    private val _specialization = MutableStateFlow<Resource<Specialization>?>(null)
    private val _addInf = MutableStateFlow<Resource<AdditionalInfo>?>(null)

    val userAllData = combine(
        _user,
        _specialization,
        _addInf
    ) { user, spec, addInf ->
        var userDate: Resource<FreelancerModel>? = null
        if (user is Resource.Success<*> && spec is Resource.Success<*> && addInf is Resource.Success<*>) {
            userDate = Resource.Success(FreelancerModel(user.data, spec.data, addInf.data))
        }
        if (user is Resource.Error<*>) {
            userDate = Resource.Error(user.message)
        }
        userDate
    }

    fun getCreator(id: String) {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                val user = repository.getUserById(token, id).body()
                var spec: Specialization? = null
                var addInf: AdditionalInfo? = null
                if (user != null) {
                    if (user.specialization.isNotEmpty())
                        spec = repository.getSpec(token, IdBody(user.specialization)).body()
                    if (user.addInf.isNotEmpty())
                        addInf = repository.getAddInf(token, IdBody(user.addInf)).body()
                    _user.emit(Resource.Success(user))
                    _specialization.emit(Resource.Success(spec))
                    _addInf.emit(Resource.Success(addInf))
                }
                else {
                    _user.emit(Resource.Error("Some object is null"))
                }

            } catch (exception: Exception) {
                _user.emit(Resource.Error(exception.message ?: "Some error occurred"))
            }
        }
    }

}