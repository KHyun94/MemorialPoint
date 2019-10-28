package com.example.memorialpoint;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DB_updateUser extends AsyncTask<String, Void, String>
{
    String TAG = "TAG";
    @Override
    protected String doInBackground(String... strings) {

        String link = strings[0] + "memorial_point/PHPs/updateUser.php";
        String user_no = strings[1];
        String user_pwd = strings[2];


        try {

            String postData = "no=" + user_no+ "&" + "pwd=" + user_pwd;

            URL url = new URL(link);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(10000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
            outputStreamWriter.write(postData);
            outputStreamWriter.flush();
            outputStreamWriter.close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

