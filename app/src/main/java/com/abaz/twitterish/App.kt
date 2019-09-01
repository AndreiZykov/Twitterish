package com.abaz.twitterish

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.abaz.twitterish.data.UserDataSource
import com.abaz.twitterish.network.OkHttpClientProvider
import com.abaz.twitterish.network.RetrofitProvider
import com.abaz.twitterish.network.TechTalkApi
import com.abaz.twitterish.data.repository.SharedPreferenceRepository
import com.abaz.twitterish.data.repository.UserRepository
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module


class App: MultiDexApplication(), KoinComponent {

//    val techTalkService by inject<TechTalkService>()

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        startKoin {
            androidContext(this@App)
            modules(diModule)
        }

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}


val diModule = module {

    single { SharedPreferenceRepository(get()) }

    single { OkHttpClientProvider(get()) }

    single { RetrofitProvider(get()) }

    factory { TechTalkApi(get()) }

    single<UserDataSource> { UserRepository(get(), get()) }

}





//    single {
//
//        val logging = HttpLoggingInterceptor()
//        logging.level = HttpLoggingInterceptor.Level.BODY
//
//        OkHttpClient()
//            .newBuilder()
//            .addInterceptor(logging)
//            .addInterceptor {
//                val token = "8015C77DC3D0A76074EC21380963E596429316E06FFB5B7F0E9A37F9A5E9ABDB"
//                val newRequest = it.request().newBuilder()
//                    .addHeader("Authorization", "Bearer $token")
//                    .addHeader("Content-Type", "application/json")
//                    .build()
//                it.proceed(newRequest)
//            }
//            .connectTimeout(15, TimeUnit.SECONDS)
//            .writeTimeout(5, TimeUnit.SECONDS)
//            .readTimeout(5, TimeUnit.SECONDS)
//            .build()
//    }



//// Koin module
//val HelloModule = applicationContext {
//    bean { HelloModel(getProperty(WHO)) }
//    bean { HelloServiceImpl(get()) as HelloService }
//}