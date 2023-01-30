package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import com.kseniabl.tasksapp.adapters.AllTasksAdapter
import com.kseniabl.tasksapp.adapters.FreelancersAdapter
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStoreInterface
import com.kseniabl.tasksapp.view.TagsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AllCardsViewModel @Inject constructor(
    private val repository: Repository,
    private val userTokenDataStore: UserTokenDataStoreInterface,
    val userDataStore: UserDataStore
): ViewModel(), AddTasksAdapter.Listener, FreelancersAdapter.Listener {

    private val _adapterValue = MutableLiveData<AllCardsAdapterInterface>(AllTasksAdapter())
    val adapterValue: LiveData<AllCardsAdapterInterface> = _adapterValue

    private val _adapterTasksList = MutableStateFlow<Resource<Response<ArrayList<CardModel>>>?>(null)
    val adapterTasksList = _adapterTasksList.asStateFlow()

    private val _adapterCreatorsList = MutableStateFlow<Resource<ArrayList<UserModel>>?>(null)
    private val _specializationDate = MutableStateFlow<Resource<ArrayList<Specialization>>?>(null)
    private val _addInf = MutableStateFlow<Resource<ArrayList<AdditionalInfo>>?>(null)

    private val _openDetailsTrigger = MutableSharedFlow<CardModel>()
    val openDetailsTrigger = _openDetailsTrigger.asSharedFlow()

    private val _openDetailsFreelancer = MutableSharedFlow<FreelancerModel>()
    val openDetailsFreelancer = _openDetailsFreelancer.asSharedFlow()

    private val _creatorCards = MutableStateFlow<ArrayList<CardModel>?>(null)
    val creatorCards = _creatorCards.asStateFlow()

    private var _allCards: ArrayList<CardModel>? = arrayListOf()
    private val _matchedCards = MutableStateFlow<ArrayList<CardModel>?>(null)
    var matchedCards = _matchedCards.asStateFlow()

    val creatorInfoData = combine(
        _adapterCreatorsList,
        _specializationDate,
        _addInf
    ) { list, spec, addInf ->
        var arrayList = arrayListOf<FreelancerModel>()
        if (list is Resource.Success<*> && spec is Resource.Success<*> && addInf is Resource.Success<*>) {
            if (!list.data.isNullOrEmpty() && !spec.data.isNullOrEmpty() && !addInf.data.isNullOrEmpty()) {
                arrayList = list.data.mapIndexed { idx, el -> FreelancerModel(el, spec.data[idx], addInf.data[idx]) } as ArrayList<FreelancerModel>
                _creatorInfoHolder.emit(arrayList)
            }
        }
        if (list is Resource.Error<*> || spec is Resource.Error<*>) {
            Log.e("qqq", "${list?.message}")
        }
        arrayList
    }

    private var _allCreatorInfo: ArrayList<FreelancerModel>? = arrayListOf()
    private val _creatorInfoHolder = MutableStateFlow<ArrayList<FreelancerModel>?>(arrayListOf())
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
                    val addInfList = arrayListOf<AdditionalInfo>()
                    for (u in users) {
                        val spec = repository.getSpec(token, IdBody(u.specialization)).body()
                        val addInf = repository.getAddInf(token, IdBody(u.addInf)).body()
                        if (spec != null)
                            specList.add(spec)
                        if (addInf != null)
                            addInfList.add(addInf)
                    }

                    if (users.size != specList.size) {
                        _adapterCreatorsList.emit(Resource.Error("Size is not the same ${users.size} ${specList.size}"))
                    }
                    else {
                        _specializationDate.emit(Resource.Success(specList))
                        _addInf.emit(Resource.Success(addInfList))
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

    fun getCardsById(id: String) {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                var cards = repository.getCardsById(token, id).body()
                cards = cards?.filter { it.active } as ArrayList<CardModel>?
                _creatorCards.emit(cards)
            } catch (exception: Exception) {
                Log.e("qqq", "load users cards error: ${exception.message}")
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

    fun setCreatorsList(list: ArrayList<FreelancerModel>?) {
        _allCreatorInfo = list
        viewModelScope.launch {
            _creatorInfoHolder.emit(_allCreatorInfo)
        }
    }

    fun setCardsList(list: ArrayList<CardModel>?) {
        _allCards = list
        viewModelScope.launch {
            _matchedCards.emit(_allCards)
        }
    }

    fun onSearchQueryChanged(search: String) {
        viewModelScope.launch {
            if (search.isEmpty()) {
                _matchedCards.emit(_allCards)
                return@launch
            }
            val filterList = _allCards?.filter { el ->
                el.tags.contains(TagsModel(search)) || el.title.lowercase().contains(search)
            } as ArrayList<CardModel>
            _matchedCards.emit(filterList)
        }
    }

    fun onSearchQueryChangedCreators(search: String) {
        viewModelScope.launch {
            if (search.isEmpty()) {
                _creatorInfoHolder.emit(_allCreatorInfo)
                return@launch
            }
            val filterList = _allCreatorInfo?.filter { el ->
                el.userInfo?.username?.lowercase()?.contains(search) == true || el.specialization?.specialization?.lowercase()?.contains(search) == true
            } as ArrayList<FreelancerModel>
            _creatorInfoHolder.emit(filterList)
        }
    }

    override fun onAddItemClick(item: CardModel) {
        viewModelScope.launch {
            _openDetailsTrigger.emit(item)
        }
    }

    override fun onAddItemClick(item: FreelancerModel) {
        viewModelScope.launch {
            _openDetailsFreelancer.emit(item)
        }
    }


    /**
     Card Details
      **/

    private val _user = MutableStateFlow<Resource<UserModel>?>(null)
    private val _specializationOneUser = MutableStateFlow<Resource<Specialization>?>(null)
    private val _addInfOneUser = MutableStateFlow<Resource<AdditionalInfo>?>(null)

    private val _stateChange = MutableSharedFlow<Resource<Response<Void>>>()
    val stateChange = _stateChange.asSharedFlow()

    val userAllData = combine(
        _user,
        _specializationOneUser,
        _addInfOneUser
    ) { user, spec, addInfOneUser ->
        var userDate: Resource<FreelancerModel>? = null
        if (user is Resource.Success<*> && spec is Resource.Success<*> && addInfOneUser is Resource.Success<*>) {
            userDate = Resource.Success(FreelancerModel(user.data, spec.data, addInfOneUser.data))
        }
        if (user is Resource.Error<*>) {
            userDate = Resource.Error(user.message)
        }
        userDate
    }

    fun updateBookDateUser(bookDateId: String) {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            val user = userDataStore.readUser.first()
            val userId = user.userInfo?.id
            if (userId.isNullOrEmpty()) {
                Log.e("qqq", "User id is null")
            }
            else {
                try {
                    _stateChange.emit(Resource.Success(repository.updateBookDateUser(
                        token,
                        UpdateBookDateUser(userId, bookDateId)
                    )))
                } catch (e: Exception) {
                    Log.e("qqq", "Error updateBookDateUser: ${e.message}")
                }
            }
        }
    }

    fun getCreator(id: String) {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                val user = repository.getUserById(token, id).body()
                var spec: Specialization? = null
                var addInf: AdditionalInfo? = null
                if (user != null) {
                    if (user.specialization.isNotEmpty())
                        spec = repository.getSpec(token, IdBody(user.specialization)).body()
                    if (user.addInf.isNotEmpty())
                        addInf = repository.getAddInf(token, IdBody(user.addInf)).body()
                    _user.emit(Resource.Success(user))
                    _specializationOneUser.emit(Resource.Success(spec))
                    _addInfOneUser.emit(Resource.Success(addInf))
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