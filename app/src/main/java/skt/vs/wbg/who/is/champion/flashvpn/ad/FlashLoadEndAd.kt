package skt.vs.wbg.who.`is`.champion.flashvpn.ad

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadEndAd {
    private val adBase = BaseAd.getEndInstance()
    private lateinit var adEndData: FlashAdBean

    fun loadEndAdvertisementFlash(context: Context, adData: FlashAdBean) {
        adEndData = adBase.beforeLoadLink(adData)

        val vpnNativeAds = AdLoader.Builder(
            context.applicationContext,
            adData.faSpla
        )
        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
            .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT)
            .build()

        vpnNativeAds.withNativeAdOptions(adOptions)
        vpnNativeAds.forNativeAd {
            adBase.appAdDataFlash = it
            it.setOnPaidEventListener { advalue ->

                it.responseInfo?.let { nav ->
                    try {
                        FlashOkHttpUtils().getAdList(context, advalue, nav, "end", adEndData)
                        BaseAd.getEndInstance().advertisementLoadingFlash(context)
                    }catch (e: Exception){
                    }
                }
            }
        }
        vpnNativeAds.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)

                adBase.isLoadingFlash = false
                adBase.appAdDataFlash = null
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                Log.d(TAG, "end广告加载失败$error ")
                DataHelp.putPointTimeYep(
                    "f31",
                    error,
                    "yn",
                    context
                )
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                adBase.loadTimeFlash = Date().time
                adBase.isLoadingFlash = false
                Log.d(TAG, "end广告加载成功")
                DataHelp.putPointTimeYep(
                    "f30",
                    "end+${adData.faSpla}",
                    "yn",
                    context
                )
            }

            override fun onAdOpened() {
                super.onAdOpened()
            }
        }).build().loadAd(AdRequest.Builder().build())
    }


    @SuppressLint("InflateParams")
    fun setDisplayEndNativeAdFlash(activity: EndActivity) {
        activity.runOnUiThread {
            val binding = activity.mBinding
            adBase.appAdDataFlash?.let { adData ->
                val state = activity.lifecycle.currentState == Lifecycle.State.RESUMED

                if (adData is NativeAd && !adBase.whetherToShowFlash && state) {
                    binding.showAd = 0
                    if (activity.isDestroyed || activity.isFinishing || activity.isChangingConfigurations) {
                        adData.destroy()
                        return@let
                    }
                    val adView = activity.layoutInflater.inflate(
                        R.layout.ad_end,
                        null
                    ) as NativeAdView
                    setCorrespondingNativeComponentFlash(adData, adView)
                    binding.adFrame.apply {
                        removeAllViews()
                        addView(adView)
                    }
                    binding.showAd = 1
                    adBase.whetherToShowFlash = true
                    adBase.appAdDataFlash = null
                    adEndData = adBase.afterLoadLink(adEndData)
                    Log.d(TAG, "end-广告-show: ")
                }
            }
        }
    }

    private fun setCorrespondingNativeComponentFlash(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)

        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        (adView.headlineView as TextView).text = nativeAd.headline
        nativeAd.mediaContent?.let {
            adView.mediaView?.apply { setImageScaleType(ImageView.ScaleType.CENTER_CROP) }?.mediaContent =
                it
        }
        adView.mediaView?.clipToOutline = true
        adView.mediaView?.outlineProvider = R8()
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
    }

}