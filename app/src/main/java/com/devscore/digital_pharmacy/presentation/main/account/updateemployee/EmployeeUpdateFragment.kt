package com.devscore.digital_pharmacy.presentation.main.account.updateemployee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.MainActivity
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.Employee
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.auth.BaseAuthFragment
import com.devscore.digital_pharmacy.presentation.main.account.createemployee.EmployeeRoleAdapter
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import com.devscore.digital_pharmacy.presentation.util.setDivider
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


            if (state.employee != null) {
                setField(state.employee)
            }
        }
    }

    private fun setField(
        employee : Employee
    ){
        emailId.setText(employee.email)
        userName.setText(employee.username)
        phoneNumberEdT.setText(employee.mobile)
        roleEdT.setText(employee.role)
        if (employee.is_active) {
            switchMaterial.isChecked = true
        }
        else {
            switchMaterial.isChecked = false
        }
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
        if (phoneNumberEdT.text.isNullOrBlank()) {
            blank = true
            phoneNumberEdT.error = "Error"
        }
        if (blank) {
            throw Exception("Error")
        }
        viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateEmail(emailId.text.toString()))
        viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateUsername(userName.text.toString()))
        viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateMobile(phoneNumberEdT.text.toString()))
        viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateRole(roleEdT.text.toString()))
        if (switchMaterial.isChecked) {
            viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateIsActive(true))
        }
        else {
            viewModel.onTriggerEvent(EmployeeUpdateEvents.OnUpdateIsActive(false))
        }
//        viewModel.onTriggerEvent(CreateEmployeeEvents.OnUpdateAddress(addressLineEtvId1.text.toString()))
        viewModel.onTriggerEvent(EmployeeUpdateEvents.Update)

    }

    private fun create() {
        try {
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