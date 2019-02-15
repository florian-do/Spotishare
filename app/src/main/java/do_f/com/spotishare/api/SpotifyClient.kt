package do_f.com.spotishare.api

import android.util.Log
import okhttp3.OkHttpClient

class SpotifyClient {

    companion object {
        val HOSTNAME = "https://api.spotify.com/v1/"
        val TAG = "SpotifyClient"
        val SP_TOKEN = "spotify_web_token"
    }

    var mAccessToken = ""

    fun get() : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request()
                Log.d(TAG, "Interceptor : ${request.url()}")
                Log.d(TAG, "Token : $mAccessToken")
                val requestBuilder =request.newBuilder()
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                it.proceed(requestBuilder.build())
            }.build()
    }
}