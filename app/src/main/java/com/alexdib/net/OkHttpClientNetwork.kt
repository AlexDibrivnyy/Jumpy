package com.alexdib.net

import com.alexdib.jumpy.AsyncJob
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class OkHttpClientNetwork : NetworkInterface {

    private var okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
        .readTimeout(90, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool())
        .retryOnConnectionFailure(false)
        .build()

    override fun get(
        link: String,
        headers: MutableMap<String, String>?,
        responseInterface: NetworkInterface.Response
    ) {
        try {
            val builder = Request.Builder().url(link)
            if (headers != null) {
                builder.headers(Headers.of(headers))
            }
            builder.addHeader("Connection", "close")
            val request = builder.build()
            okHttpClient.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    AsyncJob.doOnMainThread { responseInterface.onError(e) }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    handleResponse(response, responseInterface)
                }
            })
        } catch (e: Exception) {
            responseInterface.onError(e)
        }
    }


    override fun delete(
        link: String,
        token: String?,
        responseInterface: NetworkInterface.Response
    ) {
        val builder = Request.Builder().url(link)
        if (token != null) {
            builder.addHeader("Authorization", "Token $token")
        }

        val request = builder.delete().build()
        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                AsyncJob.doOnMainThread { responseInterface.onError(e) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, responseInterface)
            }
        })
    }

    override fun delete(
        link: String,
        token: String?,
        jsonObject: JSONObject,
        responseInterface: NetworkInterface.Response
    ) {
        val builder = Request.Builder().url(link)
        if (token != null) {
            builder.addHeader("Authorization", "Token $token")
        }
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = builder.delete(body).build()
        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                AsyncJob.doOnMainThread { responseInterface.onError(e) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, responseInterface)
            }
        })
    }

    override fun getHeader(
        link: String,
        headerKey: String,
        responseInterface: NetworkInterface.Response
    ) {
        val request = Request.Builder().url(link).build()

        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                AsyncJob.doOnMainThread { responseInterface.onError(e) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.header(headerKey, "")
                    AsyncJob.doOnMainThread { responseInterface.onSuccess(responseBody) }
                } catch (e: Exception) {
                    AsyncJob.doOnMainThread { responseInterface.onError(e) }
                }

            }
        })
    }

    override fun post(
        link: String, jsonObject: JSONObject, headers: Map<String, String>,
        responseInterface: NetworkInterface.Response
    ) {
        val body = RequestBody.create(JSON, jsonObject.toString())
        val builder = Request.Builder()
            .url(link)

        headers.forEach {
            builder.addHeader(it.key, it.value)
        }

        val request = builder.post(body).build()

        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                AsyncJob.doOnMainThread { responseInterface.onError(e) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, responseInterface)
            }
        })
    }

    override fun post(
        link: String,
        headers: Map<String, String>,
        map: Map<String, String>,
        responseInterface: NetworkInterface.Response
    ) {

        val formBuilder = FormBody.Builder()
        for ((key, value) in map) {
            formBuilder.add(key, value)
        }

        val body = formBuilder.build()

        val builder = Request.Builder()
        builder.url(link).post(body)

        for (header in headers) {
            builder.addHeader(header.key, header.value)
        }

        val request = builder.build()

        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                AsyncJob.doOnMainThread { responseInterface.onError(e) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, responseInterface)
            }
        })
    }

    override fun put(
        link: String,
        token: String?,
        jsonObject: JSONObject,
        responseInterface: NetworkInterface.Response
    ) {

        val body = RequestBody.create(JSON, jsonObject.toString())
        val builder = Request.Builder()
            .url(link)
            .header("Content-Type", "application/json")
        if (token != null) {
            builder.addHeader("Authorization", "Token $token")
        }

        val request = builder.put(body).build()

        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                AsyncJob.doOnMainThread { responseInterface.onError(e) }
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, responseInterface)
            }
        })
    }


    override fun upload(
        link: String, token: String?, map: HashMap<String, String>, fileKey: String,
        file: File, responseInterface: NetworkInterface.Response
    ) {

        val bodyBuilder = MultipartBody.Builder()
        bodyBuilder.setType(MultipartBody.FORM)
            .addFormDataPart(fileKey, file.name, RequestBody.create(MediaType.parse("*/*"), file))

        for ((key, value) in map) {
            bodyBuilder.addFormDataPart(key, value)
        }

        val requestBody = bodyBuilder.build()
        val builder = Request.Builder()
            .url(link)
            .tag(file.absolutePath)
            .post(requestBody)
        if (token != null) {
            builder.addHeader("Authorization", "Token $token")
        }
        val request = builder.build()
        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                AsyncJob.doOnMainThread { responseInterface.onError(e) }
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, responseInterface)
            }
        })
    }

    fun handleResponse(response: Response, responseInterface: NetworkInterface.Response) {
        if (response.isSuccessful) {
            try {
                val responseBody = response.body()!!.string()
                AsyncJob.doOnMainThread { responseInterface.onSuccess(responseBody) }
            } catch (e: Exception) {
                AsyncJob.doOnMainThread {
                    responseInterface.onError(
                        ServerError(
                            response.code(),
                            "UNKNOWN"
                        )
                    )
                }
            }

        } else {
            var string = ""
            try {
                string = response.body()!!.string()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val finalString = string
            AsyncJob.doOnMainThread {
                responseInterface.onError(
                    ServerError(
                        response.code(),
                        finalString
                    )
                )
            }
        }
    }


    override fun cancel(tag: String) {
        for (call in okHttpClient.dispatcher().queuedCalls()) {
            if (call.request().tag() == tag)
                call.cancel()
        }
        for (call in okHttpClient.dispatcher().runningCalls()) {
            if (call.request().tag() == tag)
                call.cancel()
        }
    }

    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")
    }

}
