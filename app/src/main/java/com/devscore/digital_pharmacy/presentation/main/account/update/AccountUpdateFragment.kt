package com.devscore.digital_pharmacy.presentation.main.account.update

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.devscore.digital_pharmacy.MainActivity
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.presentation.inventory.add.addmedicine.AddMedicineEvents
import com.devscore.digital_pharmacy.presentation.main.BaseMainFragment
import com.devscore.digital_pharmacy.presentation.main.account.profie.AccountEvents
import com.devscore.digital_pharmacy.presentation.main.account.profie.AccountViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.profileImgId
import kotlinx.android.synthetic.main.fragment_account_update.*
import kotlinx.android.synthetic.main.fragment_add_product_sub_medicine.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.password3_dialog.*


@AndroidEntryPoint
class AccountUpdateFragment : BaseMainFragment() {

    private val viewModel : AccountUpdateViewModel by viewModels()



    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_update, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClick()
        openImage()
        subscribeObserver()
    }

    private fun openImage() {
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                profilePicture.setImageURI(uri)
                viewModel.onTriggerEvent(AccountUpdateEvents.UpdateImage(uri.path))
            }
        }

//        cropActivityResultLauncher.launch(null)
    }

    private fun subscribeObserver() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)


            if (state.account != null) {
                usernameET.setText(state.account.username)
                phoneNumberET.setText(state.account.mobile)
                addressET.setText(state.account.address)
                licenseKeyET.setText(state.account.license_key)
                Glide.with(requireContext())
                    .setDefaultRequestOptions(
                        RequestOptions()
                        .placeholder(R.drawable.person)
                        .error(R.drawable.person))
                    .load(state.account.profile_picture)
                    .into(profilePicture)
            }
            if (state.updated) {
                findNavController().popBackStack()
            }
        })
    }

    private fun initClick() {
        updateProfile.setOnClickListener {
            try {
                update()
            }
            catch (e : Exception) {

            }
        }




        profilePicture.setOnClickListener {
            if (uiCommunicationListener.isStoragePermissionGranted()) {
                cropActivityResultLauncher.launch(null)
            }
        }
    }
    private fun update() {
        var username : String = ""
        if (usernameET.text.isNullOrBlank()) {
            usernameET.setError("Error")
            throw Exception("Error")
        }
        else {
            username = usernameET.text.toString()
        }

        var phoneNumber : String = ""
        if (phoneNumberET.text.isNullOrEmpty()) {
            phoneNumberET.setError("Error")
            throw Exception("Error")
        }
        else {
            phoneNumber = phoneNumberET.text.toString()
        }

        var license_key : String = ""
        if (licenseKeyET.text.isNullOrEmpty()) {
            licenseKeyET.setError("Error")
            throw Exception("Error")
        }
        else {
            license_key = licenseKeyET.text.toString()
        }


        var address : String = ""
        if (addressET.text.isNullOrEmpty()) {
            addressET.setError("Error")
            throw Exception("Error")
        }
        else {
            address = addressET.text.toString()
        }


        viewModel.onTriggerEvent(AccountUpdateEvents.AddAccountUpdate(
            username = username,
            mobile = phoneNumber,
            address = address,
            license_key = license_key,
            profile_picture = ""
        ))
    }










    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNav(true)
    }
}