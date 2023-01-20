package com.appbytes.pharma_manager.presentation.cashregister

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.appbytes.pharma_manager.presentation.UICommunicationListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseCashRegisterFragment : Fragment()
{

    val TAG: String = "AppDebug"

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onAttach(context: Context) {
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }
        super.onAttach(context)

    }
}