package no.kristiania.onepiece.database

import android.content.Context
import androidx.room.*
import no.kristiania.onepiece.entities.Feature
import no.kristiania.onepiece.entities.Place

val DATABASE_NAME = "onepiece_database"

@Database(
    entities = [Feature::class, Place::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OnePiece : RoomDatabase() {

    abstract fun featureDao(): FeatureDao
    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: OnePiece? = null

        fun getDatabase(
            context: Context
        ): OnePiece {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OnePiece::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun listToString(list: List<Double>) : String {
        return list.joinToString(" ")
    }
    @TypeConverter
    fun stringToList(string: String): List<Double> {
        return string.split(" ").map { it.toDouble() }
    }
}