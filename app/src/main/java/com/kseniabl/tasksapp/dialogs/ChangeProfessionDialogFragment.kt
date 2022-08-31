package com.kseniabl.tasksapp.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.kseniabl.tasksapp.databinding.DialogChangeProfessionBinding
import com.kseniabl.tasksapp.utils.HelperFunctions.getTextGradient

class ChangeProfessionDialogFragment: BaseDialog() {

    private var _binding: DialogChangeProfessionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        _binding = DialogChangeProfessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            val shader = getTextGradient(it)
            binding.dialogProfessionText.paint.shader = shader
        }

        val specialization = arguments?.getString("specialization").toString()
        val description = arguments?.getString("description").toString()

        if (specialization != "—")
            binding.dialogSpecializationText.setText(specialization)
        if (description != "—")
            binding.dialogDescriptionText.setText(description)

        setButtonsClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setButtonsClickListeners() {
        binding.apply {
            dialogProfChangeButton.setOnClickListener {
                setFragmentResult(
                    "ChangeProfessionDialogFragment", bundleOf(
                        "resSpecialization" to dialogSpecializationText.text.toString(),
                        "resDescription" to dialogDescriptionText.text.toString()
                    )
                )
                dialog?.cancel()
            }
            dialogProfCloseButton.setOnClickListener { dialog?.cancel() }
        }
    }

}