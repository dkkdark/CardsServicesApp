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

    private val _uiActionsRegistration = MutableSharedFlow<UIActionsRegistration>()
    val uiActionsRegistration = _uiActionsRegistration.asSharedFlow()

    private val _uiActionsLogin = MutableSharedFlow<UIActionsLogin>()
    val uiActionsLogin = _uiActionsLogin.asSharedFlow()

    private val _user = MutableStateFlow<Resource<UserModel>?>(null)
    private val _specialization = MutableStateFlow<Resource<Specialization>?>(null)
    private val _addInf = MutableStateFlow<Resource<AdditionalInfo>?>(null)

    private val _token = MutableStateFlow<String?>(null)

    val userAllData = combine(
        _user,
        _specialization,
        _addInf,
        _token
    ) { user, spec, addInf, token ->
        if (user is Resource.Success<*> && spec is Resource.Success<*> && addInf is Resource.Success<*> && !token.isNullOrEmpty()) {
            saveUser(FreelancerModel(user.data, spec.data, addInf.data), token)
        }
        if (user is Resource.Error<*>) {
            _uiActionsLogin.emit(UIActionsLogin.ShowSnackbar("Sign in was unsuccessful"))
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userTokenDataStore.readToken.collect { token ->
                _tokenFlowData.value = token
            }
        }
    }

    private fun saveUser(user: FreelancerModel, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataStore.writeUser(user)
            userTokenDataStore.saveToken(token)
        }
    }

    fun signUp(addUserModel: AddUserModel) {
        viewModelScope.launch {
            try {
                repository.addUser(addUserModel)
                _uiActionsRegistration.emit(UIActionsRegistration.ShowSnackbar("Please sign in"))
                _uiActionsRegistration.emit(UIActionsRegistration.ToLoginFragment)
            } catch (exception: Exception) {
                _uiActionsRegistration.emit(UIActionsRegistration.ShowSnackbar("Registration was unsuccessful"))
            }
        }
    }

    fun signIn(loginModel: UserLoginModel) {
        viewModelScope.launch {
            try {
                val token = repository.loginUser(loginModel).body()?.token
                if (token != null) {
                    getUser(token)
                    // TODO: load user's cards and save them
                }
                else _uiActionsLogin.emit(UIActionsLogin.ShowSnackbar("Sign in was unsuccessful"))
            } catch (exception: Exception) {
                _uiActionsLogin.emit(UIActionsLogin.ShowSnackbar("Sign in was unsuccessful"))
            }
        }
    }

    private fun getUser(token: String) {
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

                    _user.value = Resource.Success(user)
                    _specialization.value = Resource.Success(spec)
                    _addInf.value = Resource.Success(addInf)
                    _token.value = token
                }
                else {
                    _user.value = Resource.Error("Some object is null")
                }

            } catch (exception: Exception) {
                _user.value = Resource.Error(exception.message ?: "Some error occurred")
            }
        }
    }

    sealed class UIActionsRegistration {
        data class ShowSnackbar(val message: String): UIActionsRegistration()
        object ToLoginFragment: UIActionsRegistration()
    }

    sealed class UIActionsLogin {
        data class ShowSnackbar(val message: String): UIActionsLogin()

    }
}