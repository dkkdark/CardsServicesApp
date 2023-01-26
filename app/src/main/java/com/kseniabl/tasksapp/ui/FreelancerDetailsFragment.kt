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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FragmentFreelancerDetailsBinding
import com.kseniabl.tasksapp.utils.findTopNavController
import com.kseniabl.tasksapp.viewmodels.AllCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FreelancerDetailsFragment: Fragment() {

    private var _binding: FragmentFreelancerDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: FreelancerDetailsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFreelancerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = args.freelancer

        val navHost = childFragmentManager.findFragmentById(R.id.fragmentCont) as NavHostFragment
        val navController = navHost.navController

        navController.navigate(
            R.id.freelancerInfoFragment,
            bundleOf("user" to user)
        )

        binding.apply {
            infoButton.setOnClickListener {
                navController.navigate(
                    R.id.freelancerInfoFragment,
                    bundleOf("user" to user)
                )
            }
            cardsButton.setOnClickListener {
                navController.navigate(
                    R.id.freelancerCardsFragment,
                    bundleOf("id" to user.userInfo?.id)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}