package com.kseniabl.tasksapp.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.DialogCreateNewTaskBinding
import com.kseniabl.tasksapp.utils.HelperFunctions.getTextGradient
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreateNewTaskDialog: BaseDialog(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var calendar = Calendar.getInstance()

    private var _binding: DialogCreateNewTaskBinding? = null
    private val binding get() = _binding!!

    var id = ""
    var title = ""
    var description = ""
    var date = ""
    var cost: Int? = null
    var active = false
    var agreement = false
    var createTime: Long = 0
    var userId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        id = arguments?.getString("id") ?: ""
        title = arguments?.getString("title") ?: ""
        description = arguments?.getString("description") ?: ""
        date = arguments?.getString("date") ?: ""
        cost = arguments?.getInt("cost")
        active = arguments?.getBoolean("active") ?: false
        agreement = arguments?.getBoolean("agreement") ?: false
        createTime = arguments?.getLong("createTime") ?: 0
        userId = arguments?.getString("userId") ?: ""

        _binding = DialogCreateNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            val shader = getTextGradient(it)
            binding.crateChangeTask.paint.shader = shader
        }

        showInitialDate()
        if (title != "")
            setDataToDialog(title, description, date, cost, active, agreement)
        clickOnButtonsListeners()
        setListener()
        deleteTask()
    }

    private fun setDataToDialog(title: String, description: String, date: String, cost: Int?, active: Boolean, agreement: Boolean) {
        binding.apply {
            dialogTaskTitleText.setText(title)
            dialogTaskDescriptionText.setText(description)
            if (cost != null || cost != 0)
                dialogTaskCostText.setText(cost.toString())
            dialogChooseDate.text = date
            dialogCheckBox.isChecked = active
            dialogByAgreementCheckBox.isChecked = agreement
        }
    }

    private fun showInitialDate() {
        val date = Calendar.getInstance()
        date.add(Calendar.WEEK_OF_YEAR, 1)
        val dateToFormat = date.time
        val time = Calendar.getInstance().time.time
        val sdfDate = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedDate = sdfDate.format(dateToFormat)
        val formattedTime = sdfTime.format(time)
        binding.dialogChooseDate.text = "$formattedDate $formattedTime"
    }

    private fun clickOnButtonsListeners() {
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
                if (!returnOrNot) {
                    setFragmentResult(
                        "CreateNewTaskDialog", bundleOf(
                            "resId" to id,
                            "resTitle" to dialogTaskTitleText.text.toString(),
                            "resDescription" to dialogTaskDescriptionText.text.toString(),
                            "resActive" to dialogCheckBox.isChecked,
                            "resDate" to dialogChooseDate.text.toString(),
                            "resCost" to dialogTaskCostText.text.toString(),
                            "resByAgreementValue" to dialogByAgreementCheckBox.isChecked,
                            "resCreateTime" to createTime
                        )
                    )
                    dialog?.cancel()
                }
            }
            dialogCreateCloseButton.setOnClickListener { dialog?.cancel() }
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

            dialogChangeDateIcon.setOnClickListener {
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
        val sdf = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        date = sdf.format(calendar.time)
        setTime()
    }

    private fun updateTimeText() {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        var time = sdf.format(calendar.time.time)

        binding.dialogChooseDate.text = "$date $time"
    }

    private fun deleteTask() {
        binding.dialogDeleteButton.setOnClickListener {
            setAlertDialog()
        }
    }

    private fun setAlertDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.do_you_want_to_delete))
                .setPositiveButton(getString(R.string.delete)) { d, w ->
                    //deleteTask(this, d, userId, id)
                }
                .setNegativeButton(getString(R.string.cancel)) { d, w ->
                    d.dismiss()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}