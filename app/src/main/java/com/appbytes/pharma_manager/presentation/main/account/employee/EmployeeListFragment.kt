package com.appbytes.pharma_manager.presentation.main.account.employee

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appbytes.pharma_manager.MainActivity
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.Employee
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.inventory.BaseInventoryFragment
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_employee_list.*
import kotlinx.android.synthetic.main.fragment_global.*
import kotlinx.android.synthetic.main.inventory_list_filter_dialog.*


class EmployeeListFragment : BaseInventoryFragment(),
    EmployeeListAdapter.Interaction {

    private var recyclerAdapter: EmployeeListAdapter? = null // can leak memory so need to null
    private val viewModel: EmployeeListViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_employee_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        subscribeObservers()
    }

    private fun initUIClick() {
        backImage.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }






        createUserFloatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_employeeListFragment_to_createEmployeeFragment)
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
                        viewModel.onTriggerEvent(EmployeeListEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(list = state.employeeList, state.isLoading, state.isQueryExhausted)
            }
        })
    }

    private fun executeNewQuery(query: String){
        resetUI()
        viewModel.onTriggerEvent(EmployeeListEvents.UpdateQuery(query))
        viewModel.onTriggerEvent(EmployeeListEvents.NewEmployeeSearch)
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        usersRVId.apply {
            layoutManager = LinearLayoutManager(this@EmployeeListFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(15)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = EmployeeListAdapter(this@EmployeeListFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isLoading}")
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${layoutManager.findLastVisibleItemPosition()}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "GlobalFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(EmployeeListEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    override fun onItemSelected(position: Int, item: Employee) {
        val bundle = bundleOf("pk" to item.pk)
        findNavController().navigate(R.id.action_employeeListFragment_to_employeeUpdateFragment, bundle)
    }

    override fun restoreListPosition() {
    }

    override fun nextPage() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNav(true)
        viewModel.onTriggerEvent(EmployeeListEvents.NewEmployeeSearch)
    }
}