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
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.adapters.BookCardsAdapter
import com.kseniabl.tasksapp.databinding.FragmentAddCardsBinding
import com.kseniabl.tasksapp.databinding.FragmentBookedCardsBinding
import com.kseniabl.tasksapp.di.AddCardsScope
import com.kseniabl.tasksapp.di.BookCardsAnnotation
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.viewmodels.AddCardsViewModel
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
    lateinit var adapter: BookCardsAdapter

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

        binding.apply {
            bookedCardsRecycler.layoutManager = linearLayoutManager.get()
            bookedCardsRecycler.adapter = adapter
            bookedCardsRecycler.setItemViewCacheSize(20)
        }
        viewModel.getCards()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cards.collect {
                        if (it != null) {
                            val list = arrayListOf<CardModel>()
                            list.addAll(it)
                            adapter.submitList(list)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}