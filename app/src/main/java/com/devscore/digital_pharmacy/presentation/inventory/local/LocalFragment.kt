package com.devscore.digital_pharmacy.presentation.inventory.local

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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryUtils
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryUtils.Companion.BRAND_NAME
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryUtils.Companion.GENERIC
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryUtils.Companion.INDICATION
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryUtils.Companion.SYMPTOM
import com.devscore.digital_pharmacy.business.domain.models.AddMedicine
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.business.interactors.inventory.local.UpdateMedicineInteractor
import com.devscore.digital_pharmacy.presentation.inventory.BaseInventoryFragment
import com.devscore.digital_pharmacy.presentation.inventory.InventoryActivity
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.*
import kotlinx.android.synthetic.main.add_product_dialog.*
import kotlinx.android.synthetic.main.fragment_local.*
import kotlinx.android.synthetic.main.inventory_details_dialog.*
import javax.inject.Inject
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_global.*
import kotlinx.android.synthetic.main.fragment_local.globalFragmentBrandNameAction
import kotlinx.android.synthetic.main.fragment_local.globalFragmentCompanyNameAction
import kotlinx.android.synthetic.main.fragment_local.globalFragmentGenericAction
import kotlinx.android.synthetic.main.fragment_local.globalFragmentIndicationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit
import okhttp3.MultipartBody
import java.io.File


