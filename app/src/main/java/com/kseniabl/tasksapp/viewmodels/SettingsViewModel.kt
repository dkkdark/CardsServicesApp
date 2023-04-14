package com.kseniabl.tasksapp.viewmodels

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.HelperFunctions
import com.kseniabl.tasksapp.utils.HelperFunctions.getImageBitmap
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: Repository,
    private val userTokenDataStore: UserTokenDataStore,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _stateChange = MutableSharedFlow<UIActions>()
    val stateChange = _stateChange.asSharedFlow()

    private val _profileImage = MutableStateFlow<Bitmap?>(null)
    val profileImage = _profileImage.asStateFlow()


    fun updateSpecialization(spec: String, desc: String) {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            val user = userDataStore.readUser.first()
            val userId = user.id
            if (userId.isEmpty()) {
                Log.e("qqq", "User id is empty")
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
                    userDataStore.writeUser(user.copy(
                        specialization = Specialization(specId, spec, desc)
                    ))
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
            val userId = user.id
            if (userId.isEmpty()) {
                Log.e("qqq", "User id is empty")
            }
            else {
                try {
                    val addInfId = if (user.addInf?.id.isNullOrEmpty()) {
                        HelperFunctions.generateRandomKey()
                    } else {
                        user.addInf!!.id
                    }
                    repository.updateAddInf(token, UpdateAddInfModel(userId, addInfId, description, country, city, typeOfWork))
                    userDataStore.writeUser(user.copy(
                        addInf = AdditionalInfo(addInfId, description, country, city, typeOfWork)
                    ))
                } catch (exception: Exception) {
                    Log.e("qqq", "Additional Info wasn't upload. ${exception.message}")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userTokenDataStore.saveToken("")
            userDataStore.writeUser(UserModel())
        }
    }

    fun becomeCreator() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            val user = userDataStore.readUser.first()

            if (!user.specialization?.specialization.isNullOrEmpty() && !user.specialization?.description.isNullOrEmpty() &&
                !user.addInf?.description.isNullOrEmpty() && !user.addInf?.country.isNullOrEmpty() && !user.addInf?.city.isNullOrEmpty()
                && !user.addInf?.typeOfWork.isNullOrEmpty() && user.id.isNotEmpty() && user.username.isNotEmpty()
            ) {
                try {
                    repository.updateCreatorState(token, UpdateCreatorStatus(user.id, user.username))
                    userDataStore.writeUser(user.copy(
                        creator = true,
                        rolename = "creator"
                    ))
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

    fun getImage() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            val user = userDataStore.readUser.first()

            var image: ImageModel? = null
            if (user.img == null) {
                try {
                    image = repository.getImage(token).body()
                    image?.let { saveProfileImage(it) }
                } catch (e: Exception) {
                    Log.e("qqq", "Image doesn't exist or cannot be load")
                }
            }
            else {
                image = user.img
            }
            image?.content?.let {
                val bitmap = getImageBitmap(it)
                _profileImage.value = bitmap
            }
        }
    }

    fun uploadImage(stream: InputStream, extension: String) {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()

            val b = stream.readBytes()
            val imageContent = Base64.encodeToString(b, Base64.DEFAULT)

            val filename = "userPic_${token}.${extension}"

            val part = MultipartBody.Part.createFormData(
                "pic", filename, RequestBody.create(
                    MediaType.parse("image/*"),
                    b
                )
            )
            try {
                val response = repository.uploadImage(token, part)
                if (response.isSuccessful) {
                    saveProfileImage(ImageModel(filename, imageContent, "image/$extension"))
                }
            } catch (e: Exception) {
                _stateChange.emit(UIActions.ShowSnackbar("Sorry, image wasn't saved"))
            }
        }
    }

    private fun saveProfileImage(image: ImageModel) {
        viewModelScope.launch {
            val user = userDataStore.readUser.first()
            userDataStore.writeUser(
                user.copy(
                    img = image
                )
            )
        }
    }

    sealed class UIActions {
        data class ShowSnackbar(val message: String): UIActions()
        object SetCreatorSettings: UIActions()
    }
}