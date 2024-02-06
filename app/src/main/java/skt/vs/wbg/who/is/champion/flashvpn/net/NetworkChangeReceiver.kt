package skt.vs.wbg.who.`is`.champion.flashvpn.net

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkChangeReceiver(private val listener: NetworkChangeListener?) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connMgr: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        val isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected
        listener?.onNetworkChanged(isConnected)
    }
}

interface NetworkChangeListener {
    fun onNetworkChanged(isConnected: Boolean)
}