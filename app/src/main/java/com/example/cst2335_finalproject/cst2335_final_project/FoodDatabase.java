package com.example.cst2335_finalproject.cst2335_final_project;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class FoodDatabase extends AsyncTask <Void, Void, String> {
    @Override
    protected String doInBackground(Void... params) {
        String response="";
        try {
            URL API = new URL("https");
            HttpsURLConnection myConnection = (HttpsURLConnection) API.openConnection();

            myConnection.setRequestProperty("e3c68478","bbf29319775e2048d28630abf5e4f59e");

            if(myConnection.getResponseCode() == 200) {
                InputStream responseBody = myConnection.getInputStream();
            }

            return response;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}