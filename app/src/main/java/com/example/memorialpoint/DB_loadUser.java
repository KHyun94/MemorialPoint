package com.example.memorialpoint;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class DB_loadUser {

    String TAG = "TAG";
    String myJSON;
    private static final String[] TAGs = {"result", "no", "id", "pwd", "name", "birthday", "sex", "address", "email", "tel"};
  //  String url = "http://"+ R.string.ip +"/member.php";
    JSONArray members = null;
    ArrayList<Data_User> memberDataArrayList;

    public String getData(String url) {

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String link = params[0];
                Log.d(TAG, "1. doInBackground url: "+ link);
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(link);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(10000);
                    con.setConnectTimeout(10000);
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.connect();

                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    bufferedReader.close();

                    return sb.toString().trim();

                } catch (Exception e) {

                    return "error: " + e.toString();
                }

            }

        }
        GetDataJSON getDataJSON = new GetDataJSON();

        try{
            myJSON = getDataJSON.execute(url).get();
            return myJSON;
        }
        catch (Exception e)
        {
            return "error: " + e.toString();
        }

    }

    protected ArrayList<Data_User> parsingJSON(String url) {

        try {
            myJSON = getData("http://" + url + "/user.php");

            JSONObject jsonObj = new JSONObject(myJSON);
            members = jsonObj.getJSONArray(TAGs[0]);
            memberDataArrayList = new ArrayList<Data_User>();

            for (int i = 0; i < members.length(); i++) {

                JSONObject c = members.getJSONObject(i);

                String no = c.getString(TAGs[1]);
                String id = c.getString(TAGs[2]);
                String password = c.getString(TAGs[3]);
                String name = c.getString(TAGs[4]);
                String birthday = c.getString(TAGs[5]);
                String sex = c.getString(TAGs[6]);
                String address = c.getString(TAGs[7]);
                String email = c.getString(TAGs[8]);
                String tel = c.getString(TAGs[9]);

                Data_User user_data = new Data_User(no, id, password, name, birthday, sex, address, email, tel);

                memberDataArrayList.add(user_data);

                Log.d(TAG, "parsingJSON: mem: " + memberDataArrayList.get(i).getId());
            }
            Log.d(TAG, "8. parsingJSON members: ");
            return memberDataArrayList;
        } catch (JSONException e) {
            Log.d(TAG, "9. parsingJSON members: ",e);
            e.printStackTrace();

            return null;
        }

    }


}