package com.appbytes.pharma_manager.presentation.main.account.updateemployee

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appbytes.pharma_manager.MainActivity
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.auth.BaseAuthFragment
import com.appbytes.pharma_manager.presentation.main.account.createemployee.EmployeeRoleAdapter
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.processQueue
import com.appbytes.pharma_manager.presentation.util.setDivider
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_unit_select.*
import kotlinx.android.synthetic.main.fragment_employee_update.*

class EmployeeUpdateFragment : BaseAuthFragment(), EmployeeRoleAdapter.Interaction, OnCompleteCallback {

    private val viewModel: EmployeeUpdateViewModel by viewModels()
    private var roleRecyclerAdapter : EmployeeRoleAdapter? = null
    private var bottomSheetDialog : BottomSheetDialog? = null
    var pk : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pk = arguments?.getInt("pk", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_employee_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUIClick()
        updateButton.setOnClickListener {
            create()
        }
        subscribeObservers()
    }

    private fun initUIClick() {
        backImage.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        roleEdT.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog?.setContentView(R.layout.dialog_unit_select)
            val list = mutableListOf<String>()
            list.add("Sales Man")
            list.add("Cashier")
            bottomSheetDialog?.dialogClose?.setOnClickListener {
                Log.d(TAG, "Bottom Dialog dismiss")
                bottomSheetDialog?.dismiss()
            }
            initDialogRecyclerAdapter(bottomSheetDialog?.selectUnitRvId!!,list!!)
            bottomSheetDialog?.show()
        }
    }

    private fun initDialogRecyclerAdapter(recyclerView : RecyclerView, list : List<String>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(5)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            roleRecyclerAdapter = EmployeeRoleAdapter(this@EmployeeUpdateFragment)
            adapter = roleRecyclerAdapter
            setDivider(R.drawable.recycler_view_divider)
        }
        roleRecyclerAdapter?.submit(list)
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
                        viewModel.onTriggerEvent(
                            EmployeeUpdateEvents.OnRemoveHeadFromQueue)
                    }
                })
            setField(
                email = state.email,
                username = state.username,
                mobile = state.mobile,
                role = state.role,
                is_active = state.is_active
            )

        }
    }

    private fun setField(
        email : String,
        username : String,
        mobile : String,
        role : String?,
        is_active : Boolean
    ){
        emailId.setText(email)
        userName.setText(username)
        phoneNumberEdT.setText(mobile)
        if (role != null) {
            roleEdT.setText(role)
        }
        else {
            roleEdT.setText("")
        }
        if (is_active) {
            switchMaterial.isChecked = true
        }
        else {
            switchMaterial.isChecked = false
        }
    }

    private fun cacheState(){
        var blank = false
        var is_active1 = switchMaterial.isChecked

        val email1 = emailId.text.toString()
        val username1 = userName.text.toString()
        val mobile1 = phoneNumberEdT.text.toString()
        val role1 = roleEdT.text.toString()
        Log.d(TAG, "First1 " + email1 + "  " +username1 + "   " + mobile1 + " " + role1 + " " + is_active1)


        if (emailId.text.isNullOrBlank()) {
            blank = true
            emailId.error = "Error"
        }
        if (userName.text.isNullOrBlank()) {
            blank = true
            userName.error = "Error"
        }
        if (phoneNumberEdT.text.isNullOrBlank()) {
            blank = true
            phoneNumberEdT.error = "Error"
        }
        if (blank) {
            Log.d(TAG, "Error")
            throw Exception("Error")
        }
        viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateEmail(email1))
        viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateUsername(username1))
        viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateMobile(mobile1))
        viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateRole(role1))
        viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateIsActive(switchMaterial.isChecked))
        val email = emailId.text.toString()
        val username = userName.text.toString()
        val mobile = phoneNumberEdT.text.toString()
        val role = roleEdT.text.toString()
        Log.d(TAG, "Second " + email + "  " +username + "   " + mobile + " " + role + " " + is_active1.toString())
        viewModel.onTriggerEvent(EmployeeUpdateEvents.Update(
            email = email,
            username = username,
            mobile = mobile,
            role = role,
            is_active = is_active1
        ))

    }

    private fun create() {
        try {
            val email = emailId.text.toString()
            val username = userName.text.toString()
            val mobile = phoneNumberEdT.text.toString()
            val role = roleEdT.text.toString()
            Log.d(TAG, "First1 " + email + "  " +username + "   " + mobile + " " + role + " ")
            cacheState()
        }
        catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onItemSelected(position: Int, item: String) {
        roleEdT.setText(item)
        bottomSheetDialog?.dismiss()
    }




    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNav(true)
        if (pk != null && pk!! > 0) {
            viewModel.onTriggerEvent(EmployeeUpdateEvents.GetEmployee(pk!!))
        }
    }

    override fun done() {
        findNavController().popBackStack()
    }
}