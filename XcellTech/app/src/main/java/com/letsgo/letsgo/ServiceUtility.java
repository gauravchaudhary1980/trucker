package com.letsgo.letsgo;

/**
 * Created by gaurav.chaudhary on 10/31/2016.
 */
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class ServiceUtility {

    public static String GET(String url) {
        String result = BuildConfig.FLAVOR;
        try {
            InputStream inputStream = new DefaultHttpClient().execute(new HttpGet(url)).getEntity().getContent();
            if (inputStream != null) {
                return convertInputStreamToString(inputStream);
            }
            return "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            return result;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = BuildConfig.FLAVOR;
        String result = BuildConfig.FLAVOR;
        while (true) {
            line = bufferedReader.readLine();
            if (line != null) {
                result = result + line;
            } else {
                inputStream.close();
                return result;
            }
        }
    }

    public static String POST(String url, JsonObject obj) {
        String result = BuildConfig.FLAVOR;
        Gson gson = new Gson();

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String str = BuildConfig.FLAVOR;
            httpPost.setEntity(new StringEntity(gson.toJson((JsonElement) obj)));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            InputStream inputStream = httpclient.execute(httpPost).getEntity().getContent();
            if (inputStream != null) {
                return convertInputStreamToString(inputStream);
            }
            return "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            return result;
        }
    }

    public static String PUT(String url, JsonObject obj) {
        String result = BuildConfig.FLAVOR;
        Gson gson = new Gson();
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut(url);
            String str = BuildConfig.FLAVOR;
            httpPut.setEntity(new StringEntity(gson.toJson((JsonElement) obj)));
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
            InputStream inputStream = httpclient.execute(httpPut).getEntity().getContent();
            if (inputStream != null) {
                return convertInputStreamToString(inputStream);
            }
            return "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            return result;
        }
    }
}
