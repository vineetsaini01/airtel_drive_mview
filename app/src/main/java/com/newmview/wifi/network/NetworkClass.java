package com.newmview.wifi.network;

import android.os.StrictMode;

import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
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
import java.net.ProtocolException;
import java.net.URL;

public class NetworkClass {

    public static String sendPostRequest(String message) {
        int success = 0;
        String response = "";
        String errMsg = "";
        int maxRetries = 3; // Maximum number of retries
        int attempt = 0;
        int CONNECTION_TIMEOUT=30000;
        int READ_TIMEOUT = 30000;
        int retryDelay = 2000;
        String query = "";
        InputStream in = null;
        OutputStreamWriter writer = null;
        OutputStream os = null;
        BufferedReader br = null;
        HttpURLConnection connection = null;

        while (attempt < maxRetries) {
            attempt++;

            try {

                // by swapnil - 05/09/2022
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
                // instantiate the URL object with the target URL of the resource to
                // request
                URL url = new URL(Constants.URL);


                // instantiate the HttpURLConnection with the URL object - A new
                // connection is opened every time by calling the openConnection
                // method of the protocol handler for this URL.
                // 1. This is the point where the connection is opened.
                connection = (HttpURLConnection) url.openConnection();
                // set connection output to true
                connection.setDoOutput(true);
                // instead of a GET, we're going to send using method="POST"
                connection.setRequestMethod("POST");

                //by swapnil 08/23/2022
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);



                os = connection.getOutputStream();

                // instantiate OutputStreamWriter using the output stream, returned
                // from getOutputStream, that writes to this connection.
                // 2. This is the point where you'll know if the connection was
                // successfully established. If an I/O error occurs while creating
                // the output stream, you'll see an IOException.

                // OutputStream o = connection.getOutputStream();
                if (os != null) {

                    writer = new OutputStreamWriter(os, "utf-8");

                    /*
                     * BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os,
                     * "UTF-8"));
                     */
                    // BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(o,
                    // "UTF-8"));
                    // write data to the connection. This is data that you are sending
                    // to the server
                    // 3. No. Sending the data is conducted here. We established the
                    // connection with getOutputStream

                    writer.write("req=" + message);
                    writer.flush();

                    // o.write(postDataBytes);

                    // Closes this output stream and releases any system resources
                    // associated with this stream. At this point, we've sent all the
                    // data. Only the outputStream is closed at this point, not the
                    // actual connection


                    System.out.println(" : via sendPostRequest Message going in request is " + message);
                    // if there is a response code AND that response code is 200 OK, do
                    // stuff in the first if block

                    System.out.println(" : via sendPostRequest ** response code**" + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        // OK
                        System.out.println(": via sendPostRequest response is ok from server");
                        in = connection.getInputStream();
                        String line;
                        br = new BufferedReader(new InputStreamReader(in));
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                        Utils.appendLog("ELOG_NETWORK_REQUEST: Response is success from server and response is " + response);
                        return response;

                        // otherwise, if any other status code is returned, or no status
                        // code is returned, do stuff in the else block
                    } else {
                        Utils.appendLog("ELOG_NETWORK_REQUEST: Response is error from server");
                        System.out.println("response is error from server");
                        // response="0";
                        // Server returned HTTP error code.
                    }
                }
            } catch (ProtocolException e) {
                Utils.appendLog("ELOG_NETWORK_REQUEST_EXCEPTION: ProtocolException in Network class while sending request: " + e.getMessage());

                e.printStackTrace();
            } catch (MalformedURLException e) {
                Utils.appendLog("ELOG_NETWORK_REQUEST_EXCEPTION: MalformedURLException in Network class while sending request: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Utils.appendLog("ELOG_NETWORK_REQUEST: IOException in Network class while sending request: " + e.getMessage());
                Utils.appendLog("ELOG_NETWORK_REQUEST: IOException trace in Network class while sending request: "+e.getStackTrace());
                if (attempt < maxRetries) {
                    Utils.appendLog("ELOG_NETWORK_REQUEST: Retrying... Attempt " + attempt + " of " + maxRetries);
                    try {
                        Thread.sleep(retryDelay); // Wait before retrying
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();

                    }
                    if (in != null) {
                        in.close();

                    }
                    if (writer != null) {
                        writer.close();

                    }
                    if (os != null) {
                        os.close();

                    }
                    if ( connection != null) {
                        connection.disconnect();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }
        return response;
    }

    public static String sendPostRequest1(String url1, String message) {
        int success = 0;
        String response = "";
        String errMsg = "";
        String query="";
        InputStream in;
        try {
            // by swapnil - 05/09/2022
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            // instantiate the URL object with the target URL of the resource to
            // request
            URL url = new URL(url1);

            // instantiate the HttpURLConnection with the URL object - A new
            // connection is opened every time by calling the openConnection
            // method of the protocol handler for this URL.
            // 1. This is the point where the connection is opened.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // set connection output to true
            connection.setDoOutput(true);
            // instead of a GET, we're going to send using method="POST"
            connection.setRequestMethod("POST");

            //by swapnil 08/23/2022
            //connection.setConnectTimeout(15000);
            // connection.setReadTimeout(READ_TIMEOUT);


            OutputStream os = connection.getOutputStream();

            // instantiate OutputStreamWriter using the output stream, returned
            // from getOutputStream, that writes to this connection.
            // 2. This is the point where you'll know if the connection was
            // successfully established. If an I/O error occurs while creating
            // the output stream, you'll see an IOException.

            // OutputStream o = connection.getOutputStream();
            if (os != null) {

                OutputStreamWriter writer = new OutputStreamWriter(os, "utf-8");

                /*
                 * BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os,
                 * "UTF-8"));
                 */
                // BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(o,
                // "UTF-8"));
                // write data to the connection. This is data that you are sending
                // to the server
                // 3. No. Sending the data is conducted here. We established the
                // connection with getOutputStream

                writer.write("req=" +message);

                // o.write(postDataBytes);

                // Closes this output stream and releases any system resources
                // associated with this stream. At this point, we've sent all the
                // data. Only the outputStream is closed at this point, not the
                // actual connection

                writer.close();


                System.out.println(" : via sendPostRequest Message going in request is "+message);
                // if there is a response code AND that response code is 200 OK, do
                // stuff in the first if block
                System.out.println(" : via sendPostRequest ** response code**"+connection.getResponseCode());
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // OK
                    System.out.println(": via sendPostRequest response is ok from server");
                    in = connection.getInputStream();
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                    System.out.println(" : via sendPostRequest response is success from server and response is "+response);

                    // otherwise, if any other status code is returned, or no status
                    // code is returned, do stuff in the else block
                } else {
                    System.out.println("response is error from server");
                    // response="0";
                    // Server returned HTTP error code.
                }
                success = 1;
            }
        } catch (MalformedURLException e) {

            // errMsg = e.getMessage();
            // response="0";
            System.out.println(" : via sendPostRequest exception in http response " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {

            //errMsg = e.getMessage();
            // response="0";
            System.out.println(" : via sendPostRequest exception in http response " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // errMsg = e.getMessage();
            //response="0";
            System.out.println(" : via sendPostRequest exception in http response " + e.getMessage());
            e.printStackTrace();
        }
        if (success == 0) {
        }
        return response;
    }// en
}
