package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.widget.Chronometer
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.EndViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.data.MainViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ConnectedLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EndActivity : BaseActivityFlash<ConnectedLayoutBinding>() {


    override var conetcntLayoutId: Int
        get() = R.layout.connected_layout
        set(value) {}
    private var isConnected = false

    private val endViewModel: EndViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isConnected = intent.getBooleanExtra("IS_CONNECT", false)
        mBinding.back.setOnClickListener { endViewModel.showEndScAd(this) }
        val data: LocaleProfile
        if (VPNDataHelper.cachePosition != -1) {
            data = VPNDataHelper.getAllVpnListData()[VPNDataHelper.cachePosition]
            if (data.city.isNotBlank()) {
                mBinding.connectCountry.text = data.name + " " + data.city
            } else mBinding.connectCountry.text = data.name
        } else {
            data = VPNDataHelper.getAllVpnListData()[VPNDataHelper.nodeIndex]
            if (data.city.isNotBlank()) {
                mBinding.connectCountry.text = data.name + " " + data.city
            } else mBinding.connectCountry.text = data.name
        }
        mBinding.connectedLocationImage.setImageResource(VPNDataHelper.getImage(data.name))
        when (isConnected) {
            true -> {
                mBinding.chronometer.base = System.currentTimeMillis()
                mBinding.chronometer.start()
                mBinding.resultTitle.text = "Connection Succeed"
                mBinding.connectedImage.setImageResource(R.drawable.ic_connect)
            }
            else -> {
                mBinding.chronometer.stop()
                mBinding.chronometer.base = SystemClock.elapsedRealtime()
                mBinding.resultTitle.text = "Disconnection Succeed"
                mBinding.connectedImage.setImageResource(R.drawable.ic_disconnect)
            }
        }
        VPNDataHelper.cachePosition = -1
        timeFun()
    }
    private fun timeFun(){
        mBinding.chronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener { cArg ->
            val time = System.currentTimeMillis() - cArg.base
            val d = Date(time)
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            mBinding.chronometer.text = sdf.format(d)
        }
    }
    override fun onResume() {
        super.onResume()
        endViewModel.showEndAd(this)
        "f21".putPointYep(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            endViewModel.showEndScAd(this)
        }
        return true
    }
}