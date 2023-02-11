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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.tasksapp.adapters.DatesDetailAdapter
import com.kseniabl.tasksapp.databinding.FragmentCardDetailsBinding
import com.kseniabl.tasksapp.di.CardDetailsAnnotation
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.findTopNavController
import com.kseniabl.tasksapp.viewmodels.AllCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class CardDetailsFragment: Fragment() {

    private var _binding: FragmentCardDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var adapter: DatesDetailAdapter

    @Inject
    @CardDetailsAnnotation
    lateinit var linearLayoutManager: Provider<LinearLayoutManager>

    private val args: CardDetailsFragmentArgs by navArgs()

    private val viewModel: AllCardsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCardDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val card = args.card
        viewModel.getCreator(card.user_id)

        binding.apply {
            detailTitle.text = card.title
            feeText.text = if (!card.agreement) card.cost.toString() else "by agreement"
            prepaymentText.text = if (card.prepayment) "yes" else "no"
            tagView.visibility = if (card.tags.isEmpty()) View.GONE else View.VISIBLE
            tagView.tags = card.tags
            descriptionText.text = card.description

            datesRecycler.layoutManager = linearLayoutManager.get()
            datesRecycler.adapter = adapter
            datesRecycler.setItemViewCacheSize(20)

            if (card.bookDates.isEmpty())
                datesCardView.visibility = View.GONE
            else
                adapter.submitList(card.bookDates)

            respondToTask.setOnClickListener {
                if (adapter.selected == -1)
                    Snackbar.make(view, "Please choose date to book", Snackbar.LENGTH_SHORT)
                        .show()
                else {
                    viewModel.updateBookDateUser(card.bookDates[adapter.selected].id)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userAllData.collect { data ->
                        binding.nameText.text = data?.userInfo?.username
                        binding.specializationText.text = data?.specialization?.specialization
                        binding.specializationDescrText.text = data?.specialization?.description
                    }
                }
                launch {
                    viewModel.stateChange.collect {
                        when(it) {
                            is AllCardsViewModel.UIActionsDetails.ShowSnackbar -> {
                                Snackbar.make(view, it.message, Snackbar.LENGTH_SHORT).show()
                            }
                            is AllCardsViewModel.UIActionsDetails.GoToDetails -> {
                                findTopNavController().popBackStack()
                            }
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