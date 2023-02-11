package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.util.Log
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
import com.kseniabl.tasksapp.adapters.*
import com.kseniabl.tasksapp.databinding.FragmentBookedCardsBinding
import com.kseniabl.tasksapp.di.BookCardsAnnotation
import com.kseniabl.tasksapp.models.BookInfoModel
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.models.FreelancerModel
import com.kseniabl.tasksapp.models.UserModel
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.findTopNavController
import com.kseniabl.tasksapp.viewmodels.BookedCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class BookedCardsFragment: Fragment() {

    private var _binding: FragmentBookedCardsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var bookCardsAdapter: BookCardsAdapter

    @Inject
    lateinit var bookedUsersCardsAdapter: BookedUsersCardsAdapter

    @Inject
    @BookCardsAnnotation
    lateinit var linearLayoutManager: Provider<LinearLayoutManager>

    private val viewModel: BookedCardsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookedCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.changeAdapter(viewModel.bookedCardsState.value.adapterValue ?: bookCardsAdapter)

        binding.apply {
            bookedCardsRecycler.layoutManager = linearLayoutManager.get()
            bookedCardsRecycler.setHasFixedSize(true)
            bookedCardsRecycler.setItemViewCacheSize(20)

            bookedByYouButton.setOnClickListener { viewModel.changeAdapter(bookCardsAdapter) }
            bookedYourCardsButton.setOnClickListener { viewModel.changeAdapter(bookedUsersCardsAdapter) }
        }
        viewModel.getCards()
        viewModel.getBookedInfo()
        bookCardsAdapter.setOnClickListener(viewModel)
        bookedUsersCardsAdapter.setOnClickListener(viewModel)

        setupBookCardsAdapterRecyclerView(viewModel.bookedCardsState.value.cardsList)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.id.collect {
                        bookCardsAdapter.userId = it
                    }
                }
                launch {
                    viewModel.bookedCardsState.collect {
                        when(it.adapterValue) {
                            is BookCardsAdapter -> {
                                setupBookCardsAdapterRecyclerView(it.cardsList)
                            }
                            is BookedUsersCardsAdapter -> {
                                setupBookedUsersCardsRecyclerView(it.bookedInfoList)
                            }
                        }
                    }
                }
                launch {
                    viewModel.uiActionsTrigger.collect {
                        when(it) {
                            is BookedCardsViewModel.UIActions.OpenDetailsCard -> {
                                findTopNavController().navigate(
                                    TabsFragmentDirections.actionTabsFragmentToCardDetailsFragment(it.card)
                                )
                            }
                            is BookedCardsViewModel.UIActions.OpenDetailsBookInfo -> {
                                findTopNavController().navigate(
                                    TabsFragmentDirections.actionTabsFragmentToFreelancerDetailsFragment(
                                        FreelancerModel(UserModel(it.item.userId))
                                    )
                                )
                            }
                            is BookedCardsViewModel.UIActions.ShowSnackbar -> {
                                Snackbar.make(view, it.message, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupBookCardsAdapterRecyclerView(list: List<CardModel>) {
        binding.bookedCardsRecycler.adapter = bookCardsAdapter
        bookCardsAdapter.submitList(list)
    }

    private fun setupBookedUsersCardsRecyclerView(list: List<BookInfoModel>) {
        binding.bookedCardsRecycler.adapter = bookedUsersCardsAdapter
        bookedUsersCardsAdapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}