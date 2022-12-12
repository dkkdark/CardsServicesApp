package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import com.kseniabl.tasksapp.adapters.AllTasksAdapter
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserTokenDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStoreInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AllCardsViewModel @Inject constructor(
    private val repository: Repository,
    private val userTokenDataStore: UserTokenDataStoreInterface
): ViewModel() {

    private val _adapterValue = MutableLiveData<AllCardsAdapterInterface>(AllTasksAdapter())
    val adapterValue: LiveData<AllCardsAdapterInterface> = _adapterValue

    private val _adapterTasksList = MutableStateFlow<Resource<Response<ArrayList<CardModel>>>?>(null)
    val adapterTasksList = _adapterTasksList.asStateFlow()

    private val _adapterCreatorsList = MutableStateFlow<Resource<ArrayList<UserModel>>?>(null)
    private val _specializationDate = MutableStateFlow<Resource<ArrayList<Specialization>>?>(null)

    val creatorInfoData = combine(
        _adapterCreatorsList,
        _specializationDate
    ) { list, spec ->
        var arrayList = arrayListOf<FreelancerModel>()
        if (list is Resource.Success<*> && spec is Resource.Success<*>) {
            if (!list.data.isNullOrEmpty() && !spec.data.isNullOrEmpty()) {
                arrayList = list.data.mapIndexed { idx, el -> FreelancerModel(el, spec.data[idx]) } as ArrayList<FreelancerModel>
                _creatorInfoHolder.emit(arrayList)
            }
        }
        if (list is Resource.Error<*> || spec is Resource.Error<*>) {
            Log.e("qqq", "${list?.message}")
        }
        arrayList
    }

    private val _creatorInfoHolder = MutableStateFlow<ArrayList<FreelancerModel>>(arrayListOf())
    val creatorInfoHolder = _creatorInfoHolder.asStateFlow()

    private val _dialogsTrigger = MutableSharedFlow<String>()
    val dialogsTrigger: SharedFlow<String> = _dialogsTrigger


    fun changeAdapter(adapter: AllCardsAdapterInterface) {
        _adapterValue.value = adapter
    }

    fun getUsers() {
        viewModelScope.launch {
            userTokenDataStore.readToken.collect { token ->
                try {
                    val users = repository.getUsers(token).body() ?: arrayListOf()
                    val specList = arrayListOf<Specialization>()
                    for (u in users) {
                        val spec = repository.getSpec(token, IdBody(u.specialization)).body()
                        if (spec != null)
                            specList.add(spec)
                    }

                    if (users.size != specList.size) {
                        _adapterCreatorsList.emit(Resource.Error("Size is not the same $users $specList"))
                    }
                    else {
                        _specializationDate.emit(Resource.Success(specList))
                        _adapterCreatorsList.emit(Resource.Success(users))
                    }

                } catch (exception: Exception) {
                    if (exception.message.isNullOrEmpty()) {
                        _adapterCreatorsList.emit(Resource.Error("Some error occurred"))
                    }
                    else {
                        _adapterCreatorsList.emit(Resource.Error(exception.message!!))
                    }
                }
            }
        }
    }

    fun getCards() {
        viewModelScope.launch {
            userTokenDataStore.readToken.collect { token ->
                _adapterTasksList.emit(Resource.Loading())
                try {
                    _adapterTasksList.emit(Resource.Success(data = repository.getTasks(token)))
                } catch (exception: Exception) {
                    _adapterTasksList.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
                }
            }
        }
    }
}