package com.krystianrymonlipinski.testexercise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.krystianrymonlipinski.testexercise.MainActivity
import com.krystianrymonlipinski.testexercise.NumberInfoAdapter
import com.krystianrymonlipinski.testexercise.databinding.FragmentListBinding
import timber.log.Timber

class ListFragment : Fragment() {

    private lateinit var _binding: FragmentListBinding
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
        setupRecyclerView()
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

    private fun setupDataChangeObservers() {
        (activity as? MainActivity)?.viewModel?.let { viewModel ->
            viewModel.numbersData.observe(viewLifecycleOwner) {
                numberInfoAdapter.updateNumbersInfo(it)
            }
        }
    }

    private val onNumberClickedListener = object : NumberInfoAdapter.OnNumberClickedListener {
        override fun onNumberClicked(index: Int) {
            Timber.d("HERE; item $index. clicked")
            //TODO: navigate to details fragment
        }
    }
}