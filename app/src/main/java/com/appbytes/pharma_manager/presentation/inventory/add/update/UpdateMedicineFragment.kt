package com.appbytes.pharma_manager.presentation.inventory.add.update

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.models.SelectMedicineUnit
import com.appbytes.pharma_manager.business.domain.util.MedicineProperties
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.inventory.BaseInventoryFragment
import com.appbytes.pharma_manager.presentation.inventory.add.addmedicine.SelectUnitAdapter
import com.appbytes.pharma_manager.presentation.inventory.add.addmedicine.UnitListAdapter
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.processQueue
import com.appbytes.pharma_manager.presentation.util.setDivider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.add_product_dialog.*
import kotlinx.android.synthetic.main.dialog_unit_select.*
import kotlinx.android.synthetic.main.fragment_add_product_sub_medicine.*
import kotlinx.android.synthetic.main.fragment_update_medicine.*
import kotlinx.android.synthetic.main.fragment_update_medicine.addMedicineAdd
import kotlinx.android.synthetic.main.fragment_update_medicine.addMedicineUnitsRvId
import kotlinx.android.synthetic.main.fragment_update_medicine.addNewMeasureUnit
import kotlinx.android.synthetic.main.fragment_update_medicine.brand_nameET
import kotlinx.android.synthetic.main.fragment_update_medicine.dar_mr_NumberET
import kotlinx.android.synthetic.main.fragment_update_medicine.expDate
import kotlinx.android.synthetic.main.fragment_update_medicine.formET
import kotlinx.android.synthetic.main.fragment_update_medicine.genericET
import kotlinx.android.synthetic.main.fragment_update_medicine.kindET
import kotlinx.android.synthetic.main.fragment_update_medicine.manufactureET
import kotlinx.android.synthetic.main.fragment_update_medicine.minimumStockCount
import kotlinx.android.synthetic.main.fragment_update_medicine.minimumStockDecrementTV
import kotlinx.android.synthetic.main.fragment_update_medicine.minimumStockIncrementTV
import kotlinx.android.synthetic.main.fragment_update_medicine.mrpET
import kotlinx.android.synthetic.main.fragment_update_medicine.profileImgId
import kotlinx.android.synthetic.main.fragment_update_medicine.purchases_price
import kotlinx.android.synthetic.main.fragment_update_medicine.salesUnit
import kotlinx.android.synthetic.main.fragment_update_medicine.strengthET
import kotlinx.android.synthetic.main.fragment_update_medicine.symtomORPurchasesUnit
import java.util.*
import kotlin.collections.ArrayList


class UpdateMedicineFragment : BaseInventoryFragment(), UnitListAdapter.Interaction, SelectUnitAdapter.Interaction, Callback {

    private val viewModel: UpdateMedicineViewModel by viewModels()
    private var recyclerListAdapter : UnitListAdapter? = null
    private var unitRecyclerAdapter : SelectUnitAdapter? = null
    private var bottomSheetDialog : BottomSheetDialog? = null


    private lateinit var unitName : String
    private var unitCount : Int = 0




    private lateinit var datePicker : DatePickerDialog


    private val unitList = ArrayList<MedicineUnits>()





    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_update_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        initUI()
        getData()
        openImage()
        subscribeObservers()
    }




    private fun openImage() {
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                profileImgId.setImageURI(uri)
                viewModel.onTriggerEvent(UpdateMedicineEvents.UpdateImage(uri.path))
            }
        }

