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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FragmentAddCardsBinding
import com.kseniabl.tasksapp.databinding.FragmentLoginBinding
import com.kseniabl.tasksapp.db.TasksRepositoryInterface
import com.kseniabl.tasksapp.models.*
import com.kseniabl.tasksapp.ui.RegistrationFragment.Companion.EMAIL_EXTRA
import com.kseniabl.tasksapp.ui.RegistrationFragment.Companion.REQUEST_CODE
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserSaveInterface
import com.kseniabl.tasksapp.utils.UserTokenDataStore
import com.kseniabl.tasksapp.viewmodels.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment: Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var database: DatabaseReference

    @Inject
    lateinit var userTokenDataStore: UserTokenDataStore

    private val viewModel: RegistrationViewModel by viewModels()

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
            signIn(view, binding.loginText.text.toString(), binding.passwordText.text.toString())
        }

        parentFragmentManager.setFragmentResultListener(REQUEST_CODE, viewLifecycleOwner) { _, data ->
            val email = data.getString(EMAIL_EXTRA)
            binding.loginText.setText(email)
        }
    }

    /** load user's cards and save them **/
    private fun getUserCards(view: View) {
        //CoroutineScope(Dispatchers.IO).launch { repository.insertAllCards(list) }
    }

    private fun guidToTabs() {
        findNavController().navigate(R.id.action_loginFragment_to_tabsFragment)
    }

    /** save user data, additional info, spec, tags, etc. **/
    private fun loadUserData(view: View, token: String) {
        viewModel.getUser(token)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userAllData.collect { data ->
                        if (data != null) {
                            if (data is Resource.Success<*>) {
                                if (data.data != null) {
                                    viewModel.saveUser(data.data)
                                    Log.e("qqq", "User: ${data.data}")
                                }
                            }
                            if (data is Resource.Error<*>) {
                                Snackbar.make(view, "${data.message}", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.saved.collect {
                        if (it) viewModel.saveToken(token)
                    }
                }
            }
        }
    }

    private fun signIn(view: View, email: String, password: String) {
        viewModel.signIn(UserLoginModel(email, password))

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loginStatus.collect { state ->
                        if (state is Resource.Loading<*>) {
                            // TODO loading
                        }
                        if (state is Resource.Success<*>) {
                            val token = state.data?.body()?.token
                            if (token != null) {
                                loadUserData(view, token)
                                getUserCards(view)
                            }
                            else {
                                Snackbar.make(view, "${state.message}", Snackbar.LENGTH_SHORT).show()
                                Log.e("qqq", "${state.message}")
                            }
                        }
                        if (state is Resource.Error<*>) {
                            Snackbar.make(view, "${state.message}", Snackbar.LENGTH_SHORT).show()
                            Log.e("qqq", "${state.message}")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}