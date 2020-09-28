package no.kristiania.onepiece

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onepiece.R
import kotlinx.android.synthetic.main.activity_main.*
import no.kristiania.onepiece.adapters.FeaturesAdapter
import no.kristiania.onepiece.entities.Feature
import no.kristiania.onepiece.utility.StatusUpdate
import no.kristiania.onepiece.modelsForView.ViewModelMain

class ActivityMain : AppCompatActivity(), FeaturesAdapter.OnFeatureClickListener {

    private lateinit var modelMain: ViewModelMain
    private val featuresAdapter = FeaturesAdapter(emptyList(), this)
    private val queryHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MainActivity", "Activity created")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Setting up the recyclerview
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = featuresAdapter

        // Viewmodel and observers set up
        modelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        modelMain.features.observe(this, featuresObserver)
        modelMain.statusUpdate.observe(this, updateObserver)

        // Refreshlayout set up
        refresh_layout.setOnRefreshListener { modelMain.updateFeatures() }

        // Searchview and FTS search set up
        search_bar.setOnQueryTextListener(getQueryTextListener())
    }

    override fun onPause() {
        Log.i("MainActivity", "Activity paused")
        super.onPause()
        queryHandler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        Log.i("MainActivity", "Activity destroyed")
        super.onDestroy()
    }

    override fun onFeatureClicked(feature: Feature) {
        val placeActivity = Intent(applicationContext, ActivityPlace::class.java)
        placeActivity.putExtra(ActivityPlace.EXTRA_ID, feature.properties.id)
        startActivity(placeActivity)
    }

    override fun onLocationClicked(feature: Feature) {
        val mapActivity = Intent(applicationContext, ActivityMaps::class.java)
        mapActivity.putExtra(ActivityMaps.EXTRA_NAME, feature.properties.name)
        mapActivity.putExtra(ActivityMaps.EXTRA_LAT, feature.geometry?.coordinates?.get(1))
        mapActivity.putExtra(ActivityMaps.EXTRA_LON, feature.geometry?.coordinates?.get(0))
        startActivity(mapActivity)
    }

    private val featuresObserver = Observer<List<Feature>> { features ->
        features?.let {
            featuresAdapter.setFeatures(features)
            recycler_view.scrollToPosition(0)
        }
    }

    private val updateObserver = Observer<StatusUpdate> { status ->
        when (status) {
            StatusUpdate.NOOP -> {}
            StatusUpdate.UPDATING -> handleUpdating()
            StatusUpdate.SUCCESS -> handleSuccess()
            StatusUpdate.ERROR -> handleError()
            else -> Log.e("UpdateObserver", "Unknown status: $status")
        }
    }

    private fun handleUpdating() {
        // This notifies users, no reason for Toast
    }

    private fun handleSuccess() {
        Toast.makeText(this, "Places updated", Toast.LENGTH_SHORT).show()
        refresh_layout.isRefreshing = false
    }

    private fun handleError() {
        Toast.makeText(this, "Failed to update places", Toast.LENGTH_SHORT).show()
        refresh_layout.isRefreshing = false
    }

    private fun getQueryTextListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String?): Boolean {
                queryHandler.removeCallbacksAndMessages(null)
                queryHandler.postDelayed({modelMain.filterText.value = query}, 300L)
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                queryHandler.removeCallbacksAndMessages(null)
                modelMain.filterText.value = query
                return false
            }
        }
    }
}
