package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kseniabl.tasksapp.adapters.AllTasksAdapter
import com.kseniabl.tasksapp.adapters.FreelancersAdapter
import com.kseniabl.tasksapp.databinding.FragmentAllCardsBinding
import com.kseniabl.tasksapp.di.AllCardsScope
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.viewmodels.AllCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AllCardsFragment: Fragment() {

    @Inject
    @AllCardsScope
    lateinit var linearLayoutManager: Provider<LinearLayoutManager>
    @Inject
    lateinit var allTasksAdapter: AllTasksAdapter
    @Inject
    lateinit var freelancersAdapter: FreelancersAdapter

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
        //viewModel.getData()

        binding.apply {
            allCardsRecycler.layoutManager = layoutManager
            setupAllTasksRecyclerView(allCardsRecycler)
            allCardsRecycler.setItemViewCacheSize(20)

            activeTasksButton.setOnClickListener { viewModel.changeAdapter(allTasksAdapter) }
            freelancersButton.setOnClickListener { viewModel.changeAdapter(freelancersAdapter) }
        }

        viewModel.adapterValue.observe(viewLifecycleOwner) {
            if (it is AllTasksAdapter)
                setupAllTasksRecyclerView(binding.allCardsRecycler)
            if (it is FreelancersAdapter)
                setupFreelancersRecyclerView(binding.allCardsRecycler)
        }
    }

    private fun setupAllTasksRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = allTasksAdapter
        allTasksAdapter.submitList(arrayListOf(CardModel("1", "qwer", "rwewqw", "10.10", 1000000000, 20, true, false, "qeq")))
    }

    private fun setupFreelancersRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = freelancersAdapter
        freelancersAdapter.submitList(arrayListOf(FreelancerModel("werwe", "wewre", true),
            FreelancerModel("wergsdfwe", "wewre", true)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}