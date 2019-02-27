package do_f.com.spotishare

import android.app.Application
import android.arch.persistence.room.Room
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import do_f.com.spotishare.api.RefreshStrategy
import do_f.com.spotishare.api.SpotifyClient
import do_f.com.spotishare.databases.CacheDb
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    companion object {
        lateinit var session : Session
        lateinit var firebaseDb : DatabaseReference
        lateinit var mRefreshStrategy : RefreshStrategy
        lateinit var cacheDb : CacheDb
        var mSpotifyClient = SpotifyClient()
        var retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(SpotifyClient.HOSTNAME)
            .client(mSpotifyClient.get())
            .build()

        val roomCode: String
            get() = session.getRoomCode()

        val isMaster: Boolean
            get() = session.isMaster()

        val isSlave: Boolean
            get() = session.isSlave()
    }

    override fun onCreate() {
        super.onCreate()
        session = Session(applicationContext)
        firebaseDb = FirebaseDatabase.getInstance().reference
        mRefreshStrategy = RefreshStrategy(applicationContext)
        cacheDb = Room.databaseBuilder(applicationContext, CacheDb::class.java, CacheDb.DB_NAME)
            .fallbackToDestructiveMigration().build()
    }
}