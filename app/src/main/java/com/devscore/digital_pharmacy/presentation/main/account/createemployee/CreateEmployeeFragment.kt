package com.devscore.digital_pharmacy.presentation.main.account.createemployee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.MainActivity
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.auth.BaseAuthFragment
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import com.devscore.digital_pharmacy.presentation.util.setDivider
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_unit_select.*
import kotlinx.android.synthetic.main.fragment_create_employee.*

class CreateEmployeeFragment : BaseAuthFragment(), EmployeeRoleAdapter.Interaction {

    private val viewModel: CreateEmployeeViewModel by viewModels()
    private var roleRecyclerAdapter : EmployeeRoleAdapter? = null
    private var bottomSheetDialog : BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_employee, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUIClick()
        createBtnId.setOnClickListener {
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

            roleRecyclerAdapter = EmployeeRoleAdapter(this@CreateEmployeeFragment)
            adapter = roleRecyclerAdapter
            setDivider(R.drawable.recycler_view_divider)
        }
        roleRecyclerAdapter?.submit(list)
    }

    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(
                            CreateEmployeeEvents.OnRemoveHeadFromQueue)
                    }
                })
        }
    }

    private fun setRegisterFields(
        email: String,
        username: String,
        password: String,
        confirmPassword: String,
        mobile : String,
        role : String,
        address : String
    ){
        emailId.setText(email)
        userName.setText(username)
        passwordEdT.setText(password)
        confirmPasswordEdT.setText(confirmPassword)
        phoneNumberEdT.setText(mobile)
        roleEdT.setText(role)
    }

    private fun cacheState(){
        var blank = false





        if (emailId.text.isNullOrBlank()) {
            blank = true
            emailId.error = "Error"
        }
        if (userName.text.isNullOrBlank()) {
            blank = true
            userName.error = "Error"
        }
        if (passwordEdT.text.isNullOrBlank()) {
            blank = true
            passwordEdT.error = "Error"
        }
        if (confirmPasswordEdT.text.isNullOrBlank()) {
            blank = true
            confirmPasswordEdT.error = "Error"
        }
        if (phoneNumberEdT.text.isNullOrBlank()) {
            blank = true
            phoneNumberEdT.error = "Error"
        }
        if (blank) {
            throw Exception("Error")
        }
        viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdateEmail(emailId.text.toString()))
        viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdateUsername(userName.text.toString()))
        viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdatePassword(passwordEdT.text.toString()))
        viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdateConfirmPassword(confirmPasswordEdT.text.toString()))
        viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdateMobile(phoneNumberEdT.text.toString()))
        viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdateRole(roleEdT.text.toString()))
        if (switchMaterial.isChecked) {
            viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdateIsActive(true))
        }
        else {
            viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdateIsActive(false))
        }
//        viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdateAddress(addressLineEtvId1.text.toString()))
        viewModel.onTriggerEvent(CreateEmployeeEvents.Create)

    }

    private fun create() {
        try {
            cacheState()
        }
        catch (e : Exception) {

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
    }




    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNav(true)
    }
}