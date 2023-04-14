package com.kseniabl.tasksapp.viewmodels

import android.graphics.Bitmap
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

    private val _creatorCards = MutableStateFlow<ArrayList<CardModel>?>(null)
    val creatorCards = _creatorCards.asStateFlow()

    private var _allCards: ArrayList<CardModel>? = arrayListOf()
    private var _allCreatorInfo: ArrayList<UserModel>? = arrayListOf()

    private val _actionsTrigger = MutableSharedFlow<UIActionsAllCards>()
    val actionsTrigger = _actionsTrigger.asSharedFlow()

    private val _allCardsState = MutableStateFlow(AllCardsModel())
    val allCardsState = _allCardsState.asStateFlow()

    fun changeAdapter(adapter: AllCardsAdapterInterface) {
        _allCardsState.value = _allCardsState.value.copy(adapterValue = adapter)
    }

    fun getUsers() {
        viewModelScope.launch {
            userTokenDataStore.readToken.collect { token ->
                try {
                    val users = repository.getUsers(token).body() ?: arrayListOf()
                    _allCreatorInfo = users

                    _allCardsState.value = _allCardsState.value.copy(creatorList = users)
                } catch (exception: Exception) {
                    _stateChange.emit(UIActionsDetails.ShowSnackbar("Users cannot be load"))
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
                el.username.lowercase().contains(search) || el.specialization?.specialization?.lowercase()?.contains(search) == true
            } as ArrayList<UserModel>
            _allCardsState.value = _allCardsState.value.copy(creatorList = filterList)
        }
    }

    override fun onAddItemClick(item: CardModel) {
        viewModelScope.launch {
            _actionsTrigger.emit(UIActionsAllCards.OpenDetailsCard(item))
        }
    }

    override fun onAddItemClick(user: UserModel) {
        viewModelScope.launch {
            _actionsTrigger.emit(UIActionsAllCards.OpenFreelancerDetails(user))
        }
    }

    sealed class UIActionsAllCards {
        data class OpenDetailsCard(val card: CardModel): UIActionsAllCards()
        data class OpenFreelancerDetails(val user: UserModel): UIActionsAllCards()
    }

    sealed class UIActionsDetails {
        data class ShowSnackbar(val message: String): UIActionsDetails()
        object GoToDetails: UIActionsDetails()
    }


    /**
     Card Details
      **/

    private val _stateChange = MutableSharedFlow<UIActionsDetails>()
    val stateChange = _stateChange.asSharedFlow()

    private var position = -1

    private val _userAllData = MutableStateFlow<UserModel?>(null)
    val userAllData = _userAllData.asStateFlow()

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
        val userId = user.id
        if (userId.isEmpty()) {
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
                _userAllData.value = user
            } catch (exception: Exception) {
                _stateChange.emit(UIActionsDetails.ShowSnackbar("We couldn't get user"))
            }
        }
    }

    override fun onSelectChosen(position: Int) {
        this.position = position
    }

}