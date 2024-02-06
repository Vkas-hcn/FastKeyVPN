package skt.vs.wbg.who.`is`.champion.flashvpn.tab

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import skt.vs.wbg.who.`is`.champion.flashvpn.page.LocaleProfile
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData

object OnlineVpnHelp {
    /**
     * 检查是否有下发服务器数据
     */
    fun checkServerData(context: Context): Boolean {
        val data = getDataFromTheServer()
        return if (data == null) {
            FlashOkHttpUtils().getVpnData(context)
            false
        } else {
            true
        }
    }
    /**
     * 获取下发服务器数据
     */
     fun getDataFromTheServer(): MutableList<LocaleProfile>? {
        val data = BaseAppUtils.vpn_online.getLoadStringData()
        return runCatching {
            val spinVpnBean = Gson().fromJson(data, OnlineVpnBean::class.java)
            val data = spinVpnBean.data.BDQkmnzDYM
            val data2 = data.distinctBy { it.VUjGPZr }
            Log.e(TAG,"data=${data}")
            if (data2.isNotEmpty()) {
                data2.map {
                    LocaleProfile().apply {
                        onLm_host = it.VUjGPZr
                        onLo_Port = it.iGsETAErS
                        onLi = it.ytJ
                        onLu_password = it.HTnEGo
                        city = it.goyZFW
                        name = it.DWmA
                    }
                }.toMutableList()
            } else {
                null
            }
        }.getOrElse {
            null
        }
    }

    fun getDataFastServerData(): MutableList<LocaleProfile>? {
        val data =  BaseAppUtils.vpn_online.getLoadStringData()
        return runCatching {
            val spinVpnBean = Gson().fromJson(data, OnlineVpnBean::class.java)
            val data = spinVpnBean.data.FBoulP
            if (data.isNotEmpty()) {
                data.distinctBy { it.VUjGPZr }.map {
                    LocaleProfile().apply {
                        onLm_host = it.VUjGPZr
                        onLo_Port = it.iGsETAErS
                        onLi = it.ytJ
                        onLu_password = it.HTnEGo
                        city = it.goyZFW
                        name = it.DWmA
                    }
                }.toMutableList()
            } else {
                null
            }
        }.getOrElse {
            null
        }
    }
}