package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.WebbbbbbbbBinding

class WebFlashActivity : BaseActivityFlash<WebbbbbbbbBinding>() {


    override var conetcntLayoutId: Int
        get() = R.layout.webbbbbbbb
        set(value) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "Loading", Toast.LENGTH_LONG).show()
        mBinding.webViewBack.setOnClickListener { finish() }
        mBinding.webView.webViewClient = object : WebViewClient() {}
        mBinding.webView.canGoForward()
        mBinding.webView.loadUrl("https://blog.csdn.net/LoveFHM?type=blog")
    }
}