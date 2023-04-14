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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userTokenDataStore.readToken.collect { token ->
                _tokenFlowData.value = token
            }
        }
    }

    private fun saveUser(user: UserModel, token: String) {
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
                }
                else _uiActionsLogin.emit(UIActionsLogin.ShowSnackbar("User wasn't found"))
            } catch (exception: Exception) {
                _uiActionsLogin.emit(UIActionsLogin.ShowSnackbar("Sign in was unsuccessful due to server error"))
            }
        }
    }

    private fun getUser(token: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserByToken(token).body()
                Log.e("qqq", "user $user")
                if (user != null)
                    saveUser(user, token)
                else
                    _uiActionsLogin.emit(UIActionsLogin.ShowSnackbar("User wasn't found"))

            } catch (exception: Exception) {
                _uiActionsLogin.emit(UIActionsLogin.ShowSnackbar("Sign in was unsuccessful due to server error"))
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