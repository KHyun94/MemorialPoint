
package com.example.memorialpoint;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


        public class DB_InsertUser extends AsyncTask<String, Void, String>
        {
            String TAG = "TAG";
            @Override
            protected String doInBackground(String... strings) {
                Log.d(TAG, "doInBackground: 1");

                String link = "http://"+strings[0]+"/insertUser.php";
                String user_id = strings[1];
                String user_pwd = strings[2];
                String user_name = strings[3];
                String user_birthday = strings[4];
                String user_sex = strings[5];
                String user_address = strings[6];
                String user_email = strings[7];
                String user_tel = strings[8];

                try {
                    Log.d(TAG, "doInBackground: 2");


                    String postData = "id=" + user_id + "&" + "pwd=" + user_pwd+ "&" + "name=" + user_name +
                            "&" +"birthday=" + user_birthday + "&" + "sex=" + user_sex+ "&" + "address=" + user_address +"&"+ "email=" + user_email + "&" + "tel=" + user_tel;

                    URL url = new URL(link);
                    Log.d(TAG, "doInBackground: 3 url: " + url);
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

                    Log.d(TAG, "doInBackground: 5");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    Log.d(TAG, "doInBackground: 6");
                    // Read Server Response
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }

        }



