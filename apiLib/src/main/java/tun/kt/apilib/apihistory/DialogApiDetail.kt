package tun.kt.apilib.apihistory

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import tun.kt.apilib.R

class DialogApiDetail private constructor(
    val context: FragmentActivity,
    apiDetails: ApiDetails,
    private val parent: ViewGroup
) {
    private var apiDetail: ApiDetails = apiDetails
    private val alertDialog: AlertDialog
    private val view: View
    private val viewPager: ViewPager2
    private val tabLayout: TabLayout
    private val txtClose: TextView
    var apiDetails: ApiDetails
        get() = apiDetail
        set(value) {
            apiDetail = value
        }

    init {
        this.apiDetail = apiDetails
        view = LayoutInflater.from(context).inflate(R.layout.dialog_api_detail, parent, false)
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        txtClose = view.findViewById(R.id.txtClose)
        alertDialog = AlertDialog.Builder(context, R.style.DialogAnimationSlideCenter)
            .setView(view)
            .setCancelable(true)
            .create()
        alertDialog.window?.let {
            if (context.resources.displayMetrics.densityDpi < 600) {
                it.attributes?.windowAnimations = R.style.DialogAnimationSlideCenter
            }
        }
        val listFragment = arrayListOf<Fragment>()
        listFragment.add(FragmentDialog(apiDetails, FragmentDialog.Type.INFO))
        listFragment.add(FragmentDialog(apiDetails, FragmentDialog.Type.REQUEST))
        listFragment.add(FragmentDialog(apiDetails, FragmentDialog.Type.RESPONSE))
        viewPager.adapter = PagerDetailApiAdapter(context, listFragment)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
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
        txtClose.setOnClickListener {
            dismiss()
        }
    }

    fun show() {
        alertDialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            Constants.setWindowDialogHideNavigationBar(it)
        }
        if (context.resources.displayMetrics.xdpi < 600) {
            alertDialog.show()
        } else {
            alertDialog.show()
            ObjectAnimator.ofFloat(
                view,
                View.TRANSLATION_X,
                parent.width.toFloat(),
                0f
            ).apply {
                duration = 500
                start()
            }
        }
        alertDialog.window?.let {
            alertDialog.window?.setLayout(parent.width, parent.height)
            Constants.clearFlagWindowDialogNotFocusable(it)
        }
    }

    fun dismiss() {
        if (context.resources.displayMetrics.xdpi < 600) {
            alertDialog.dismiss()
        } else {
            ObjectAnimator.ofFloat(
                view,
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

    companion object {
        private const val INFO = "Info"
        private const val REQUEST = "Request"
        private const val RESPONSE = "Response"
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
}