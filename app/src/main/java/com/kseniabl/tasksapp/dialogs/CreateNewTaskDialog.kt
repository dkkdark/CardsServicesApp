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
import com.kseniabl.tasksapp.view.TagsModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class CreateNewTaskDialog: BaseDialog(){

    private var _binding: DialogCreateNewTaskBinding? = null
    private val binding get() = _binding!!

    var id = ""
    var title = ""
    var description = ""
    var cost: Float? = null
    var tagsList: ArrayList<String> = arrayListOf()
    var active = false
    var agreement = false
    var prepayment = false
    var createTime: Long = 0
    var userId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        id = arguments?.getString("id") ?: ""
        title = arguments?.getString("title") ?: ""
        description = arguments?.getString("description") ?: ""
        cost = arguments?.getFloat("cost")
        tagsList = arguments?.getStringArrayList("tags") ?: arrayListOf()
        active = arguments?.getBoolean("active") ?: false
        agreement = arguments?.getBoolean("agreement") ?: false
        prepayment = arguments?.getBoolean("prepayment") ?: false
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

        setDataToDialog(title, description, cost, active, agreement, prepayment)
        clickOnButtonsListeners()
        setListener()
        deleteTask()
    }

    private fun setDataToDialog(title: String, description: String, cost: Float?, active: Boolean, agreement: Boolean, prepayment: Boolean) {
        binding.apply {
            dialogTaskTitleText.setText(title)
            dialogTaskDescriptionText.setText(description)
            if (cost != null || cost != 0F) {
                dialogTaskCostText.visibility = View.VISIBLE
                dialogTaskCostText.setText(cost.toString())
            }
            else
                dialogTaskCostText.visibility = View.GONE

            dialogCheckBox.isChecked = active
            dialogByAgreementCheckBox.isChecked = agreement
            prepaymentCheckBox.isChecked = prepayment

            if (tagsList.isEmpty())
                tagView.visibility = View.GONE
            else {
                tagView.visibility = View.VISIBLE
                tagView.tags = tagsList.map { TagsModel(name = it) } as ArrayList<TagsModel>
            }
        }
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
                            "resCost" to dialogTaskCostText.text.toString().toFloat(),
                            "resTagsList" to tagsList,
                            "resByAgreementValue" to dialogByAgreementCheckBox.isChecked,
                            "resPrepayment" to prepaymentCheckBox.isChecked,
                            "resCreateTime" to createTime,
                            "resUserId" to userId
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

            dialogAddTagButton.setOnClickListener {
                if (!dialogTagAddText.text.isNullOrEmpty()) {
                    tagView.visibility = View.VISIBLE
                    val tags = tagView.tags
                    tags.add(TagsModel(dialogTagAddText.text.toString()))
                    tagsList.add(dialogTagAddText.text.toString())
                    tagView.tags = tags
                    dialogTagAddText.setText("")
                }
            }
        }
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