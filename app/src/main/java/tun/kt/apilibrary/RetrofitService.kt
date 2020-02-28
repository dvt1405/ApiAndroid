package tun.kt.apilibrary

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import tun.kt.apilib.apihistory.CustomInterceptor

class RetrofitService {
    private val interceptor = CustomInterceptor()
    val client: OkHttpClient

    init {
        interceptor.level = CustomInterceptor.Level.BODY
        client = OkHttpClient.Builder()
            .addInterceptor{
                val newRequest = it.request()
                    .newBuilder()
                    .build()
                it.proceed(newRequest)
            }
            .addInterceptor(interceptor)
            .build()
    }

    var service: API = retrofit2.Retrofit.Builder()
        .client(client)
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
        .create(API::class.java)
}