//        cropActivityResultLauncher.launch(null)
    }

    private fun initRecyclerView() {
        addMedicineUnitsRvId.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(5)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerListAdapter = UnitListAdapter(this@UpdateMedicineFragment)
            adapter = recyclerListAdapter
//                setDivider(R.drawable.recycler_view_divider)
        }
    }

    private fun getData() {
        val id = arguments?.getInt("id", -1)
        Log.d(TAG, arguments.toString())
        Log.d(TAG, "id " + id.toString())
        if (id != -1) {
            viewModel.onTriggerEvent(UpdateMedicineEvents.UpdateId(id!!))
            viewModel.onTriggerEvent(UpdateMedicineEvents.FetchData)
        }
    }

    private fun initUI() {

    }

    private fun initUIClick() {
        addNewMeasureUnit.setOnClickListener {
            addMeasuringUnit()
        }

        minimumStockIncrementTV.setOnClickListener {
            val value = minimumStockCount.text.toString().toFloat().toInt() + 1
            minimumStockCount.setText(value.toString())
        }

        minimumStockDecrementTV.setOnClickListener {
            val value = minimumStockCount.text.toString().toFloat().toInt() - 1
            minimumStockCount.setText(value.toString())
        }


        addMedicineAdd.setOnClickListener {
            try {
                cacheState()
                viewModel.onTriggerEvent(UpdateMedicineEvents.NewUpdateMedicine)
            }catch (e : Exception) {
                e.printStackTrace()
//                MaterialDialog(requireContext())
//                    .show{
//                        title(R.string.text_info)
//                        message(text = "Some thing is wrong")
//                        onDismiss {
//                        }
//                        cancelable(true)
//                    }
            }
        }



        salesUnit.setOnClickListener {
            salesUnitSelectDialog()
        }

        symtomORPurchasesUnit.setOnClickListener {
            purchasesUnitSelectDialog()
        }

        profileImgId.setOnClickListener {
            if (uiCommunicationListener.isStoragePermissionGranted()) {
                cropActivityResultLauncher.launch(null)
            }
        }

        val dataPickerListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val date = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
                expDate.setText(date)
                Log.d(TAG, "Year " + year + " Month " + month + " Day " + dayOfMonth)
            }

        }


        expDate.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val monty = calender.get(Calendar.MONTH)
            val date = calender.get(Calendar.DATE)
            datePicker = DatePickerDialog(requireContext(),dataPickerListener, year, monty, date)
            datePicker.show()
        }


    }

    private fun purchasesUnitSelectDialog() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(R.layout.dialog_unit_select)
        val list = viewModel.state.value?.unitList?.map {
            SelectMedicineUnit(
                unit = it
            )
        }
        viewModel.onTriggerEvent(UpdateMedicineEvents.UpdateAction(MedicineProperties.PURCHASES_UNIT))
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
        viewModel.onTriggerEvent(UpdateMedicineEvents.UpdateAction(MedicineProperties.SALES_UNIT))
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

            unitRecyclerAdapter = SelectUnitAdapter(this@UpdateMedicineFragment)
            adapter = unitRecyclerAdapter
            setDivider(R.drawable.recycler_view_divider)
        }
        unitRecyclerAdapter?.submit(list)
    }

    private fun subscribeObservers(){
        viewModel.submit(this)
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(UpdateMedicineEvents.OnRemoveHeadFromQueue)
                    }
                })
            Log.d(TAG, state.medicine.toString())



            if (state.medicine != null) {
                updateField(state.medicine)
            }



            recyclerListAdapter?.submit(state.unitList)

            if (state.salesUnit != null) {
                salesUnit.setText(state.salesUnit.name)
            }



            if (state.purchasesUnit != null) {
                symtomORPurchasesUnit.setText(state.purchasesUnit.name)
            }


            if (state.medicine.image != null) {
                Glide
                    .with(requireContext())
                    .load(state.medicine.image)
                    .into(profileImgId)
            }
        })
    }

    private fun updateField(medicine : LocalMedicine?) {
        brand_nameET.setText(medicine?.brand_name.toString())
        if (medicine?.dar_number != null) {
            dar_mr_NumberET.setText(medicine?.dar_number)
        }

        if (medicine?.manufacture != null) {
            manufactureET.setText(medicine.manufacture.toString())
        }

        if (medicine?.generic != null) {
            genericET.setText(medicine?.generic.toString())
        }





        if (medicine?.kind != null) {
            kindET.setText(medicine.kind)
        }



        if (medicine?.form != null) {
            formET.setText(medicine.form)
        }

        if (medicine?.strength != null) {
            strengthET.setText(medicine?.strength.toString())
        }

        if (medicine?.mrp != null) {
            mrpET.setText(medicine.mrp.toString())
        }

        if (medicine?.purchase_price != null) {
            purchases_price.setText(medicine.purchase_price.toString())
        }

        if (medicine?.remaining_quantity != null) {
            minimumStockCount.setText(medicine.remaining_quantity.toString())
        }


        if (medicine?.exp_date != null) {
            expDate.setText(medicine.exp_date)
        }
    }


    fun cacheState() {
        var dar_number : String = ""
        if (dar_mr_NumberET.text.isNullOrEmpty()) {
            dar_mr_NumberET.setError("Error")
            throw Exception("Error")
        }
        else {

            dar_number = dar_mr_NumberET.text.toString()
        }
            var brand_name : String = ""
            if (brand_nameET.text.isNullOrBlank()) {
                brand_nameET.setError("Error")
                throw Exception("Error")
            }
            else {
                 brand_name = brand_nameET.text.toString()
            }




            var manufacture : String = ""
            if (manufactureET.text.isNullOrEmpty()) {
                manufactureET.setError("Error")
                throw Exception("Error")
            }
            else {
                manufacture = manufactureET.text.toString()
            }

            var generic : String = ""
            if (genericET.text.isNullOrEmpty()) {
                genericET.setError("Error")
                throw Exception("Error")
            }
            else {
                generic = genericET.text.toString()
            }

            var kind : String = ""
            if (kindET.text.isNullOrEmpty()) {
                kindET.setError("Error")
                throw Exception("Error")
            }
            else {
                kind = kindET.text.toString()
            }
//            val kind = "Human"
            var form : String = ""
            if (formET.text.isNullOrEmpty()) {
                formET.setError("Error")
                throw Exception("Error")
            }
            else {
                form = formET.text.toString()
            }
//            val form = "TABLET"
            var strength : String = ""
            if (strengthET.text.isNullOrEmpty()) {
                strengthET.setError("Error")
                throw Exception("Error")
            }
            else {

                strength = strengthET.text.toString()
            }
//        val salesUnit = salesUnit.text.toString()
//        val purchasesUnit = symtomORPurchasesUnit.text.toString()







        var date : String = ""
        if (expDate.text.isNullOrEmpty()) {
            expDate.setError("Error")
            throw Exception("Error")
        }
        else {

            date = expDate.text.toString()
        }
            var mrp : Float = 0.0f
            if (mrpET.text.isNullOrEmpty()) {
                mrpET.setError("Error")
                throw Exception("Error")
            }
            else {
                mrp = mrpET.text.toString().toFloat()
            }
            var purchase_price : Float = 0.0f
            if (purchases_price.text.isNullOrEmpty()) {
                purchases_price.setError("Error")
                throw Exception("Error")
            }
            else {
                purchase_price = purchases_price.text.toString().toFloat()
            }
            var stock : Int = 0
            if (minimumStockCount.text.isNullOrEmpty()) {
                minimumStockCount.setError("Error")
                throw Exception("Error")
            }
            else {

                stock = minimumStockCount.text.toString().toFloat().toInt()
            }


        Log.d(TAG, "Cach")

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
                purchase_price = purchase_price,
                remaining_quantity = stock.toFloat(),
                exp_date = date,
                units = getUnits()
            )
            viewModel.onTriggerEvent(UpdateMedicineEvents.CacheState(local_medicine))

    }

    private fun getUnits(): List<MedicineUnits> {
        viewModel.state.value?.let { state ->
            val list = mutableListOf<MedicineUnits>()
            for (item in state.unitList) {
                if (state.salesUnit != null) {
                    if (item.id == state.salesUnit.id) {
                        list.add(
                            item.copy(
                                type = MedicineProperties.SALES_UNIT
                            )
                        )
                        continue
                    }
                }
                if (state.purchasesUnit != null) {
                    if (item.id == state.purchasesUnit.id) {
                        list.add(
                            item.copy(
                                type = MedicineProperties.PURCHASES_UNIT
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

            viewModel.onTriggerEvent(
                UpdateMedicineEvents.UpdateUnitList(
                    MedicineUnits(
                        id = -1,
                        name = unitName,
                        quantity = unitCount,
                        type = MedicineProperties.OTHER
                    )
                ))
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onItemSelected(position: Int, item: MedicineUnits) {
        viewModel.onTriggerEvent(UpdateMedicineEvents.RemoveUnit(item))
    }

    override fun onItemSelectedUnit(position: Int, item: SelectMedicineUnit) {
        if (viewModel.state.value?.action.equals(MedicineProperties.SALES_UNIT)) {
            viewModel.onTriggerEvent(UpdateMedicineEvents.UpdateSalesUnit(item.unit))
        }
        if (viewModel.state.value?.action.equals(MedicineProperties.PURCHASES_UNIT)) {
            viewModel.onTriggerEvent(UpdateMedicineEvents.UpdatePurchasesUnit(item.unit))
        }
        viewModel.onTriggerEvent(UpdateMedicineEvents.UpdateAction(""))
        bottomSheetDialog?.dismiss()
        bottomSheetDialog = null
    }

    override fun done() {
        findNavController().popBackStack()
    }

}