package com.alexdib.net

import android.util.Log
import org.json.JSONObject
import java.io.File
import java.util.*

object RequestHelper {

    private val networkInterface: NetworkInterface by lazy { OkHttpClientNetwork() }

    operator fun get(link: String, response: NetworkInterface.Response) {
        get(link, null, response)
    }

    operator fun get(link: String, headers: Map<String, String>?, response: NetworkInterface.Response) {
        Log.d("RequestHelper", "get request -> Headers: $headers Link: $link")
        networkInterface.get(link, headers, object : NetworkInterface.Response {
            override fun onSuccess(s: String) {
                Log.d("RequestHelper", "get response -> link: $link response: $s")
                response.onSuccess(s)
            }

            override fun onError(e: Exception) {
                Log.d("RequestHelper", "get error -> Exception: " + e.javaClass + " " + e.message)
                response.onError(e)
            }
        })
    }

    fun getHeaders(link: String, headerKey: String, response: NetworkInterface.Response) {
        Log.d("RequestHelper", "getHeaders request -> headerKey: $headerKey Link: $link")
        networkInterface.getHeader(link, headerKey, object : NetworkInterface.Response {
            override fun onSuccess(s: String) {
                Log.d("RequestHelper", "getHeaders response -> link: $link response: $s")
                response.onSuccess(s)
            }

            override fun onError(e: Exception) {
                Log.e("RequestHelper", "getHeaders error -> Exception: " + e.javaClass + " " + e.message)
                response.onError(e)
            }
        })
    }

    fun delete(link: String, token: String, response: NetworkInterface.Response) {
        Log.d("RequestHelper", "delete request -> Link: $link")
        networkInterface.delete(link, token, object : NetworkInterface.Response {
            override fun onSuccess(s: String) {
                Log.d("RequestHelper", "delete response -> link: $link response: $s")
                response.onSuccess(s)
            }

            override fun onError(e: Exception) {
                Log.d("RequestHelper", "delete error -> Exception: " + e.javaClass + " " + e.message)
                response.onError(e)
            }
        })
    }

    fun delete(link: String, token: String, jsonObject: JSONObject, response: NetworkInterface.Response) {
        Log.d("RequestHelper", "delete request -> Link: $link")
        networkInterface.delete(link, token, jsonObject, object : NetworkInterface.Response {
            override fun onSuccess(s: String) {
                Log.d("RequestHelper", "delete response -> link: $link response: $s")
                response.onSuccess(s)
            }

            override fun onError(e: Exception) {
                Log.d("RequestHelper", "delete error -> Exception: " + e.javaClass + " " + e.message)
                response.onError(e)
            }
        })
    }

    fun post(link: String, jsonObject: JSONObject, headers: Map<String, String>, response: NetworkInterface.Response) {
        val params = "json: $jsonObject"
        Log.d("RequestHelper", "post request -> Link: $link params: $params")
        networkInterface.post(link, jsonObject, headers, object : NetworkInterface.Response {
            override fun onSuccess(s: String) {
                Log.d("RequestHelper", "post response -> link: $link response: $s")
                response.onSuccess(s)
            }

            override fun onError(e: Exception) {
                Log.d("RequestHelper", "post error -> Exception: " + e.javaClass + " " + e.message)
                response.onError(e)
            }
        })
    }

    fun post(link: String, headers: Map<String, String>, map: Map<String, String>, response: NetworkInterface.Response) {
        Log.d("RequestHelper", "post request -> Link: " + link + " params: " + JSONObject(map).toString())

        networkInterface.post(link, headers, map, object : NetworkInterface.Response {
            override fun onSuccess(s: String) {
                Log.d("RequestHelper", "post response -> link: $link response: $s")
                response.onSuccess(s)
            }

            override fun onError(e: Exception) {
                Log.d("RequestHelper", "post error -> Exception: " + e.javaClass + " " + e.message)
                response.onError(e)
            }
        })
    }

    fun put(link: String, token: String, jsonObject: JSONObject, response: NetworkInterface.Response) {
        val params = "json: $jsonObject"
        Log.d("RequestHelper", "put request -> Token: $token Link: $link params: $params")
        networkInterface.put(link, token, jsonObject, object : NetworkInterface.Response {
            override fun onSuccess(s: String) {
                Log.d("RequestHelper", "put response -> link: $link response: $s")
                response.onSuccess(s)
            }

            override fun onError(e: Exception) {
                Log.d("RequestHelper", "put error -> Exception: " + e.javaClass + " " + e.message)
                response.onError(e)
            }
        })
    }

    fun upload(link: String, token: String, map: HashMap<String, String>, fileKey: String, file: File, response: NetworkInterface.Response) {
        val params = StringBuilder()
        try {
            for (keys in map.keys) {
                params.append("key: ").append(keys).append(" -> value: ").append(map[keys])
                        .append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("RequestHelper", "upload request -> " + "Link: " + link + "\ntoken: " + token + "\nparams: " + params + "file_key: " + fileKey + "\nfile: " + file.absolutePath)
        networkInterface.upload(link, token, map, fileKey, file, object : NetworkInterface.Response {
            override fun onSuccess(s: String) {
                Log.d("RequestHelper", "upload response -> link: $link response: $s")
                response.onSuccess(s)
            }

            override fun onError(e: Exception) {
                Log.d("RequestHelper", "upload error -> Exception: " + e.javaClass + " " + e.message)
                response.onError(e)
            }
        })

    }

    fun cancel(tag: String) {
        Log.d("RequestHelper", "cancel request -> tag: $tag")
        networkInterface.cancel(tag)
    }
}
