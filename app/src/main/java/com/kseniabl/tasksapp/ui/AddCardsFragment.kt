package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.databinding.FragmentAddCardsBinding
import com.kseniabl.tasksapp.di.AddCardsScope
import com.kseniabl.tasksapp.dialogs.CreateNewTaskDialog
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.utils.HelperFunctions.generateRandomKey
import com.kseniabl.tasksapp.utils.UserSaveInterface
import com.kseniabl.tasksapp.viewmodels.AddCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AddCardsFragment: Fragment() {

    @Inject
    lateinit var saveUser: UserSaveInterface

    private var _binding: FragmentAddCardsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tasksAdapter: AddTasksAdapter
    @Inject
    @AddCardsScope
    lateinit var linearLayoutManager: Provider<LinearLayoutManager>

    private val viewModel: AddCardsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = linearLayoutManager.get()

        binding.apply {
            addCardsRecycler.layoutManager = layoutManager
            addCardsRecycler.adapter = tasksAdapter
            addCardsRecycler.setItemViewCacheSize(20)

            activeButton.setOnClickListener { viewModel.changeList(true) }
            draftButton.setOnClickListener { viewModel.changeList(false) }
            addCardFab.setOnClickListener { showCreateTaskDialog(viewModel.adapterList.value) }

            tasksAdapter.setOnClickListener(viewModel)

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.dialogTrigger.collect {
                            showCreateTaskDialog(it.active, it)
                        }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.adapterList.collect {
                            setupTasksRecyclerView(it, viewModel.getList())
                        }
                    }
                    launch {
                        viewModel.cards.collect {
                            setupTasksRecyclerView(viewModel.adapterList.value, it)
                        }
                    }
                }
            }

        }
    }

    private fun setupTasksRecyclerView(active: Boolean, array: List<CardModel>) {
        val list = array.filter { if (active) it.active else !it.active}.reversed()
        CoroutineScope(Dispatchers.Main).launch { tasksAdapter.submitList(list) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.setFragmentResultListener("CreateNewTaskDialog", this) { _, bundle ->
            val resId = bundle.getString("resId") ?: ""
            val resTitle = bundle.getString("resTitle")
            val resDescription = bundle.getString("resDescription")
            val resActive = bundle.getBoolean("resActive")
            val resDate = bundle.getString("resDate")
            val resCost = bundle.getString("resCost")
            val resByAgreementValue = bundle.getBoolean("resByAgreementValue")
            var resCreateTime = bundle.getLong("resCreateTime")

            if (resTitle != null && resDescription != null && resDate != null && resCost != null) {
                val cost = try {
                    resCost.toInt()
                } catch (e: NumberFormatException) {
                    0
                }
                if (resCreateTime == 0L)
                    resCreateTime = Calendar.getInstance().time.time

                saveUser.readSharedPref()?.id?.let { id ->
                    if (resId == "") {
                        insertCard(resTitle, resDescription, resDate, resCreateTime, cost, resActive, resByAgreementValue, id)
                    }
                    else {
                        changeCard(CardModel(resId, resTitle, resDescription, resDate, resCreateTime, cost, resActive, resByAgreementValue, id))
                    }
                }

            }
        }
    }

    private fun changeCard(card: CardModel) {
        viewModel.changeCard(card)
    }

    private fun insertCard(resTitle: String, resDescription: String, resDate: String,
                           resCreateTime: Long, cost: Int, resActive: Boolean, resByAgreementValue: Boolean, id: String) {
        val cardId = generateRandomKey()
        viewModel.insertCard(
            CardModel(
                cardId, resTitle,
                resDescription, resDate,
                resCreateTime, cost,
                resActive, resByAgreementValue, id
            )
        )
    }

    private fun showCreateTaskDialog(active: Boolean, item: CardModel? = null) {
        val args = Bundle()
        args.putString("id", item?.id)
        args.putString("title", item?.title)
        args.putString("description", item?.description)
        args.putInt("cost", item?.cost ?: 0)
        args.putString("date", item?.date)
        args.putBoolean("active", item?.active ?: active)
        args.putBoolean("agreement", item?.agreement ?: false)
        args.putLong("createTime", item?.createTime ?: 0)
        args.putString("userId", item?.user_id)
        val dialog = CreateNewTaskDialog()
        dialog.arguments = args
        dialog.show(childFragmentManager, "CreateNewTaskDialog")
    }
}