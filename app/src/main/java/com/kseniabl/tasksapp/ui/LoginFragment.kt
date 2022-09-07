package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.transition.*
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FragmentAddCardsBinding
import com.kseniabl.tasksapp.databinding.FragmentLoginBinding
import com.kseniabl.tasksapp.ui.RegistrationFragment.Companion.EMAIL_EXTRA
import com.kseniabl.tasksapp.ui.RegistrationFragment.Companion.REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment: Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var database: DatabaseReference

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        binding.enterButton.setOnClickListener {
            signInWithEmail(view, binding.loginText.text.toString(), binding.passwordText.text.toString())
        }

        parentFragmentManager.setFragmentResultListener(REQUEST_CODE, viewLifecycleOwner) { _, data ->
            val email = data.getString(EMAIL_EXTRA)
            binding.loginText.setText(email)
        }
    }

    private fun loadUserData(view: View, user: FirebaseUser) {
        database.child("users").child(user.uid).get()
            .addOnSuccessListener {
                Log.e("qqq", "${it.value}")
                findNavController().navigate(R.id.action_loginFragment_to_tabsFragment)
            }
            .addOnFailureListener {
                Snackbar.make(view, "User can't be sign in. Please try again",
                    Snackbar.LENGTH_SHORT).show()
            }
    }

    private fun signIn(view: View, user: FirebaseUser) {
        if (user.isEmailVerified) {
            Snackbar.make(view, "Welcome!", Snackbar.LENGTH_SHORT).show()
            loadUserData(view, user)
        } else {
            Snackbar.make(view, "Please verify your email",
                Snackbar.LENGTH_SHORT).show()
            auth.signOut()
        }
    }

    private fun signInWithEmail(view: View, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user == null) Snackbar.make(view, "User can't be sign in. Please try again",
                        Snackbar.LENGTH_SHORT).show()
                    else {
                        signIn(view, user)
                    }
                }
                else {
                    Snackbar.make(view, "${task.exception}. Please try again",
                        Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}