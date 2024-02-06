package skt.vs.wbg.who.`is`.champion.flashvpn.tab

import android.content.Context
import android.util.Base64
import android.util.Log
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo
import org.json.JSONObject
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG

class FlashOkHttpUtils {
    val client = NetClientHelp()

    fun getTbaIp(context: Context) {
        try {
            client.get(context, "https://ifconfig.me/ip", object : NetClientHelp.Callback {
                override fun onSuccess(response: String) {
                    BaseAppUtils.setLoadData(BaseAppUtils.ip_tab_flash,response)
                }

                override fun onFailure(error: String) {
                }
            })
        } catch (e: Exception) {

        }

    }

    fun getSessionList(context: Context) {
        val data = DataHelp.getSessionJson(context)
        Log.e(TAG, "Session request data= $data")
        try {
            client.post(BaseAppUtils.tab_url, data, object : NetClientHelp.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "Session-- success----->${response}")
                }

                override fun onFailure(error: String) {
                    Log.e(TAG, "Session-- error----->${error}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Session-- error----->${e}")
        }
    }

    fun getInstallList(context: Context, rd: ReferrerDetails) {
        val data = DataHelp.getInstallJson(rd, context)
        Log.d(TAG, "Install request data= $data")
        try {
            client.post(BaseAppUtils.tab_url, data, object : NetClientHelp.Callback {
                override fun onSuccess(response: String) {
                    Log.d(TAG, "Install 事件上报成功----->${response}")
                    BaseAppUtils.setLoadData(BaseAppUtils.refer_tab, true)
                }

                override fun onFailure(error: String) {
                    BaseAppUtils.setLoadData(BaseAppUtils.refer_tab, false)
                    Log.d(TAG, "Install事件上报失败----->${error}")
                }
            })
        } catch (e: Exception) {
            BaseAppUtils.setLoadData(BaseAppUtils.refer_tab, false)
            Log.d(TAG, "Install事件上报失败----->${e}")
        }
    }

    fun getAdList(
        context: Context,
        adValue: AdValue,
        responseInfo: ResponseInfo,
        type: String,
        flashAdBean: FlashAdBean
    ) {
        DataHelp.putPointTimeYep("f37",flashAdBean.loadCity,"xxd",context)
        DataHelp.putPointTimeYep("f38",flashAdBean.showTheCity,"uds",context)
        val json = DataHelp.getAdJson(context, adValue, responseInfo, type, flashAdBean)
        Log.d(TAG, "ad---${type}--request data-->${json}")
        try {
            client.post(BaseAppUtils.tab_url, json, object : NetClientHelp.Callback {
                override fun onSuccess(response: String) {
                    Log.d(TAG, "${type}广告事件上报-成功->")
                }

                override fun onFailure(error: String) {
                    Log.d(TAG, "${type}广告事件上报-失败-->${error}")
                }
            })
        } catch (e: Exception) {

        }
        DataHelp.putPointAdJiaZhiOnline(adValue.valueMicros)

    }


    fun getTbaList(
        context: Context,
        eventName: String,
        parameterName: String = "",
        tbaValue: Any = 0,
        wTime: Int = 0,
    ) {
        val json = if (wTime == 0) {
            DataHelp.getTbaDataJson(context, eventName)
        } else {
            DataHelp.getTbaTimeDataJson(context, tbaValue, eventName, parameterName)
        }
        Log.d(TAG, "${eventName}--TBA事件上报-->${json}")
        try {
            client.post(BaseAppUtils.tab_url, json, object : NetClientHelp.Callback {
                override fun onSuccess(response: String) {
                    Log.d(TAG, "${eventName}--TBA事件上报-成功->")
                }

                override fun onFailure(error: String) {
                    Log.d(TAG, "${eventName}--TBA事件上报-失败-->${error}")
                }
            })
        } catch (e: Exception) {

        }
    }


    fun getVpnData(context: Context) {
        "f34".putPointYep( context)
        val date = System.currentTimeMillis()
        try {
            client.get(context, BaseAppUtils.vpn_url, object : NetClientHelp.Callback {
                override fun onSuccess(response: String) {
                    val responseData = processString(response)
                    BaseAppUtils.setLoadData(BaseAppUtils.vpn_online, responseData)
                    Log.d(TAG, "获取下发服务器数据-成功->$responseData")
                    "f35".putPointYep(context)
                    val date2 = (System.currentTimeMillis()-date)/1000
                    DataHelp.putPointTimeYep("f36", date2,"time",context)
                }

                override fun onFailure(error: String) {
                    Log.d(TAG, "获取下发服务器数据-失败->${error}")
                }
            })
        } catch (e: Exception) {
        }
    }

    fun processString(inputString: String): String {
        val trimmedString = inputString.substring(45) // 去掉前 45 个字符
        val reversedString = trimmedString.reversed() // 反转字符串
        val decodedBytes = Base64.decode(reversedString, Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }

}