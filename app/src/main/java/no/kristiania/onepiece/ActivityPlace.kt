package no.kristiania.onepiece

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.onepiece.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_place.*
import no.kristiania.onepiece.entities.Place
import no.kristiania.onepiece.utility.StatusUpdate
import no.kristiania.onepiece.modelsForView.ViewModelPlace

class ActivityPlace : AppCompatActivity() {

    private lateinit var modelPlace: ViewModelPlace
    private val handlerForUpdate = Handler()
    private var placeId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("ActivityPlace", "Created Activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)
        supportActionBar?.hide()

        placeId = intent.extras!!.get(EXTRA_ID) as Long
        modelPlace = ViewModelProvider(
            this,

            ConstructPlaceView(application, placeId)
        ).get(ViewModelPlace::class.java)

        modelPlace.place.observe(this, observerForPlace)
        modelPlace.statusUpdate.observe(this, observerUpdater)

        // This is the back button
        back_button.setOnClickListener { finish() }

        // This is the map button
        location_button.setOnClickListener {
            val place = modelPlace.place.value
            val activityForMap = Intent(applicationContext, ActivityMaps::class.java)
            activityForMap.putExtra(ActivityMaps.EXTRA_NAME, place?.name)
            activityForMap.putExtra(ActivityMaps.EXTRA_LAT, place?.lat)
            activityForMap.putExtra(ActivityMaps.EXTRA_LON, place?.lon)
            startActivity(activityForMap)
        }
    }

    override fun onPause() {
        Log.i("ActivityForPlace", "Paused Activity")
        super.onPause()
    }

    override fun onDestroy() {
        Log.i("ActivityForPlace", "Destroyed Activity")
        super.onDestroy()
        handlerForUpdate.removeCallbacksAndMessages(null)
    }

    private val observerForPlace = Observer<Place> { place ->
        place?.let {
            place_title.text = place.name
            place_comment.text = place.comments
            if (!place.banner.isNullOrBlank()) {
                Picasso.get().load(place.banner).into(place_image)
            }
        }
    }

    private val observerUpdater = Observer<StatusUpdate> { status ->
        when (status) {
            StatusUpdate.NOOP -> {
            }
            StatusUpdate.UPDATING -> {
            }
            StatusUpdate.SUCCESS -> {
            }
            StatusUpdate.ERROR -> handlerForUpdate.postDelayed({ handleError() }, 500L)
            else -> Log.e("ObserverUpdate", "Unknown status: $status")
        }
    }

    private fun handleError() {
        Log.i("PlaceActivity", "handleError started")
        val message = if (modelPlace.place.value == null) {
            "Could not connect to the server.\nPlease try again later"
        } else {
            "Could not connect to the server.\nShowing cached data"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.i("PlaceActivity", "handleError finished")
    }

    // Passing the placeId to the ViewModel constructor.
    inner class ConstructPlaceView(
        private val application: Application,
        private val placeId: Long
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ViewModelPlace(application, placeId) as T
        }
    }

    companion object {
        const val EXTRA_ID: String  = "no.kristiania.onepiece.entities.Place.id"
    }
}
