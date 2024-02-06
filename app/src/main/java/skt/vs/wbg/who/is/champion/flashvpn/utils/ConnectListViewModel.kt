package skt.vs.wbg.who.`is`.champion.flashvpn.utils

import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBackAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ConfigActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.HomeActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import java.lang.ref.WeakReference

class ConnectListViewModel : ViewModel() {
    private lateinit var activity: WeakReference<ConfigActivity>
    var isConnected: Boolean = false
    private var clickPosition: Int = 0

    fun init(ac: ConfigActivity, isConnected: Boolean) {
        activity = WeakReference(ac)
        this.isConnected = isConnected
    }

    fun onItemClick(position: Int, isChoosePosition: Boolean = false) {
        clickPosition = position
        if (isChoosePosition && !isConnected) {
            switchVpnConfig()
        } else if (isConnected) {
            activity.get()?.let {
                isSwitchDialog()
            }
        } else {
            switchVpnConfig()
        }
    }

    private fun isSwitchDialog() {
        activity.get()?.let {
            val customDialog = Dialog(it, R.style.AppDialogStyle)
            val localLayoutParams = customDialog.window?.attributes
            localLayoutParams?.gravity = Gravity.CENTER
            customDialog.window?.attributes = localLayoutParams
            customDialog.setContentView(R.layout.dialog_check)
            val confirmButton = customDialog.findViewById<AppCompatTextView>(R.id.confirmmmmmmmmmm)
            val cancel = customDialog.findViewById<AppCompatTextView>(R.id.cancelllllllll)
            confirmButton.setOnClickListener {
                switchVpnConfig(disconnect = true)
                customDialog.dismiss()
            }
            cancel.setOnClickListener {
                customDialog.dismiss()
            }
            customDialog.show()
        }
    }

    private fun switchVpnConfig(position: Int = clickPosition, disconnect: Boolean = false) {
        activity.get().let {
            if (disconnect) VPNDataHelper.cachePosition = VPNDataHelper.nodeIndex
            VPNDataHelper.nodeIndex = position
            BaseAppFlash.xkamkaxmak.encode("icConnect", true)
            val intent = Intent(it, HomeActivity::class.java)
            (it as ConfigActivity).setResult(100, intent)
            it.finish()
            VPNDataHelper.addRecentlyList(VPNDataHelper.getAllVpnListData()[position])
        }

    }

    fun showEndScAd(activity: ConfigActivity) {
        "f24".putPointYep(activity)
        if (FlashLoadBackAd.displayBackAdvertisementFlash(2,activity, closeWindowFun = {
                activity.finish()
            }) != 2) {
            activity.finish()
        }
    }
}