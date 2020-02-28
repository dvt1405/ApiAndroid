package tun.kt.apilib.apihistory

import android.os.Build
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

class ApiDetails(
    num: Int,
    request: Request,
    response: Response?,
    date: Date,
    tookTimes: Long
) {
    var num: Int
    var request: Request
    var response: Response?
    var date: Date
    var tookTimes: Long
    var responseSize: Long? = null
    var requestSize: Long? = null
    val headerRequest: Map<String, String>
    val headerResponse: Map<String, String>
    val bodyRequest: String?
    val bodyResponse: String?

    init {
        this.num = num
        this.request = request
        this.response = response
        this.date = date
        this.tookTimes = tookTimes
        headerRequest = getHeadersRequest()
        headerResponse = getHeadersResponse()
        bodyRequest = getRequestBody()
        bodyResponse = getResponseBody()
    }

    private fun getHeadersRequest(): Map<String, String> {
        val mapHeaders = mutableMapOf<String, String>()
        val requestBody = request.body
        val headers = request.headers
        if (headers.size > 0) {
            headers.forEach {
                mapHeaders[it.first] = it.second
            }
        }
        requestBody?.contentType()?.let {
            if (headers[CONTENT_TYPE] == null) {
                mapHeaders[CONTENT_TYPE] = "$it"
            }
        }
        requestBody?.contentLength()?.let {
            if (headers[CONTENT_LENGTH] == null) {
                mapHeaders[CONTENT_LENGTH] = "${requestBody.contentLength()} bytes"
            } else {
                mapHeaders[CONTENT_LENGTH] = "${mapHeaders[CONTENT_LENGTH]} bytes"
            }
        }
        return mapHeaders
    }

    fun getRequestBody(): String? {
        val requestBody = request.body ?: return null
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        val contentType = requestBody.contentType()
        val charset: Charset =
            contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
        if (buffer.isProbablyUtf8()) {
            return buffer.readString(charset).replace("\\n","")
        }
        return "${request.method} (binary ${requestBody.contentLength()}-byte body omitted)"
    }

    fun getHeadersResponse(): Map<String, String> {
        val mapHeaders = mutableMapOf<String, String>()
        val headers = response?.headers ?: return mapHeaders
        headers.forEach {
            mapHeaders[it.first] = it.second
        }
        if (mapHeaders[CONTENT_LENGTH] != null) {
            mapHeaders[CONTENT_LENGTH] = "${mapHeaders[CONTENT_LENGTH]} bytes"
        }
        return mapHeaders
    }

    private fun getResponseBody(): String? {
        val responseBody = response!!.body ?: return null
        val contentLength = responseBody.contentLength()
        if (response!!.promisesBody()) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer
            var gzipLength: Long? = null
            if ("gzip".equals(response!!.headers["Content-Encoding"], ignoreCase = true)) {
                gzipLength = buffer.size
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }
            if (gzipLength != null) {
                responseSize = gzipLength
            } else {
                responseSize = buffer.size
            }
            val contentType = responseBody.contentType()
            val charset: Charset =
                contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
            if (!buffer.isProbablyUtf8()) {
                return null
            }
            if (contentLength != 0L) {
                return buffer.clone().readString(charset)
            }

        }
        return null
    }

    companion object {
        const val CONTENT_TYPE = "Content-Type"
        const val CONTENT_LENGTH = "Content-Length"
    }

}

internal fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}