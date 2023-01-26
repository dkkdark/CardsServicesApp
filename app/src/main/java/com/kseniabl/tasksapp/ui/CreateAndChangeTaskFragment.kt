package com.kseniabl.tasksapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.adapters.BookDateAdapter
import com.kseniabl.tasksapp.databinding.DialogCreateNewTaskBinding
import com.kseniabl.tasksapp.di.CreateAndChangeTaskAnnotation
import com.kseniabl.tasksapp.models.BookDate
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.utils.HelperFunctions
import com.kseniabl.tasksapp.utils.findTopNavController
import com.kseniabl.tasksapp.view.TagsModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CreateAndChangeTaskFragment: Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var _binding: DialogCreateNewTaskBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var adapter: BookDateAdapter

    @Inject
    @CreateAndChangeTaskAnnotation
    lateinit var linearLayoutManager: Provider<LinearLayoutManager>

    private val args: CreateAndChangeTaskFragmentArgs by navArgs()
    private var tagsList: ArrayList<TagsModel> = arrayListOf()

    private var card: CardModel? = null
    private var calendar = Calendar.getInstance()

    var date = ""
    var bookList = arrayListOf<BookDate>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCreateNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        card = args.card
        tagsList = card?.tags ?: arrayListOf()
        bookList = card?.bookDates ?: arrayListOf()
        card?.let { setDataToDialog(it) }
        setListener()
        clickOnButtonsListeners(view)
        addBookDate()
    }

    private fun setDataToDialog(card: CardModel) {
        binding.apply {
            dialogTaskTitleText.setText(card.title)
            dialogTaskDescriptionText.setText(card.description)
            if (card.active) {
                dialogTaskCostText.visibility = View.VISIBLE
                dialogTaskCostText.setText(card.cost.toString())
            }
            else
                dialogTaskCostText.visibility = View.GONE

            dialogCheckBox.isChecked = card.active
            dialogByAgreementCheckBox.isChecked = card.agreement
            prepaymentCheckBox.isChecked = card.prepayment

            if (tagsList.isEmpty()) {
                tagView.visibility = View.GONE
                dialogDeleteTags.visibility = View.GONE
            }
            else {
                tagView.visibility = View.VISIBLE
                dialogDeleteTags.visibility = View.VISIBLE
                tagView.tags = tagsList
            }

            if (bookList.isNotEmpty())
                adapter.submitList(bookList)
        }
    }

    private fun clickOnButtonsListeners(view: View) {
        binding.apply {
            dialogCreateChangeButton.setOnClickListener {
                dialogTaskTitleText.error = null
                dialogTaskCostText.error = null
                var returnOrNot = false

                if (dialogTaskTitleText.text?.isEmpty() == true) {
                    dialogTaskTitleText.error = "You didn't specify title"
                    returnOrNot = true
                }
                if (dialogTaskCostText.text?.isEmpty() == true && !dialogByAgreementCheckBox.isChecked) {
                    dialogTaskCostText.error = "You didn't specify cost"
                    returnOrNot = true
                }
                if (bookList.isEmpty()) {
                    Snackbar.make(view, "You should choose at least one book date", Snackbar.LENGTH_SHORT)
                        .show()
                    returnOrNot = true
                }
                if (!returnOrNot) {
                    findTopNavController().previousBackStackEntry?.savedStateHandle?.set("CARD_BACK",
                    CardModel(card?.id ?: "", dialogTaskTitleText.text.toString(), dialogTaskDescriptionText.text.toString(),
                        dialogTaskCostText.text.toString().toFloat(), dialogByAgreementCheckBox.isChecked, prepaymentCheckBox.isChecked,
                    tagsList, bookList, card?.createTime ?: 0, dialogCheckBox.isChecked, card?.user_id ?: ""))
                    findTopNavController().popBackStack()
                }
            }
            dialogCreateCloseButton.setOnClickListener {
                findTopNavController().popBackStack()
            }
        }
    }

    private fun setListener() {
        binding.apply {
            dialogByAgreementCheckBox.setOnClickListener {
                if (dialogByAgreementCheckBox.isChecked) {
                    dialogTaskCostField.visibility = View.GONE
                    dollarText.visibility = View.GONE
                } else {
                    dialogTaskCostField.visibility = View.VISIBLE
                    dollarText.visibility = View.VISIBLE
                }
            }

            dialogAddTagButton.setOnClickListener {
                if (!dialogTagAddText.text.isNullOrEmpty()) {
                    tagView.visibility = View.VISIBLE
                    dialogDeleteTags.visibility = View.VISIBLE
                    tagsList.add(TagsModel(dialogTagAddText.text.toString()))
                    tagView.tags = tagsList
                    dialogTagAddText.setText("")
                }
            }
            dialogDeleteTags.setOnClickListener {
                tagsList.clear()
                tagView.tags.clear()
                tagView.visibility = View.GONE
                dialogDeleteTags.visibility = View.GONE
            }
        }
    }

    private fun addBookDate() {
        binding.apply {
            newDateRecycler.layoutManager = linearLayoutManager.get()
            newDateRecycler.adapter = adapter
            newDateRecycler.setItemViewCacheSize(20)

            addBookDateButton.setOnClickListener {
                setDate()
            }
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateText()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minutes: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minutes)
        updateTimeText()
    }

    private fun setDate() {
        context?.let {
            val datePickerDialog = DatePickerDialog(requireContext(), this, Calendar.getInstance()[Calendar.YEAR],
                Calendar.getInstance()[Calendar.MONTH], Calendar.getInstance()[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }
    }

    private fun setTime() {
        TimePickerDialog(context, this,
            Calendar.getInstance()[Calendar.HOUR_OF_DAY],
            Calendar.getInstance()[Calendar.MINUTE], false).show()
    }

    private fun updateDateText() {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        date = sdf.format(calendar.time)
        setTime()
    }

    private fun updateTimeText() {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val time = sdf.format(calendar.time.time)

        bookList.add(BookDate(id = HelperFunctions.generateRandomKey(), date = "$date $time"))
        adapter.submitList(bookList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}