package no.kristiania.onepiece.modelsForView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.kristiania.onepiece.database.OnePiece
import no.kristiania.onepiece.entities.Place
import no.kristiania.onepiece.despository.DepositoryPlace
import no.kristiania.onepiece.utility.StatusUpdate

class ViewModelPlace(
    application: Application,
    placeId: Long
) : AndroidViewModel(application) {

    private val depositoryPlace: DepositoryPlace
    val place: LiveData<Place>
    val statusUpdate: LiveData<StatusUpdate>

    init {
        val db = OnePiece.getDatabase(application.applicationContext)
        val placeDao = db.placeDao()
        depositoryPlace = DepositoryPlace(application.applicationContext, placeDao)
        statusUpdate = depositoryPlace.statusUpdate
        place = getById(placeId)
    }

    fun getById(id: Long): LiveData<Place> {
        viewModelScope.launch { depositoryPlace.updatePlace(id) }
        return depositoryPlace.getById(id)
    }

}