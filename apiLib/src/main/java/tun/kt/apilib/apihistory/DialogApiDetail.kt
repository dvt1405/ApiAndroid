package com.kt.express.presentation.ui.apihistory

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import tun.kt.apilib.R
import tun.kt.apilib.apihistory.Constants
import tun.kt.apilib.databinding.DialogApiDetailBinding
import java.lang.StringBuilder

class DialogApiDetail private constructor(
    val context: FragmentActivity,
    apiDetails: ApiDetails,
    private val parent: ViewGroup
) {
    private var apiDetail: ApiDetails = apiDetails
    private val alertDialog: AlertDialog
    private val dataBinding: DialogApiDetailBinding
    var apiDetails: ApiDetails
        get() = apiDetail
        set(value) {
            apiDetail = value
        }

    init {
        this.apiDetail = apiDetails
        dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_api_detail,
            parent,
            false

        )

        alertDialog = AlertDialog.Builder(context, R.style.DialogAnimationSlideCenter)
            .setView(dataBinding.root)
            .setCancelable(false)
            .create()
        val listFragment = arrayListOf<Fragment>()
        listFragment.add(FragmentDialog(apiDetails, FragmentDialog.Type.INFO))
        listFragment.add(FragmentDialog(apiDetails, FragmentDialog.Type.REQUEST))
        listFragment.add(FragmentDialog(apiDetails, FragmentDialog.Type.RESPONSE))
        dataBinding.viewPager.adapter = PagerDetailApiAdapter(context, listFragment)
        TabLayoutMediator(dataBinding.tabLayout, dataBinding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = INFO
                }
                1 -> {
                    tab.text = REQUEST
                }
                2 -> {
                    tab.text = RESPONSE
                }
            }
        }.attach()
        dataBinding.txtClose.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private val INFO = "Info"
        private val REQUEST = "Request"
        private val RESPONSE = "Response"
        private var INSTANCE: DialogApiDetail? = null
        fun getInstance(
            context: FragmentActivity,
            apiDetails: ApiDetails,
            parent: ViewGroup
        ): DialogApiDetail {
            if (INSTANCE == null) {
                INSTANCE = DialogApiDetail(context, apiDetails, parent)
            } else {
                if (Build.VERSION.SDK_INT >= 26) {
                    val oldActivity = INSTANCE!!.context.isActivityTransitionRunning
                    if (!oldActivity) {
                        INSTANCE = DialogApiDetail(context, apiDetails, parent)
                    }
                    INSTANCE!!.apiDetail = apiDetails
                } else {
                    INSTANCE = DialogApiDetail(context, apiDetails, parent)
                }
            }
            return INSTANCE!!
        }

        fun getInstance(): DialogApiDetail? = INSTANCE
    }

    fun show() {
        alertDialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            Constants.setWindowDialogHideNavigationBar(it)
        }
        alertDialog.show()
        ObjectAnimator.ofFloat(
            dataBinding.root,
            View.TRANSLATION_X,
            parent.width.toFloat(),
            0f
        ).apply {
            duration = 500
            start()
        }
        alertDialog.window?.let {
            it.setLayout(
                parent.width,
                parent.height
            )
            Constants.clearFlagWindowDialogNotFocusable(it)
        }
    }

    fun dismiss() {
        ObjectAnimator.ofFloat(
            dataBinding.root,
            View.TRANSLATION_X,
            0f,
            parent.width.toFloat()
        ).apply {
            duration = 500
            start()
        }
        Handler().postDelayed(Runnable {
            alertDialog.dismiss()
        }, 500)

    }

}