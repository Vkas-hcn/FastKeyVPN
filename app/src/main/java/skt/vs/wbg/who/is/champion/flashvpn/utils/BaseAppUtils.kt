package skt.vs.wbg.who.`is`.champion.flashvpn.utils

import android.app.ActivityManager
import android.app.Application
import android.os.Build
import android.os.Process
import android.util.Log
import android.webkit.WebView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import skt.vs.wbg.who.`is`.champion.flashvpn.BuildConfig
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashLogicBean
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashUserBean
import skt.vs.wbg.who.`is`.champion.flashvpn.net.FlashCloak
import skt.vs.wbg.who.`is`.champion.flashvpn.page.SPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper.initVpnFb
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData

object BaseAppUtils {
    const val TAG = "FlashVPN"
    const val vpn_url = "https://test.fastkeyconnection.com/aDrx/STzvKnnR/"
    const val tab_url = "https://test-varnish.fastkeyconnection.com/trap/romania"
    const val vpn_online = "vpn_online"
    const val ip_tab_flash = "ip_tab_flash"
    const val refer_tab = "refer_tab"
    const val refer_state = "refer_state"

    const val logTagFlash = "FlashVPN"
    const val vpn_ip = "vpn_ip"
    const val vpn_city = "vpn_city"
    const val ad_user_state = "ad_user_state"
    //refer_data
    const val refer_data = "refer_data"
    var isStartYep: Boolean = true
    var raoLiuTba = "raoLiuTba"
    //ad
    const val faSsion = "faSsion"

    // 买量
    const val faSsou = "faSsou"

    //类型
    const val faStry = "faStry"

    // 本地广告数据
    const val local_ad_data = """
{
  "faSdif":"ca-app-pub-3940256099942544/9257395921",
  "faSlity":"ca-app-pub-3940256099942544/6300978111",
  "faSpla":"ca-app-pub-3940256099942544/2247696110",
  "faSity":"ca-app-pub-3940256099942544/8691691433",
  "faSmay":"ca-app-pub-3940256099942544/8691691433"
}
    """

    //本地买量数据
    const val local_purchase_data = """
        {
    "faSment": 1,
    "faSunt": 2,
    "faSlite": 2,
    "faSand": 2,
    "faSroit": 2,
    "faSog": 2,
    "faSbre": 2
}
    """

    //本地广告逻辑
    const val local_ad_logic = """
{
    "faScorp": "1",
    "faSekin": "2",
    "faSsap": "2"
}    """

    fun initApp(application: Application) {
        if (isMainProcess(application)) {
            MobileAds.initialize(application) {}
            Firebase.initialize(application)
            FirebaseApp.initializeApp(application)
            initVpnFb()
            FlashCloak().checkIsLimitCloak()
            "f15".putPointYep(application)
        }
    }

    private fun isMainProcess(application: Application): Boolean {
        val myPid = Process.myPid()
        val activityManager = application.getSystemService(ActivityManager::class.java)
        val processInfoList = activityManager.runningAppProcesses
        val packageName = BuildConfig.APPLICATION_ID

        for (info in processInfoList) {
            if (info.pid == myPid && packageName == info.processName) {
                return true
            }
        }
        return false
    }


    private fun getAdString(data: String): String {
        return decodeBase64(data)
    }

    private fun getPurchaseString(data: String): String {
        return decodeBase64(data)
    }

    private fun getAdLogicString(data: String): String {
        return decodeBase64(data)
    }

    private fun decodeBase64(str: String): String {
        return String(android.util.Base64.decode(str, android.util.Base64.DEFAULT))
    }

    private fun fromJson(json: String): FlashAdBean {
        val gson = Gson()
        return gson.fromJson(json, FlashAdBean::class.java)
    }

    private fun fromUserJson(json: String): FlashUserBean {
        val gson = Gson()
        return gson.fromJson(json, FlashUserBean::class.java)
    }

    fun fromLogicJson(json: String): FlashLogicBean {
        val gson = Gson()
        return gson.fromJson(json, FlashLogicBean::class.java)
    }

    fun getAdJson(): FlashAdBean {
        val dataJson = SPUtils.getInstance().getString(faSsion).let {
            if (it.isNullOrEmpty()) {
                local_ad_data
            } else {
                getAdString(it)
            }
        }
        return runCatching {
            fromJson(dataJson)
        }.getOrNull() ?: fromJson(local_ad_data)
    }


    private fun getUserJson(): FlashUserBean {
        val dataJson = SPUtils.getInstance().getString(faSsou).let {
            if (it.isNullOrEmpty()) {
                local_purchase_data

            } else {
                getPurchaseString(it)
            }
        }
        return runCatching {
            fromUserJson(dataJson)
        }.getOrNull() ?: fromUserJson(local_purchase_data)
    }

    private fun getLogicJson(): FlashLogicBean {
        val dataJson = SPUtils.getInstance().getString(faStry).let {
            if (it.isNullOrEmpty()) {
                local_ad_logic
            } else {
                getAdLogicString(it)
            }
        }
        return runCatching {
            fromLogicJson(dataJson)
        }.getOrNull() ?: fromLogicJson(local_ad_logic)
    }
    private fun isFacebookUser(): Boolean {
        val data = getUserJson()
        val referrer = SPUtils.getInstance().getString(refer_data)
        val pattern = "fb4a|facebook".toRegex(RegexOption.IGNORE_CASE)
        return pattern.containsMatchIn(referrer) && data.faSment == "1"
    }

    fun isItABuyingUser(): Boolean {
        val data = getUserJson()
        val referrer = SPUtils.getInstance().getString(refer_data)
        val conditions = listOf(
            { isFacebookUser() },
            { data.faSunt == "1" && referrer.contains("gclid", true) },
            { data.faSlite == "1" && referrer.contains("not%20set", true) },
            { data.faSand == "1" && referrer.contains("youtubeads", true) },
            { data.faSroit == "1" && referrer.contains("%7B%22", true) },
            { data.faSog == "1" && referrer.contains("adjust", true) },
            { data.faSbre == "1" && referrer.contains("bytedance", true) }
        )
        return conditions.any { it() }
    }

    fun blockAdUsers(): Boolean {
        val data = getLogicJson().faScorp
        return when (data) {
            "1" -> true
            "2" -> isItABuyingUser()
            "3" -> false
            else -> true
        }
    }

    fun blockAdBlacklist(): Boolean {
        val blackData = SPUtils.getInstance().getBoolean(FlashCloak.IS_BLACK, true)
        return getLogicJson().faSekin == "1" && !blackData || getLogicJson().faSekin == "2"
    }

    fun spoilerOrNot(): Boolean {
        val faSsap = getLogicJson().faSsap
        return (faSsap == "1") || (faSsap == "3" && !isItABuyingUser())
    }

    fun setLoadData(key: String, value: Any) {
        when (value) {
            is String -> {
                SPUtils.getInstance().put(key, value)
            }

            is Int -> {
                SPUtils.getInstance().put(key, value)
            }

            is Boolean -> {
                SPUtils.getInstance().put(key, value)
            }

            is Float -> {
                SPUtils.getInstance().put(key, value)
            }

            is Long -> {
                SPUtils.getInstance().put(key, value)
            }

            else -> {
            }
        }
    }

    fun String.getLoadStringData(): String {
        return SPUtils.getInstance().getString(this)
    }

    fun String.getLoadBooleanData(): Boolean {
        return SPUtils.getInstance().getBoolean(this,false)
    }
}