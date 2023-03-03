package com.krystianrymonlipinski.testexercise.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.krystianrymonlipinski.testexercise.MainActivity
import com.krystianrymonlipinski.testexercise.R

class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as? MainActivity)?.toggleUpButton(shouldShowUpButton = true)
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