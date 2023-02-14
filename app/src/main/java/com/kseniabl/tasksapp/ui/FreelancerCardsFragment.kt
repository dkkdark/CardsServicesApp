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
import com.kseniabl.tasksapp.adapters.CreatorsCardsAdapter
import com.kseniabl.tasksapp.databinding.FragmentFreelancerCardsBinding
import com.kseniabl.tasksapp.di.scopes.CreatorCardsScope
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.viewmodels.AllCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class FreelancerCardsFragment: Fragment() {

    private var _binding: FragmentFreelancerCardsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllCardsViewModel by viewModels()

    @Inject
    @CreatorCardsScope
    lateinit var linearLayoutManager: Provider<LinearLayoutManager>
    @Inject
    lateinit var creatorCardsAdapter: CreatorsCardsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFreelancerCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = linearLayoutManager.get()

        binding.apply {
            cardsFreelancerRecyclerView.layoutManager = layoutManager
            cardsFreelancerRecyclerView.setHasFixedSize(true)
            cardsFreelancerRecyclerView.setItemViewCacheSize(20)
        }

        val id = arguments?.getString("id")

        if (id != null)
            viewModel.getCardsById(id)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.creatorCards.collect {
                        if (it != null)
                            setupCreatorCardsRecyclerView(it)
                        Log.e("qqq", "creator cards $it")
                    }
                }
            }
        }
    }

    private fun setupCreatorCardsRecyclerView(list: List<CardModel>) {
        binding.cardsFreelancerRecyclerView.adapter = creatorCardsAdapter
        creatorCardsAdapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}