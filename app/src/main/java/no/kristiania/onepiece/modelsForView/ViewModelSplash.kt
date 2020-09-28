package no.kristiania.onepiece.modelsForView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.kristiania.onepiece.database.OnePiece
import no.kristiania.onepiece.entities.Feature
import no.kristiania.onepiece.despository.DepositoryFeature
import no.kristiania.onepiece.utility.StatusUpdate

class ViewModelSplash(application: Application) : AndroidViewModel(application) {

    private val depositoryFeature: DepositoryFeature
    val features: LiveData<List<Feature>>
    val statusUpdate: LiveData<StatusUpdate>

    init {
        val db = OnePiece.getDatabase(application.applicationContext)
        val featureDao = db.featureDao()
        depositoryFeature = DepositoryFeature(application.applicationContext, featureDao)
        features = depositoryFeature.allFeatures
        statusUpdate = depositoryFeature.statusUpdate
        updateFeatures()
    }

    private fun updateFeatures() =
        viewModelScope.launch {
            depositoryFeature.updateFeatures()
        }
}
