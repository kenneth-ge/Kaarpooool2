package com.example.carpool;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetHandler {

    public static void init(){
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    public static String calculate(String id) throws Exception {
        URL url = new URL("http://10.189.86.52:8080/calculate/" + id);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        System.out.println(responseCode);

        if(responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }

        System.out.println("Didn't return the right result");

        return null;
    }

    public static JSONObject update(String id, JSONObject newObject) throws IOException, JSONException {
        URL obj = new URL("http://10.189.86.52:8080/update_session/" + id);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");

        System.out.println("New object: ");
        System.out.println(newObject.toString());

        // For POST only - START
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()){
            os.write(("{\"json\":" + newObject.toString() + "}").getBytes());
            os.flush();
        }

        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return new JSONObject(response.toString());
        } else {
            System.out.println("POST request didn't work");
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static JSONObject create(String password) throws Exception {
        URL obj = new URL("http://10.189.86.52:8080/create_session");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", "Mozilla/5.0");

        // For POST only - START
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()){
            os.write(("psw=" + password).getBytes());
            os.flush();
            os.close();
        }

        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return new JSONObject(response.toString());
        } else {
            System.out.println("POST request didn't work");
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static JSONObject login(long id, String password) throws IOException, JSONException {
        URL obj = new URL("http://10.189.86.52:8080/login");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", "Mozilla/5.0");

        // For POST only - START
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()){
            os.write(("uname=" + id + "&psw=" + password).getBytes());
            os.flush();
        }

        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());

            return new JSONObject(response.toString());
        } else {
            System.out.println("POST request didn't work");
        }

        return null;
    }

    public static JSONObject query_session(long id) throws Exception {
        URL url = new URL("http://10.189.86.52:8080/session/" + id);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        //con.setDoOutput(true);

        int responseCode = con.getResponseCode();

        System.out.println(responseCode);

        if(responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());

            return new JSONObject(response.toString());
        }

        return null;
    }

}
