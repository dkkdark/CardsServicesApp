package com.kseniabl.tasksapp.ui

import android.os.Bundle
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
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.viewmodels.AllCardsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCardsFragment: Fragment() {

    //@Inject
    private lateinit var layoutManager: LinearLayoutManager
    //@Inject
    private lateinit var allTasksAdapter: AllTasksAdapter
    //@Inject
    private lateinit var freelancersAdapter: FreelancersAdapter

    private val viewModel: AllCardsViewModel by viewModels()

    private var _binding: FragmentAllCardsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(requireContext())
        allTasksAdapter = AllTasksAdapter()
        freelancersAdapter = FreelancersAdapter(requireContext())

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