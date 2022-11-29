package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.models.AddUserModel
import com.kseniabl.tasksapp.models.TokenModel
import com.kseniabl.tasksapp.models.UserLoginModel
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserTokenDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStoreInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    val repository: Repository,
    val userTokenDataStore: UserTokenDataStoreInterface
): ViewModel() {

    private val _tokenFlowData = MutableStateFlow<Resource<String>>(Resource.Loading())
    val tokenFlowData = _tokenFlowData.asStateFlow()

    private val _loginStatus = MutableSharedFlow<Resource<Response<TokenModel>>>()
    val loginStatus = _loginStatus.asSharedFlow()

    private val _registrationStatus = MutableSharedFlow<Resource<Response<Void>>>()
    val registrationStatus = _registrationStatus.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _tokenFlowData.emit(Resource.Loading())
            userTokenDataStore.readToken.collect { token ->
                _tokenFlowData.emit(Resource.Success(data = token))
            }
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userTokenDataStore.saveToken(token)
        }
    }

    fun signUp(addUserModel: AddUserModel) {
        viewModelScope.launch {
            _registrationStatus.emit(Resource.Loading())
            try {
                _registrationStatus.emit(Resource.Success(data = repository.addUser(addUserModel)))
            } catch (exception: Exception) {
                _registrationStatus.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
            }
        }
    }

    fun signIn(loginModel: UserLoginModel) {
        viewModelScope.launch {
            _loginStatus.emit(Resource.Loading())
            try {
                _loginStatus.emit(Resource.Success(data = repository.loginUser(loginModel)))
            } catch (exception: Exception) {
                _loginStatus.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
            }
        }
    }
}