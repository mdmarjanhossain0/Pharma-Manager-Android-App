package com.appbytes.pharma_manager.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.auth.BaseAuthFragment
import com.appbytes.pharma_manager.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_launcher.*
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseAuthFragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        authLoginBtnId.setOnClickListener {
            cacheState()
            login()
        }

        resendTvId.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner) { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(LoginEvents.OnRemoveHeadFromQueue)
                    }
                })
        }
    }

    private fun login(){
        viewModel.onTriggerEvent(
            LoginEvents.Login(
            email = phoneNumberEtvIdToggle.text.toString(),
            password = otpEtvIdToggle.text.toString()
        ))
    }

    private fun cacheState(){
        viewModel.onTriggerEvent(LoginEvents.OnUpdateEmail(phoneNumberEtvIdToggle.text.toString()))
        viewModel.onTriggerEvent(LoginEvents.OnUpdatePassword(otpEtvIdToggle.text.toString()))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}