@AndroidEntryPoint
class LocalFragment : BaseInventoryFragment(),
    LocalAdapter.Interaction, LocalMedicineCallback {


    @Inject
    lateinit var updateMedicineInteractor: UpdateMedicineInteractor


    private lateinit var searchView: SearchView
    private var recyclerAdapter: LocalAdapter? = null // can leak memory so need to null
    private val viewModel: LocalMedicineViewModel by viewModels()
    private val disposables = CompositeDisposable()


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
        return inflater.inflate(R.layout.fragment_local, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
//        openImage()
        initUIClick()
        bouncingSearch()
        subscribeObservers()
    }

    private fun openImage() {
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            val list = mutableListOf<MedicineUnits>()
            list.add(
                MedicineUnits(
                    id = -1,
                    name = "p",
                    quantity = 5,
                    type = "SALES"
                )
            )
            list.add(
                MedicineUnits(
                    id = -1,
                    name = "ppp",
                    quantity = 5,
                    type = "SALES"
                )
            )

            val brand_name = RequestBody.create(
                MediaType.parse("text/plain"),
                "napa a"
            )

            val is_percent_discount = RequestBody.create(
                MediaType.parse("text/plain"),
                "true"
            )

            val json = Gson()
            val gson = json.toJson(list)
            val units = RequestBody.create(
                MediaType.parse("application/json"),
                gson
            )



            var multipartBody: MultipartBody.Part? = null
            val imageFile = File(uri.path)
            if(imageFile.exists()){
                val requestBody =
                    RequestBody.create(
                        MediaType.parse("image/jpeg"),
                        imageFile
                    )
                multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )

                val unitsList = mutableListOf<MedicineUnits>()
                unitsList.add(
                    MedicineUnits(
                        id = 65,
                        quantity = 10,
                        type = "OTHER",
                        name = "PCS"
                    )
                )
                unitsList.add(
                    MedicineUnits(
                        id = 66,
                        quantity = 10,
                        type = "OTHER",
                        name = "PCS"
                    )
                )

                val medicine = AddMedicine(
                    brand_name = "Paracitamol7",
                    sku = null,
                    dar_number = "235343",
                    mr_number = null,
                    generic = "",
                    indication = null,
                    symptom = null,
                    strength = null,
                    description = null,
                    image = null,
                    mrp = 9f,
                    purchases_price = 7f,
                    discount = 10000f,
                    is_percent_discount = true,
                    manufacture = "dfsfsdf",
                    kind = "Human",
                    form = "OTHER",
                    remaining_quantity = 15f,
                    damage_quantity = 5f,
                    exp_date = null,
                    rack_number = "abc",
                    units = unitsList
                )

                val launch = CoroutineScope(Dispatchers.IO)
//                updateMedicineInteractor.execute(
//                    id = 58,
//                    medicine = medicine,
//                    image = uri.path
//                ).launchIn(launch)
            }
            Log.d(TAG, uri.toString())
        }

        cropActivityResultLauncher.launch(null)
    }

    private fun initUIClick() {


/*        CoroutineScope(IO).launch {
            val result = inventoryApiService.addMedicine(
                "Token 0c58549b616cba8e1f39a4ed1c86b019b52ea764",
                AddMedicine(
                    brand_name = "Paracitamol7",
                    sku = null,
                    dar_number = null,
                    mr_number = null,
                    generic = null,
                    indication = null,
                    symptom = null,
                    strength = null,
                    description = null,
                    mrp = null,
                    purchases_price = null,
                    discount = null,
                    is_percent_discount = false,
                    manufacture = null,
                    kind = null,
                    form = null,
                    remaining_quantity = null,
                    damage_quantity = null,
                    rack_number = null,
                    units = listOf<MedicineUnits>()
                )
            )
            Log.d(TAG, result.toString())
        }*/


        localFragmentFloatingActionButton.setOnClickListener {
            (activity as InventoryActivity).navigateGlobalFragmentToAddMedicineContainerFragment()
        }

        globalFragmentBrandNameAction.setOnClickListener {
            if (viewModel.state.value?.action.equals(BRAND_NAME)!!) {
                viewModel.onTriggerEvent(LocalMedicineEvents.SetSearchSelection(""))
            }
            else {
                viewModel.onTriggerEvent(LocalMedicineEvents.SetSearchSelection(InventoryUtils.BRAND_NAME))
            }
        }

        globalFragmentGenericAction.setOnClickListener {
            if (viewModel.state.value?.action.equals(GENERIC)!!) {
                viewModel.onTriggerEvent(LocalMedicineEvents.SetSearchSelection(""))
            }
            else {
                viewModel.onTriggerEvent(LocalMedicineEvents.SetSearchSelection(InventoryUtils.GENERIC))
            }
        }

        globalFragmentIndicationAction.setOnClickListener {
            if (viewModel.state.value?.action.equals(INDICATION)!!) {
                viewModel.onTriggerEvent(LocalMedicineEvents.SetSearchSelection(""))
            }
            else {
                viewModel.onTriggerEvent(LocalMedicineEvents.SetSearchSelection(InventoryUtils.INDICATION))
            }
        }


        globalFragmentCompanyNameAction.setOnClickListener {
            if (viewModel.state.value?.action.equals(SYMPTOM)!!) {
                viewModel.onTriggerEvent(LocalMedicineEvents.SetSearchSelection(""))
            }
            else {
                viewModel.onTriggerEvent(LocalMedicineEvents.SetSearchSelection(InventoryUtils.SYMPTOM))
            }
        }
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

//            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(LocalMedicineEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(medicineList = state.localMedicineList, state.isLoading, state.isQueryExhausted)
            }







            when(state.action) {
                InventoryUtils.BRAND_NAME -> {



                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.color.white)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.black))

                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.white))
                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.white))
                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_right)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.white))
                }
                InventoryUtils.GENERIC -> {



                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_left)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.white))

                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.color.white)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.black))

                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.white))

                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_right)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.white))
                }
                InventoryUtils.INDICATION -> {




                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_left)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.white))
                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.white))

                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.color.white)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.black))
                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_right)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.white))
                }
                InventoryUtils.SYMPTOM -> {



                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_left)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.white))

                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.white))
                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.white))

                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.color.white)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.black))
                }
                else -> {
                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_left)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.white))

                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.white))
                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.white))
                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_right)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.white))
                }

            }
        })
    }

    private fun executeNewQuery(query: String){
        resetUI()
        viewModel.onTriggerEvent(LocalMedicineEvents.UpdateQuery(query))
        viewModel.onTriggerEvent(LocalMedicineEvents.NewLocalMedicineSearch)
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        localRvId.apply {
            layoutManager = LinearLayoutManager(this@LocalFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = LocalAdapter(this@LocalFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "GlobalFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(LocalMedicineEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        disposables.dispose()
    }

    override fun onItemSelected(position: Int, item: LocalMedicine) {
//        localMedicineDetails(item)
        if (item.id == null || item.id!! < 1) {
            Toast.makeText(context, "You should sync first", Toast.LENGTH_SHORT).show()
        }
        else {
            (activity as InventoryActivity).navUpdateMedicineFragment(item.id)
        }
    }

    override fun onItemReturnSelected(position: Int, item: LocalMedicine) {
        (activity as InventoryActivity).navigateLocalFragmentToReturnFragment()
    }

    override fun onItemDeleteSelected(position: Int, item: LocalMedicine) {
        viewModel.onTriggerEvent(LocalMedicineEvents.DeleteLocalMedicine(item))
    }

    override fun onItemAddShortList(position: Int, item: LocalMedicine) {
        viewModel.onTriggerEvent(LocalMedicineEvents.AddShortList(item))
    }

    override fun restoreListPosition() {
    }

    override fun nextPage() {
//        viewModel.onTriggerEvent(LocalMedicineEvents.NewLocalMedicineSearch)
    }



    fun localMedicineDetails(item: LocalMedicine) {
        val dialog = MaterialDialog(requireContext())
        dialog.cancelable(false)
        dialog.setContentView(R.layout.inventory_details_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.productDetailsBrandName.setText(item.brand_name)
        dialog.productDetailsMenufactureName.setText(item.manufacture)
        dialog.productDetailsCompanyName.setText(item.generic)
        dialog.productDetailsMRPValue.setText(item.mrp.toString())
        dialog.productDetailsCloseButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.productDetailsCloseIcon.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    fun bouncingSearch() {
        val searchQueryObservable = Observable.create(object : ObservableOnSubscribe<String>{
            override fun subscribe(emitter: ObservableEmitter<String>) {
                localFragmentSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        if (!emitter.isDisposed) {
                            emitter.onNext(newText)
                        }
                        return true
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        executeNewQuery(query)
                        return true
                    }
                })
            }
        })
            .debounce(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(io())


        searchQueryObservable.subscribe(
            object : Observer<String>{
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: String) {
                    Log.d(TAG, t.toString())
//                    if (viewModel.state.value?.query != t.toString()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            executeNewQuery(t)
                        }
//                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            }
        )
    }

    override fun onDeleteDone() {
        viewModel.onTriggerEvent(LocalMedicineEvents.NewLocalMedicineSearch)
    }
}