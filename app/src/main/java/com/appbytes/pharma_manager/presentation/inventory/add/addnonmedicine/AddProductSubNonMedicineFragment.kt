package com.appbytes.pharma_manager.presentation.inventory.add.addnonmedicine

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
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.util.MedicineProperties
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.inventory.BaseInventoryFragment
import com.appbytes.pharma_manager.presentation.inventory.add.addmedicine.AddMedicineEvents
import com.appbytes.pharma_manager.presentation.inventory.add.addmedicine.AddMedicineViewModel
import com.appbytes.pharma_manager.presentation.util.processQueue
import kotlinx.android.synthetic.main.add_product_dialog.*
import kotlinx.android.synthetic.main.fragment_add_product_sub_medicine.*
import kotlinx.android.synthetic.main.fragment_add_product_sub_non_medicine.*

class AddProductSubNonMedicineFragment : BaseInventoryFragment() {

    private val viewModel: AddMedicineViewModel by viewModels()


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
        initUIClick()
        initUI()
        subscribeObservers()
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
        /*addNewMeasureUnit.setOnClickListener {
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
        }*/
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
        })
    }


    fun cacheState() {
//        val brand_name = brand_nameET.text.toString()
//        val dar_number = dar_mr_NumberET.text.toString()
//        val manufacture = manufactureET.text.toString()
//        val generic = genericET.text.toString()
////        val kind = kindET.text.toString()
//        val kind = "Human"
////        val form = formET.text.toString()
//        val form = "TABLET"
//        val strength = strengthET.text.toString()
//        val salesUnit = salesUnit.text.toString()
//        val purchasesUnit = symtomORPurchasesUnit.text.toString()
//        val mrp = mrpET.text.toString().toInt()
//        val purchases_price = purchases_price.text.toString().toInt()
//        val minimum_stock = minimumStockCount.text.toString().toInt()

//        val local_medicine = LocalMedicine(
//            id = -1,
//            brand_name = brand_name,
//            sku = null,
//            dar_number = dar_number,
//            manufacture = manufacture,
//            generic = generic,
//            indication = null,
//            symptom = null,
//            description = null,
//            kind = kind,
//            form = form,
//            strength = strength,
//            mrp = mrp,
//            purchases_price = purchases_price,
//            units = unitList
//        )
//        viewModel.onTriggerEvent(AddMedicineEvents.CacheState(local_medicine))
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


            unitList.add(
                MedicineUnits(
                    id = -1,
                    name = unitName,
                    quantity = unitCount,
                    type = MedicineProperties.OTHER
                )
            )
            dialog.dismiss()
        }
        dialog.show()
    }

}