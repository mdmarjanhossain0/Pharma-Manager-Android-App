package com.appbytes.pharma_manager.presentation.auth.forgot_password

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.util.*
import com.appbytes.pharma_manager.presentation.auth.BaseAuthFragment
import com.appbytes.pharma_manager.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordFragment : BaseAuthFragment() {

    private lateinit var webView: WebView

    private val viewModel: ForgotPasswordViewModel by viewModels()

    private val webInteractionCallback = object : WebAppInterface.OnWebInteractionCallback {

        override fun onError(errorMessage: String) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.onTriggerEvent(
                    ForgotPasswordEvents.Error(
                        stateMessage = StateMessage(
                            response = Response(
                                message = errorMessage,
                                uiComponentType = UIComponentType.Dialog(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                )
            }
        }

        override fun onSuccess(email: String) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.onTriggerEvent(ForgotPasswordEvents.OnPasswordResetLinkSent)
            }
        }

        override fun onLoading(isLoading: Boolean) {
            CoroutineScope(Dispatchers.Main).launch {
                uiCommunicationListener.displayProgressBar(isLoading)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webview)

        loadPasswordResetWebView()

        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ForgotPasswordEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
            if (state.isPasswordResetLinkSent) {
                onPasswordResetLinkSent()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPasswordResetWebView() {
        uiCommunicationListener.displayProgressBar(true)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                uiCommunicationListener.displayProgressBar(false)
            }
        }
        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(
            WebAppInterface(webInteractionCallback),
            "AndroidTextListener"
        )
    }

    private class WebAppInterface
    constructor(
        private val callback: OnWebInteractionCallback
    ) {

        private val TAG: String = "AppDebug"

        @JavascriptInterface
        fun onSuccess(email: String) {
            callback.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(errorMessage: String) {
            callback.onError(errorMessage)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean) {
            callback.onLoading(isLoading)
        }

        interface OnWebInteractionCallback {

            fun onSuccess(email: String)

            fun onError(errorMessage: String)

            fun onLoading(isLoading: Boolean)
        }
    }

    private fun onPasswordResetLinkSent() {
        webView.destroy()

        val animation = TranslateAnimation(
            password_reset_done_container.width.toFloat(),
            0f,
            0f,
            0f
        )
        animation.duration = 500
        password_reset_done_container.startAnimation(animation)
        password_reset_done_container.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}