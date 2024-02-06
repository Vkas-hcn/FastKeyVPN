package skt.vs.wbg.who.`is`.champion.flashvpn.ad

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ConfigActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.HomeActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadBackAd {
    private val adBase = BaseAd.getBackInstance()
    private lateinit var adBackData: FlashAdBean
    fun loadBackAdvertisementFlash(context: Context, adData: FlashAdBean) {
        val adRequest = AdRequest.Builder().build()
        adBackData = adBase.beforeLoadLink(adData)

        InterstitialAd.load(
            context,
            adData.faSmay,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = null
                    val error =
                        """
           domain: ${adError.domain}, code: ${adError.code}, message: ${adError.message}
          """"
                    DataHelp.putPointTimeYep(
                        "f31",
                        error,
                        "yn",
                        context
                    )
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    adBase.loadTimeFlash = Date().time
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = interstitialAd
                    interstitialAd.setOnPaidEventListener { adValue ->
                        FlashOkHttpUtils().getAdList(
                            context,
                            adValue,
                            interstitialAd.responseInfo,
                            "back",
                            adBackData
                        )
                    }
                    DataHelp.putPointTimeYep(
                        "f30",
                        "back+${adData.faSmay}",
                        "yn",
                        context
                    )
                }
            })
    }

    private fun backScreenAdCallback(closeWindowFun: () -> Unit) {
        (adBase.appAdDataFlash as? InterstitialAd)?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdClicked() {
                }

                override fun onAdDismissedFullScreenContent() {
                    closeWindowFun()
                    adBase.appAdDataFlash = null
                    adBase.whetherToShowFlash = false
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // Called when ad fails to show.
                    adBase.appAdDataFlash = null
                    adBase.whetherToShowFlash = false
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                }

                override fun onAdShowedFullScreenContent() {
                    adBase.appAdDataFlash = null
                    // Called when ad is shown.
                    adBase.whetherToShowFlash = true
                    adBackData = adBase.afterLoadLink(adBackData)
                }
            }
    }


    fun displayBackAdvertisementFlash(
        type: Int,
        activity: AppCompatActivity,
        closeWindowFun: () -> Unit
    ): Int {
        val userData = BaseAppUtils.blockAdUsers()
        val blacklistState = BaseAppUtils.blockAdBlacklist()
        if (!blacklistState) {
            return 0
        }
        if (!userData) {
            return 0
        }

        if (adBase.appAdDataFlash == null) {
            return 1
        }

        if (adBase.whetherToShowFlash || activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            return 1
        }
        backScreenAdCallback(closeWindowFun)
        activity.lifecycleScope.launch(Dispatchers.Main) {
            if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                if (type == 1) {
                    activity as EndActivity
                    activity.mBinding.showLoad = true
                    delay(1500)
                    activity.mBinding.showLoad = false
                }
                if (type == 2) {
                    activity as ConfigActivity
                    activity.mBinding.showLoad = true
                    delay(1500)
                    activity.mBinding.showLoad = false
                }
                (adBase.appAdDataFlash as InterstitialAd).show(activity)
            }
        }
        return 2
    }
}