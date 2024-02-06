package skt.vs.wbg.who.`is`.champion.flashvpn.tab

import androidx.annotation.Keep

data class OnlineVpnBean(
    val code: Int,
    val `data`: Data,
    val msg: String
)

data class Data(
    val BDQkmnzDYM: List<Server>,
    val FBoulP: List<Smart>
)

data class Server(
    val goyZFW: String,
    val DWmA: String,
    val ytJ: String,
    val VUjGPZr: String,
    val lWRklgRMo: String,
    val iGsETAErS: Int,
    val PDiBTREsni: String,
    val HTnEGo: String
)

data class Smart(
    val goyZFW: String,
    val DWmA: String,
    val ytJ: String,
    val VUjGPZr: String,
    val lWRklgRMo: String,
    val iGsETAErS: Int,
    val PDiBTREsni: String,
    val HTnEGo: String
)
