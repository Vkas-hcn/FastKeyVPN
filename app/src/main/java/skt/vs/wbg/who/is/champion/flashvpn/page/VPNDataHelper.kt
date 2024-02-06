package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.BuildConfig
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.OnlineVpnHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import java.lang.reflect.Type

object VPNDataHelper {
    var nodeIndex: Int
        get() {
            var node = BaseAppFlash.xkamkaxmak.decodeInt("nodeIndex", 0)
            if (getAllVpnListData().size <= node) node = 0
            return node
        }
        set(value) {
            BaseAppFlash.xkamkaxmak.encode("nodeIndex", value)
        }

    var cachePosition = -1


    fun getAllVpnListData(): MutableList<LocaleProfile> {
        return getAllLocaleProfile()
    }
    val recentlyListData: MutableList<LocaleProfile> by lazy {
        mutableListOf()
    }
    fun getRecentlyList(): MutableList<LocaleProfile> {
        val recentlyListJson = BaseAppFlash.xkamkaxmak.decodeString("recentlyListData")
        recentlyListData.clear()
        if (recentlyListJson?.isNotBlank() == true) {
            val listType: Type = object : TypeToken<ArrayList<LocaleProfile>>() {}.type
            val dataList: ArrayList<LocaleProfile> = Gson().fromJson(recentlyListJson, listType)
            recentlyListData.addAll(dataList)
        }
        //截取前2个
        return recentlyListData.take(2).toMutableList()
    }
    fun addRecentlyList(data: LocaleProfile) {
        recentlyListData.add(0, data)
        saveRecentlyList()
    }
    private fun saveRecentlyList() {
        BaseAppFlash.xkamkaxmak.encode("recentlyListData", Gson().toJson(recentlyListData))
    }


     fun getAllLocaleProfile(): MutableList<LocaleProfile> {
        val list = OnlineVpnHelp.getDataFromTheServer()
        val data = Gson().toJson(list)
        list?.add(0, getFastVpnOnLine())
        return list ?: local
    }

    private val local = listOf(
        LocaleProfile(
            city = "",
            name = "",
            onLi = "",
            onLm_host = "",
            onLo_Port = 0,
            onLu_password = ""
        )
    ).toMutableList()

    private fun getFastVpnOnLine(): LocaleProfile {
        val ufVpnBean: MutableList<LocaleProfile>? = OnlineVpnHelp.getDataFastServerData()
        return if (ufVpnBean == null) {
            val data = OnlineVpnHelp.getDataFromTheServer()?.getOrNull(0)
            LocaleProfile(
                city = data?.city.toString(),
                name = data?.name.toString(),
                onLu_password = data?.onLu_password.toString(),
                onLo_Port = data?.onLo_Port ?: 0,
                onLm_host = data?.onLm_host.toString(),
                onLi = data?.onLi.toString()
            )
        } else {
            ufVpnBean.shuffled().first().apply {
                name = "Fast Server"
            }
        }
    }

    fun getImage(name: String): Int {
        val a = name.trim().replace(" ", "").lowercase()
        when (a) {
            "japan" -> return R.drawable.icon_japan
            "koreasouth" -> return R.drawable.icon_korea
            "netherlands" -> return R.drawable.icon_netherlands
            "brazil" -> return R.drawable.icon_brazil
            "canada" -> return R.drawable.icon_canada
            "france" -> return R.drawable.icon_france
            "india" -> return R.drawable.icon_india
            "singapore" -> return R.drawable.icon_singapore
            "unitedkingdom" -> return R.drawable.icon_gb
            "unitedstates" -> return R.drawable.icon_united_states
            "australia" -> return R.drawable.icon_australia
            else -> return R.drawable.icon_fast
        }
    }

    private var remoteVPNNormalString: String? = null
    private var remoteVPNSmartString: String? = null

    @SuppressLint("StaticFieldLeak")
    var remoteConfig: FirebaseRemoteConfig? = null
    private var isGetRemoteString = false

    private var remoteAllList: ArrayList<LocaleProfile>? = null
    private var remoteSmartStringList: ArrayList<String>? = null


    private fun appInitGetVPNFB() {
        remoteConfig = Firebase.remoteConfig
        remoteConfig?.fetchAndActivate()?.addOnSuccessListener {
            remoteVPNNormalString = remoteConfig?.getString("onLwww")
            remoteVPNSmartString = remoteConfig?.getString("onLppp")
            dealFBData()
        }
    }

    private fun dealFBData() {
        if (remoteVPNNormalString?.isNotBlank() == true) {
            try {
                val gson = Gson()
                val base64ListAd = Base64Utils.decode(remoteVPNNormalString)
                val listType: Type = object : TypeToken<ArrayList<LocaleProfile>>() {}.type
                val dataList: ArrayList<LocaleProfile> = gson.fromJson(base64ListAd, listType)
                if (dataList.size > 0) {
                    isGetRemoteString = true
                    remoteAllList = dataList
                }
            } catch (e: Exception) {
                e.printStackTrace()
                remoteVPNNormalString = null
            }
        }
        if (remoteVPNSmartString?.isNotBlank() == true) {
            try {
                val gson = Gson()
                val base64ListAd = Base64Utils.decode(remoteVPNSmartString)
                val listType: Type = object : TypeToken<ArrayList<String>>() {}.type
                val dataList: ArrayList<String> = gson.fromJson(base64ListAd, listType)

                if (dataList.size > 0) {
                    remoteSmartStringList = dataList
                }
            } catch (e: Exception) {
                e.printStackTrace()
                remoteVPNSmartString = null
            }
        }

    }


    fun initVpnFb() {
        if (!BuildConfig.DEBUG) {
            appInitGetVPNFB()
            MainScope().launch {
                delay(4100)
                if (!isGetRemoteString) {
                    while (true) {
                        if (!isGetRemoteString) dealFBData()
                        delay(1900)
                    }
                }
            }
        }
    }

}

object Base64Utils {
    fun decode(encodedString: String?): String {
        return String(Base64.decode(encodedString?.toByteArray(), Base64.DEFAULT))
    }
}

@Keep
data class LocaleProfile(
    @SerializedName("onLu")
    var onLu_password: String = "",
    @SerializedName("onLi")
    var onLi: String = "",
    @SerializedName("onLo")
    var onLo_Port: Int = 0,
    @SerializedName("onLp")
    var name: String = "",
    @SerializedName("onLl")
    var city: String = "",
    @SerializedName("onLm")
    var onLm_host: String = ""
)
