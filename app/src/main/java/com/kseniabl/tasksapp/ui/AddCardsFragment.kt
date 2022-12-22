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
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.databinding.FragmentAddCardsBinding
import com.kseniabl.tasksapp.di.AddCardsScope
import com.kseniabl.tasksapp.dialogs.CreateNewTaskDialog
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.models.FreelancerModel
import com.kseniabl.tasksapp.utils.HelperFunctions.generateRandomKey
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.view.TagsModel
import com.kseniabl.tasksapp.viewmodels.AddCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddCardsFragment: Fragment() {

    private var _binding: FragmentAddCardsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tasksAdapter: AddTasksAdapter

    @Inject
    @AddCardsScope
    lateinit var linearLayoutManager: Provider<LinearLayoutManager>

    private val viewModel: AddCardsViewModel by viewModels()

    @Inject
    lateinit var userDataStore: UserDataStore

    private var user: FreelancerModel? = null

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
            addCardFab.setOnClickListener {
                viewModel.isUserCreator()
            }

            viewModel.getCards()
            tasksAdapter.setOnClickListener(viewModel)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dialogTrigger.collect {
                        if (it is Resource.Success<*>) {
                            showCreateTaskDialog(it.data)
                        }
                        if (it is Resource.Error<*>)
                            Snackbar.make(view, it.message ?: "You cannot add cards", Snackbar.LENGTH_SHORT).show()
                    }
                }
                launch {
                    viewModel.adapterList.collect {
                        val cards = viewModel.cards.value
                        if (!cards.isNullOrEmpty())
                            setupTasksRecyclerView(it, cards)
                    }
                }
                launch {
                    viewModel.cards.collect {
                        setupTasksRecyclerView(viewModel.adapterList.value, it)
                    }
                }
                launch {
                    user = userDataStore.readUser.first()
                }
            }
        }
    }

    private fun setupTasksRecyclerView(active: Boolean, array: ArrayList<CardModel>?) {
        val list = array?.filter { if (active) it.active else !it.active}
        CoroutineScope(Dispatchers.Main).launch { tasksAdapter.submitList(list ?: arrayListOf()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.setFragmentResultListener("CreateNewTaskDialog", this) { _, bundle ->
            var resId = bundle.getString("resId")
            val resTitle = bundle.getString("resTitle")
            val resDescription = bundle.getString("resDescription")
            val resActive = bundle.getBoolean("resActive")
            val resCost = bundle.getFloat("resCost")
            val resTagsList = bundle.getStringArrayList("resTagsList") ?: arrayListOf()
            val resByAgreementValue = bundle.getBoolean("resByAgreementValue")
            val resPrepayment = bundle.getBoolean("resPrepayment")
            var resCreateTime = bundle.getLong("resCreateTime")
            var resUserId = bundle.getString("resUserId")

            if (resTitle != null && resDescription != null) {
                if (resCreateTime == 0L)
                    resCreateTime = Calendar.getInstance().time.time
                if (resId.isNullOrEmpty()) {
                    resId = generateRandomKey()
                }
                if (resUserId.isNullOrEmpty() && user?.userInfo != null) {
                    resUserId = user!!.userInfo!!.id
                }

                resUserId?.let {
                    viewModel.updateCard(
                        CardModel(
                            resId, resTitle, resDescription, resCost,
                            resByAgreementValue, resPrepayment, resTagsList.map { name -> TagsModel(name = name) } as ArrayList<TagsModel>,
                            resCreateTime, resActive, it
                        )
                    )
                }
            }
        }
    }

    private fun showCreateTaskDialog(item: CardModel? = null) {
        val args = Bundle()
        args.putString("id", item?.id)
        args.putString("title", item?.title)
        args.putString("description", item?.description)
        args.putFloat("cost", item?.cost ?: 0F)
        args.putStringArrayList("tags", item?.tags?.map { it.name } as ArrayList<String>?)
        args.putBoolean("active", item?.active ?: false)
        args.putBoolean("agreement", item?.agreement ?: false)
        args.putBoolean("prepayment", item?.prepayment ?: false)
        args.putLong("createTime", item?.createTime ?: 0)
        args.putString("userId", item?.user_id)
        val dialog = CreateNewTaskDialog()
        dialog.arguments = args
        dialog.show(childFragmentManager, "CreateNewTaskDialog")
    }
}