package com.devscore.digital_pharmacy.presentation.inventory.dispensing

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.DispensingMedicine
import com.devscore.digital_pharmacy.presentation.inventory.BaseInventoryFragment
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_add_customer.*
import kotlinx.android.synthetic.main.fragment_dispensing.*
import kotlinx.android.synthetic.main.fragment_local.*


class DispensingFragment : BaseInventoryFragment(), DispensingAdapter.Interaction {

    private var recyclerAdapter: DispensingAdapter? = null // can leak memory so need to null
    private val viewModel: DispensingViewModel by viewModels()


    val args : DispensingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dispensing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        subscribeObservers()
        show("H")
    }

    private fun subscribeObservers(){

    }

    private fun executeNewQuery(query: String){
        resetUI()
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        dispensingRecyclerViewID.apply {
            layoutManager = LinearLayoutManager(this@DispensingFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = DispensingAdapter(this@DispensingFragment)
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }

    override fun onItemSelected(position: Int, item: DispensingMedicine) {
    }

    override fun onDispensingCountIncrement(position: Int, item: DispensingMedicine, value: Int) {
        viewModel.onTriggerEvent(DispensingEvents.Count(position, value))
    }

    override fun onDispensingCountDecrement(position: Int, item: DispensingMedicine, value: Int) {
        viewModel.onTriggerEvent(DispensingEvents.Count(position, value))
    }

    override fun onItemDeleteSelected(position: Int, item: DispensingMedicine) {
        show("Hi")
    }

    override fun restoreListPosition() {
    }

    override fun nextPage() {
    }


    fun show(message : String) {
        val dialog = MaterialDialog(requireContext())
        dialog.setContentView(R.layout.dispensing_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

}