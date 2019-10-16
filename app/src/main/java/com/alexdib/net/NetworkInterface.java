package com.alexdib.net;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public interface NetworkInterface {

    interface Response {
        void onSuccess(String s);

        void onError(Exception e);
    }

    void get(String link, Map<String, String> headers, Response response);

    void delete(String link, String token, Response response);

    void delete(String link, String token, JSONObject jsonObject, Response response);

    void getHeader(String link, String headerKey, Response response);

    void post(String link,  JSONObject jsonObject, Map<String, String> headers, Response response);

    void post(String link, Map<String, String> headers, Map<String, String> map, Response responseInterface);

    void put(String link, String token, JSONObject jsonObject, Response response);

    void upload(String link, String token, HashMap<String, String> map, String fileKey, File file, Response response);

    void cancel(String tag);

}
