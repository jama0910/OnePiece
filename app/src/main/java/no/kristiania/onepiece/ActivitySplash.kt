package no.kristiania.onepiece

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.onepiece.R
import no.kristiania.onepiece.entities.Feature
import no.kristiania.onepiece.utility.StatusUpdate
import no.kristiania.onepiece.modelsForView.ViewModelSplash

class ActivitySplash : AppCompatActivity() {

    private val splashTime = 2000
    private val startTime = SystemClock.uptimeMillis()

    private lateinit var viewModelSplash: ViewModelSplash
    private val splashHandler = Handler()
    private var haveCache: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("SplashActivity", "Created Activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        // Set up the ViewModel and Observers
        viewModelSplash = ViewModelProvider(this).get(ViewModelSplash::class.java)
        viewModelSplash.features.observe(this, observerFeature)
        viewModelSplash.statusUpdate.observe(this, updateObserver)
    }

    override fun onPause() {
        Log.i("SplashActivity", "Paused Activity")
        super.onPause()
        splashHandler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        Log.i("SplashActivity", "Destroyed Activity")
        super.onDestroy()
        // Remove all delayed actions when destroying the SplashActivity
        splashHandler.removeCallbacksAndMessages(null)
    }

    private val updateObserver = Observer<StatusUpdate> { status ->
        Log.d("SplashActivity", "Updated status: $status")
        when (status) {
            StatusUpdate.NOOP -> {
            }
            StatusUpdate.UPDATING -> {
            }
            StatusUpdate.SUCCESS -> successHandling()
            StatusUpdate.ERROR -> handlingErrors()
            else -> Log.e("UpdateWatcher", "Status unknown: $status")
        }
    }

    private fun handlingErrors() {
        val delay = splashTime - sinceStart()
        splashHandler.postDelayed(errorHandling, delay)
    }

    private val observerFeature = Observer<List<Feature>> { features ->
        haveCache = features.isNotEmpty()
    }

    private val errorHandling = {

        if (haveCache) {
            Toast.makeText(
                this,
                "Couldn't find new data\n" +
                        "Revealing new data",
                Toast.LENGTH_LONG
            ).show()
            successHandling()
        } else {
            Toast.makeText(
                this,
                "Couldn't retrieve data from servers.\n" +
                        "Try again later",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun successHandling() {
        val delay = splashTime - sinceStart()
        splashHandler.postDelayed(transportToMain, delay)
    }


    private val transportToMain = {
        Log.i("SplashActivity", "Time in Splash: ${sinceStart()}")
        val mainActivity = Intent(applicationContext, ActivityMain::class.java)
        startActivity(mainActivity)
        finish()
    }
    private fun sinceStart(): Long {
        val nowTime = SystemClock.uptimeMillis()
        return nowTime - startTime
    }
}

