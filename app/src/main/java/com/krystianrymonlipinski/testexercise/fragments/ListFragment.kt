package com.krystianrymonlipinski.testexercise.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.krystianrymonlipinski.testexercise.*
import com.krystianrymonlipinski.testexercise.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var _binding: FragmentListBinding
    private var loadingDialog: LoadingDialog? = null

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
        (activity as? MainActivity)?.toggleUpButton(shouldShowUpButton = false)

        setupRecyclerView()
        setupUiListeners()
        setupDataChangeObservers()
    }

    private fun setupRecyclerView() {
        numberInfoAdapter = NumberInfoAdapter(onNumberClickedListener)

        _binding.rvNumbersInfo.apply {
            adapter = numberInfoAdapter
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    private fun setupUiListeners() {
        _binding.btnTryAgain.setOnClickListener {
            (activity as? MainActivity)?.viewModel?.setDataRetrievalState(
                MainActivityViewModel.DataRetrievalState.LOADING)
        }
    }

    private fun setupDataChangeObservers() {
        (activity as? MainActivity)?.viewModel?.let { viewModel ->
            viewModel.numbersData.observe(viewLifecycleOwner) {
                numberInfoAdapter.updateNumbersInfo(it)
            }
            viewModel.dataRetrievalState.observe(this) {
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
        val navFragment = activity?.supportFragmentManager?.findFragmentById(
            R.id.nav_host_fragment_container) as? NavHostFragment
        navFragment?.navController?.navigate(R.id.action_listFragment_to_detailsFragment)
    }

    private fun showProgressDialog() {
        loadingDialog = LoadingDialog().also{
            it.show(childFragmentManager, "loading_dialog")
        }
    }

    private fun toggleMainScreen(isRetrievalSuccessful: Boolean) {
        _binding.apply {
            llNoDataLoaded.visibility = if (isRetrievalSuccessful) View.GONE else View.VISIBLE
            rvNumbersInfo.visibility = if (isRetrievalSuccessful) View.VISIBLE else View.GONE
        }
    }

    private val onNumberClickedListener = object : NumberInfoAdapter.OnNumberClickedListener {
        override fun onNumberClicked(index: Int) {
            (activity as? MainActivity)?.viewModel?.setSelectedNumber(index)
            navigateToDetails()
        }
    }

    companion object {
        private const val ANIMATION_DELAY = 500L // loading can be super-quick, give user a chance to comprehend what is going on
    }

}