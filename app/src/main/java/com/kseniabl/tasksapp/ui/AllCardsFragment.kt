package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kseniabl.tasksapp.adapters.AllTasksAdapter
import com.kseniabl.tasksapp.adapters.FreelancersAdapter
import com.kseniabl.tasksapp.databinding.FragmentAllCardsBinding
import com.kseniabl.tasksapp.di.scopes.AllCardsScope
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.utils.findTopNavController
import com.kseniabl.tasksapp.viewmodels.AllCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AllCardsFragment: Fragment() {

    @Inject
    @AllCardsScope
    lateinit var linearLayoutManager: Provider<FlexboxLayoutManager>
    @Inject
    lateinit var allTasksAdapter: AllTasksAdapter
    @Inject
    lateinit var creatorsAdapter: FreelancersAdapter

    private val viewModel: AllCardsViewModel by viewModels()

    private var _binding: FragmentAllCardsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = linearLayoutManager.get()
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_AROUND

        viewModel.changeAdapter(viewModel.allCardsState.value.adapterValue ?: allTasksAdapter)

        binding.apply {
            allCardsRecycler.layoutManager = layoutManager
            allCardsRecycler.setHasFixedSize(true)
            allCardsRecycler.setItemViewCacheSize(20)
        }

        binding.apply {
            allCardsRecycler.layoutManager = layoutManager
            setupAllTasksRecyclerView(viewModel.allCardsState.value.cardsList)
            allCardsRecycler.setItemViewCacheSize(20)

            activeTasksButton.setOnClickListener { viewModel.changeAdapter(allTasksAdapter) }
            freelancersButton.setOnClickListener { viewModel.changeAdapter(creatorsAdapter) }

            searchText.addTextChangedListener {
                if (viewModel.allCardsState.value.adapterValue is AllTasksAdapter)
                    viewModel.onSearchQueryChanged(searchText.text.toString().lowercase())
                else
                    viewModel.onSearchQueryChangedCreators(searchText.text.toString().lowercase())
            }
        }

        viewModel.getCards()
        viewModel.getUsers()
        allTasksAdapter.setOnClickListener(viewModel)
        creatorsAdapter.setOnClickListener(viewModel)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.actionsTrigger.collect {
                        when (it) {
                            is AllCardsViewModel.UIActionsAllCards.OpenDetailsCard -> {
                                findTopNavController().navigate(
                                    TabsFragmentDirections.actionTabsFragmentToCardDetailsFragment(it.card)
                                )
                            }
                            is AllCardsViewModel.UIActionsAllCards.OpenFreelancerDetails -> {
                                findTopNavController().navigate(
                                    TabsFragmentDirections.actionTabsFragmentToFreelancerDetailsFragment(it.freelancer)
                                )
                            }
                        }
                    }
                }
                launch {
                    viewModel.creatorInfoData.collect {
                        if (viewModel.allCardsState.value.adapterValue is FreelancersAdapter) {
                            setupFreelancersRecyclerView(it)
                        }
                    }
                }
                launch {
                    viewModel.allCardsState.collect {
                        when (it.adapterValue) {
                            is AllTasksAdapter -> {
                                setupAllTasksRecyclerView(it.cardsList)
                            }
                            is FreelancersAdapter -> {
                                setupFreelancersRecyclerView(it.creatorList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupAllTasksRecyclerView(list: List<CardModel>) {
        if (binding.allCardsRecycler.adapter !is AllTasksAdapter) {
            binding.allCardsRecycler.adapter = allTasksAdapter
        }
        allTasksAdapter.submitList(list)
    }

    private fun setupFreelancersRecyclerView(list: List<FreelancerModel>) {
        if (binding.allCardsRecycler.adapter !is FreelancersAdapter) {
            binding.allCardsRecycler.adapter = creatorsAdapter
        }
        creatorsAdapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}