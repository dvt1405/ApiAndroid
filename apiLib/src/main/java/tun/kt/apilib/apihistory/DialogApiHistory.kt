package tun.kt.apilib.apihistory

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tun.kt.apilib.R

class DialogApiHistory private constructor(private val context: Activity) {
    var alertDialog: AlertDialog? = null
    private var adapter: ApiHistoryAdapter? = null
    private var apiHistoryApiCall: HistoryApiCall? = null
    private var listHistoryApi: RecyclerView? = null
    private var txtClose: TextView? = null
    private var viewGroup: ViewGroup? = null

    init {
        val view = LayoutInflater.from(context.applicationContext)
            .inflate(R.layout.dialog_api_history, null, false)
        apiHistoryApiCall = HistoryApiCall.getInstance()
        viewGroup = view.findViewById(R.id.viewGroup)
        txtClose = view.findViewById(R.id.txtClose)
        listHistoryApi = view.findViewById(R.id.listHistoryApi)
        adapter = ApiHistoryAdapter(context.applicationContext, apiHistoryApiCall!!,
            object : ApiHistoryAdapter.OnItemClickListener {
                override fun onClick(api: ApiDetails) {
                    DialogApiDetail.getInstance(
                        context as FragmentActivity,
                        api,
                        viewGroup!!
                    ).show()
                }
            })

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        listHistoryApi?.layoutManager = linearLayoutManager
        listHistoryApi?.adapter = adapter
        alertDialog = AlertDialog.Builder(context, R.style.DialogTheme)
            .setCancelable(true)
            .setView(view)
            .create()
        alertDialog?.window?.let {
            it.attributes.windowAnimations = R.style.DialogTheme
        }
        txtClose?.setOnClickListener {
            alertDialog?.dismiss()
        }
        apiHistoryApiCall?.wasUpdated!!.observe(context as LifecycleOwner, Observer {
            if (it) {
                adapter!!.apiHistory = apiHistoryApiCall!!
                listHistoryApi!!.scrollToPosition(adapter!!.itemCount - 1)
            }
        })
    }

    fun show() {
        isShowing = false
        adapter!!.apiHistory = apiHistoryApiCall!!
        alertDialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        if (!context.isFinishing) {
            alertDialog?.show()
        }
        alertDialog?.window?.let {
            if (context.resources.displayMetrics.xdpi < 600) {
                it.setLayout(
                    context.resources.displayMetrics.widthPixels,
                    context.resources.displayMetrics.heightPixels
                )
            } else {
                it.setLayout(
                    context.resources.displayMetrics.widthPixels * 2 / 3,
                    context.resources.displayMetrics.heightPixels * 5 / 6
                )
            }
            Constants.clearFlagWindowDialogNotFocusable(it)
        }
    }

    fun dismiss() {
        DialogApiDetail.getInstance()?.dismiss()
        alertDialog?.dismiss()
        isShowing = true
    }

    var isShowing = alertDialog!!.isShowing

    companion object {
        private var dialogApiHistory: DialogApiHistory? = null
        fun getInstance(context: Activity): DialogApiHistory {
            dialogApiHistory = DialogApiHistory(context)
            return dialogApiHistory!!
        }
    }

}

