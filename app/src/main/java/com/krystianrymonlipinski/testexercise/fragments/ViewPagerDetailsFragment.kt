package com.krystianrymonlipinski.testexercise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.krystianrymonlipinski.testexercise.activities.MainActivity
import com.krystianrymonlipinski.testexercise.viewmodels.MainActivityViewModel
import com.krystianrymonlipinski.testexercise.R
import com.krystianrymonlipinski.testexercise.databinding.FragmentViewPagerDetailsBinding

class ViewPagerDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentViewPagerDetailsBinding
    private var viewModel: MainActivityViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerDetailsBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as? MainActivity)?.viewModel
        setupViewPager()
        _binding.detailsViewPager.currentItem = viewModel?.selectedNumber?.value?.index ?: 0
    }

    private fun setupViewPager() {
        viewModel?.getAllNumbersInfo()?.let { with(_binding) {
            generateTabs(it.map { data -> data.name })
            detailsViewPager.apply {
                adapter = PagerAdapter(it.size)
                registerOnPageChangeCallback(onPageChangeCallback)
            }
            TabLayoutMediator(detailsTabLayout, detailsViewPager) { tab, position ->
                tab.text = it[position].name
            }.attach()
        } } ?: Toast.makeText(activity, getString(R.string.fetching_data_failed), Toast.LENGTH_SHORT).show()



    }

    private fun generateTabs(tabCaptions: List<String>) {
        with(_binding.detailsTabLayout) {
            for (caption in tabCaptions) {
                addTab(this.newTab())
            }
        }
    }

    private val onPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel?.handleOnNumberSelected(position)
        }
    }

    private inner class PagerAdapter(private val numberOfTabs: Int) : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return numberOfTabs
        }

        override fun createFragment(position: Int): Fragment {
            return DetailsFragment()
        }

    }

}