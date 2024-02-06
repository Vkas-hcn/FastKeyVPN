package skt.vs.wbg.who.`is`.champion.flashvpn.data

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.VpnService
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Chronometer
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.gson.Gson
import de.blinkt.openvpn.api.ExternalOpenVPNService
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBannerAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadConnectAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash.Companion.isUserMainBack
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ConfigActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.HomeActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.LocaleProfile
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper
import skt.vs.wbg.who.`is`.champion.flashvpn.page.WebFlashActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.OnlineVpnHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@SuppressLint("StaticFieldLeak")
class MainViewModel : ViewModel() {
    var userInterrupt = false
    lateinit var activity: WeakReference<HomeActivity>
    var openServerState = MutableLiveData<OpenServiceState>()
    private var shadowsocksJob: Job? = null

    private lateinit var setIcon: AppCompatImageView
    private lateinit var connectImg: AppCompatImageView
    private lateinit var flashListName: AppCompatTextView
    private lateinit var connectAnimate: AppCompatImageView
    private lateinit var flashListIcon: AppCompatImageView
    private lateinit var listCl: ConstraintLayout
    private lateinit var chronometer: Chronometer
    private lateinit var requestPermissionForResultVPN: ActivityResultLauncher<Intent?>
    private lateinit var mainToListResultIntent: ActivityResultLauncher<Intent>
    lateinit var rotateAnimation: Animation
    private var curServerState: String? = ""

    var showConnectLive = MutableLiveData<Boolean>()
    private var showConnectJob: Job? = null
    private var isClickConnect = false

    enum class OpenServiceState { CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED }

