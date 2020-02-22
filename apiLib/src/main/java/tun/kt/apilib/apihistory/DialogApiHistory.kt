package com.kt.express.presentation.ui.apihistory

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import tun.kt.apilib.R
import tun.kt.apilib.apihistory.Constants
import tun.kt.apilib.databinding.DialogApiHistoryBinding

class DialogApiHistory private constructor(private val context: Activity) {
    var alertDialog: AlertDialog? = null
    private var adapter: ApiHistoryAdapter? = null
    private var apiHistoryApiCall: HistoryApiCall? = null

    init {
        val dataBinding: DialogApiHistoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context.applicationContext),
            R.layout.dialog_api_history,
            null,
            false
        )
        apiHistoryApiCall = HistoryApiCall.getInstance()
        adapter = ApiHistoryAdapter(context.applicationContext, apiHistoryApiCall!!,
            object : ApiHistoryAdapter.OnItemClickListener {
                override fun onClick(api: ApiDetails) {
                    DialogApiDetail.getInstance(
                        context as FragmentActivity,
                        api,
                        dataBinding.viewGroup
                    ).show()
                }
            })

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        dataBinding.listHistoryApi.layoutManager = linearLayoutManager
        dataBinding.listHistoryApi.adapter = adapter
        alertDialog = AlertDialog.Builder(context, R.style.DialogTheme)
            .setCancelable(true)
            .setView(dataBinding.root)
            .create()
        alertDialog?.window?.let {
            Constants.setWindowDialogHideNavigationBar(it)
        }
        dataBinding.txtClose.setOnClickListener {
            alertDialog?.dismiss()
        }
        apiHistoryApiCall?.wasUpdated!!.observe(context as LifecycleOwner, Observer {
            if (it) {
                adapter!!.apiHistory = apiHistoryApiCall!!
                dataBinding.listHistoryApi.scrollToPosition(adapter!!.itemCount - 1)
            }
        })
    }

    companion object {
        private var dialogApiHistory: DialogApiHistory? = null
        fun getInstance(context: Activity): DialogApiHistory {
            dialogApiHistory = DialogApiHistory(context)
            return dialogApiHistory!!
        }
    }

    fun show() {
        isShowing = false
        adapter!!.apiHistory = apiHistoryApiCall!!
        alertDialog?.window?.let {
            Constants.setWindowDialogHideNavigationBar(it)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        if (!context.isFinishing) {
            alertDialog?.show()
        }
        alertDialog?.window?.let {
            it.setLayout(
                context.resources.displayMetrics.widthPixels * 2 / 3,
                context.resources.displayMetrics.heightPixels * 5 / 6
            )
            Log.e(
                "Width",
                context.resources.displayMetrics.widthPixels.toString() + context.resources.displayMetrics.heightPixels.toString()
            )
            if (context.resources.displayMetrics.widthPixels / context.resources.displayMetrics.ydpi < 600) {
                it.setLayout(
                    context.resources.displayMetrics.widthPixels,
                    context.resources.displayMetrics.heightPixels
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
}

