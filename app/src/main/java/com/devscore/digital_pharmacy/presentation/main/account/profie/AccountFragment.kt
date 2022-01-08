package com.devscore.digital_pharmacy.presentation.main.account.profie

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.devscore.digital_pharmacy.MainActivity
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.customer.details.CustomerDetailsEvents
import com.devscore.digital_pharmacy.presentation.main.BaseMainFragment
import com.devscore.digital_pharmacy.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_settings.logout_button
import kotlinx.android.synthetic.main.inventory_details_dialog.*
import kotlinx.android.synthetic.main.password3_dialog.*


@AndroidEntryPoint
class AccountFragment : BaseMainFragment() {

    private val viewModel : AccountViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClick()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            accountEmail.setText(state.account?.email)
            accountMobile.setText(state.account?.mobile)
            if (state.account?.username != null) {
                useNameTvId.setText(state.account?.username)
            }
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(AccountEvents.OnRemoveHeadFromQueue)
                    }
                })
            Glide.with(requireContext())
                .setDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(R.drawable.person)
                        .error(R.drawable.person))
                .load(state.account?.profile_picture)
                .into(profileImgId)
        })
    }

    private fun initClick() {
        logout_button.setOnClickListener {
            logout()
        }


        passCVId.setOnClickListener {
            changePasswordDialog()
        }


        userCVId.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_employeeListFragment)
        }

        updateAccountCard.setOnClickListener {
            if (viewModel.state.value?.account != null) {
                if (viewModel.state.value?.account?.is_employee!! < 1) {
                    findNavController().navigate(R.id.action_settingsFragment_to_accountUpdateFragment)
                }
                else {
                    Toast.makeText(context, "Your are not owner", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(context, "Account is loading now", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changePasswordDialog() {
        val dialog = MaterialDialog(requireContext())
        dialog.cancelable(true)
        dialog.setContentView(R.layout.password3_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.updateBtnId.setOnClickListener {

            var oldPassword : String? = null
            var newPassword : String? = null
            var confirmPassword : String? = null
            try {
                oldPassword = dialog.oldPassword.text.toString()
            } catch (e : Exception) {
                dialog.oldPassword.error = "Some is wrong"
                return@setOnClickListener
            }

            try {
                newPassword = dialog.newPassword.text.toString()
            } catch (e : Exception) {
                dialog.newPassword.error = "Some is wrong"
                return@setOnClickListener
            }

            try {
                confirmPassword = dialog.confirmPassword.text.toString()
            } catch (e : Exception) {
                dialog.confirmPassword.error = "Some is wrong"
                return@setOnClickListener
            }

            if (oldPassword == null) {
                dialog.oldPassword.error = "Must enter old password"
                return@setOnClickListener
            }
            if (newPassword == null) {
                dialog.newPassword.error = "Must enter old password"
                return@setOnClickListener
            }
            if (confirmPassword == null) {
                dialog.confirmPassword.error = "Must enter old password"
                return@setOnClickListener
            }

            Log.d(TAG, "old " + oldPassword)
            Log.d(TAG, "new " + newPassword)
            Log.d(TAG, "confirm " + confirmPassword)
            viewModel.onTriggerEvent(AccountEvents.ChangePassword(
                currentPassword = oldPassword,
                newPassword = newPassword,
                confirmNewPassword = confirmPassword
            ))
            dialog.dismiss()
        }
        dialog.show()
    }



    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNav(false)
    }


    fun logout(){
        viewModel.onTriggerEvent(AccountEvents.Logout)
    }
}