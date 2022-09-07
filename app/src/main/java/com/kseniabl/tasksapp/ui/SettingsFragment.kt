package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FragmentSettingsBinding
import com.kseniabl.tasksapp.dialogs.ChangeAdditionalInfoDialog
import com.kseniabl.tasksapp.dialogs.ChangeNameDialogFragment
import com.kseniabl.tasksapp.dialogs.ChangeProfessionDialogFragment
import com.kseniabl.tasksapp.models.AdditionalInfo
import com.kseniabl.tasksapp.models.Profession
import com.kseniabl.tasksapp.models.UserModel
import com.kseniabl.tasksapp.utils.HelperFunctions.getTextGradient
import com.kseniabl.tasksapp.utils.UserSaveInterface
import com.kseniabl.tasksapp.utils.findTopNavController
import com.kseniabl.tasksapp.utils.stringLiveData
import com.kseniabl.tasksapp.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment() {

    @Inject
    lateinit var saveUser: UserSaveInterface

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGradient()

        binding.apply {
            changeNameImage.setOnClickListener { showChangeNameDialog() }
            editProfessionImage.setOnClickListener { showChangeProfessionDialog() }
            editAdditionalInfoImage.setOnClickListener { showChangeAdditionalInfoDialog() }

            beFreelancerCheckBox.setOnCheckedChangeListener { _, value ->
                viewModel.updateUserFreelanceState(value)
            }
            logoutButton.setOnClickListener {
                viewModel.signOut()
                findTopNavController().navigate(R.id.action_tabsFragment_to_loginFragment)
            }
        }

        viewModel.liveUser.observe(viewLifecycleOwner) {
            val user = saveUser.jsonToUserModel(it)
            setupUserInfo(user)
        }
    }

    private fun setupUserInfo(user: UserModel?) {
        binding.apply {
            viewModel.setupUserInfo(user,
                profileNameText, beFreelancerCheckBox,
                emailChangeText, specializationChangeText,
                descriptionSpecializationChangeText, descriptionAddInfoChangeText,
                countryChangeText, cityChangeText, typeOfWorkChangeText
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.setFragmentResultListener("ChangeNameDialogFragment", this) { _, bundle ->
            val result = bundle.getString("resName")
            if (result != null) {
                viewModel.updateUserName(result)
            }
        }

        childFragmentManager.setFragmentResultListener("ChangeProfessionDialogFragment", this) { _, bundle ->
            val resSpecialization = bundle.getString("resSpecialization")
            val resDescription = bundle.getString("resDescription")
            if (resSpecialization != null && resDescription != null) {
                viewModel.updateUserProf(resSpecialization, resDescription)
            }
        }

        childFragmentManager.setFragmentResultListener("ChangeAdditionalInfoDialog", this) { _, bundle ->
            val resDescription = bundle.getString("resDescription")
            val resCountry = bundle.getString("resCountry")
            val resCity = bundle.getString("resCity")
            val resTypeOfWork = bundle.getString("resTypeOfWork")
            if (resDescription != null && resCountry != null && resCity != null && resTypeOfWork != null) {
                viewModel.updateUserAdditionalInfo(resDescription, resCountry, resCity, resTypeOfWork)
            }
        }
    }

    private fun showChangeNameDialog() {
        val args = Bundle()
        args.putString("name", binding.profileNameText.text.toString())
        val dialog = ChangeNameDialogFragment()
        dialog.arguments = args
        dialog.show(childFragmentManager, "ChangeNameDialogFragment")
    }

    private fun showChangeProfessionDialog() {
        val args = Bundle()
        args.putString("specialization", binding.specializationChangeText.text.toString())
        args.putString("description", binding.descriptionSpecializationChangeText.text.toString())

        val dialog = ChangeProfessionDialogFragment()
        dialog.arguments = args
        dialog.show(childFragmentManager, "ChangeProfessionDialogFragment")
    }

    private fun showChangeAdditionalInfoDialog() {
        val args = Bundle()
        args.putString("description", binding.descriptionAddInfoChangeText.text.toString())
        args.putString("country", binding.countryChangeText.text.toString())
        args.putString("city", binding.cityChangeText.text.toString())
        args.putString("typeOfWork", binding.typeOfWorkChangeText.text.toString())

        val dialog = ChangeAdditionalInfoDialog()
        dialog.arguments = args
        dialog.show(childFragmentManager, "ChangeAdditionalInfoDialog")
    }

    private fun setGradient() {
        activity?.let {
            binding.apply {
                val shader = getTextGradient(it)
                professionText.paint.shader = shader
                additionalInfoText.paint.shader = shader
                otherInfoText.paint.shader = shader
                changePasswordButton.paint.shader = shader
                logoutButton.paint.shader = shader
            }
        }
    }
}