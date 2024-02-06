package skt.vs.wbg.who.`is`.champion.flashvpn.base

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.google.android.gms.ads.AdActivity
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ProgressActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.SPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadBooleanData
import android.content.Context
import android.os.Handler
import android.os.Looper

class BaseAppFlash : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        var application: BaseAppFlash? = null
        fun getInstance(): BaseAppFlash {
            if (application == null) application = BaseAppFlash()
            return application ?: BaseAppFlash()
        }

        var isHotStart: Boolean = false
        var isUserMainBack: Boolean = false
        var isFlashAppBackGround: Boolean = false
        var acFlashTotal = 0
        var acFlashList = mutableListOf<Activity>()
        var exitAppTime = 0L
        val xkamkaxmak by lazy { MMKV.defaultMMKV() }
        val mmkvFlash by lazy {
            MMKV.mmkvWithID("FlashVpn", MMKV.MULTI_PROCESS_MODE)
        }
        var vpnState = ""
        var vpnClickState = -1
        var uuidData = ""
    }

    var adActivity: Activity? = null
    var referJobFlash: Job? = null
    override fun onCreate() {
        super.onCreate()
        application = this
        MMKV.initialize(application)
        BaseAppUtils.initApp(this)
        registerActivityLifecycleCallbacks(this)
        getReferInformation(this)
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        acFlashList.add(activity)

    }

    override fun onActivityStarted(activity: Activity) {
        acFlashTotal++

        if (activity is AdActivity) {
            adActivity = activity
        }
        if (isFlashAppBackGround) {
            isFlashAppBackGround = false
            if ((System.currentTimeMillis() - exitAppTime) / 1000 > 3) {
                toSplash(activity)
            }
        }

    }

    private fun toSplash(activity: Activity) {
        "f14".putPointYep(activity)
        if (activity is ProgressActivity) {
            activity.finish()
        }
        val intent = Intent(activity, ProgressActivity::class.java)
        activity.startActivity(intent)
        if (adActivity != null) {
            adActivity?.finish()
        }

        isHotStart = true
        BaseAd.getEndInstance().whetherToShowFlash = false
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        acFlashTotal--
        if (acFlashTotal == 0) {
            isFlashAppBackGround = true
            exitAppTime = System.currentTimeMillis()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        acFlashList.remove(activity)
    }

    private var handler: Handler? = null
    private val delayMillis: Long = 5000
    private fun getReferInformation(context: Context) {
        handler?.removeCallbacksAndMessages(null)
        handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
//                SPUtils.getInstance().put(BaseAppUtils.refer_data, "fb4a")
                if (SPUtils.getInstance().getString(BaseAppUtils.refer_data).isNullOrEmpty()) {
                    getInstallReferrer(context)
                    handler?.postDelayed(this, delayMillis)
                } else {
                    cancel()
                }
            }
        }
        handler?.postDelayed(runnable, delayMillis)
    }

    fun cancel() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    private fun handleInstallReferrerOK(context: Context, installReferrer: String, date: Long,referrerClient: InstallReferrerClient) {
        SPUtils.getInstance().put(BaseAppUtils.refer_data, installReferrer)
        val loadDate = (System.currentTimeMillis() - date) / 1000
        DataHelp.putPointTimeYep("f4", loadDate.toInt(), "conntime", context)
        if (!BaseAppUtils.refer_tab.getLoadBooleanData()) {
            runCatching {
                referrerClient.installReferrer?.run {
                    FlashOkHttpUtils().getInstallList(context, this)
                }
            }.exceptionOrNull()
        }
    }

    private fun getInstallReferrer(context: Context) {
        val referrer = SPUtils.getInstance().getString(BaseAppUtils.refer_data)
        if (referrer.isNotBlank()) {
            return
        }
        val date = System.currentTimeMillis()
        val referrerClient = InstallReferrerClient.newBuilder(context).build()

        try {
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val installReferrer =
                                referrerClient.installReferrer.installReferrer ?: ""
                            handleInstallReferrerOK(context, installReferrer, date,referrerClient)
                        }
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        } catch (e: Exception) {
            // 处理异常
        }
    }



}