package com.test.recovermessages.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.test.recovermessages.App
import com.test.recovermessages.R
import com.test.recovermessages.utils.AdmobHelper

class WAFragment : Fragment() {

    private var filesFragment_: FilesFragment? = null
    private var users_fragment: UsersFragment? = null
    private var pack: String? = null
    private var pager: ViewPager? = null
    private var mView: View? = null
    fun newInstance(str: String?): WAFragment {
        val wA_fragment = WAFragment()
        val bundle = Bundle()
        bundle.putString("pack", str)
        wA_fragment.arguments = bundle
        return wA_fragment
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        mView = layoutInflater.inflate(R.layout.wa_fragment, viewGroup, false)
        return mView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pack = requireArguments().getString("pack")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pager = mView!!.findViewById<View>(R.id.pager) as ViewPager
        val tabLayout: TabLayout = mView!!.findViewById(R.id.tab)
        val firstTab = tabLayout.newTab()
        firstTab.text = "Chats"
        val secondTab = tabLayout.newTab()
        secondTab.text = "Images"
        tabLayout.addTab(firstTab)
        tabLayout.addTab(secondTab)
        tabLayout.setTabTextColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.dinwhite
            ),
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
        tabLayout.setSelectedTabIndicatorHeight(0)
        pager?.adapter = FragAdapter(
            childFragmentManager
        ) as PagerAdapter?
        try {
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    try {
                        if (App.adCount % 4 == 0) {
                            AdmobHelper.showInterstitialAd(context, AdmobHelper.ADSHOWN)
                        }
                        App.adCount++
                        sendToCleanSaverAdapter()
                        sendToCleanHome()
                        pager!!.currentItem = tab.position
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            pager!!.addOnPageChangeListener((TabLayoutOnPageChangeListener(tabLayout) as OnPageChangeListener))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun sendToCleanHome() {
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(Intent("update").putExtra("update", 2))
    }

    private fun sendToCleanSaverAdapter() {
        Handler(Looper.getMainLooper()).postDelayed({
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent("cleansaver"))
        }, 500)
    }

    private inner class FragAdapter(fragmentManager: FragmentManager?) : FragmentPagerAdapter(
        fragmentManager!!
    ) {
        override fun getCount(): Int {
            return 2
        }

        override fun getItem(i: Int): Fragment {
            return if (i == 0) {
                users_fragment = UsersFragment().newInstance(pack)
                users_fragment!!
            } else {
                filesFragment_ = FilesFragment().newInstance(pack)
                filesFragment_!!
            }
        }
    }
}