    fun init(
        activity: HomeActivity,
        setIcon: AppCompatImageView,
        connectImg: AppCompatImageView,
        connectAnimate: AppCompatImageView,
        listCl: ConstraintLayout,
        flashListIcon: AppCompatImageView,
        flashListName: AppCompatTextView,
        chronometer: Chronometer
    ) {
        this.activity = WeakReference(activity)
        activity.bindService(
            Intent(activity, ExternalOpenVPNService::class.java),
            mConnection,
            AppCompatActivity.BIND_AUTO_CREATE
        )
        this.setIcon = setIcon
        this.connectImg = connectImg
        this.connectAnimate = connectAnimate
        this.listCl = listCl
        this.flashListName = flashListName
        this.flashListIcon = flashListIcon
        this.chronometer = chronometer
        openServerState.postValue(OpenServiceState.DISCONNECTED)
        rotateAnimation = AnimationUtils.loadAnimation(activity, R.anim.ratate_anim)

        initOb()
        initViewAndListener()
        requestPermissionForResultVPN =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                requestPermissionForResult(it)
            }
        mainToListResultIntent =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == 100) {
                    toConnectVerifyNet()
                }
            }

    }


    private fun requestPermissionForResult(result: ActivityResult) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            "f7".putPointYep(this.activity.get()!!)
            activity.get()?.let { mService?.let { it1 -> toConnectVerifyNet() } }
        } else {
            openServerState.postValue(OpenServiceState.DISCONNECTED)
        }
    }

    private fun getConnectTime(context: Context) {
        val time = (System.currentTimeMillis() - connectTime) / 1000
        DataHelp.putPointTimeYep("f13", time, "conntime", context)
        connectTime = 0
    }

    private fun initOb() {
        activity.get()?.mBinding?.lifecycleOwner?.let {
            openServerState.observe(it) { state ->
                when (state) {
                    OpenServiceState.CONNECTING -> {
                        setViewEnabled(false)
                    }

                    OpenServiceState.CONNECTED -> {
                        "f11".putPointYep(activity.get()!!)
                        getConnectTime(activity.get()!!)
                        if (isClickConnect && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                            showConnectLive.postValue(true)
                            isClickConnect = false
                            BaseAd.getBackInstance().advertisementLoadingFlash(activity.get()!!)
                        } else {
                            setViewEnabled(true)
                            stopConnectAnimation()
                            setChromometer()
                        }
                    }

                    OpenServiceState.DISCONNECTING -> {
                        setViewEnabled(true)
                    }

                    OpenServiceState.DISCONNECTED -> {
                        Log.e(
                            TAG,
                            "initOb: ${it.lifecycle.currentState == Lifecycle.State.RESUMED}"
                        )
                        if (isClickConnect && !isFailConnect && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                            showConnectLive.postValue(false)
                            isClickConnect = false
                        } else {
                            setViewEnabled(true)
                            stopConnectAnimation()
                            setChromometer()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun stopConnectAnimation() {
        connectAnimate.clearAnimation()
        if (openServerState.value == OpenServiceState.CONNECTED) {
            connectAnimate.setImageResource(R.drawable.c_okkkkk)
            connectImg.setImageResource(R.drawable.connect_ok)
        } else {
            connectAnimate.setImageResource(R.drawable.c_noooo)
            connectImg.setImageResource(R.drawable.connect_no)
        }
    }

    private fun setViewEnabled(b: Boolean) {
        setIcon.isEnabled = b
        connectAnimate.isEnabled = b
        listCl.isEnabled = b
        connectImg.isEnabled = b
    }

    private var lastClickTime: Long = 0
    private val delayMillis: Long = 2000
    private fun clickToAction(activity: HomeActivity) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= delayMillis) {
            connectPut(activity)
            if (activity.isShowGuide) {
                activity.cancelGuideLottie()
            }
            toConnectVerifyNet()
            lastClickTime = currentTime
        }
    }

    private fun initViewAndListener() {
        activity.get()?.let { ac ->
            chronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener { cArg ->
                val time = System.currentTimeMillis() - cArg.base
                val d = Date(time)
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                chronometer.text = sdf.format(d)
            }

            setIcon.setOnClickListener {
                "f26".putPointYep(ac)
                if (openServerState.value == OpenServiceState.DISCONNECTING) {
                    stopToConnectOrDisConnect()
                    return@setOnClickListener
                }
                if (!ac.mBinding.drawer.isOpen) ac.mBinding.drawer.open()
            }
            connectAnimate.setOnClickListener {
                clickToAction(ac)
            }
            connectImg.setOnClickListener {
                clickToAction(ac)
            }
            listCl.setOnClickListener {
                if (openServerState.value == OpenServiceState.DISCONNECTING) {
                    stopToConnectOrDisConnect()
                    return@setOnClickListener
                }
                isNextConnect(ac) {
                    val intent = Intent(ac, ConfigActivity::class.java)
                    intent.putExtra(
                        "IS_CONNECT", openServerState.value == OpenServiceState.CONNECTED
                    )
                    intent.putExtra("IS_HAVE_DATA", it)
                    mainToListResultIntent.launch(intent)
                }
            }
            ac.mBinding.pppppppp.setOnClickListener {
                ac.startActivity(Intent(ac, WebFlashActivity::class.java))
            }

            ac.mBinding.update.setOnClickListener {
                val appPackageName = ac.packageName
                try {
                    val launchIntent = Intent()
                    launchIntent.data = Uri.parse("market://details?id=$appPackageName")
                    ac.startActivity(launchIntent)
                } catch (anfe: ActivityNotFoundException) {
                    ac.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }

            ac.mBinding.clShare.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=${ac.packageName}"
                )
                sendIntent.type = "text/plain"
                val shareIntent = Intent.createChooser(sendIntent, null)
                ac.startActivity(shareIntent)
            }

            ac.mBinding.lifecycleOwner?.let {
                ac.onBackPressedDispatcher.addCallback(it, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (ac.isShowGuide) {
                            ac.cancelGuideLottie()
                        } else if (ac.mBinding.drawer.isOpen) {
                            ac.mBinding.drawer.close()
                        } else if (openServerState.value == OpenServiceState.CONNECTING) {
                        } else if (openServerState.value == OpenServiceState.DISCONNECTING) {
                            stopToConnectOrDisConnect()
                        } else {
                            ac.finish()
                            ac.moveTaskToBack(true)
                            isUserMainBack = true
                        }
                    }
                })
            }
        }
    }

    private var lastExecutionTime = 0L

    private fun connectPut(activity: HomeActivity) {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastExecutionTime < 1000) {
            return
        }
        lastExecutionTime = currentTime
        if (DataHelp.isConnectFun()) {
            BaseAd.getBackInstance().advertisementLoadingFlash(activity)
            "f28".putPointYep(activity)
        } else {
            "f27".putPointYep(activity)
        }
    }

    private fun setChromometer() {
        if (userInterrupt) {
            userInterrupt = false
            if (VPNDataHelper.cachePosition != -1) {
                VPNDataHelper.nodeIndex = VPNDataHelper.cachePosition
                VPNDataHelper.cachePosition = -1
            }
            return
        }
        when (openServerState.value) {
            OpenServiceState.CONNECTED -> {
                chronometer.base = System.currentTimeMillis()
                chronometer.start()
            }

            OpenServiceState.DISCONNECTED -> {
                chronometer.stop()
                chronometer.base = SystemClock.elapsedRealtime()
            }

            else -> {}
        }
    }


    private fun toEndAc() {
        activity.get()?.let {
            if (BaseAppFlash.isHotStart) {
                BaseAppFlash.isHotStart = false
                return
            }
            val intent = Intent(it, EndActivity::class.java)
            intent.putExtra(
                "IS_CONNECT", curServerState == "CONNECTED"
            )
            it.startActivity(intent)
            BaseAd.getEndInstance().whetherToShowFlash = false
        }
    }

    fun activityResume() {
        if (VPNDataHelper.cachePosition != -1) {
            val node: LocaleProfile = VPNDataHelper.getAllVpnListData()[VPNDataHelper.cachePosition]
            flashListIcon.setImageResource(VPNDataHelper.getImage(node.name))
            flashListName.text =
                if (node.city.isNotBlank()) node.name + "-" + node.city else node.name
        } else {
            val node: LocaleProfile = VPNDataHelper.getAllVpnListData()[VPNDataHelper.nodeIndex]
            flashListIcon.setImageResource(VPNDataHelper.getImage(node.name))
            flashListName.text =
                if (node.city.isNotBlank()) node.name + "-" + node.city else node.name
        }
    }

    fun stopToConnectOrDisConnect() {
        if (shadowsocksJob?.isActive == true) {
            shadowsocksJob?.cancel()

            when (openServerState.value) {
                OpenServiceState.CONNECTING -> {
                    openServerState.postValue(OpenServiceState.DISCONNECTED)
                    mService?.disconnect()
                    cancelConnect = true

                }

                OpenServiceState.DISCONNECTING -> {
                    userInterrupt = true
                    openServerState.postValue(OpenServiceState.CONNECTED)
                }

                else -> {}
            }
        }
    }

    fun stopToConnectOrDisConnect2() {
        if (shadowsocksJob?.isActive == true) {
            shadowsocksJob?.cancel()
            when (openServerState.value) {
                OpenServiceState.CONNECTING -> {
                    openServerState.postValue(OpenServiceState.DISCONNECTED)
                    mService?.disconnect()
                    cancelConnect = true
                    "f19".putPointYep(activity.get()!!)
                }

                OpenServiceState.DISCONNECTING -> {
                    userInterrupt = true
                    openServerState.postValue(OpenServiceState.CONNECTED)
                    "f20".putPointYep(activity.get()!!)

                }

                OpenServiceState.CONNECTED -> {
                    openServerState.postValue(OpenServiceState.CONNECTED)
                }

                else -> {}
            }
        } else {
            if (openServerState.value == OpenServiceState.DISCONNECTED) {
                openServerState.postValue(OpenServiceState.DISCONNECTED)
            }
        }

    }

    fun toConnectVerifyNet() {
        if (isAppOnline(activity.get())) {
            isNextConnect(activity.get()!!) {
                toConnectOrDisConnect()
            }
        } else {
            activity.get()?.let {
                val customDialog = Dialog(it, R.style.AppDialogStyle)
                val localLayoutParams = customDialog.window?.attributes
                localLayoutParams?.gravity = Gravity.CENTER
                customDialog.window?.attributes = localLayoutParams
                customDialog.setContentView(R.layout.nettttttttttttttttt)
                val confirmButton = customDialog.findViewById<AppCompatTextView>(R.id.confirmButton)
                confirmButton.setOnClickListener {
                    customDialog.dismiss()
                }
                customDialog.show()
            }
        }
    }

    private var toAction = false

    private fun isNextConnect(activity: HomeActivity, nextFun: (haveData: Boolean) -> Unit) {
        activity.lifecycleScope.launch {
            activity.mBinding.inLoad.tvLoading.text = "The server is loading"
            val data = OnlineVpnHelp.checkServerData(activity)
            activity.mBinding.showLoad = true
            if (data) {
                nextFun(true)
                activity.mBinding.showLoad = false
            } else {
                delay(2000)
                activity.mBinding.showLoad = false
                nextFun(false)
            }
        }
    }

    private fun toConnectOrDisConnect() {

        activity.get()?.let {
            toAction = true
            BaseAppFlash.isHotStart = false
            cancelConnect = false
            BaseAd.getEndInstance().advertisementLoadingFlash(it)
            when (openServerState.value) {
                OpenServiceState.CONNECTED -> {
                    BaseAppFlash.vpnClickState = 1
                    playConnectAnimation()
                    shadowsocksJob = disconnectShadowsocks()
                }

                OpenServiceState.DISCONNECTED -> {
                    BaseAppFlash.vpnClickState = 0
                    playConnectAnimation()
                    shadowsocksJob =
                        activity.get()?.let { mService?.let { it1 -> openVTool(it, it1) } }
                }

                else -> {}
            }
        }
    }

    private fun playConnectAnimation() {
        connectAnimate.setImageResource(R.drawable.c_loading)
        connectAnimate.startAnimation(rotateAnimation)
    }

    private fun disconnectShadowsocks(): Job {
        val job = MainScope().launch(Dispatchers.IO) {
            activity.get().let { ac ->
                if (ac != null) {
                    openServerState.postValue(OpenServiceState.DISCONNECTING)
                    delay(2000)
                }
                if (ac?.mBinding?.drawer?.isOpen == true) {
                    userInterrupt = true
                    stopToConnectOrDisConnect()
                    return@launch
                } else if (ac?.canJump == false) {
                    return@launch
                } else if (isActive) {
                    mService?.disconnect()
                    "f10".putPointYep(activity.get()!!)
                }
            }
        }
        return job
    }


    private fun isAppOnline(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (networkCapabilities != null) {
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        return false
    }


    private fun checkVPNPermission(ac: Activity): Boolean {
        VpnService.prepare(ac).let {
            return it == null
        }
    }


    var mService: IOpenVPNAPIService? = null

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName?,
            service: IBinder?,
        ) {
            mService = IOpenVPNAPIService.Stub.asInterface(service)
            try {
                mService?.registerStatusCallback(mCallback)
            } catch (e: Exception) {
            }
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            mService = null
        }
    }

    fun showConnecetNextFun(activity: HomeActivity, isConnect: Boolean) {
        showConnectAd(activity) {
            activity.let { it1 ->
                setViewEnabled(true)
                stopConnectAnimation()
                setChromometer()
                if (isConnect) {
                    toEndAc()
                } else {
                    if (!cancelConnect) toEndAc()
                    else cancelConnect = false
                }
            }
        }
    }

    private val mCallback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, state: String?, message: String?, level: String?) {
            curServerState = state
            BaseAppFlash.vpnState = state ?: ""
            when (state) {
                "CONNECTED" -> {
                    if (toAction) {
                        toAction = false
                        userInterrupt = false
                        isClickConnect = true
                        isFailConnect = false
                        openServerState.postValue(OpenServiceState.CONNECTED)
                    }
                }

                "RECONNECTING" -> {
                    Toast.makeText(activity.get(), "Reconnecting", Toast.LENGTH_LONG).show()

                }

                "NOPROCESS" -> {
                    if (toAction) {
                        toAction = false
                        userInterrupt = false
                        isClickConnect = true
                        openServerState.postValue(OpenServiceState.DISCONNECTED)
                    }
                }


                else -> {}
            }

        }
    }
    var cancelConnect = false
    var isFailConnect = false
    var connectTime: Long = 0

    fun openVTool(context: Context, server: IOpenVPNAPIService): Job? {
        activity.get()?.let { ac ->
            if (checkVPNPermission(ac)) {
                val job = MainScope().launch(Dispatchers.IO) {
                    openServerState.postValue(OpenServiceState.CONNECTING)
                    if (isAppOnline(ac)) {
                        connectTime = System.currentTimeMillis()
                        "f9".putPointYep(context)
                        val data = VPNDataHelper.getAllLocaleProfile()[VPNDataHelper.nodeIndex]
                        runCatching {
                            BaseAppUtils.setLoadData(BaseAppUtils.vpn_ip, data.onLm_host)
                            BaseAppUtils.setLoadData(BaseAppUtils.vpn_city, data.city)
                            val conf = context.assets.open("fast_ippooltest.ovpn")
                            val br = BufferedReader(InputStreamReader(conf))
                            val config = StringBuilder()
                            var line: String?
                            while (true) {
                                line = br.readLine()
                                if (line == null) break
                                if (line.contains("remote 195", true)) {
                                    line = "remote ${data.onLm_host} ${data.onLo_Port}"
                                } else if (line.contains("wrongpassword", true)) {
                                    line = data.onLu_password
                                } else if (line.contains("cipher AES-256-GCM", true)) {
                                    line = "cipher ${data.onLi}"
                                }
                                config.append(line).append("\n")
                            }
                            br.close()
                            conf.close()
                            server.startVPN(config.toString())
                            delay(12000)
                            if ((!DataHelp.isConnectFun()) && BaseAppFlash.vpnClickState == 0) {
                                isFailConnect = true
                                cancelConnect = true
                                DataHelp.putPointTimeYep(
                                    "f12",
                                    "Connect Failed!",
                                    "re",
                                    activity.get()!!
                                )
                                stopToConnectOrDisConnect()
                                Looper.prepare()
                                Toast.makeText(activity.get(), "Connect Failed!", Toast.LENGTH_LONG)
                                    .show()
                                Looper.loop()
                                cancel()
                            }
                        }.onFailure {
                        }
                    } else {
                        stopToConnectOrDisConnect()
                    }

                }
                return job

            } else {
                VpnService.prepare(ac).let {
                    requestPermissionForResultVPN.launch(it)
                }
            }
        }
        return null
    }


    fun showBannerAd(activity: HomeActivity) {
        activity.lifecycleScope.launch {
            delay(200)
            if (activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            val state = FlashLoadBannerAd.getAdISLoadSuccess()
            if (!state) {
                BaseAd.getBannerInstance().advertisementLoadingFlash(activity)
            }
            while (isActive) {
                if (state) {
                    FlashLoadBannerAd.showBannerAdFlash(activity)
                    cancel()
                    break
                }
                delay(500)
            }
        }
    }

    fun showConnectAd(activity: HomeActivity, nextFun: () -> Unit) {
        showConnectJob = activity.lifecycleScope.launch() {
            val adConnectData = BaseAd.getConnectInstance().appAdDataFlash
            if (adConnectData == null) {
                BaseAd.getConnectInstance().advertisementLoadingFlash(activity)
            }
            try {
                withTimeout(8000) {
                    while (isActive) {
                        when (FlashLoadConnectAd.displayConnectAdvertisementFlash(
                            activity,
                            closeWindowFun = {
                                BaseAd.getConnectInstance().advertisementLoadingFlash(activity)
                                nextFun()
                            })) {
                            2 -> {
                                cancel()
                                showConnectJob = null
                            }

                            0 -> {
                                cancel()
                                nextFun()
                                showConnectJob = null
                            }
                        }
                        delay(500)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                showConnectJob?.cancel()
                nextFun()
                showConnectJob = null
            }
        }
    }
}