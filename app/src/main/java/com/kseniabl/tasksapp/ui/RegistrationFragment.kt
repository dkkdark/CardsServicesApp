package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.kseniabl.tasksapp.databinding.FragmentRegistrationBinding
import com.kseniabl.tasksapp.models.AddUserModel
import com.kseniabl.tasksapp.utils.Constants.masterPassword
import com.kseniabl.tasksapp.utils.Constants.ordinary
import com.kseniabl.tasksapp.utils.HelperFunctions.isValidPassword
import com.kseniabl.tasksapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class RegistrationFragment: Fragment() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            logButtonText.setOnClickListener {
                findNavController().popBackStack()
            }
            regButton.setOnClickListener {
                if (!checkEmptyFields(view))
                    singUp(nicknameText.text.toString(), emailRegisterText.text.toString(), passwordRegText.text.toString(), view)
            }
        }
    }

    private fun singUp(name: String, email: String, password: String, view: View) {
        viewModel.signUp(AddUserModel(masterPassword, name, password, ordinary, email))

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiActionsRegistration.collect {
                        when(it) {
                            is MainViewModel.UIActions.ShowSnackbar -> {
                                Snackbar.make(view, it.message, Snackbar.LENGTH_SHORT).show()
                            }
                            is MainViewModel.UIActions.ToLoginFragment -> {
                                guideToLoginFragment(email)
                            }
                            else -> { Log.e("qqq", "do nothing") }
                        }
                    }
                }
            }
        }

    }

    private fun guideToLoginFragment(email: String) {
        parentFragmentManager.setFragmentResult(REQUEST_CODE, bundleOf(EMAIL_EXTRA to email))
        findNavController().popBackStack()
    }

    private fun checkEmptyFields(view: View): Boolean {
        var noEmptyFields = false
        binding.apply {
            if (nicknameText.text.toString().length < 6) {
                nicknameText.error = "Your name is too small"
                noEmptyFields = true
            }
            if (emailRegisterText.text.isNullOrEmpty()) {
                emailRegisterText.error = "You didn't specify email"
                noEmptyFields = true
            }
            if (!isValidPassword(passwordRegText.text.toString().trim())) {
                passwordRegText.error = "Your password is weak. Use capital letters, numbers, special characters"
                noEmptyFields = true
            }
            if (passwordRegText.text.toString() != repeatPasswordText.text.toString()
                && !passwordRegText.text.isNullOrEmpty() && !repeatPasswordText.text.isNullOrEmpty()) {
                Snackbar.make(view, "Passwords doesn't match", Snackbar.LENGTH_SHORT).show()
                noEmptyFields = true
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailRegisterText.text.toString()).matches()
                && !emailRegisterText.text.isNullOrEmpty()) {
                Snackbar.make(view, "Please write correct email", Snackbar.LENGTH_SHORT).show()
                noEmptyFields = true
            }
        }
        return noEmptyFields
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EMAIL_EXTRA = "EMAIL_CODE"
        const val REQUEST_CODE = "REQUEST_CODE"
    }
}