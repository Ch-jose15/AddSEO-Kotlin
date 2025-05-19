package com.example.addseo

import ReviewFragment
import SupportFragment
import PostFragment
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerContainer: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        viewPagerContainer = findViewById(R.id.viewPagerContainer)

        viewPager.adapter = ViewPagerAdapter(this)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val layoutParams = viewPagerContainer.layoutParams
                layoutParams.height = when (position) {
                    2 -> resources.getDimensionPixelSize(R.dimen.blog_height)
                    else -> resources.getDimensionPixelSize(R.dimen.card_compact_height)
                }
                viewPagerContainer.layoutParams = layoutParams
            }
        })
    }

    private inner class ViewPagerAdapter(activity: AppCompatActivity) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> SupportFragment()
            1 -> ReviewFragment()
            2 -> PostFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
