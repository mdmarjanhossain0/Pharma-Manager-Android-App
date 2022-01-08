package com.devscore.digital_pharmacy.presentation.inventory.add.addmedicine

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.util.MedicineProperties.Companion.OTHER
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.inventory.BaseInventoryFragment
import com.devscore.digital_pharmacy.presentation.util.processQueue
import kotlinx.android.synthetic.main.add_product_dialog.*
import kotlinx.android.synthetic.main.fragment_add_product_sub_medicine.*

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.business.domain.models.SelectMedicineUnit
import com.devscore.digital_pharmacy.business.domain.util.MedicineProperties.Companion.PURCHASES_UNIT
import com.devscore.digital_pharmacy.business.domain.util.MedicineProperties.Companion.SALES_UNIT
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.setDivider
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_unit_select.*

import kotlinx.android.synthetic.main.fragment_global.*


class AddProductSubMedicineFragment : BaseInventoryFragment(), UnitListAdapter.Interaction, SelectUnitAdapter.Interaction {

    private val viewModel: AddMedicineViewModel by viewModels()
    private var recyclerListAdapter : UnitListAdapter? = null
    private var unitRecyclerAdapter : SelectUnitAdapter? = null
    private var bottomSheetDialog : BottomSheetDialog? = null


    private lateinit var unitName : String
    private var unitCount : Int = 0


