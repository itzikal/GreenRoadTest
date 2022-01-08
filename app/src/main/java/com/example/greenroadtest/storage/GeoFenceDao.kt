package com.example.greenroadtest.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.greenroadtest.model.GeoFenceModel

@Dao
abstract class GeoFenceDao : BaseDao<GeoFenceModel>() {
    @Query("SELECT * FROM geo_fence_table")
    abstract override fun getAllData(): List<GeoFenceModel>

    @Query("SELECT * FROM geo_fence_table ORDER BY `id` DESC")
    abstract fun getAllDataLive(): LiveData<List<GeoFenceModel>>

    @Query("SELECT * FROM geo_fence_table ORDER BY `id` DESC LIMIT 1")
    abstract fun getLastGeoFenceLive(): LiveData<GeoFenceModel>

    @Query("SELECT * FROM geo_fence_table ORDER BY `id` DESC LIMIT 1")
    abstract fun getLastGeoFence(): GeoFenceModel?

}