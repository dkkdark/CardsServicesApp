package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.kseniabl.tasksapp.models.UserModel
import com.kseniabl.tasksapp.utils.UserSave
import com.kseniabl.tasksapp.utils.UserSaveInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val userSave: UserSaveInterface): ViewModel() {

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

    private fun checkIsEmpty(textView: TextView, value: String) {
        if (value.isNotEmpty())
            textView.text = value
        else
            textView.text = "—"
    }
}