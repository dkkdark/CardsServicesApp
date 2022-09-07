package com.kseniabl.tasksapp.ui

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.kseniabl.tasksapp.databinding.FragmentRegistrationBinding
import com.kseniabl.tasksapp.models.AdditionalInfo
import com.kseniabl.tasksapp.models.Profession
import com.kseniabl.tasksapp.models.UserModel
import com.kseniabl.tasksapp.utils.HelperFunctions.generateRandomKey
import com.kseniabl.tasksapp.utils.UserSaveInterface
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegistrationFragment: Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var database: DatabaseReference
    @Inject
    lateinit var userSave: UserSaveInterface

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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user == null) Snackbar.make(view, "User can't be create. Please try again",
                        Snackbar.LENGTH_SHORT).show()
                    else {
                        sendVerificationEmail(user, email, name)
                    }
                } else {
                    Snackbar.make(view, "${task.exception}. Please try again.",
                        Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendVerificationEmail(user: FirebaseUser, email: String, name: String) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addUserToDb(user, email, name)
                    guideToLoginFragment(email)

                } else {
                    auth.signOut()
                }
            }
    }

    private fun addUserToDb(fUser: FirebaseUser, email: String, name: String) {
        val id = fUser.uid
        val user = UserModel(id, name, email,false, "", "", AdditionalInfo("", "", "", ""), Profession("", "", arrayListOf()))
        userSave.saveCurrentUser(user)
        database.child("users").child(id).setValue(user)
    }

    private fun guideToLoginFragment(email: String) {
        parentFragmentManager.setFragmentResult(REQUEST_CODE, bundleOf(EMAIL_EXTRA to email))
        findNavController().popBackStack()
    }

    private fun checkEmptyFields(view: View): Boolean {
        var noEmptyFields = false
        binding.apply {
            if (nicknameText.text.isNullOrEmpty()) {
                nicknameText.error = "You didn't specify name"
                noEmptyFields = true
            }
            if (emailRegisterText.text.isNullOrEmpty()) {
                emailRegisterText.error = "You didn't specify email"
                noEmptyFields = true
            }
            if (passwordRegText.text.isNullOrEmpty()) {
                passwordRegText.error = "You didn't specify password"
                noEmptyFields = true
            }
            if (repeatPasswordText.text.isNullOrEmpty()) {
                repeatPasswordText.error = "You didn't specify password"
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