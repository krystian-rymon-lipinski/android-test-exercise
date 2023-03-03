package com.krystianrymonlipinski.testexercise.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.krystianrymonlipinski.testexercise.MainActivity
import com.krystianrymonlipinski.testexercise.NumberData
import com.krystianrymonlipinski.testexercise.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {

    private lateinit var _binding: FragmentDetailsBinding

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
        (activity as? MainActivity)?.toggleUpButton(shouldShowUpButton = true)

        setupDataChangeObservers()
    }

    private fun setupDataChangeObservers() {
        (activity as? MainActivity)?.viewModel?.let { viewModel ->
            viewModel.selectedNumber.observe(viewLifecycleOwner) {
                viewModel.loadNumberInfo(it)
            }
            viewModel.displayedImage.observe(viewLifecycleOwner) {
                displayImage(it)
            }
        }
    }

    private fun displayImage(data: NumberData) {
        _binding.apply {
            ivNumberImageLarge.setImageBitmap(data.image)
            tvNumberNameLarge.text = data.name
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                (activity as? MainActivity)?.let {
                    it.onBackPressed()
                    it.toggleUpButton(shouldShowUpButton = false)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}