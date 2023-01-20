package com.appbytes.pharma_manager.presentation.main.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbytes.pharma_manager.MainActivity
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.main.BaseMainFragment
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_sales_pay_now.*
import kotlinx.coroutines.*

@AndroidEntryPoint
class NotificationsFragment : BaseMainFragment(){


    private var recyclerAdapter: NotificationAdapter? = null // can leak memory so need to null
    private val viewModel: NotificationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).hideBottomNav(true)
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        subscribeObservers()
        Log.d(TAG, "SalesPayNowFragment ViewModel " + viewModel.toString())
    }

    private fun initUIClick() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            (activity as MainActivity).hideBottomNav(false)
            findNavController().popBackStack()
            Log.d(TAG, "Fragment On Back Press Callback call")
        }

        backImage.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(NotificationEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(state.notificationList)
            }
        })
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
    }

    private fun initRecyclerView(){
        notificationRvId.apply {
            layoutManager = LinearLayoutManager(this@NotificationsFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = NotificationAdapter()
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }

}