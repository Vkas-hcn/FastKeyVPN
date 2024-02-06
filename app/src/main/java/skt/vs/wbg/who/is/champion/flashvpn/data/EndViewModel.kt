package skt.vs.wbg.who.`is`.champion.flashvpn.data

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBackAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadEndAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash

class EndViewModel : ViewModel() {
    fun showEndAd(activity: EndActivity) {
        activity.lifecycleScope.launch {
            delay(200)
            val adEndData = BaseAd.getEndInstance().appAdDataFlash
            if (adEndData == null) {
                BaseAd.getEndInstance().advertisementLoadingFlash(activity)
            }
            while (isActive) {
                if (BaseAd.getEndInstance().appAdDataFlash != null) {
                    FlashLoadEndAd.setDisplayEndNativeAdFlash(activity)
                    cancel()
                    break
                }
                delay(500)
            }
        }
    }

    fun showEndScAd(activity: EndActivity) {
        "f22".putPointYep(activity)
        if (FlashLoadBackAd.displayBackAdvertisementFlash(1,activity, closeWindowFun = {
                activity.finish()
            }) != 2) {
            activity.finish()
        }
    }
}