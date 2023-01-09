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
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.tasksapp.adapters.BookDateAdapter
import com.kseniabl.tasksapp.adapters.DatesDetailAdapter
import com.kseniabl.tasksapp.databinding.FragmentCardDetailsBinding
import com.kseniabl.tasksapp.di.CardDetailsAnnotation
import com.kseniabl.tasksapp.di.CreateAndChangeTaskAnnotation
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.findTopNavController
import com.kseniabl.tasksapp.viewmodels.AllCardsViewModel
import com.kseniabl.tasksapp.viewmodels.CardDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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

    private val viewModel: CardDetailsViewModel by viewModels()

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
                        if (data != null) {
                            if (data is Resource.Success<*>) {
                                if (data.data != null) {
                                    binding.nameText.text = data.data.userInfo?.username
                                    binding.specializationText.text = data.data.specialization?.specialization
                                    binding.specializationDescrText.text = data.data.specialization?.description
                                }
                            }
                            if (data is Resource.Error<*>) {
                                Snackbar.make(view, "${data.message}", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.stateChange.collect {
                        if (it is Resource.Success<*>) {
                            Snackbar.make(view, "You booked successfully", Snackbar.LENGTH_SHORT).show()
                            findTopNavController().popBackStack()
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