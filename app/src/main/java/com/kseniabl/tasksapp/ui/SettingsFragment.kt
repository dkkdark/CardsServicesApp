package com.kseniabl.tasksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FragmentSettingsBinding
import com.kseniabl.tasksapp.dialogs.ChangeAdditionalInfoDialog
import com.kseniabl.tasksapp.dialogs.ChangeNameDialogFragment
import com.kseniabl.tasksapp.dialogs.ChangeProfessionDialogFragment
import com.kseniabl.tasksapp.models.UserModel
import com.kseniabl.tasksapp.utils.HelperFunctions.getTextGradient
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.utils.findTopNavController
import com.kseniabl.tasksapp.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment() {

    @Inject
    lateinit var userDataStore: UserDataStore

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

            logoutButton.setOnClickListener {
                viewModel.signOut()
            }

            beCreatorButton.setOnClickListener {
                viewModel.becomeCreator()
            }
        }

        setLoadedInfo()
        updateState(view)
    }

    private fun updateState(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateChange.collect { data ->
                        if (data is Resource.Loading<*>) {
                            // TODO loading
                        }
                        if (data is Resource.Success<*>) {
                            if (data.data?.isSuccessful == true) {
                                Snackbar.make(view, "You became a creator!", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                        if (data is Resource.Error<*>) {
                            Snackbar.make(view, data.message ?: "Some error. You cannot change state", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setLoadedInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    binding.apply {
                        userDataStore.readUser.collect { user ->
                            checkIsEmpty(profileNameText, user.userInfo?.username)
                            checkIsEmpty(specializationChangeText, user.specialization?.specialization)
                            checkIsEmpty(descriptionSpecializationChangeText, user.specialization?.description)
                            checkIsEmpty(descriptionAddInfoChangeText, user.additionalInfo?.description)
                            checkIsEmpty(countryChangeText, user.additionalInfo?.country)
                            checkIsEmpty(cityChangeText, user.additionalInfo?.city)
                            checkIsEmpty(typeOfWorkChangeText, user.additionalInfo?.typeOfWork)
                        }
                    }
                }
            }
        }
    }

    private fun checkIsEmpty(textView: TextView, value: String?) {
        if (value.isNullOrEmpty())
            textView.text = "—"
        else
            textView.text = value
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
                //viewModel.updateUserName(result)
            }
        }

        childFragmentManager.setFragmentResultListener("ChangeProfessionDialogFragment", this) { _, bundle ->
            val resSpecialization = bundle.getString("resSpecialization")
            val resDescription = bundle.getString("resDescription")
            if (resSpecialization != null && resDescription != null) {
                viewModel.updateSpecialization(resSpecialization, resDescription)
            }
        }

        childFragmentManager.setFragmentResultListener("ChangeAdditionalInfoDialog", this) { _, bundle ->
            val resDescription = bundle.getString("resDescription")
            val resCountry = bundle.getString("resCountry")
            val resCity = bundle.getString("resCity")
            val resTypeOfWork = bundle.getString("resTypeOfWork")
            if (resDescription != null && resCountry != null && resCity != null && resTypeOfWork != null) {
                viewModel.updateAddInf(resDescription, resCountry, resCity, resTypeOfWork)
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
                beCreatorButton.paint.shader = shader
                logoutButton.paint.shader = shader
            }
        }
    }
}