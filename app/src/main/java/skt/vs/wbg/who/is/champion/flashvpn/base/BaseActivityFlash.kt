package skt.vs.wbg.who.`is`.champion.flashvpn.base

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.net.IPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.net.NetworkChangeListener
import skt.vs.wbg.who.`is`.champion.flashvpn.net.NetworkChangeReceiver


abstract class BaseActivityFlash<M : ViewDataBinding> : AppCompatActivity(), NetworkChangeListener {

    var canJump = false
    lateinit var mBinding: M
    open var conetcntLayoutId = 0
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        ssss()
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, conetcntLayoutId)
        mBinding.lifecycleOwner = this
        setContentView(mBinding.root)
        networkChangeReceiver = NetworkChangeReceiver(this)
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
        registerReceiver(networkChangeReceiver, intentFilter)
        IPUtils.checkIp(this)
    }


    override fun onResume() {
        canJump = true
        super.onResume()
    }

    override fun onStop() {
        canJump = false
        super.onStop()
    }

    fun ssss() {
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        if (isConnected) IPUtils.checkIp(this)
    }

}