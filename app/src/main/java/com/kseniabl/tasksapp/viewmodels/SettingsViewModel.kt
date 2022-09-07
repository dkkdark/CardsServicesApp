package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kseniabl.tasksapp.db.TasksRepository
import com.kseniabl.tasksapp.db.TasksRepositoryInterface
import com.kseniabl.tasksapp.models.AdditionalInfo
import com.kseniabl.tasksapp.models.Profession
import com.kseniabl.tasksapp.models.UserModel
import com.kseniabl.tasksapp.utils.UserSave
import com.kseniabl.tasksapp.utils.UserSaveInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSave: UserSaveInterface,
    private val repository: TasksRepositoryInterface,
    private val saveUser: UserSaveInterface,
    private val auth: FirebaseAuth
    ) : ViewModel() {

    val liveUser = userSave.getLiveSharedPref()

    fun setupUserInfo(userModel: UserModel?, name: TextView, checkBox: CheckBox, email: TextView, spec: TextView, descr: TextView,
                      descrAdd: TextView, country: TextView, city: TextView, type: TextView) {
        userModel?.let {
            name.text = it.username
            checkBox.isChecked = it.isFreelancer
            email.text = it.email
            checkIsEmpty(spec, it.profession.specialization)
            checkIsEmpty(descr, it.profession.description)
            checkIsEmpty(descrAdd, it.additionalInfo.description)
            checkIsEmpty(country, it.additionalInfo.country)
            checkIsEmpty(city, it.additionalInfo.city)
            checkIsEmpty(type, it.additionalInfo.typeOfWork)
        }
    }

    fun updateUserName(name: String) {
        val user = userSave.readSharedPref()
        user?.username = name
        user?.let { userSave.saveCurrentUser(it) }
    }

    fun updateUserProf(spec: String, descr: String) {
        val user = userSave.readSharedPref()
        user?.profession?.description = descr
        user?.profession?.specialization = spec
        user?.let { userSave.saveCurrentUser(it) }
    }

    fun updateUserAdditionalInfo(descrAdd: String, country: String, city: String, type: String) {
        val user = userSave.readSharedPref()
        user?.additionalInfo?.description = descrAdd
        user?.additionalInfo?.country = country
        user?.additionalInfo?.city = city
        user?.additionalInfo?.typeOfWork = type
        user?.let { userSave.saveCurrentUser(it) }
    }

    fun updateUserFreelanceState(state: Boolean) {
        val user = userSave.readSharedPref()
        user?.isFreelancer = state
        user?.let { userSave.saveCurrentUser(it) }
    }

    fun signOut() {
        val user = UserModel("", "", "",false, "", "", AdditionalInfo("", "", "", ""), Profession("", "", arrayListOf()))
        saveUser.saveCurrentUser(user)
        viewModelScope.launch {
            repository.clearAddProdCards()
        }
        auth.signOut()
    }

    private fun checkIsEmpty(textView: TextView, value: String) {
        if (value.isNotEmpty())
            textView.text = value
        else
            textView.text = "—"
    }
}