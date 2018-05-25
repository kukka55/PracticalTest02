package com.example.student.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    final public static String TAG = "[PracticalTest02]";

    private String address;
    private int port;
    private String city;
    private String minLenght;
    private TextView anagramsTextView;

    private Socket socket;

    public ClientThread(String address, int port, String city, String minLenght, TextView anagramsTextView) {
        this.address = address;
        this.port = port;
        this.city = city;
        this.minLenght = minLenght;
        this.anagramsTextView = anagramsTextView;
    }

    public static BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = getReader(socket);
            PrintWriter printWriter = getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(city);
            printWriter.flush();
            printWriter.println(minLenght);
            printWriter.flush();
            String anagram;
            while ((anagram = bufferedReader.readLine()) != null) {
                final String finalizedAnagrams = anagram;
                anagramsTextView.post(new Runnable() {
                   @Override
                    public void run() {
                       anagramsTextView.setText(finalizedAnagrams);
                   }
                });
            }
        } catch (IOException ioException) {
            Log.e(TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }

}
