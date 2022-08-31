package com.kseniabl.tasksapp.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.kseniabl.tasksapp.databinding.DialogChangeAdditionalInfoBinding
import com.kseniabl.tasksapp.utils.HelperFunctions.getTextGradient

class ChangeAdditionalInfoDialog: BaseDialog() {

    private val listForSpinner = arrayListOf("—", "Online", "Offline")

    private var _binding: DialogChangeAdditionalInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogChangeAdditionalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            val shader = getTextGradient(it)
            binding.additionalInfoText.paint.shader = shader
        }

        setTypeOfWWorkSpinner()
        setButtonsClickListeners()

        val description = arguments?.getString("description")
        val country = arguments?.getString("country")
        val city = arguments?.getString("city")
        val typeOfWork = arguments?.getString("typeOfWork")

        if (description != "—")
            binding.dialogAdditionalDescriptionText.setText(description)
        if (country != "—")
            binding.dialogCountryText.setText(country)
        if (city != "—")
            binding.dialogCityText.setText(city)
        if (typeOfWork != "—")
            binding.dialogTypeOfWorkField.setSelection(listForSpinner.indexOf(typeOfWork))
    }

    private fun setButtonsClickListeners() {
        binding.apply {
            dialogAdditionalChangeButton.setOnClickListener {
                setFragmentResult(
                    "ChangeAdditionalInfoDialog", bundleOf(
                        "resDescription" to dialogAdditionalDescriptionText.text.toString(),
                        "resCountry" to dialogCountryText.text.toString(),
                        "resCity" to dialogCityText.text.toString(),
                        "resTypeOfWork" to dialogTypeOfWorkField.selectedItem.toString()
                    )
                )
                dialog?.cancel()
                dialog?.dismiss()
            }
            dialogAdditionalCloseButton.setOnClickListener {
                dialog?.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setTypeOfWWorkSpinner() {
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listForSpinner) {}
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dialogTypeOfWorkField.adapter = adapter

        binding.dialogTypeOfWorkField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, itemSelected: View?, selectedItemPos: Int, selectedId: Long) {
                when (listForSpinner[selectedItemPos]) {
                    "—" -> {}
                    "Online" -> {}
                    "Offline" -> {}
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
}