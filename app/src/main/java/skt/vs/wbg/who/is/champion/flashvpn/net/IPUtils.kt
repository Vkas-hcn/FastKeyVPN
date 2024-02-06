package skt.vs.wbg.who.`is`.champion.flashvpn.net

import android.app.Dialog
import android.util.Log
import android.view.Gravity
import android.webkit.WebSettings
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatTextView
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ProgressActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

object IPUtils {

    private var retrofit: Retrofit? = null
    private val netInterceptor = NetInterceptor()

    private val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit =
                    Retrofit.Builder().client(createOkhttp()).baseUrl("https://ipapi.co/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit
        }
    var isShowBandedDialog = false

    private fun createOkhttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        return builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
            .addNetworkInterceptor(netInterceptor).readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true).build()
    }

    fun checkIp(activity: BaseActivityFlash<*>) {
        val apiService: ApiService = retrofitInstance!!.create(ApiService::class.java)

        val call: Call<KKKKKK> = apiService.getIPAddress1()

        call.enqueue(object : Callback<KKKKKK> {
            override fun onResponse(call: Call<KKKKKK>, response: Response<KKKKKK>) {
                if (response.isSuccessful) {
                    val data: KKKKKK? = response.body()
                    Log.e("okhttp", "sus ${data?.country_code}")

                    isShowBandedDialog = checkIpIsBanded(data?.country_code?.lowercase())
                    if (isShowBandedDialog && activity !is ProgressActivity) {
                        showDialog(activity)
                    }
                    if (data?.country_code?.isNotBlank() == true)
                        BaseAppFlash.xkamkaxmak.encode(
                            "BaseActivityTree.country_code",
                            data.country_code.lowercase()
                        )

                } else {
                    setIsBanded(activity)
                }

            }

            override fun onFailure(call: Call<KKKKKK>, t: Throwable) {
                setIsBanded(activity)
                Log.e("okhttp", t.message.toString())

            }

        })
    }

    fun setIsBanded(activity: BaseActivityFlash<*>) {
        val countryCode =
            BaseAppFlash.xkamkaxmak.getString("BaseActivityTree.country_code", "") ?: ""

        isShowBandedDialog = if (countryCode.isBlank()) {
            checkIpIsBandedForLanguage()
        } else {
            checkIpIsBanded(countryCode.lowercase())
        }
        if (isShowBandedDialog && activity !is ProgressActivity) {
            showDialog(activity)
        }

    }

    private var isShowNum = false
    private fun showDialog(activity: BaseActivityFlash<*>) {
        if (!isShowNum) {
            "f2".putPointYep(activity)
            isShowNum = true
        }
        val customDialog = Dialog(activity, R.style.AppDialogStyle)
        val localLayoutParams = customDialog.window?.attributes
        localLayoutParams?.gravity = Gravity.CENTER
        customDialog.window?.attributes = localLayoutParams
        customDialog.setContentView(R.layout.lllllllll)
        val confirmButton = customDialog.findViewById<AppCompatTextView>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            BaseAppFlash.acFlashList.forEach { it.finish() }
            exitProcess(0)
        }
//        customDialog.show()
    }

    fun checkIpIsBanded(string: String?): Boolean {
        Log.e("okhttp banded", "checkIpIsBanded")
        return if (string.isNullOrEmpty()) {
            checkIpIsBandedForLanguage()
        } else {
            when (string) {
                "cn", "hk", "ir", "mo" -> true
                else -> false
            }
        }
    }

    private fun checkIpIsBandedForLanguage(): Boolean {
        Log.e("okhttp banded", "checkIpIsBandedForLanguage")
        return when (Locale.getDefault().language.toLowerCase()) {
            "zh", "fa" -> true

            else -> false
        }
    }


}

class NetInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request: Request = chain.request().newBuilder()
            .addHeader("Connection", "close")
            .addHeader(
                "User-Agent",
                WebSettings.getDefaultUserAgent(BaseAppFlash.getInstance().applicationContext)
            )
            .build()
        return chain.proceed(request)
    }
}

interface ApiService {
    @GET("json")
    fun getIPAddress1(): Call<KKKKKK>
}

@Keep
data class KKKKKK(
    var ip: String, var city: String, var country_code: String
)