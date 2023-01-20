package com.appbytes.pharma_manager.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.appbytes.pharma_manager.MainActivity
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.BaseActivity
import com.appbytes.pharma_manager.presentation.session.SessionEvents
import com.appbytes.pharma_manager.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*

@AndroidEntryPoint
class AuthActivity : BaseActivity()
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        subscribeObservers()
    }

    private fun subscribeObservers(){
        sessionManager.state.observe(this) { state ->
            displayProgressBar(state.isLoading)
            processQueue(
                context = this,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        sessionManager.onTriggerEvent(SessionEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
            if (state.didCheckForPreviousAuthUser) {
                onFinishCheckPreviousAuthUser()
            }
            if (state.authToken != null && state.authToken.accountPk != -1) {
                navMainActivity()
            }
        }
    }

    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
        splash_logo.visibility = View.INVISIBLE
    }

    private fun navMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(isLoading: Boolean){
        if(isLoading){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun expandAppBar() {
        // ignore
    }

}
