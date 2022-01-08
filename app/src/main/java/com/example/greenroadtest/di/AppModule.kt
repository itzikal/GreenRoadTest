package com.example.greenroadtest.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.greenroadtest.MapsViewModel
import com.example.greenroadtest.location.GeoClientWrapper
import com.example.greenroadtest.location.LocationManager
import com.example.greenroadtest.storage.GeoFanceRepository
import com.example.greenroadtest.storage.GeoFenceDB
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module


val appModule = module {

    single { GeoClientWrapper(get()) }
    single { get<GeoFenceDB>().geoFanceDao() }
    single { GeoFanceRepository(get(), get()) }

    single {
        Room.databaseBuilder(
            get(),
            GeoFenceDB::class.java, "GreenRoadTestDB"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                }

                override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                    super.onDestructiveMigration(db)
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                }
            }).fallbackToDestructiveMigration().build()
    } bind RoomDatabase::class

    factory { LocationManager(get()) }

    viewModel { MapsViewModel(get(), get()) }
}