package com.example.student.practicaltest02.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;


public class CommunicationThread extends Thread {

    final public static String WEB_SERVICE_ADDRESS = "http://services.aonaware.com/CountCheatService/CountCheatService.asmx?op=LetterSolutions";
    final public static String QUERY_ATTRIBUTE = "query";

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public static BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("Practic", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = getReader(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter printWriter = getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("Practic", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            String city = bufferedReader.readLine();
            String minLenght = bufferedReader.readLine();
            if (city == null || city.isEmpty() || minLenght == null || minLenght.isEmpty()) {
                Log.e("Practic", "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            String result = null;

            Log.i("Practic", "[COMMUNICATION THREAD] Getting the information from the webservice...");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(WEB_SERVICE_ADDRESS);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("anagram", city));
                params.add(new BasicNameValuePair("minLetters", minLenght));

                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);

                Log.i("Practic URL ADDRESS", urlEncodedFormEntity.toString());

                HttpResponse httpPostResponse = httpClient.execute(httpPost);

                Log.i("Practic", httpPostResponse.toString());

                HttpEntity httpPostEntity = httpPostResponse.getEntity();

                result = httpPostResponse.toString();
                if (httpPostEntity != null) {
                    // do something with the response
                    Log.i("Practic", EntityUtils.toString(httpPostEntity));
                }
            } catch (Exception exception) {
                Log.e("Practic", exception.getMessage());
            }




            printWriter.println(result);
            printWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("Practic", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }

}
