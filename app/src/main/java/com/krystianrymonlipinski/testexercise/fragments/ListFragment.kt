package com.krystianrymonlipinski.testexercise.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.krystianrymonlipinski.testexercise.*
import com.krystianrymonlipinski.testexercise.activities.MainActivity
import com.krystianrymonlipinski.testexercise.adapters.NumberInfoAdapter
import com.krystianrymonlipinski.testexercise.databinding.FragmentListBinding
import com.krystianrymonlipinski.testexercise.dialogs.LoadingDialog
import com.krystianrymonlipinski.testexercise.viewmodels.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    private lateinit var _binding: FragmentListBinding
    private var viewModel: MainActivityViewModel? = null
    private var layoutMode: MainActivity.LayoutMode? = null
    private var loadingDialog: LoadingDialog? = null

    private var isNavigatingPending = false

    private lateinit var numberInfoAdapter: NumberInfoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.let {
            it.toggleUpButton(shouldShowUpButton = false)
            viewModel = it.viewModel
            layoutMode = it.layoutMode
        }
        isNavigatingPending = false

        setupRecyclerView()
        setupUiListeners()
        setupDataChangeObservers()

        viewModel?.let {
            if (it.isNumberSelected()) navigateToDetails()
        }
    }

    private fun setupRecyclerView() {
        numberInfoAdapter = NumberInfoAdapter(
            if (layoutMode == MainActivity.LayoutMode.LANDSCAPE) viewModel?.getCurrentlySelectedNumber() else null,
            onNumberClickedListener
        )

        _binding.rvNumbersInfo.apply {
            adapter = numberInfoAdapter
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
        viewModel?.let { withCoroutine {
            numberInfoAdapter.updateNumbersInfo(it.getAllNumbersInfo())
        } }
    }

    private fun setupUiListeners() {
        _binding.btnTryAgain.setOnClickListener {
            viewModel?.setDataRetrievalState(MainActivityViewModel.DataRetrievalState.LOADING)
        }
    }

    private fun setupDataChangeObservers() {
        viewModel?.let { viewModel ->
            viewModel.numbersData.observe(viewLifecycleOwner) { withCoroutine {
                numberInfoAdapter.updateNumbersInfo(it)
            } }
            viewModel.dataRetrievalState.observe(viewLifecycleOwner) {
                when (it) {
                    MainActivityViewModel.DataRetrievalState.LOADING -> {
                        viewModel.loadAllNumbersInfo()
                        showProgressDialog()
                    }
                    MainActivityViewModel.DataRetrievalState.SUCCESS,
                    MainActivityViewModel.DataRetrievalState.FAILURE -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            loadingDialog?.dismiss()
                            toggleMainScreen(it == MainActivityViewModel.DataRetrievalState.SUCCESS)
                        }, ANIMATION_DELAY)
                    }
                    else -> { }
                }
            }
        }
    }

    private fun navigateToDetails() {
        if (layoutMode == MainActivity.LayoutMode.PORTRAIT) {
            val navFragment = activity?.supportFragmentManager?.findFragmentById(
                R.id.nav_host_fragment_container) as? NavHostFragment
            if (!isNavigatingPending) {
                isNavigatingPending = true
                navFragment?.navController?.navigate(R.id.action_listFragment_to_viewPagerDetailsFragment)
            }
        }
    }

    private fun showProgressDialog() {
        loadingDialog = LoadingDialog().also {
            it.show(childFragmentManager, "loading_dialog")
        }
    }

    private fun toggleMainScreen(isRetrievalSuccessful: Boolean) {
        _binding.apply {
            llNoDataLoaded.visibility = if (isRetrievalSuccessful) View.GONE else View.VISIBLE
            rvNumbersInfo.visibility = if (isRetrievalSuccessful) View.VISIBLE else View.GONE
        }
    }

    private fun withCoroutine(action: () -> Unit) {
        lifecycleScope.launch(Dispatchers.Main) {
            action.invoke()
        }
    }

    private val onNumberClickedListener = object : NumberInfoAdapter.OnNumberClickedListener {
        override fun onNumberClicked(index: Int) {
            viewModel?.handleOnNumberSelected(index)
            navigateToDetails()
        }
    }

    companion object {
        private const val ANIMATION_DELAY = 500L // loading can be super-quick, give user a chance to comprehend what is going on
    }

}