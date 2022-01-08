package com.devscore.digital_pharmacy.presentation.sales

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.devscore.digital_pharmacy.presentation.UICommunicationListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseSalesFragment : Fragment()
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