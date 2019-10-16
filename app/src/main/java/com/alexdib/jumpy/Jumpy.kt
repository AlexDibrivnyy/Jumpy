package com.alexdib.jumpy

import android.util.Log
import com.alexdib.net.NetworkInterface
import com.alexdib.net.RequestHelper
import com.alexdib.net.ServerError
import com.google.gson.Gson
import com.loader.core.QueueLoaderBuilder
import com.loader.interfaces.ICameraLoaderListener
import com.loader.interfaces.IRequestLoadedListener
import com.loader.request.CustomRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume

class Jumpy {
    interface JumpyListener {
        fun exception(exception: Exception)

        fun loaded(resultObjects: MutableMap<String, Any?>)
    }

    enum class RestType { GET, POST, POST_JSON }

    open class ObjectRequest<T : Any>(
        val url: String, val type: RestType = RestType.GET,
        val params: Map<String, String> = emptyMap(), val headers: Map<String, String> = emptyMap()
    ) {

        open fun parse(json: String): T? {
            val clazz = classType as Class<T>
            Log.d("Jumpy", "parse $clazz")
            return Gson().fromJson(json, clazz)
        }

        lateinit var classType: Class<*>

        fun <T> setParcelableObject(java: Class<T>) {
            classType = java
        }

        fun getTag(): String {
            return if (params.isNotEmpty()) {
                url + params.toString()
            } else {
                url
            }
        }
    }

    val requests = mutableListOf<ObjectRequest<*>>()

    inline fun <reified T : Any> add(obj: ObjectRequest<T>): String {
        Log.d("Jumpy", "add object " + T::class.java)
        obj.setParcelableObject(T::class.java)
        requests.add(obj)
        return obj.getTag()
    }

    suspend fun load(): Map<String, Any?>? {
        return suspendCancellableCoroutine {
            load(object : JumpyListener {
                override fun exception(exception: Exception) {
                    if (it.isActive) {
                        it.resume(null)
                    }
                }

                override fun loaded(resultObjects: MutableMap<String, Any?>) {
                    if (it.isActive) {
                        it.resume(resultObjects.toMap())
                    }
                }
            })
        }
    }


    fun load(doneListener: JumpyListener) {
        if (requests.isEmpty()) {
            doneListener.loaded(emptyMap<String, Any>().toMutableMap())
            return
        }

        val loader = QueueLoaderBuilder().build()

        val resultObjects = mutableMapOf<String, Any?>()
        val statsRetryCounter = AtomicInteger(0)

        requests.forEach { request ->

            loader.add(object : CustomRequest() {
                override fun load(listener: IRequestLoadedListener<Boolean>) {
                    try {
                        val reqestListener = object : NetworkInterface.Response {
                            override fun onSuccess(s: String) {

                                AsyncJob.doInBackground {
                                    val key = request.getTag()

                                    var errorMessage = ""
                                    try {
                                        resultObjects[key] = request.parse(s)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        resultObjects[key] = null
                                        errorMessage = e.message ?: "Unknown parsing error"
                                    }

                                    statsRetryCounter.set(0)
                                    listener.onFinish()
                                }
                            }

                            override fun onError(e: java.lang.Exception) {
                                e.printStackTrace()
                                if (e is ServerError) {
                                    statsRetryCounter.set(0)
                                    listener.onFinish()
                                } else {
                                    if (statsRetryCounter.incrementAndGet() >= 2) {
                                        statsRetryCounter.set(0)
                                        listener.onFinish()
                                    } else {
                                        listener.onError(e)
                                    }
                                }
                            }
                        }

                        when (request.type) {
                            RestType.GET -> if (request.headers.isEmpty()) {
                                RequestHelper.get(request.url, reqestListener)
                            } else {
                                RequestHelper.get(request.url, request.headers, reqestListener)
                            }
                            RestType.POST -> RequestHelper.post(
                                request.url,
                                request.headers,
                                request.params,
                                reqestListener
                            )
                            RestType.POST_JSON -> RequestHelper.post(
                                request.url,
                                JSONObject(request.params),
                                request.headers,
                                reqestListener
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        listener.onFinish()
                    }
                }
            })

            loader.setQueueLoaderListener(object : ICameraLoaderListener {
                override fun allRequestLoaded() {
                    AsyncJob.doInBackground {
                        try {
                            doneListener.loaded(resultObjects)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            doneListener.exception(e)
                        }
                    }
                }

                override fun errorHappened(e: java.lang.Exception?) {
                }
            })
        }
        loader.load()
    }
}