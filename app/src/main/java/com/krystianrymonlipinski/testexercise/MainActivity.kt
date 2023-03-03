package com.krystianrymonlipinski.testexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.loadAllNumbersInfo()
        }
    }


    fun toggleUpButton(shouldShowUpButton: Boolean) {
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(shouldShowUpButton)
            setDisplayHomeAsUpEnabled(shouldShowUpButton)
        }
    }

}