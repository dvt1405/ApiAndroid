package tun.kt.apilib.apihistory

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tun.kt.apilib.R

class ApiHistoryAdapter(
    private val context: Context,
    private var listApiCall: HistoryApiCall,
    private val callback: OnItemClickListener
) :
    RecyclerView.Adapter<ApiHistoryAdapter.ApiHistoryViewHolder>() {

    public var apiHistory: HistoryApiCall
        get() = listApiCall
        set(value) {
            listApiCall = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_api_history_item, parent, false)
        return ApiHistoryViewHolder(view)
    }

    override fun getItemCount(): Int = listApiCall.listApiDetails.size

    override fun onBindViewHolder(holder: ApiHistoryViewHolder, position: Int) {
        holder.onBind(listApiCall.listApiDetails[position])
        holder.view.setOnClickListener {
            callback.onClick(listApiCall.listApiDetails[position])
        }
    }


    class ApiHistoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var textViewTime: TextView
        private var textViewMethod: TextView
        private var textViewUrl: TextView
        private var scaleDestiny: Float

        init {
            textViewMethod = view.findViewById(R.id.textMethod)
            textViewTime = view.findViewById(R.id.textTime)
            textViewUrl = view.findViewById(R.id.textUrl)
            scaleDestiny = view.context.resources.displayMetrics.scaledDensity
        }

        fun onBind(apiDetails: ApiDetails) {
            textViewUrl.text = apiDetails.request.url.toString()
            textViewMethod.text = apiDetails.request.method
            when (apiDetails.request.method) {
                "GET" -> {
                    textViewMethod.setTextColor(ColorStateList.valueOf(Color.GREEN))
                }
                "POST" -> {
                    textViewMethod.setTextColor(ColorStateList.valueOf(Color.BLUE))
                }
            }
            textViewTime.text = Constants.parseTime(apiDetails.date, false)
            if (apiDetails.num == 0) {
                textViewUrl.setPadding(
                    (20 * scaleDestiny).toInt(),
                    (10 * scaleDestiny).toInt(),
                    0,
                    (80 * scaleDestiny).toInt()
                )
            } else {
                textViewUrl.setPadding(
                    (20 * scaleDestiny).toInt(),
                    (10 * scaleDestiny).toInt(),
                    0,
                    0
                )
                textViewTime.setPadding(
                    (20 * scaleDestiny).toInt(),
                    (10 * scaleDestiny).toInt(),
                    0,
                    0
                )
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(api: ApiDetails)
    }
}