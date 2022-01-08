package com.devscore.digital_pharmacy.presentation.main.report

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.Report
import com.devscore.digital_pharmacy.presentation.util.GenericViewHolder
import kotlinx.android.synthetic.main.item_cash_report.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReportAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "ReportAdapter"


    val loadingItem = Report(
        pk = -2,
        amount = 0f,
        type = "",
        details = "",
        remark = "",
        created_at = ""
    )

    val notFound = Report(
        pk = -3,
        amount = 0f,
        type = "",
        details = "",
        remark = "",
        created_at = ""
    )

    companion object {

        const val IMAGE_ITEM = 1
        const val LOADING_ITEM = 2
        const val NOT_FOUND = 3

        const val LOADING = 1
        const val RETRY =2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            IMAGE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cash_report,parent,false)
                return LocalDataViewHolder(itemView, interaction)
            }

            LOADING_ITEM -> {
                Log.d(TAG, "onCreateViewHolder: No more results...")
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_loading,
                        parent,
                        false
                    )
                )
            }

            else -> {
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_result_not_found,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList.size != 0) {
            if(differ.currentList.get(position).pk == -2){
                return ReportAdapter.LOADING_ITEM
            }
            if(differ.currentList.get(position).pk == -3){
                return ReportAdapter.NOT_FOUND
            }
            return ReportAdapter.IMAGE_ITEM
        }
        else {
            return ReportAdapter.LOADING_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LocalDataViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG, "GlobalAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Report>() {

        override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            LocalRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class LocalRecyclerChangeCallback(
        private val adapter: ReportAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    fun submitList(reportList : List<Report>?, isLoading : Boolean = true, queryExhausted : Boolean = false){
        val newList = reportList?.toMutableList()
        if (isLoading) {
            newList?.add(loadingItem)
        }
        else {
            if (queryExhausted) {
                newList?.add(notFound)
            }
        }
        differ.submitList(newList)
    }

    fun changeBottom(bottomState : Int) {
    }

    class LocalDataViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Report) = with(itemView) {
            val timeZone = TimeZone.getTimeZone("Asia/Dhaka")
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy hh:mm")
            val sourceFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val destFormat = SimpleDateFormat("dd/MM/yyyy HH:mm aa")
            sourceFormat.timeZone = timeZone
            val convertedDate = sourceFormat.parse(item.created_at)
            val newDate = destFormat.format(convertedDate)
            Log.d("AppDebug", " " + item.created_at + " " + newDate)

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }


            itemView.reportDate.setText(newDate)
            itemView.reportAmount.setText("Total: ৳ " + item.amount)
            itemView.reportDetails.setText(item.details)
            itemView.reportId.setText("#" + item.type + " " + item.pk)
            if (item.remark != null) {
                itemView.reportOwner.setText(item.remark + "")
            }
            else {
                itemView.reportOwner.setText("")
            }

        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: Report)

        fun onItemReturnSelected(position: Int, item: Report)

        fun onItemDeleteSelected(position: Int, item: Report)

        fun restoreListPosition()

        fun nextPage()
    }
}