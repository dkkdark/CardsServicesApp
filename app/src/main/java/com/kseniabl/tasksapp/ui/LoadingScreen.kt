package com.kseniabl.tasksapp.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FragmentFreelancerInfoBinding
import com.kseniabl.tasksapp.databinding.FragmentLoadingScreenBinding
import com.kseniabl.tasksapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoadingScreen: Fragment() {

    private var _binding: FragmentLoadingScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingConstraintLayout.setBackgroundResource(R.drawable.app_gradient)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.moveToScreen.collect {
                        when (it) {
                            is MainViewModel.UIActions.ToWelcomeFragment -> {
                                findNavController().navigate(R.id.action_loadingScreen_to_welcomeFragment)
                            }

                            is MainViewModel.UIActions.ToLoginFragment -> {
                                findNavController().navigate(R.id.action_loadingScreen_to_loginFragment)
                            }

                            is MainViewModel.UIActions.ToTabFragment -> {
                                findNavController().navigate(R.id.action_loadingScreen_to_tabsFragment)
                            }

                            is MainViewModel.UIActions.Waiting -> {
                                Log.e("qqq", "Waiting until we can move further")
                            }

                            else -> {
                                Log.e("qqq", "do nothing")
                            }
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