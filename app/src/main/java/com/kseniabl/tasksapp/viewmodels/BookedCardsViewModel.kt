package com.kseniabl.tasksapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import com.kseniabl.tasksapp.adapters.AllTasksAdapter
import com.kseniabl.tasksapp.adapters.BookCardsAdapter
import com.kseniabl.tasksapp.adapters.BookedUsersCardsAdapter
import com.kseniabl.tasksapp.models.BookInfoModel
import com.kseniabl.tasksapp.models.BookedCardsModel
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.utils.UserTokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class BookedCardsViewModel @Inject constructor(
    private val repository: Repository,
    private val userDataStore: UserDataStore,
    private val userTokenDataStore: UserTokenDataStore
): ViewModel(), BookCardsAdapter.Listener, BookedUsersCardsAdapter.Listener {

    private val _bookedCardsState = MutableStateFlow(BookedCardsModel())
    val bookedCardsState = _bookedCardsState.asStateFlow()

    private val _uiActionsTrigger = MutableSharedFlow<UIActions>()
    val uiActionsTrigger = _uiActionsTrigger.asSharedFlow()

    private val _id = MutableStateFlow<String?>(null)
    val id = _id.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = userDataStore.readUser.first().id
            _id.emit(userId)
        }
    }

    fun changeAdapter(adapter: AllCardsAdapterInterface) {
        _bookedCardsState.value = _bookedCardsState.value.copy(adapterValue = adapter)
    }

    fun getCards() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                val cards = repository.getBookedCards(token).body()
                _bookedCardsState.value = _bookedCardsState.value.copy(cardsList = cards ?: arrayListOf())
            } catch (exception: Exception) {
                _uiActionsTrigger.emit(UIActions.ShowSnackbar("Problem occurred while data loading"))
                Log.e("qqq", "load users cards error: ${exception.message}")
            }
        }
    }

    fun getBookedInfo() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                val bookedInfo = repository.getBookedInfoCards(token).body()
                _bookedCardsState.value = _bookedCardsState.value.copy(bookedInfoList = bookedInfo ?: arrayListOf())
            } catch (exception: Exception) {
                _uiActionsTrigger.emit(UIActions.ShowSnackbar("Problem occurred while data loading"))
                Log.e("qqq", "${exception.message}")
            }
        }
    }

    override fun onAddItemClick(item: CardModel) {
        viewModelScope.launch {
            _uiActionsTrigger.emit(UIActions.OpenDetailsCard(item))
        }
    }

    override fun onAddItemClick(item: BookInfoModel) {
        viewModelScope.launch {
            _uiActionsTrigger.emit(UIActions.OpenDetailsBookInfo(item))
        }
    }

    sealed class UIActions {
        data class ShowSnackbar(val message: String): UIActions()
        data class OpenDetailsCard(val card: CardModel): UIActions()
        data class OpenDetailsBookInfo(val item: BookInfoModel): UIActions()
    }
}