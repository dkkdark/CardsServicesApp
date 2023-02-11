package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.databinding.FragmentAddCardsBinding
import com.kseniabl.tasksapp.di.AddCardsScope
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.models.FreelancerModel
import com.kseniabl.tasksapp.utils.HelperFunctions.generateRandomKey
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.utils.findTopNavController
import com.kseniabl.tasksapp.viewmodels.AddCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddCardsFragment: Fragment() {

    private var _binding: FragmentAddCardsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tasksAdapter: AddTasksAdapter

    @Inject
    @AddCardsScope
    lateinit var linearLayoutManager: Provider<LinearLayoutManager>

    private val viewModel: AddCardsViewModel by viewModels()

    @Inject
    lateinit var userDataStore: UserDataStore

    private var user: FreelancerModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            addCardsRecycler.layoutManager = linearLayoutManager.get()
            addCardsRecycler.adapter = tasksAdapter
            addCardsRecycler.setItemViewCacheSize(20)

            activeButton.setOnClickListener { viewModel.changeList(true) }
            draftButton.setOnClickListener { viewModel.changeList(false) }
            addCardFab.setOnClickListener {
                viewModel.isUserCreator()
            }

            viewModel.getCards()
            tasksAdapter.setOnClickListener(viewModel)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.actionsTrigger.collect {
                        when (it) {
                            is AddCardsViewModel.UIActions.GoToDialog -> {
                                showCreateTaskDialog(it.card)
                            }
                            is AddCardsViewModel.UIActions.ShowSnackbar -> {
                                Snackbar.make(view, it.message, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.adapterList.collect {
                        val cards = viewModel.cards.value
                        if (!cards.isNullOrEmpty())
                            setupTasksRecyclerView(it, cards)
                    }
                }
                launch {
                    viewModel.cards.collect {
                        setupTasksRecyclerView(viewModel.adapterList.value, it)
                    }
                }
                launch {
                    user = userDataStore.readUser.first()
                }
            }
        }

        observeBackStackEntry()
    }

    private fun setupTasksRecyclerView(active: Boolean, array: ArrayList<CardModel>?) {
        val list = array?.filter { if (active) it.active else !it.active}
        CoroutineScope(Dispatchers.Main).launch { tasksAdapter.submitList(list ?: arrayListOf()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeBackStackEntry() {
        val liveDataRes = findTopNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<CardModel?>("CARD_BACK")
        liveDataRes?.observe(viewLifecycleOwner) { card ->
            if (card?.title != null && card?.description != null) {
                if (card.createTime == 0L)
                    card.createTime = Calendar.getInstance().time.time
                if (card.id.isEmpty()) {
                    card.id = generateRandomKey()
                }
                if (card.user_id.isEmpty() && user?.userInfo != null) {
                    card.user_id = user!!.userInfo!!.id
                }

                viewModel.updateCard(
                    CardModel(
                        card.id, card.title, card.description, card.cost,
                        card.agreement, card.prepayment, card.tags, card.bookDates,
                        card.createTime, card.active, card.user_id
                    )
                )
            }
        }
    }

    private fun showCreateTaskDialog(item: CardModel? = null) {
        findTopNavController().navigate(
            TabsFragmentDirections.actionTabsFragmentToCreateAndChangeTaskFragment(item)
        )
    }
}