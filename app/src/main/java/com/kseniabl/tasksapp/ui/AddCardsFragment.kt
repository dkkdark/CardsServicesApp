package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kseniabl.tasksapp.adapters.DraftTasksAdapter
import com.kseniabl.tasksapp.databinding.FragmentAddCardsBinding
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.viewmodels.AddCardsViewModel

class AddCardsFragment: Fragment() {

    private var _binding: FragmentAddCardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var tasksAdapter: DraftTasksAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val viewModel: AddCardsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasksAdapter = DraftTasksAdapter()
        layoutManager = LinearLayoutManager(requireContext())

        binding.apply {
            addCardsRecycler.layoutManager = layoutManager
            addCardsRecycler.adapter = tasksAdapter
            addCardsRecycler.setItemViewCacheSize(20)

            activeButton.setOnClickListener { viewModel.changeList(true) }
            draftButton.setOnClickListener { viewModel.changeList(false) }

            viewModel.adapterList.observe(viewLifecycleOwner) {
                setupTasksRecyclerView(it)
            }
        }
    }

    private fun setupTasksRecyclerView(active: Boolean) {
        if (active)
            tasksAdapter.submitList(arrayListOf(CardModel("1", "qwer", "rwewqw", "10.10", 1000000000, 20, true, false, "qeq"),))
        else
            tasksAdapter.submitList(arrayListOf(CardModel("12", "qwe3r", "rwewqw", "10.10", 1000000000, 20, true, false, "qeq"),
                CardModel("2", "qwfder", "rwedfwqw", "10.10", 1000000000, 20, true, false, "qeq")))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}