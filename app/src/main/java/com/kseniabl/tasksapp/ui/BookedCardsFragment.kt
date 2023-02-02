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

        viewModel.changeAdapter(viewModel.adapterValue.value ?: bookCardsAdapter)

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

        setupBookCardsAdapterRecyclerView(viewModel.cards.value ?: arrayListOf())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.adapterValue.collect {
                        if (it is BookCardsAdapter)
                            setupBookCardsAdapterRecyclerView(viewModel.cards.value ?: arrayListOf())
                        if (it is BookedUsersCardsAdapter) {
                            setupBookedUsersCardsRecyclerView(viewModel.bookedInfo.value?.data?.body() ?: arrayListOf())
                        }
                    }
                }
                launch {
                    viewModel.id.collect {
                        bookCardsAdapter.userId = it
                    }
                }
                launch {
                    viewModel.cards.collect {
                        if (it != null && viewModel.adapterValue.value is BookCardsAdapter) {
                            val list = arrayListOf<CardModel>()
                            list.addAll(it)
                            setupBookCardsAdapterRecyclerView(list)
                        }
                    }
                }
                launch {
                    viewModel.bookedInfo.collect { value ->
                        if (viewModel.adapterValue.value is BookedUsersCardsAdapter) {
                            if (value is Resource.Loading<*>) {
                                // TODO
                            }
                            if (value is Resource.Success<*>) {
                                setupBookedUsersCardsRecyclerView(value.data?.body() ?: arrayListOf())
                            }
                            if (value is Resource.Error<*>) {
                                Log.e("qqq", "Error: ${value.message}")
                            }
                        }
                    }
                }
                launch {
                    viewModel.openDetailsCardsTrigger.collect {
                        findTopNavController().navigate(
                            TabsFragmentDirections.actionTabsFragmentToCardDetailsFragment(it)
                        )
                    }
                }
                launch {
                    viewModel.openDetailsBookInfo.collect {
                        findTopNavController().navigate(
                            TabsFragmentDirections.actionTabsFragmentToFreelancerDetailsFragment(
                                FreelancerModel(UserModel(it.userId))
                            )
                        )
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