package com.appbytes.pharma_manager.presentation.auth.launcher

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.datasource.network.auth.AuthService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_launcher.*
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