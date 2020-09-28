package no.kristiania.onepiece.despository

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.kristiania.onepiece.database.FeatureDao
import no.kristiania.onepiece.entities.Feature
import no.kristiania.onepiece.services.NoForeignLandService
import no.kristiania.onepiece.utility.StatusUpdate
import no.kristiania.onepiece.utility.Utils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DepositoryFeature(
    private val context: Context,
    private val featureDao: FeatureDao
) {

    private val service: NoForeignLandService = getService()
    val allFeatures: LiveData<List<Feature>> = featureDao.getAll()

    private val _StatusUpdate: MutableLiveData<StatusUpdate> = MutableLiveData(StatusUpdate.NOOP)
    val statusUpdate: LiveData<StatusUpdate> = _StatusUpdate

    suspend fun updateFeatures() {

        Log.i("FeatureRepository", "Attempting to update Features")
        _StatusUpdate.value = StatusUpdate.UPDATING
        val startTime = SystemClock.uptimeMillis()

        if (Utils.isOnline(context)) {
            try {
                val wrapper = service.getAll()
                val features = wrapper.features
                featureDao.updateFeatures(features)
                Log.i(
                    "FeatureRepository",
                    "Updated ${features.size} Features in ${SystemClock.uptimeMillis() - startTime}ms"
                )
                _StatusUpdate.value = StatusUpdate.SUCCESS
            } catch (e: Exception) {
                Log.w("FeatureRepository", "Failed to update features: $e")
                _StatusUpdate.value = StatusUpdate.ERROR
            }
        } else {
            Log.w("FeatureRepository", "Failed to update features: No internet connection")
            _StatusUpdate.value = StatusUpdate.ERROR
        }
    }

    fun getFilteredPosts(filter: String): LiveData<List<Feature>> {
        return featureDao.searchFeatures("$filter*")
    }

    private fun getService(): NoForeignLandService {
        return Retrofit.Builder()
            .baseUrl("https://www.noforeignland.com/home/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NoForeignLandService::class.java)
    }
}