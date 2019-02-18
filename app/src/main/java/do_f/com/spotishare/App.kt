package do_f.com.spotishare

import android.app.Application
import android.arch.persistence.room.Room
import do_f.com.spotishare.api.RefreshStrategy
import do_f.com.spotishare.api.SpotifyClient
import do_f.com.spotishare.databases.CacheDb
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    companion object {
        lateinit var mRefreshStrategy : RefreshStrategy
        lateinit var cacheDb : CacheDb
        var mSpotifyClient = SpotifyClient()
        var retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(SpotifyClient.HOSTNAME)
            .client(mSpotifyClient.get())
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        mRefreshStrategy = RefreshStrategy(applicationContext)
        cacheDb = Room.databaseBuilder(applicationContext, CacheDb::class.java, CacheDb.DB_NAME)
            .fallbackToDestructiveMigration().build()
    }
}