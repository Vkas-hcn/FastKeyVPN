package skt.vs.wbg.who.`is`.champion.flashvpn.tab

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.android.installreferrer.api.ReferrerDetails
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import skt.vs.wbg.who.`is`.champion.flashvpn.BuildConfig
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.AdType
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.page.SPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import java.util.Currency
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

object DataHelp {
    val tillData = BaseAppFlash.mmkvFlash.getString("till", "")
    fun getAppVersion(application: Application): String {
        try {
            val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)

            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Version information not available"
    }

    private fun getAppId(application: Application): String {
        return Settings.Secure.getString(
            application.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
    fun cloakMapData(application: Application): Map<String, Any> {
        getTillData(application)
        return mapOf(
            //distinct_id
            "rhoda" to (UUID.randomUUID().toString()),
            //client_ts
            "burnish" to (System.currentTimeMillis()),
            //device_model
            "vanilla" to Build.MODEL,
            //bundle_id
            "levin" to ("com.fast.key.connection.secure.tool"),
            //os_version
            "artemis" to Build.VERSION.RELEASE,
            //gaid
            "till" to tillData as Any,
            //android_id
            "classy" to getAppId(application),
            //os
            "puccini" to "gourmet",
            //app_version
            "weapon" to getAppVersion(application),
        )
    }
    fun getTillData(context: Context){
        GlobalScope.launch(Dispatchers.IO) {
            if(tillData.isNullOrEmpty()){
                BaseAppFlash.mmkvFlash.encode("till", runCatching { AdvertisingIdClient.getAdvertisingIdInfo(context).id }.getOrNull() ?: "")
            }
        }
    }
    @SuppressLint("HardwareIds")
    private fun createJsonData(context: Context): JSONObject {
        getTillData(context)
        val jsonData = JSONObject()
        //client_ts
        jsonData.put("burnish", System.currentTimeMillis())
        //manufacturer
        jsonData.put("wilkes", Build.MODEL)
        //os_version
        jsonData.put("artemis", Build.VERSION.RELEASE)
        //gaid
        jsonData.put(
            "till",
            (BaseAppFlash.mmkvFlash.getString("till", ""))
        )
        //device_model
        jsonData.put("vanilla", Build.MODEL)
        //android_id
        jsonData.put(
            "classy",
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        )
        //network_type
        jsonData.put("ideology", "")
        //operator
        jsonData.put("heroin", getNetworkInfo(context))
        //distinct_id
        jsonData.put(
            "rhoda",
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        )
        //app_version
        jsonData.put("weapon", getAppVersion(context))
        //system_language
        jsonData.put("stamina", "${Locale.getDefault().language}_${Locale.getDefault().country}")
        //log_id
        jsonData.put("emission", UUID.randomUUID().toString())
        //channel
        jsonData.put("paranoia", "")
        //bundle_id
        jsonData.put("levin", context.packageName)
        //os
        jsonData.put("puccini", "gourmet")

        return jsonData
    }

    fun getSessionJson(context: Context): String {
        val topLevelJson = createJsonData(context)
        topLevelJson.apply {
            put("gale", JSONObject())
        }
        return topLevelJson.toString()
    }

    fun getInstallJson(rd: ReferrerDetails, context: Context): String {
        val topLevelJson = createJsonData(context)
        topLevelJson.apply {
            //build
            put("hiawatha", "build/${Build.ID}")

            //referrer_url
            put("titrate", rd.installReferrer)

            //install_version
            put("build", rd.installVersion)

            //user_agent
            put("seawater", getWebDefaultUserAgent(context))

            //lat
            put("gamble", getLimitTracking(context))

            //referrer_click_timestamp_seconds
            put("utopian", rd.referrerClickTimestampSeconds)

            //install_begin_timestamp_seconds
            put("starry", rd.installBeginTimestampSeconds)

            //referrer_click_timestamp_server_seconds
            put("burnt", rd.referrerClickTimestampServerSeconds)

            //install_begin_timestamp_server_seconds
            put("solenoid", rd.installBeginTimestampServerSeconds)

            //install_first_seconds
            put("kinesic", getFirstInstallTime(context))

            //last_update_seconds
            put("warranty", getLastUpdateTime(context))
            put("best", "squeegee")
        }

        return topLevelJson.toString()
    }

    fun getAdJson(
        context: Context, adValue: AdValue,
        responseInfo: ResponseInfo,
        type: String,
        yepAdBean: FlashAdBean
    ): String {
        val topLevelJson = createJsonData(context)
        val cause = JSONObject()
        //ad_pre_ecpm
        cause.put("procure", adValue.valueMicros)
        //currency
        cause.put("porte", adValue.currencyCode)
        //ad_network
        cause.put(
            "uplift",
            responseInfo.mediationAdapterClassName
        )
        //ad_source
        cause.put("amount", "admob")
        //ad_code_id
        cause.put("marvin", getAdType(type).id)
        //ad_pos_id
        cause.put("mentor", getAdType(type).name)
        //ad_rit_id
        cause.put("crewcut", "")
        //ad_sense
        cause.put("concept", "")
        //ad_format
        cause.put("textural", getAdType(type).type)
        //precision_type
        cause.put("glance", getPrecisionType(adValue.precisionType))
        //ad_load_ip
        cause.put("bauble", yepAdBean.loadIp ?: "")
        //ad_impression_ip
        cause.put("craw", yepAdBean.showIp ?: "")
        topLevelJson.put("cause", cause)

        return topLevelJson.toString()
    }

    fun getTbaDataJson(context: Context, name: String): String {
        return createJsonData(context).apply {
            put("best", name)
        }.toString()
    }

    fun getTbaTimeDataJson(
        context: Context,
        time: Any,
        name: String,
        parameterName: String
    ): String {
        val data = JSONObject()
        data.put(parameterName, time)
        return createJsonData(context).apply {
            put("best", name)
            put("facto",JSONObject().apply {
                put(parameterName, time)
            })
        }.toString()
    }

    private fun getAppVersion(context: Context): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Version information not available"
    }


    private fun getNetworkInfo(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val carrierName = telephonyManager.networkOperatorName
        val networkOperator = telephonyManager.networkOperator
        val mcc = if (networkOperator.length >= 3) networkOperator.substring(0, 3) else ""
        val mnc = if (networkOperator.length >= 5) networkOperator.substring(3) else ""

        return """
        Carrier Name: $carrierName
        MCC: $mcc
        MNC: $mnc
    """.trimIndent()
    }


    private fun getWebDefaultUserAgent(context: Context): String {
        return try {
            WebSettings.getDefaultUserAgent(context)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getLimitTracking(context: Context): String {
        return try {
            if (AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled) {
                "dome"
            } else {
                "lactose"
            }
        } catch (e: Exception) {
            "lactose"
        }
    }

    private fun getFirstInstallTime(context: Context): Long {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun getLastUpdateTime(context: Context): Long {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.lastUpdateTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }


    private fun getAdType(type: String): AdType {
        var adType = AdType("", "", "", "")
        val adData = BaseAppUtils.getAdJson()
        when (type) {
            "open" -> {
                adType = AdType(adData.faSdif, "open", "faSdif", "open")
            }

            "home" -> {
                adType = AdType(adData.faSlity, "home", "faSlity", "native")
            }

            "end" -> {
                adType = AdType(adData.faSpla, "end", "faSpla", "native")
            }

            "connect" -> {
                adType = AdType(adData.faSity, "connect", "faSity", "interstitial")
            }

            "back" -> {
                adType = AdType(adData.faSmay, "back", "faSmay", "interstitial")
            }

            "banner" -> {
                adType = AdType(adData.faSlity, "back", "faSlity", "banner")
            }
        }
        return adType
    }

    private fun getPrecisionType(precisionType: Int): String {
        return when (precisionType) {
            0 -> {
                "UNKNOWN"
            }

            1 -> {
                "ESTIMATED"
            }

            2 -> {
                "PUBLISHER_PROVIDED"
            }

            3 -> {
                "PRECISE"
            }

            else -> {
                "UNKNOWN"
            }
        }
    }

    fun String.putPointYep(context: Context) {
        FlashOkHttpUtils().getTbaList(context, this)
    }

    fun putPointTimeYep(name: String, time: Any, parameterName: String, context: Context) {
        FlashOkHttpUtils().getTbaList(context, name, parameterName, time, 1)
    }

    fun putPointAdJiaZhiOnline(adValue: Long) {
        if(BuildConfig.DEBUG){
            return
        }
        AppEventsLogger.newLogger(BaseAppFlash.getInstance()).logPurchase(
            (adValue / 1000000.0).toBigDecimal(), Currency.getInstance("USD")
        )
    }

    fun isConnectFun(): Boolean {
        return BaseAppFlash.vpnState == "CONNECTED"
    }


}