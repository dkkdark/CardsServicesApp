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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FragmentFreelancerDetailsBinding

import com.kseniabl.tasksapp.models.UserModel
import com.kseniabl.tasksapp.utils.HelperFunctions
import com.kseniabl.tasksapp.viewmodels.AllCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FreelancerDetailsFragment: Fragment() {

    private var _binding: FragmentFreelancerDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: FreelancerDetailsFragmentArgs by navArgs()

    private val viewModel: AllCardsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFreelancerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var user = args.user

        val navHost = childFragmentManager.findFragmentById(R.id.fragmentCont) as NavHostFragment
        val navController = navHost.navController

        if (user.username == "" && user.id != "") {
            viewModel.getCreator(user.id)
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.userAllData.collect { data ->
                            if (data != null) user = data
                            goToFreelancerInfoFragment(navController, user)
                            binding.freelancerNameText.text = user.username
                        }
                    }
                    launch {
                        viewModel.stateChange.collect {
                            when (it) {
                                is AllCardsViewModel.UIActionsDetails.ShowSnackbar -> {
                                    Snackbar.make(view, it.message, Snackbar.LENGTH_SHORT).show()
                                }
                                is AllCardsViewModel.UIActionsDetails.GoToDetails -> { /** do nothing in this very fragment **/ }
                            }
                        }
                    }
                }
            }
        }

        goToFreelancerInfoFragment(navController, user)
        binding.freelancerNameText.text = user.username
        Log.e("qqq", "img ${user}")
        val bitmap = user.img?.content?.let { HelperFunctions.getImageBitmap(it) }
        bitmap?.let {
            Glide.with(requireContext()).asBitmap()
                .load(bitmap).placeholder(R.drawable.user).into(binding.imageViewFreelancer)
        }

        binding.apply {
            infoButton.setOnClickListener { goToFreelancerInfoFragment(navController, user) }
            cardsButton.setOnClickListener { goToFreelancerCardsFragment(navController, user.id) }
        }
    }

    private fun goToFreelancerInfoFragment(navController: NavController, user: UserModel) {
        navController.navigate(
            R.id.freelancerInfoFragment,
            bundleOf("user" to user)
        )
    }

    private fun goToFreelancerCardsFragment(navController: NavController, id: String?) {
        navController.navigate(
            R.id.freelancerCardsFragment,
            bundleOf("id" to id)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}