package no.kristiania.onepiece.services

import no.kristiania.onepiece.entities.FeaturesWrapperDto
import no.kristiania.onepiece.entities.PlaceWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface NoForeignLandService {

    @GET("place")
    suspend fun getById(@Query("id") id: Long): PlaceWrapper

    @GET("places")
    suspend fun getAll(): FeaturesWrapperDto
}