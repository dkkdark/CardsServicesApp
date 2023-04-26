package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserDataStoreInterface
import com.kseniabl.tasksapp.utils.UserTokenDataStoreInterface
import com.kseniabl.tasksapp.utils.WelcomeScreenDataStoreInterface
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
    val userDataStore: UserDataStoreInterface,
    val welcomeScreenDataStore: WelcomeScreenDataStoreInterface
): ViewModel() {

    private val _tokenFlowData = MutableStateFlow<String?>(null)
    //val tokenFlowData = _tokenFlowData.asStateFlow()

    private val _firstTimeEnter = MutableStateFlow<Boolean?>(null)

    private val _uiActionsRegistration = MutableSharedFlow<UIActions>()
    val uiActionsRegistration = _uiActionsRegistration.asSharedFlow()

    private val _uiActionsLogin = MutableSharedFlow<UIActions>()
    val uiActionsLogin = _uiActionsLogin.asSharedFlow()

    val moveToScreen = combine(_tokenFlowData, _firstTimeEnter) { token, state ->
        Log.e("qqq", "token $token state $state")
        return@combine if (token != null && state != null) {
            if (token.isEmpty() && state)
                UIActions.ToLoginFragment
            else if (token.isEmpty() && !state) {
                UIActions.ToWelcomeFragment
            }
            else if (token.isNotEmpty() && state)
                UIActions.ToTabFragment
            else {
                Log.e("qqq", "This should be unreachable")
                UIActions.ToLoginFragment
            }
        }
        else UIActions.Waiting
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userTokenDataStore.readToken.collect { token ->
                _tokenFlowData.value = token
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            welcomeScreenDataStore.readState.collect { state ->
                _firstTimeEnter.value = state
            }
        }
    }

    fun setFirstEntrance() {
        viewModelScope.launch(Dispatchers.IO) {
            welcomeScreenDataStore.saveState(true)
        }
    }

    private fun saveUser(user: UserModel, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataStore.writeUser(user)
            userTokenDataStore.saveToken(token)
            _uiActionsLogin.emit(UIActions.ToTabFragment)
        }
    }

    fun signUp(addUserModel: AddUserModel) {
        viewModelScope.launch {
            try {
                repository.addUser(addUserModel)
                _uiActionsRegistration.emit(UIActions.ShowSnackbar("Please sign in"))
                _uiActionsRegistration.emit(UIActions.ToLoginFragment)
            } catch (exception: Exception) {
                _uiActionsRegistration.emit(UIActions.ShowSnackbar("Registration was unsuccessful"))
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
                else _uiActionsLogin.emit(UIActions.ShowSnackbar("User wasn't found"))
            } catch (exception: Exception) {
                _uiActionsLogin.emit(UIActions.ShowSnackbar("Sign in was unsuccessful due to server error"))
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
                    _uiActionsLogin.emit(UIActions.ShowSnackbar("User wasn't found"))

            } catch (exception: Exception) {
                _uiActionsLogin.emit(UIActions.ShowSnackbar("Sign in was unsuccessful due to server error"))
            }
        }
    }

    sealed class UIActions {
        data class ShowSnackbar(val message: String): UIActions()
        object ToLoginFragment: UIActions()
        object ToTabFragment: UIActions()
        object ToWelcomeFragment: UIActions()
        object Waiting: UIActions()

    }
}