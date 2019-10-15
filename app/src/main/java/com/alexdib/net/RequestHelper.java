package com.alexdib.net;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RequestHelper {
    private static volatile NetworkInterface networkInterface;

    private RequestHelper() {
    }

    private static NetworkInterface getNetworkInterface() {
        if (networkInterface == null) {
            synchronized (RequestHelper.class) {
                if (networkInterface == null) {
//                    networkInterface = new AsyncHttpClientNetwork();
                    networkInterface = new OkHttpClientNetwork();
                    Log.d("RequestHelper", "create networkInterface: " + networkInterface.getClass());
                }
            }
        }

        Log.d("RequestHelper", "return networkInterface");
        return networkInterface;
    }

    public static void setNetworkInterface(NetworkInterface networkInterface) {
        RequestHelper.networkInterface = networkInterface;
    }

    public static void get(final String link, final NetworkInterface.Response response) {
        get(link, null, response);
    }

    public static void get(final String link, Map<String, String> headers, final NetworkInterface.Response response) {
        Log.d("RequestHelper", "get request -> Headers: " + headers + " Link: " + link);
        getNetworkInterface().get(link, headers, new NetworkInterface.Response() {
            @Override
            public void onSuccess(String s) {
                Log.d("RequestHelper", "get response -> link: " + link + " response: " + s);
                response.onSuccess(s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("RequestHelper", "get error -> Exception: " + e.getClass() + " " + e.getMessage());
                response.onError(e);
            }
        });
    }

    public static void getHeaders(final String link, final String headerKey, final NetworkInterface.Response response) {
        Log.d("RequestHelper", "getHeaders request -> headerKey: " + headerKey + " Link: " + link);
        getNetworkInterface().getHeader(link, headerKey, new NetworkInterface.Response() {
            @Override
            public void onSuccess(String s) {
                Log.d("RequestHelper", "getHeaders response -> link: " + link + " response: " + s);
                response.onSuccess(s);
            }

            @Override
            public void onError(Exception e) {
                Log.e("RequestHelper", "getHeaders error -> Exception: " + e.getClass() + " " + e.getMessage());
                response.onError(e);
            }
        });
    }

    public static void delete(final String link, final String token,
                              final NetworkInterface.Response response) {
        Log.d("RequestHelper", "delete request -> Link: " + link);
        getNetworkInterface().delete(link, token, new NetworkInterface.Response() {
            @Override
            public void onSuccess(String s) {
                Log.d("RequestHelper", "delete response -> link: "
                        + link + " response: " + s);
                response.onSuccess(s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("RequestHelper", "delete error -> Exception: " + e.getClass() + " " + e.getMessage());
                response.onError(e);
            }
        });
    }

    public static void delete(final String link, final String token, JSONObject jsonObject,
                              final NetworkInterface.Response response) {
        Log.d("RequestHelper", "delete request -> Link: " + link);
        getNetworkInterface().delete(link, token, jsonObject, new NetworkInterface.Response() {
            @Override
            public void onSuccess(String s) {
                Log.d("RequestHelper", "delete response -> link: "
                        + link + " response: " + s);
                response.onSuccess(s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("RequestHelper", "delete error -> Exception: " + e.getClass() + " " + e.getMessage());
                response.onError(e);
            }
        });
    }

    public static void post(final String link, JSONObject jsonObject, final NetworkInterface.Response response) {
        post(link, null, jsonObject, response);
    }

    public static void post(final String link, final String token, JSONObject jsonObject, final NetworkInterface.Response response) {
        String params = "json: " + jsonObject;


        Log.d("RequestHelper", "post request -> Token: " + token + " Link: "
                + link + " params: " + params);

        getNetworkInterface().post(link, token, jsonObject, new NetworkInterface.Response() {
            @Override
            public void onSuccess(String s) {
                Log.d("RequestHelper", "post response -> link: "
                        + link + " response: " + s);
                response.onSuccess(s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("RequestHelper", "post error -> Exception: " + e.getClass() + " " + e.getMessage());
                response.onError(e);
            }
        });
    }

    public static void post(final String link, Map<String, String> headers, Map<String, String> map, final NetworkInterface.Response response) {
        Log.d("RequestHelper", "post request -> Link: "
                + link + " params: " + new JSONObject(map).toString());

        getNetworkInterface().post(link, headers, map, new NetworkInterface.Response() {
            @Override
            public void onSuccess(String s) {
                Log.d("RequestHelper", "post response -> link: "
                        + link + " response: " + s);
                response.onSuccess(s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("RequestHelper", "post error -> Exception: " + e.getClass() + " " + e.getMessage());
                response.onError(e);
            }
        });
    }

    public static void put(final String link, final String token, JSONObject jsonObject, final NetworkInterface.Response response) {
        String params = "json: " + jsonObject;


        Log.d("RequestHelper", "put request -> Token: " + token + " Link: "
                + link + " params: " + params);

        getNetworkInterface().put(link, token, jsonObject, new NetworkInterface.Response() {
            @Override
            public void onSuccess(String s) {
                Log.d("RequestHelper", "put response -> link: "
                        + link + " response: " + s);
                response.onSuccess(s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("RequestHelper", "put error -> Exception: " + e.getClass() + " " + e.getMessage());
                response.onError(e);
            }
        });
    }

    public static void upload(final String link, String token, HashMap<String, String> map,
                              final String fileKey, final File file, final NetworkInterface.Response response) {

        StringBuilder params = new StringBuilder();
        try {
            for (String keys : map.keySet()) {
                params.append("key: ").append(keys).append(" -> value: ").append(map.get(keys)).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("RequestHelper", "upload request -> " +
                "Link: " + link +
                "\ntoken: " + token +
                "\nparams: " + params +
                "file_key: " + fileKey +
                "\nfile: " + file.getAbsolutePath());

        getNetworkInterface().upload(link, token, map, fileKey, file, new NetworkInterface.Response() {
            @Override
            public void onSuccess(String s) {
                Log.d("RequestHelper", "upload response -> link: "
                        + link + " response: " + s);
                response.onSuccess(s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("RequestHelper", "upload error -> Exception: " + e.getClass() + " " + e.getMessage());
                response.onError(e);
            }
        });

    }

    public static void cancel(String tag) {
        Log.d("RequestHelper", "cancel request -> tag: " + tag);
        getNetworkInterface().cancel(tag);
    }


}
