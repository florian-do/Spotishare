package do_f.com.spotishare

import android.app.Application
import do_f.com.spotishare.api.RefreshStrategy
import do_f.com.spotishare.api.SpotifyClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    companion object {
        lateinit var mRefreshStrategy : RefreshStrategy
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
    }
}