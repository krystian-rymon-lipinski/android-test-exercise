package com.krystianrymonlipinski.testexercise

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel
    lateinit var layoutMode: LayoutMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calculateLayoutMode()

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java].also {
            val isDataLoaded = savedInstanceState?.getBoolean(SAVED_STATE_DATA_RETRIEVAL) ?: false

            if (!isDataLoaded) {
                it.setDataRetrievalState(MainActivityViewModel.DataRetrievalState.LOADING)
            }
        }
    }

    override fun onBackPressed() {
        Timber.d("HERE; onBackPressed")
        if (onBackPressedDispatcher.hasEnabledCallbacks()) {
            Timber.d("HERE; callbacks enabled")
            onBackPressedDispatcher.onBackPressed()
        } else super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_STATE_DATA_RETRIEVAL, viewModel.isDataLoaded())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        calculateLayoutMode()

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

    private fun calculateLayoutMode() {
        with(resources.configuration) {
            layoutMode =
                if (screenWidthDp >= LANDSCAPE_MODE_SCREEN_WIDTH &&
                    orientation == Configuration.ORIENTATION_LANDSCAPE) LayoutMode.LANDSCAPE
                else LayoutMode.PORTRAIT
        }
    }

    enum class LayoutMode {
        PORTRAIT,
        LANDSCAPE
    }

    companion object {
        private const val SAVED_STATE_DATA_RETRIEVAL: String = "data_retrieval"
        private const val LANDSCAPE_MODE_SCREEN_WIDTH = 840f
    }

}