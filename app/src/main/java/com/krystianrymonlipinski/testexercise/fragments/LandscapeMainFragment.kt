package com.krystianrymonlipinski.testexercise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.krystianrymonlipinski.testexercise.R
import com.krystianrymonlipinski.testexercise.databinding.FragmentLandscapeMainBinding
import timber.log.Timber

class LandscapeMainFragment : Fragment() {

    private lateinit var _binding: FragmentLandscapeMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandscapeMainBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("HERE, landscape fragment")

        childFragmentManager.beginTransaction().apply {
            add(R.id.list_fragment_container, ListFragment())
            add(R.id.details_fragment_container, DetailsFragment())
        }.commit()
    }
}