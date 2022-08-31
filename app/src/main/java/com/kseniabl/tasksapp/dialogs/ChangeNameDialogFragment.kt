package com.kseniabl.tasksapp.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.kseniabl.tasksapp.databinding.DialogChangeNameBinding

class ChangeNameDialogFragment: BaseDialog() {

    private var name = ""

    private var _binding: DialogChangeNameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        name = arguments?.getString("name").toString()

        _binding = DialogChangeNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            dialogNewNameText.setText(name)
            dialogNewNameField.requestFocus()
            dialogNewNameText.setSelection(name.length)

            changeButton.setOnClickListener {
                setFragmentResult(
                    "ChangeNameDialogFragment",
                    bundleOf("resName" to dialogNewNameText.text.toString())
                )
                dialog?.cancel()
            }
            closeButton.setOnClickListener { dialog?.cancel() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}