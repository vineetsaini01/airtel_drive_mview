package com.webservice;

import android.content.Context;
import android.net.Uri;


import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by functionapps on 2/27/2019.
 */

public class PviewWebService {

    private static String finalres;

    private static HttpURLConnection connection;

    public static String sendPostRequest(String url1, HashMap<String, String> message, Context context) {
        int success = 0;
        String response = "";
        String errMsg = "";
        String query = "";


        InputStream in;
        try {
            // instantiate the URL object with the target URL of the resource to
            // request
            URL url = new URL(url1);


            System.out.println("req in before connecting......... " + Utils.getDateTime());


            // instantiate the HttpURLConnection with the URL object - A new
            // connection is opened every time by calling the openConnecti on
            // method of the protocol handler for this URL.
            // 1. This is the point where the connection is opened.
            connection = (HttpURLConnection) url.openConnection();
            System.out.println("req in after opening connection......... " + Utils.getDateTime());
            // set connection output to true
            connection.setDoOutput(true);
            // instead of a GET, we're going to send using method="POST"
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(Constants.CONNECTION_TIMEOUT);

            // build url with parameters.
            Uri.Builder builder = Uri.parse(url1).buildUpon();
            if (message != null) {
                for (Map.Entry<String, String> entry : message.entrySet()) {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue());
                }
                query = builder.build().getEncodedQuery();
            }
            System.out.println("response query " + query);
            OutputStream os = connection.getOutputStream();

            System.out.println("req in after buildng query......... " + Utils.getDateTime());

            if (os != null) {

                OutputStreamWriter writer = new OutputStreamWriter(os, "utf-8");
                writer.write(query);
                writer.close();
                // if there is a response code AND that response code is 200 OK, do
                // stuff in the first if block
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // OK
                    System.out.println("response is ok from server");
                    in = connection.getInputStream();
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    finalres = response;

                    // otherwise, if any other status code is returned, or no status
                    // code is returned, do stuff in the else block
                } else {
                    System.out.println("response is error from server" + connection.getResponseCode());
                    // Server returned HTTP error code.
                }
                success = 1;
            }
        } catch (MalformedURLException e) {
            Constants.ERROR = 2;
            errMsg = e.getMessage();
            System.out.println("exception in http malformed  response " + e.toString());
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return "timeout";


        } catch (IOException e) {
            //  Constants.ERROR=2;


            errMsg = e.getMessage();
            System.out.println("exception in http IO response " + e.toString());
            return Constants.SERVER_ERROR;
        } catch
        (Exception e) {
            //   Constants.ERROR=2;
            errMsg = e.getMessage();
            System.out.println("exception in http response " + e.toString());
            return Constants.SERVER_ERROR;

        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        if (success == 0) {
        }
        System.out.println("returning response " + response);
        return finalres;
    }










}
