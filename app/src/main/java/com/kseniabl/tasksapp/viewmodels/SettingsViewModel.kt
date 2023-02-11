package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.HelperFunctions
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    //private val repository: TasksRepositoryInterface,
    private val repository: Repository,
    private val userTokenDataStore: UserTokenDataStore,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _stateChange = MutableSharedFlow<UIActions>()
    val stateChange = _stateChange.asSharedFlow()


    fun updateSpecialization(spec: String, desc: String) {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            val user = userDataStore.readUser.first()
            val userId = user.userInfo?.id
            if (userId.isNullOrEmpty()) {
                Log.e("qqq", "User id is null")
            }
            else {
                try {
                    var specId = ""
                    specId = if (user.specialization?.id.isNullOrEmpty()) {
                        HelperFunctions.generateRandomKey()
                    } else {
                        user.specialization!!.id
                    }
                    repository.updateSpec(token, UpdateSpecModel(userId, specId, spec, desc))
                    val newUser = FreelancerModel(UserModel(user.userInfo.id, user.userInfo.username, user.userInfo.creator, user.userInfo.img, user.userInfo.rolename, specId, user.userInfo.addInf),
                        Specialization(specId, spec, desc), user.additionalInfo)
                    userDataStore.writeUser(newUser)
                } catch (exception: Exception) {
                    Log.e("qqq", "Specialization wasn't upload. ${exception.message}")
                }
            }
        }
    }

    fun updateAddInf(description: String, country: String, city: String, typeOfWork: String) {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            val user = userDataStore.readUser.first()
            val userId = user.userInfo?.id
            if (userId.isNullOrEmpty()) {
                Log.e("qqq", "User id is null")
            }
            else {
                try {
                    val addInfId = if (user.additionalInfo?.id.isNullOrEmpty()) {
                        HelperFunctions.generateRandomKey()
                    } else {
                        user.additionalInfo!!.id
                    }
                    repository.updateAddInf(token, UpdateAddInfModel(userId, addInfId, description, country, city, typeOfWork))
                    val newUser = FreelancerModel(
                        UserModel(user.userInfo.id, user.userInfo.username, user.userInfo.creator, user.userInfo.img, user.userInfo.rolename, user.userInfo.specialization, addInfId),
                        user.specialization, AdditionalInfo(addInfId, description, country, city, typeOfWork))
                    userDataStore.writeUser(newUser)
                } catch (exception: Exception) {
                    Log.e("qqq", "Additional Info wasn't upload. ${exception.message}")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userTokenDataStore.saveToken("")
            userDataStore.writeUser(FreelancerModel())
        }
    }

    fun becomeCreator() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            val user = userDataStore.readUser.first()

            if (!user.specialization?.specialization.isNullOrEmpty() && !user.specialization?.description.isNullOrEmpty() &&
                !user.additionalInfo?.description.isNullOrEmpty() && !user.additionalInfo?.country.isNullOrEmpty() && !user.additionalInfo?.city.isNullOrEmpty()
                && !user.additionalInfo?.typeOfWork.isNullOrEmpty() && !user.userInfo?.id.isNullOrEmpty() && !user.userInfo?.username.isNullOrEmpty()) {
                try {
                    repository.updateCreatorState(token, UpdateCreatorStatus(user.userInfo!!.id, user.userInfo.username))
                    val newUser = FreelancerModel(UserModel(user.userInfo.id, user.userInfo.username, true, user.userInfo.img, "creator", user.userInfo.specialization,
                        user.userInfo.addInf), user.specialization, user.additionalInfo)
                    userDataStore.writeUser(newUser)
                    _stateChange.emit(UIActions.SetCreatorSettings)
                } catch (exception: Exception) {
                    _stateChange.emit(UIActions.ShowSnackbar("State cannot be update"))
                }
            }
            else {
                _stateChange.emit(UIActions.ShowSnackbar("Fill all fields to become a creator"))
            }
        }
    }

    sealed class UIActions {
        data class ShowSnackbar(val message: String): UIActions()
        object SetCreatorSettings: UIActions()
    }
}