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

    private val _adapterValue = MutableLiveData<AllCardsAdapterInterface>(null)
    val adapterValue: LiveData<AllCardsAdapterInterface> = _adapterValue

    private val _cards = MutableStateFlow<ArrayList<CardModel>?>(null)
    val cards = _cards.asStateFlow()

    private val _bookedInfo = MutableStateFlow<Resource<Response<ArrayList<BookInfoModel>>>?>(null)
    val bookedInfo = _bookedInfo.asStateFlow()

    private val _openDetailsCardsTrigger = MutableSharedFlow<CardModel>()
    val openDetailsCardsTrigger = _openDetailsCardsTrigger.asSharedFlow()

    private val _openDetailsBookInfo = MutableSharedFlow<BookInfoModel>()
    val openDetailsBookInfo = _openDetailsBookInfo.asSharedFlow()

    private val _id = MutableStateFlow<String?>(null)
    val id = _id.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = userDataStore.readUser.first().userInfo?.id
            _id.emit(userId)
        }
    }

    fun changeAdapter(adapter: AllCardsAdapterInterface) {
        _adapterValue.value = adapter
    }

    fun getCards() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            try {
                val cards = repository.getBookedCards(token).body()
                _cards.emit(cards)
            } catch (exception: Exception) {
                Log.e("qqq", "load users cards error: ${exception.message}")
            }
        }
    }

    fun getBookedInfo() {
        viewModelScope.launch {
            val token = userTokenDataStore.readToken.first()
            _bookedInfo.emit(Resource.Loading())
            try {
                _bookedInfo.emit(Resource.Success(data = repository.getBookedInfoCards(token)))
            } catch (exception: Exception) {
                _bookedInfo.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
            }
        }
    }

    override fun onAddItemClick(item: CardModel) {
        viewModelScope.launch {
            _openDetailsCardsTrigger.emit(item)
        }
    }

    override fun onAddItemClick(item: BookInfoModel) {
        viewModelScope.launch {
            _openDetailsBookInfo.emit(item)
        }
    }
}