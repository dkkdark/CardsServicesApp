package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserDataStoreInterface
import com.kseniabl.tasksapp.utils.UserTokenDataStoreInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: Repository,
    val userTokenDataStore: UserTokenDataStoreInterface,
    val userDataStore: UserDataStoreInterface
): ViewModel() {

    private val _tokenFlowData = MutableStateFlow<String?>(null)
    val tokenFlowData = _tokenFlowData.asStateFlow()

    private val _loginStatus = MutableSharedFlow<Resource<Response<TokenModel>>>()
    val loginStatus = _loginStatus.asSharedFlow()

    private val _registrationStatus = MutableSharedFlow<Resource<Response<Void>>>()
    val registrationStatus = _registrationStatus.asSharedFlow()

    private val _user = MutableStateFlow<Resource<UserModel>?>(null)
    private val _specialization = MutableStateFlow<Resource<Specialization>?>(null)
    private val _addInf = MutableStateFlow<Resource<AdditionalInfo>?>(null)

    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userTokenDataStore.readToken.collect { token ->
                _tokenFlowData.emit(token)
            }
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userTokenDataStore.saveToken(token)
        }
    }

    fun saveUser(user: FreelancerModel) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataStore.writeUser(user)
            _saved.emit(true)
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

    fun getUser(token: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserByToken(token).body()
                var spec: Specialization? = null
                var addInf: AdditionalInfo? = null
                Log.e("qqq", "user $token")
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