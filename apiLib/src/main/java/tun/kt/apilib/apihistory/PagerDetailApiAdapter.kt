package tun.kt.apilib.apihistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerDetailApiAdapter(
    fragmentActivity: FragmentActivity,
    private var listFragment: List<Fragment>
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = listFragment.get(position)

    var listFragmentApiDetails: List<Fragment>
        get() = listFragment
        set(value) {
            listFragment = value
            notifyDataSetChanged()
        }
}