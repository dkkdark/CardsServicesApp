package com.kseniabl.tasksapp.ui

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FragmentSettingsBinding
import com.kseniabl.tasksapp.dialogs.ChangeAdditionalInfoDialog
import com.kseniabl.tasksapp.dialogs.ChangeNameDialogFragment
import com.kseniabl.tasksapp.dialogs.ChangeProfessionDialogFragment
import com.kseniabl.tasksapp.utils.HelperFunctions.getTextGradient
import com.kseniabl.tasksapp.utils.UserDataStore
import com.kseniabl.tasksapp.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
            imageViewProfile.setOnClickListener { updateProfileImage() }
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
        setProfileImage()
        updateState(view)

        viewModel.getImage()
    }

    private fun updateProfileImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri?.path != null) {
            val stream = requireContext().contentResolver.openInputStream(uri)
            val ext = requireContext().contentResolver.getType(uri)?.substringAfter("/")
                ?: "jpeg"
            Glide.with(requireContext())
                .load(uri).placeholder(R.drawable.user).into(binding.imageViewProfile)

            stream?.let { viewModel.uploadImage(it, ext) }

        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private fun setProfileImage() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.profileImage.collect {
                        Glide.with(requireContext()).asBitmap()
                            .load(it).placeholder(R.drawable.user).into(binding.imageViewProfile)
                    }
                }
            }
        }
    }

    private fun updateState(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateChange.collect {
                        when(it) {
                            is SettingsViewModel.UIActions.ShowSnackbar -> {
                                Snackbar.make(view, it.message, Snackbar.LENGTH_SHORT).show()
                            }
                            is SettingsViewModel.UIActions.SetCreatorSettings -> {
                                Snackbar.make(view, "You became a creator!", Snackbar.LENGTH_SHORT).show()
                                binding.beCreatorButton.isEnabled = false
                                binding.beCreatorButton.text = "You are creator"
                            }
                            else -> {}
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
                            checkIsEmpty(profileNameText, user.username)
                            checkIsEmpty(specializationChangeText, user.specialization?.specialization)
                            checkIsEmpty(descriptionSpecializationChangeText, user.specialization?.description)
                            checkIsEmpty(descriptionAddInfoChangeText, user.addInf?.description)
                            checkIsEmpty(countryChangeText, user.addInf?.country)
                            checkIsEmpty(cityChangeText, user.addInf?.city)
                            checkIsEmpty(typeOfWorkChangeText, user.addInf?.typeOfWork)

                            if (user.creator) {
                                beCreatorButton.isEnabled = false
                                beCreatorButton.text = "You are creator"
                            }
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