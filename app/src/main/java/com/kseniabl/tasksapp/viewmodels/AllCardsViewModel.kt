package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import com.kseniabl.tasksapp.adapters.DatesDetailAdapter
import com.kseniabl.tasksapp.adapters.FreelancersAdapter
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStoreInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllCardsViewModel @Inject constructor(
    private val repository: Repository,
    private val userTokenDataStore: UserTokenDataStoreInterface,
    val userDataStore: UserDataStore
): ViewModel(), AddTasksAdapter.Listener, FreelancersAdapter.Listener, DatesDetailAdapter.Listener {

    private val _adapterCreatorsList = MutableStateFlow<Resource<ArrayList<UserModel>>?>(null)
    private val _specializationDate = MutableStateFlow<Resource<ArrayList<Specialization>>?>(null)
    private val _addInf = MutableStateFlow<Resource<ArrayList<AdditionalInfo>>?>(null)

    private val _creatorCards = MutableStateFlow<ArrayList<CardModel>?>(null)
    val creatorCards = _creatorCards.asStateFlow()

    private var _allCards: ArrayList<CardModel>? = arrayListOf()
    private var _allCreatorInfo: ArrayList<FreelancerModel>? = arrayListOf()

    private val _actionsTrigger = MutableSharedFlow<UIActionsAllCards>()
    val actionsTrigger = _actionsTrigger.asSharedFlow()

    private val _allCardsState = MutableStateFlow(AllCardsModel())
    val allCardsState = _allCardsState.asStateFlow()

    val creatorInfoData = combine(
        _adapterCreatorsList,
        _specializationDate,
        _addInf
    ) { list, spec, addInf ->
        var arrayList = arrayListOf<FreelancerModel>()
        if (list is Resource.Success<*> && spec is Resource.Success<*> && addInf is Resource.Success<*>) {
            if (!list.data.isNullOrEmpty() && !spec.data.isNullOrEmpty() && !addInf.data.isNullOrEmpty()) {
                arrayList = list.data.mapIndexed { idx, el -> FreelancerModel(el, spec.data[idx], addInf.data[idx]) } as ArrayList<FreelancerModel>
                _allCreatorInfo = arrayList
                _allCardsState.value = _allCardsState.value.copy(creatorList = arrayList)
            }
        }
        if (list is Resource.Error<*> || spec is Resource.Error<*>) {
            Log.e("qqq", "${list?.message}")
        }
        arrayList
    }

    fun changeAdapter(adapter: AllCardsAdapterInterface) {
        _allCardsState.value = _allCardsState.value.copy(adapterValue = adapter)
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
                        _adapterCreatorsList.value = (Resource.Error("Size is not the same ${users.size} ${specList.size}"))
                    }
                    else {
                        _specializationDate.value = (Resource.Success(specList))
                        _addInf.value = (Resource.Success(addInfList))
                        _adapterCreatorsList.value = (Resource.Success(users))
                    }

                } catch (exception: Exception) {
                    if (exception.message.isNullOrEmpty()) {
                        _adapterCreatorsList.value = (Resource.Error("Some error occurred"))
                    }
                    else {
                        _adapterCreatorsList.value = (Resource.Error(exception.message!!))
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
                _creatorCards.value = cards
            } catch (exception: Exception) {
                Log.e("qqq", "load users cards error: ${exception.message}")
            }
        }
    }

    fun getCards() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                val cards = repository.getTasks(token).body()
                _allCards = cards

                _allCardsState.value = _allCardsState.value.copy(cardsList = cards ?: arrayListOf())
            } catch (exception: Exception) {
                Log.e("qqq", "load users cards error: ${exception.message}")
            }
        }
    }

    fun onSearchQueryChanged(search: String) {
        viewModelScope.launch {
            if (search.isEmpty()) {
                _allCardsState.value = _allCardsState.value.copy(cardsList = _allCards ?: arrayListOf())
                return@launch
            }
            val filterList = _allCards?.filter { el ->
                el.tags.map { it.name.lowercase() }.contains(search) || el.title.lowercase().contains(search)
            } as ArrayList<CardModel>
            _allCardsState.value = _allCardsState.value.copy(cardsList = filterList )
        }
    }

    fun onSearchQueryChangedCreators(search: String) {
        viewModelScope.launch {
            if (search.isEmpty()) {
                _allCardsState.value = _allCardsState.value.copy(creatorList = _allCreatorInfo ?: arrayListOf())
                return@launch
            }
            val filterList = _allCreatorInfo?.filter { el ->
                el.userInfo?.username?.lowercase()?.contains(search) == true || el.specialization?.specialization?.lowercase()?.contains(search) == true
            } as ArrayList<FreelancerModel>
            _allCardsState.value = _allCardsState.value.copy(creatorList = filterList)
        }
    }

    override fun onAddItemClick(item: CardModel) {
        viewModelScope.launch {
            _actionsTrigger.emit(UIActionsAllCards.OpenDetailsCard(item))
        }
    }

    override fun onAddItemClick(item: FreelancerModel) {
        viewModelScope.launch {
            _actionsTrigger.emit(UIActionsAllCards.OpenFreelancerDetails(item))
        }
    }

    sealed class UIActionsAllCards {
        data class OpenDetailsCard(val card: CardModel): UIActionsAllCards()
        data class OpenFreelancerDetails(val freelancer: FreelancerModel): UIActionsAllCards()
    }

    sealed class UIActionsDetails {
        data class ShowSnackbar(val message: String): UIActionsDetails()
        object GoToDetails: UIActionsDetails()
    }


    /**
     Card Details
      **/

    private val _user = MutableStateFlow<Resource<UserModel>?>(null)
    private val _specializationOneUser = MutableStateFlow<Resource<Specialization>?>(null)
    private val _addInfOneUser = MutableStateFlow<Resource<AdditionalInfo>?>(null)

    private val _stateChange = MutableSharedFlow<UIActionsDetails>()
    val stateChange = _stateChange.asSharedFlow()

    private var position = -1

    val userAllData = combine(
        _user,
        _specializationOneUser,
        _addInfOneUser
    ) { user, spec, addInfOneUser ->
        var userDate: FreelancerModel? = null
        if (user is Resource.Success<*> && spec is Resource.Success<*> && addInfOneUser is Resource.Success<*>) {
            userDate = FreelancerModel(user.data, spec.data, addInfOneUser.data)
        }
        if (user is Resource.Error<*>) {
            _stateChange.emit(UIActionsDetails.ShowSnackbar(user.message ?: "User was not load"))
            userDate = null
        }
        userDate
    }

    fun onRespondToTaskButtonClick(bookDate: ArrayList<BookDate>) {
        viewModelScope.launch {
            if (position != -1) {
                val id = bookDate[position].id
                updateBookDateUser(id)
            } else
                _stateChange.emit(UIActionsDetails.ShowSnackbar("Please choose date to book"))
        }
    }

    private suspend fun updateBookDateUser(bookDateId: String) {
        val token = userTokenDataStore.readToken.first()
        val user = userDataStore.readUser.first()
        val userId = user.userInfo?.id
        if (userId.isNullOrEmpty()) {
            Log.e("qqq", "User id is null")
        }
        else {
            try {
                repository.updateBookDateUser(token, UpdateBookDateUser(userId, bookDateId))
                _stateChange.emit(UIActionsDetails.ShowSnackbar("You booked successfully"))
                _stateChange.emit(UIActionsDetails.GoToDetails)
            } catch (e: Exception) {
                _stateChange.emit(UIActionsDetails.ShowSnackbar("Something went wrong"))
                Log.e("qqq", "Error updateBookDateUser: ${e.message}")
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
                    _user.value = Resource.Success(user)
                    _specializationOneUser.value = Resource.Success(spec)
                    _addInfOneUser.value = Resource.Success(addInf)
                }
                else {
                    _user.value = (Resource.Error("Some object is null"))
                }

            } catch (exception: Exception) {
                _user.value = (Resource.Error(exception.message ?: "Some error occurred"))
            }
        }
    }

    override fun onSelectChosen(position: Int) {
        this.position = position
    }

}