package com.kseniabl.tasksapp.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FragmentFreelancerInfoBinding
import com.kseniabl.tasksapp.databinding.FragmentLoadingScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadingScreen: Fragment() {

    private var _binding: FragmentLoadingScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoadingScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingConstraintLayout.setBackgroundResource(R.drawable.app_gradient)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}