    private val unitList = ArrayList<MedicineUnits>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_product_sub_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        initUI()
        getData()
        subscribeObservers()
    }

    private fun initRecyclerView() {
        addMedicineUnitsRvId.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(5)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

                recyclerListAdapter = UnitListAdapter(this@AddProductSubMedicineFragment)
                adapter = recyclerListAdapter
//                setDivider(R.drawable.recycler_view_divider)
        }
    }

    private fun getData() {
        val id = arguments?.getInt("id", -1)
        Log.d(TAG, arguments.toString())
        Log.d(TAG, "id " + id.toString())
        if (id != -1) {
            viewModel.onTriggerEvent(AddMedicineEvents.UpdateId(id!!))
            viewModel.onTriggerEvent(AddMedicineEvents.FetchData)
        }
    }

    private fun initUI() {
//        val kindList = ArrayList<String>()
//        kindList.add("Human")
//        kindList.add("Veterinary")
//        val kindAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            kindList
//        )
//
//        kindAdapter.setDropDownViewResource(
//            android.R.layout
//                .simple_spinner_dropdown_item
//        )
//
//        kindET.setAdapter(kindAdapter)
//
//        val formList = ArrayList<String>()
//        formList.add("TABLET")
//        formList.add("LIQUITE")
//        formList.add("OTHER")
//        val formAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            formList
//        )
//
//        formAdapter.setDropDownViewResource(
//            android.R.layout
//                .simple_spinner_dropdown_item
//        )
//
//        formET.setAdapter(formAdapter)

    }

    private fun initUIClick() {
        addNewMeasureUnit.setOnClickListener {
            addMeasuringUnit()
        }

        minimumStockIncrementTV.setOnClickListener {
            val value = minimumStockCount.text.toString().toInt() + 1
            minimumStockCount.setText(value.toString())
        }

        minimumStockDecrementTV.setOnClickListener {
            val value = minimumStockCount.text.toString().toInt() - 1
            minimumStockCount.setText(value.toString())
        }


        addMedicineAdd.setOnClickListener {
            cacheState()
            viewModel.onTriggerEvent(AddMedicineEvents.NewAddMedicine)
        }



        salesUnit.setOnClickListener {
            salesUnitSelectDialog()
        }

        symtomORPurchasesUnit.setOnClickListener {
            purchasesUnitSelectDialog()
        }




//        tv16.setOnClickListener {
//            val bottomSheetDialog = BottomSheetDialog(requireContext())
//            bottomSheetDialog.setContentView(com.devscore.digital_pharmacy.R.layout.fragment_unit_select)
//            bottomSheetDialog.show()
//        }
    }

    private fun purchasesUnitSelectDialog() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(R.layout.dialog_unit_select)
        val list = viewModel.state.value?.unitList?.map {
            SelectMedicineUnit(
                unit = it
            )
        }
        viewModel.onTriggerEvent(AddMedicineEvents.UpdateAction(PURCHASES_UNIT))
        initDialogRecyclerAdapter(bottomSheetDialog?.selectUnitRvId!!,list!!)
        bottomSheetDialog?.show()
    }

    private fun salesUnitSelectDialog() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(R.layout.dialog_unit_select)
        val list = viewModel.state.value?.unitList?.map {
            SelectMedicineUnit(
                unit = it
            )
        }
        viewModel.onTriggerEvent(AddMedicineEvents.UpdateAction(SALES_UNIT))
        initDialogRecyclerAdapter(bottomSheetDialog?.selectUnitRvId!!,list!!)
        bottomSheetDialog?.dialogClose?.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
        bottomSheetDialog?.show()
    }

    private fun initDialogRecyclerAdapter(recyclerView : RecyclerView, list : List<SelectMedicineUnit>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(5)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            unitRecyclerAdapter = SelectUnitAdapter(this@AddProductSubMedicineFragment)
            adapter = unitRecyclerAdapter
            setDivider(R.drawable.recycler_view_divider)
        }
        unitRecyclerAdapter?.submit(list)
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(AddMedicineEvents.OnRemoveHeadFromQueue)
                    }
                })
            Log.d(TAG, state.medicine.toString())



            if (state.globalMedicine != null) {
                updateField(state.globalMedicine)
            }



            recyclerListAdapter?.submit(state.unitList)

            if (state.salesUnit != null) {
                salesUnit.setText(state.salesUnit.name)
            }



            if (state.purchasesUnit != null) {
                symtomORPurchasesUnit.setText(state.purchasesUnit.name)
            }
        })
    }

    private fun updateField(globalMedicine : GlobalMedicine?) {
        brand_nameET.setText(globalMedicine?.brand_name.toString())
        if (globalMedicine?.darNumber != null) {
            dar_mr_NumberET.setText(globalMedicine?.darNumber)
        }

        if (globalMedicine?.manufacture != null) {
            manufactureET.setText(globalMedicine.manufacture.toString())
        }

        if (globalMedicine?.generic != null) {
            genericET.setText(globalMedicine?.generic.toString())
        }

        kindET.setText("Human")
        formET.setText("TABLET")

        if (globalMedicine?.strength != null) {
            strengthET.setText(globalMedicine?.strength.toString())
        }
    }


    fun cacheState() {
        try {
            val brand_name = brand_nameET.text.toString()
            val dar_number = dar_mr_NumberET.text.toString()
            val manufacture = manufactureET.text.toString()
            val generic = genericET.text.toString()
//        val kind = kindET.text.toString()
            val kind = "Human"
//        val form = formET.text.toString()
            val form = "TABLET"
            val strength = strengthET.text.toString()
//        val salesUnit = salesUnit.text.toString()
//        val purchasesUnit = symtomORPurchasesUnit.text.toString()
            val mrp = mrpET.text.toString().toFloat()
            val purchases_price = purchases_price.text.toString().toFloat()
            val stock = minimumStockCount.text.toString().toInt()

            val local_medicine = LocalMedicine(
                id = -1,
                brand_name = brand_name,
                sku = null,
                dar_number = dar_number,
                manufacture = manufacture,
                generic = generic,
                indication = null,
                symptom = null,
                description = null,
                image = null,
                kind = kind,
                form = form,
                strength = strength,
                mrp = mrp,
                purchase_price = purchases_price,
                exp_date = null,
                remaining_quantity = stock.toFloat(),
                units = getUnits()
            )
            viewModel.onTriggerEvent(AddMedicineEvents.CacheState(local_medicine))
        }
        catch (e : Exception) {
            MaterialDialog(requireContext())
                .show{
                    title(R.string.text_info)
                    message(text = "Some thing is wrong")
                    onDismiss {
                    }
                    cancelable(true)
                }
        }

    }

    private fun getUnits(): List<MedicineUnits> {
        viewModel.state.value?.let { state ->
            val list = mutableListOf<MedicineUnits>()
            for (item in state.unitList) {
                if (state.salesUnit != null) {
                    if (item.id == state.salesUnit.id) {
                        list.add(
                            item.copy(
                                type = SALES_UNIT
                            )
                        )
                        continue
                    }
                }
                if (state.purchasesUnit != null) {
                    if (item.id == state.purchasesUnit.id) {
                        list.add(
                            item.copy(
                                type = PURCHASES_UNIT
                            )
                        )
                        continue
                    }
                }
                list.add(item)
            }
            return state.unitList
        }
        return listOf()
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }


    fun addMeasuringUnit() {
        val dialog = MaterialDialog(requireContext())
        dialog.cancelable(false)
        dialog.setContentView(R.layout.add_product_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var count = dialog.unitCountET.text.toString().toInt()
        dialog.unitCountIncrementTV.setOnClickListener {
            count = count + 1
            dialog.unitCountET.setText(count.toString())
        }

        dialog.unitCountDecrementTV.setOnClickListener {
            count = count - 1
            dialog.unitCountET.setText(count.toString())
        }
        dialog.addMedicineClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.addMedicineUnitAdd.setOnClickListener {
            val unitName = dialog.unitNameET.text.toString()
            if (unitName == null || count < 1) {
                Toast.makeText(context, "Something is wrong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            this.unitName = unitName
            this.unitCount = count


//            unitList.add(
//                MedicineUnits(
//                    id = -1,
//                    name = unitName,
//                    quantity = unitCount,
//                    type = OTHER
//                )
//            )
            viewModel.onTriggerEvent(AddMedicineEvents.UpdateUnitList(
                MedicineUnits(
                    id = -1,
                    name = unitName,
                    quantity = unitCount,
                    type = OTHER
                )
            ))
            updateSpinner()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateSpinner() {
//        val newList = ArrayList<String>()
//        for (a in unitList) {
//            newList.add(a.name)
//        }
//        val kindAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            newList
//        )
//
//        kindAdapter.setDropDownViewResource(
//            android.R.layout
//                .simple_spinner_dropdown_item
//        )
//
//        salesUnit.setAdapter(kindAdapter)
//        symtomORPurchasesUnit.setAdapter(kindAdapter)
    }

    override fun onItemSelected(position: Int, item: MedicineUnits) {
        viewModel.onTriggerEvent(AddMedicineEvents.RemoveUnit(item))
        updateSpinner()
    }

    override fun onItemSelectedUnit(position: Int, item: SelectMedicineUnit) {
        if (viewModel.state.value?.action.equals(SALES_UNIT)) {
            viewModel.onTriggerEvent(AddMedicineEvents.UpdateSalesUnit(item.unit))
        }
        if (viewModel.state.value?.action.equals(PURCHASES_UNIT)) {
            viewModel.onTriggerEvent(AddMedicineEvents.UpdatePurchasesUnit(item.unit))
        }
        viewModel.onTriggerEvent(AddMedicineEvents.UpdateAction(""))
        bottomSheetDialog?.dismiss()
        bottomSheetDialog = null
    }

}