package com.devscore.digital_pharmacy.presentation.auth.launcher

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.datasource.network.auth.AuthService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_launcher.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class LauncherFragment : Fragment(R.layout.fragment_launcher) {


    @Inject
    lateinit var service: AuthService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("dsf", "dsfd")

        loginBtnId.setOnClickListener {
            navigateLoginFragment()
        }

        registerTvId.setOnClickListener {
            navigateRegisterFragment()
        }
    }


    private fun navigateLoginFragment() {
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)

//            CoroutineScope(IO).launch {
//                try{
//                val oder = AuthService.Oder(
//                    oderItem = AuthService.OderItem(
//                        quantity = 5,
//                        medicine_id = 6
//                    )
//                )
//                val r = service.testpost(
//                    "Token b579361fd986aa325635e924b2e51b88694eef17",
//                    oder
//                )
//                Log.d("TAG", r.toString())
//                }
//                catch (e : Exception){
//                    print(e.toString())
//                }
//            }
    }

    private fun navigateRegisterFragment() {
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }
}