package no.kristiania.onepiece.despository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.kristiania.onepiece.database.PlaceDao
import no.kristiania.onepiece.entities.Place
import no.kristiania.onepiece.services.NoForeignLandService
import no.kristiania.onepiece.utility.StatusUpdate
import no.kristiania.onepiece.utility.Utils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DepositoryPlace(
    private val context: Context,
    private val placeDao: PlaceDao
) {
    private val service: NoForeignLandService = getService()

    private val _StatusUpdate: MutableLiveData<StatusUpdate> = MutableLiveData(StatusUpdate.NOOP)
    val statusUpdate: LiveData<StatusUpdate> = _StatusUpdate

    fun getById(id: Long): LiveData<Place> {
        return placeDao.getById(id)
    }

    suspend fun updatePlace(id: Long) {

        Log.i("PlaceRepository", "Attempting to update place with ID $id")
        _StatusUpdate.value = StatusUpdate.UPDATING

        if (Utils.isOnline(context)) {
            try {
                val wrapper = service.getById(id)
                val place = wrapper.place
                // Comments often include HTML-tags, so this parses this to a clean String
                place.comments = android.text.Html.fromHtml(place.comments).toString().trim()
                placeDao.insert(place)
                _StatusUpdate.value = StatusUpdate.SUCCESS
                Log.i("PlaceRepository", "Successfully updated place with ID $id")
            } catch (e: Exception) {
                _StatusUpdate.value = StatusUpdate.ERROR
                Log.w("PlaceRepository", "Failed to update place: $e")
            }
        } else {
            _StatusUpdate.value = StatusUpdate.ERROR
            Log.w("PlaceRepository", "Failed to update place: No internet connection")
        }
    }

    private fun getService(): NoForeignLandService {
        return Retrofit.Builder()
            .baseUrl("https://www.noforeignland.com/home/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NoForeignLandService::class.java)
    }
}