package skt.vs.wbg.who.`is`.champion.flashvpn.net

import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import skt.vs.wbg.who.`is`.champion.flashvpn.BuildConfig
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.page.SPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.NetClientHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.io.IOException
import java.lang.reflect.Type
import java.util.UUID
import java.util.concurrent.TimeUnit

open class FlashCloak {
    private val netInterceptor = NetInterceptor()
    private fun createOkhttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val httpLogging = HttpLoggingInterceptor()
            httpLogging.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(httpLogging)
        }

        return builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
            .addNetworkInterceptor(netInterceptor).readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true).build()

    }

    private val retrofitCloak by lazy {
        Retrofit.Builder().client(createOkhttp())
            .baseUrl("https://trypsin.fastkeyconnection.com/magnet/skid/frisian/")
            .addConverterFactory(TreeAppStringConverter())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    val api2: CloakApiService by lazy {
        retrofitCloak.create(CloakApiService::class.java)
    }


    companion object {
        const val IS_BLACK = "aaaaaaa"
        const val CLOAK_STRING = "bbbbbbb"
        const val IS_HAVE_GET_CLOAK = "cccccc"
        var gaid = ""
    }


    open fun checkIsLimitCloak(): Boolean {
        val isHaveCloak = SPUtils.getInstance().getBoolean(IS_HAVE_GET_CLOAK, false)
        val isBlack = SPUtils.getInstance().getBoolean(IS_BLACK, true)
        return if (isHaveCloak && isBlack) true
        else if (!isBlack) false
        else {
            circleToCheckCloak()
            true
        }
    }

    private fun circleToCheckCloak() {
        var i = 0
        MainScope().launch {
            while (true) {
                if (!SPUtils.getInstance().getBoolean(IS_HAVE_GET_CLOAK)) {
                    getCloakType(0)
                } else {
                    break
                }
                if (i == 3) break
                delay(10000)
                i++
            }
            while (true) {
                delay(300)
                if (SPUtils.getInstance().getBoolean(IS_HAVE_GET_CLOAK)) break
                if (BaseAppFlash.isFlashAppBackGround) i = 0
            }
        }

    }

    private fun getAndroidID(): String {
        return Settings.System.getString(
            BaseAppFlash.getInstance().contentResolver, Settings.Secure.ANDROID_ID
        )
    }


    fun getCloakType(a: Int) {
        val client = NetClientHelp()
        client.getMap("https://trypsin.fastkeyconnection.com/magnet/skid/frisian",DataHelp.cloakMapData(BaseAppFlash.getInstance()),object :NetClientHelp.Callback{
            override fun onSuccess(response: String) {
                when (response) {
                    "melville" -> {
                        SPUtils.getInstance().put(IS_HAVE_GET_CLOAK, true)
                        SPUtils.getInstance().put(IS_BLACK, false)
                        SPUtils.getInstance().put(CLOAK_STRING, a)
                    }

                    "patient" -> {
                        SPUtils.getInstance().put(IS_HAVE_GET_CLOAK, true)
                        SPUtils.getInstance().put(IS_BLACK, true)
                        SPUtils.getInstance().put(CLOAK_STRING, a)
                    }

                    else -> {
                        SPUtils.getInstance().put(IS_BLACK, true)
                        SPUtils.getInstance().put(CLOAK_STRING, a)
                    }
                }
            }

            override fun onFailure(error: String) {
            }
        })
    }

    interface CloakApiService {
        @GET("pascal")
        @Headers("Content-Type: application/json")
        fun getClock2(
            @Query("rhoda") rhoda: String,//distinct_id
            @Query("burnish") burnish: String,//client_ts
            @Query("vanilla") vanilla: String,//device_model
            @Query("levin") levin: String,//bundle_id
            @Query("artemis") artemis: String,//os_version
            @Query("till") till: String,//gaid
            @Query("classy") classy: String,//android_id
            @Query("puccini") puccini: String,//os
            @Query("weapon") weapon: String,//app_version
        ): Call<String>
    }


}

class TreeAppStringConverter : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type === String::class.java) {
            StringConverterTRee()
        } else null
    }
}


class StringConverterTRee : Converter<ResponseBody, String> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): String {
        return try {
            value.string()
        } finally {
            value.close()
        }
    }
}
