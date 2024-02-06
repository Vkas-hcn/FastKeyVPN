package skt.vs.wbg.who.`is`.champion.flashvpn.tab
import android.content.Context
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
class NetClientHelp {
    private val client: OkHttpClient = OkHttpClient()

    interface Callback {
        fun onSuccess(response: String)
        fun onFailure(error: String)
    }

    fun get(context: Context, url: String, callback: Callback) {
        val request = Request.Builder()
            .addHeader("XRAY", context.packageName)
            .addHeader("JJI", "ZZ")
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    callback.onSuccess(responseBody)
                } else {
                    callback.onFailure(responseBody.toString())
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e.toString())
            }
        })
    }

    fun post(url: String, body: Any, callback: Callback) {
        val requestBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), body.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .tag(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    callback.onSuccess(responseBody)
                } else {
                    callback.onFailure(responseBody.toString())
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure("Network error")
            }
        })
    }

    fun getMap(url: String, map: Map<String, Any>, callback: Callback) {
        val urlBuilder = url.toHttpUrl().newBuilder()

        map.forEach { entry ->
            urlBuilder.addEncodedQueryParameter(
                entry.key,
                URLEncoder.encode(entry.value.toString(), StandardCharsets.UTF_8.toString())
            )
        }
        val request = Request.Builder()

            .get()
            .tag(map)
            .url(urlBuilder.build())
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    callback.onSuccess(responseBody)
                } else {
                    callback.onFailure(responseBody.toString())
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure("Network error")
            }
        })
    }
}