package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.adapters.AllTasksAdapter
import com.kseniabl.tasksapp.adapters.FreelancersAdapter
import com.kseniabl.tasksapp.databinding.FragmentAllCardsBinding
import com.kseniabl.tasksapp.di.AllCardsScope
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.utils.Resource
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

        viewModel.changeAdapter(viewModel.adapterValue.value ?: allTasksAdapter)

        binding.apply {
            allCardsRecycler.layoutManager = layoutManager
            allCardsRecycler.setHasFixedSize(true)
            allCardsRecycler.setItemViewCacheSize(20)
        }

        binding.apply {
            allCardsRecycler.layoutManager = layoutManager
            setupAllTasksRecyclerView(viewModel.adapterTasksList.value ?: arrayListOf())
            allCardsRecycler.setItemViewCacheSize(20)

            activeTasksButton.setOnClickListener { viewModel.changeAdapter(allTasksAdapter) }
            freelancersButton.setOnClickListener { viewModel.changeAdapter(creatorsAdapter) }

            searchText.addTextChangedListener {
                if (viewModel.adapterValue.value is AllTasksAdapter)
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
                    viewModel.adapterValue.collect {
                        if (it is AllTasksAdapter)
                            setupAllTasksRecyclerView(viewModel.adapterTasksList.value ?: arrayListOf())
                        if (it is FreelancersAdapter) {
                            setupFreelancersRecyclerView(viewModel.creatorInfoHolder.value ?: arrayListOf())
                        }
                    }
                }
                launch {
                    viewModel.dialogsTrigger.collect {
                        callSnackbar(view, it)
                    }
                }
                launch {
                    viewModel.adapterTasksList.collect { value ->
                        if (viewModel.adapterValue.value is AllTasksAdapter) {
                            setupAllTasksRecyclerView(value ?: arrayListOf())
                            viewModel.setCardsList(value)
                        }
                    }
                }
                launch {
                    viewModel.matchedCards.collect {
                        if (viewModel.adapterValue.value is AllTasksAdapter)
                            setupAllTasksRecyclerView(it ?: arrayListOf())
                    }
                }
                launch {
                    viewModel.creatorInfoData.collect {
                        viewModel.setCreatorsList(it)
                        if (viewModel.adapterValue.value is FreelancersAdapter) {
                            setupFreelancersRecyclerView(it)
                        }
                    }
                }
                launch {
                    viewModel.creatorInfoHolder.collect {
                        if (viewModel.adapterValue.value is FreelancersAdapter) {
                            setupFreelancersRecyclerView(it ?: arrayListOf())
                        }
                    }
                }
                launch {
                    viewModel.openDetailsTrigger.collect {
                        findTopNavController().navigate(
                            TabsFragmentDirections.actionTabsFragmentToCardDetailsFragment(it)
                        )
                    }
                }
                launch {
                    viewModel.openDetailsFreelancer.collect {
                        findTopNavController().navigate(
                            TabsFragmentDirections.actionTabsFragmentToFreelancerDetailsFragment(it)
                        )
                    }
                }
            }
        }
    }

    private fun callSnackbar(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupAllTasksRecyclerView(list: List<CardModel>) {
        binding.allCardsRecycler.adapter = allTasksAdapter
        allTasksAdapter.submitList(list)
    }

    private fun setupFreelancersRecyclerView(list: List<FreelancerModel>) {
        binding.allCardsRecycler.adapter = creatorsAdapter
        creatorsAdapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}