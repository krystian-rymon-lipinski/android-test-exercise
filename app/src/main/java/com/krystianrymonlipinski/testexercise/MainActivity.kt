package com.krystianrymonlipinski.testexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java].also {
            it.setDataRetrievalState(MainActivityViewModel.DataRetrievalState.LOADING)
        }
    }

    fun toggleUpButton(shouldShowUpButton: Boolean) {
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(shouldShowUpButton)
            setDisplayHomeAsUpEnabled(shouldShowUpButton)
        }
    }

}