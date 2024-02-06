package skt.vs.wbg.who.`is`.champion.flashvpn.data

import androidx.annotation.Keep
@Keep
data class FlashAdBean(
    val faSdif: String,
    val faSlity: String,
    val faSpla: String,
    val faSity: String,
    val faSmay: String,
    var loadCity: String,
    var showTheCity: String,

    var loadIp: String,
    var showIp: String,
)
@Keep
data class AdType(
    val id: String,
    val where: String,
    val name:String,
    val type: String,
)

