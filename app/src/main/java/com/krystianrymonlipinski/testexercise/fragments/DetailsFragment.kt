package com.krystianrymonlipinski.testexercise.fragments

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.krystianrymonlipinski.testexercise.MainActivity
import com.krystianrymonlipinski.testexercise.MainActivityViewModel
import com.krystianrymonlipinski.testexercise.R
import com.krystianrymonlipinski.testexercise.databinding.FragmentDetailsBinding
import timber.log.Timber

class DetailsFragment : Fragment() {

    private lateinit var _binding: FragmentDetailsBinding
    private lateinit var layoutMode: MainActivity.LayoutMode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as? MainActivity)?.let {
            layoutMode = it.layoutMode
            if (layoutMode == MainActivity.LayoutMode.PORTRAIT) {
                it.toggleUpButton(shouldShowUpButton = true)
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
        setupDataChangeObservers()
    }

    private fun setupDataChangeObservers() {
        (activity as? MainActivity)?.viewModel?.let { viewModel ->
            viewModel.selectedNumber.observe(viewLifecycleOwner) {
                displayInfo(it)
            }
        }
    }

    private fun displayInfo(data: MainActivityViewModel.SelectedCard?) {
        data?.let {
            _binding.apply {
                ivNumberImageLarge.setImageBitmap(it.image)
                tvNumberNameLarge.text = it.numberName
            }
        }
    }

    private fun exitDetailsScreen() {
        (activity as? MainActivity)?.let {
            val navFragment = it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
            navFragment.navController.popBackStack()
            it.viewModel.clearSelectedNumber()
            it.toggleUpButton(shouldShowUpButton = false)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (layoutMode == MainActivity.LayoutMode.PORTRAIT) {
                exitDetailsScreen()
            } else {
                this.remove()
                activity?.onBackPressed()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                exitDetailsScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}