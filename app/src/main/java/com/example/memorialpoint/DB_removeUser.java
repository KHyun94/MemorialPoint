package com.example.memorialpoint;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DB_removeUser extends AsyncTask<String, Void, String>
{
    String TAG = "TAG";
    @Override
    protected String doInBackground(String... strings) {

        String link = "http://"+ strings[0] +"/removeUser.php";
        String user_no = strings[1];

        try {

            String postData = "no=" + user_no;


            URL url = new URL(link);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(10000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            Log.d(TAG, "4doInBackground: ");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
            Log.d(TAG, "5doInBackground: ");
            outputStreamWriter.write(postData);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            Log.d(TAG, "6doInBackground: ");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            Log.d(TAG, "7doInBackground: ");
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                Log.d(TAG, "8doInBackground: ");
                break;
            }
            Log.d(TAG, "10doInBackground: ");
            return sb.toString();
        } catch (Exception e) {
            Log.d(TAG, "11doInBackground: ");
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

