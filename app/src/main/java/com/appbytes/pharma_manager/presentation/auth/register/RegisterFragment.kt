package com.appbytes.pharma_manager.presentation.auth.register


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.viewModels
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.util.Constants.Companion.PRIVACY_POLIC
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.auth.AuthActivity
import com.appbytes.pharma_manager.presentation.auth.BaseAuthFragment
import com.appbytes.pharma_manager.presentation.util.processQueue
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : BaseAuthFragment(), RegisterCallback {

    private val viewModel: RegisterViewModel by viewModels()




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
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUIClick()
        openImage()
        registerBtnId.setOnClickListener {
            if (regCheckBokId.isChecked) {
                register()
            }
        }
        subscribeObservers()
    }

    private fun initUIClick() {
        regCheckBokId.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    registerBtnId.background.setTint(resources.getColor(R.color.colorPrimaryVariant))
                }
                else {
                    registerBtnId.background.setTint(resources.getColor(R.color.register_button_default))
                }
            }

        })




        profileImgId.setOnClickListener {
            if (uiCommunicationListener.isStoragePermissionGranted()) {
                cropActivityResultLauncher.launch(null)
            }
        }



        tcTvId.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(PRIVACY_POLIC)
            startActivity(browserIntent)
        }
    }

    private fun subscribeObservers() {
        viewModel.submit(this)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(RegisterEvents.OnRemoveHeadFromQueue)
                    }
                })
        }
    }

    private fun openImage() {
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                profileImgId.setImageURI(uri)
            }
        }

//        cropActivityResultLauncher.launch(null)
    }

    private fun setRegisterFields(
        email: String,
        username: String,
        password: String,
        confirmPassword: String,
        businessName : String,
        mobile : String,
        license_key : String,
        address : String
    ){
        emailEtvId.setText(email)
        userNameEtvId.setText(username)
        passwordEt.setText(password)
//        password2Et.setText(confirmPassword)
//        businessNameEtv.setText(businessName)
//        phoneNumberEtvId.setText(mobile)
//        pharmacyNumberEtvId.setText(license_key)
        addressLineEtvId1.setText(address)
    }

    private fun cacheState(){
        var blank = false
        if (emailEtvId.text.isNullOrBlank()) {
            blank = true
            emailEtvId.error = "Required"
        }
        else {
            if (!Patterns.EMAIL_ADDRESS.matcher(emailEtvId.text).matches()) {
                blank = true
                emailEtvId.error = "Invalid email address"
            }
        }
        if (shopNameET.text.isNullOrBlank()) {
            blank = true
            shopNameET.error = "Required"
        }
        if (phoneNumberEtvId.text.isNullOrBlank()) {
            blank = true
            phoneNumberEtvId.error = "Required"
        }
        else {
            if (!Patterns.PHONE.matcher(phoneNumberEtvId.text).matches()) {
                blank = true
                phoneNumberEtvId.error = "Invalid phone number"
            }
        }
        if (addressLineEtvId1.text.isNullOrBlank()) {
            blank = true
            addressLineEtvId1.error = "Required"
        }
//
//        if (pharmacyNumberEtvId.text.isNullOrBlank()) {
//            blank = true
//            pharmacyNumberEtvId.error = "Required"
//        }




        if (blank) {
            throw Exception("error")
        }
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateEmail(emailEtvId.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateUsername(userNameEtvId.text.toString()))
//        viewModel.onTriggerEvent(RegisterEvents.OnUpdatePassword(passwordEt.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdatePassword(passwordEt.text.toString()))
//        viewModel.onTriggerEvent(RegisterEvents.OnUpdateConfirmPassword(password2Et.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateConfirmPassword(password2Et.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateMobile(phoneNumberEtvId.text.toString()))
//        viewModel.onTriggerEvent(RegisterEvents.OnUpdateLicenseKey(pharmacyNumberEtvId.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateAddress(addressLineEtvId1.text.toString()))

    }

    private fun register() {
        try {
            cacheState()
            viewModel.onTriggerEvent(RegisterEvents.Register(
                email = emailEtvId.text.toString(),
                username = userNameEtvId.text.toString(),
                shop_name = shopNameET.text.toString(),
                password = passwordEt.text.toString(),
                confirmPassword = password2Et.text.toString(),
                mobile = phoneNumberEtvId.text.toString(),
//                license_key = pharmacyNumberEtvId.text.toString(),
                license_key = "license_key",
                address = addressLineEtvId1.text.toString()
            ))
        }
        catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
//        cacheState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun done() {
//        findNavController().navigate(R.id.action_registerFragment_to_otpVerificationFragment)
        (activity as AuthActivity).onBackPressed()
    }
}