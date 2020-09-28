package no.kristiania.onepiece.modelsForView

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import no.kristiania.onepiece.database.OnePiece
import no.kristiania.onepiece.entities.Feature
import no.kristiania.onepiece.despository.DepositoryFeature
import no.kristiania.onepiece.utility.StatusUpdate

class ViewModelMain(application: Application) : AndroidViewModel(application) {

    private val depositoryFeature: DepositoryFeature
    val features: LiveData<List<Feature>>
    val filterText: MutableLiveData<String> = MutableLiveData("")

    val statusUpdate: LiveData<StatusUpdate>

    init {
        val db = OnePiece.getDatabase(application.applicationContext)
        val featureDao = db.featureDao()
        depositoryFeature = DepositoryFeature(application.applicationContext, featureDao)
        statusUpdate = depositoryFeature.statusUpdate
        features = Transformations.switchMap(filterText) { query ->
            if (query.isNullOrBlank()) {
                depositoryFeature.allFeatures
            } else {
                depositoryFeature.getFilteredPosts(query)
            }
        }
    }

    fun updateFeatures() = viewModelScope.launch { depositoryFeature.updateFeatures() }

}