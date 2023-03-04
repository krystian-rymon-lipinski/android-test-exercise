package com.krystianrymonlipinski.testexercise

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java].also {
            val isDataLoaded = savedInstanceState?.getBoolean(SAVED_STATE_DATA_RETRIEVAL) ?: false

            if (!isDataLoaded) {
                it.setDataRetrievalState(MainActivityViewModel.DataRetrievalState.LOADING)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_STATE_DATA_RETRIEVAL, viewModel.isDataLoaded())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        with(navFragment.navController) {
            graph = navInflater.inflate(R.navigation.navigation_main)
        }
    }

    fun toggleUpButton(shouldShowUpButton: Boolean) {
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(shouldShowUpButton)
            setDisplayHomeAsUpEnabled(shouldShowUpButton)
        }
    }

    companion object {
        private const val SAVED_STATE_DATA_RETRIEVAL: String = "data_retrieval"
    }

}