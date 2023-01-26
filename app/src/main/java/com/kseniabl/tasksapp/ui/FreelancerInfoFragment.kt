package com.kseniabl.tasksapp.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.kseniabl.tasksapp.databinding.FragmentAddCardsBinding
import com.kseniabl.tasksapp.databinding.FragmentFreelancerInfoBinding
import com.kseniabl.tasksapp.models.FreelancerModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreelancerInfoFragment: Fragment() {

    private var _binding: FragmentFreelancerInfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFreelancerInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = if (Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable("user", FreelancerModel::class.java)
        } else {
            @Suppress("DEPRECATION") arguments?.getParcelable("user")
        }

        binding.apply {
            specializationChangeTextFreelancerDetails.text = user?.specialization?.specialization
            descriptionSpecializationChangeTextFreelancerDetails.text = user?.specialization?.description
            descriptionAddInfoChangeTextFreelancerDetails.text = user?.additionalInfo?.description
            countryChangeTextFreelancerDetails.text = user?.additionalInfo?.country
            cityChangeTextFreelancerDetails.text = user?.additionalInfo?.city
            typeOfWorkChangeTextFreelancerDetails.text = user?.additionalInfo?.typeOfWork
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}