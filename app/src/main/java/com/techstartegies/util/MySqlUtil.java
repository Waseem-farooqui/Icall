package com.techstartegies.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techstartegies.icall.AddDoctor;
import com.techstartegies.icall.DoctorMenu;
import com.techstartegies.icall.MainActivity;
import com.techstartegies.model.Doctor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Waseem on 16-Nov-15.
 */
public class MySqlUtil extends AsyncTask<String, Void, String> {

    private ProgressDialog pd;
    private Activity activity = null;


    public MySqlUtil(Activity activity){

        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show(activity, "Searching",
                "Please wait while we are acquiring data..");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String link = "http://techstrategies.com.pk";
        String data = "";
        URL url = null;
        HttpURLConnection conn = null;
        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        Log.e("Num of Params", params.length+"");

        if(params.length == 1) {

            String cnic = (String)params[0];
            //link="http://techstrategies.com.pk/icall/register.php";
            link += "/icall/register.php";
            try {
                data  = URLEncoder.encode("cnic", "UTF-8") + "=" + URLEncoder.encode(cnic, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        else if(params.length == 2){

                String username = (String) params[0];
                String password = (String) params[1];

                //Log.e("as", password);
                //link = "http://techstrategies.com.pk/icall/app_auth.php";
                link += "/icall/app_auth.php";
                try {
                    data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
        }
        else if (params.length == 5){

            String username = (String)params[0];
            String password = (String)params[1];
            String cnic = (String)params[2];
            String status = (String)params[3];
            String type = (String)params[4];
            link += "/icall/add_doctor.php";
            try {
                data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("cnic", "UTF-8") + "=" + URLEncoder.encode(cnic, "UTF-8");
                data += "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");
                data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

        }
        try {
            url = new URL(link);
        } catch (MalformedURLException e1) {
            Log.e("MalformedUrl", e1.getMessage());
        }
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String line = "";
            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
            pd.dismiss();
            return sb.toString();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
}

    @Override
    protected void onPostExecute(String result) {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> map = null;

        try {
                map = mapper.readValue(result, new TypeReference<HashMap<String, String>>() {});

        } catch (IOException e) {
            Log.e("Reading Map", e.getMessage());
        }
        try{
            if(map.get("message").equals("1")) {
                Toast.makeText(activity.getApplicationContext(), map.get("firstname") + "  " + map.get("lastname"), Toast.LENGTH_SHORT).show();
                AddDoctor.first_name.setText(map.get("firstname"));
                AddDoctor.last_name.setText(map.get("lastname"));
                AddDoctor.contact.setText(map.get("contact"));
                AddDoctor.special.setText(map.get("specialization"));
            }
            else if(map.get("message").equals("2")){
                Toast.makeText(activity.getApplicationContext(),"cnic is "+ map.get("username") , Toast.LENGTH_SHORT).show();
                if(map.get("type").equals("admin"))
                    activity.startActivity(new Intent(activity, DoctorMenu.class).putExtra("username",map.get("username")));
                else
                    activity.startActivity(new Intent(activity, MainActivity.class));
                new Doctor().setUsername(map.get("username"));
            }
            else if(map.get("message").equals("3")){
                Toast.makeText(activity.getApplicationContext(),"Record Inserted "+ map.get("username") , Toast.LENGTH_SHORT).show();
            }
            else if(map.get("message").equals("f"))
                Toast.makeText(activity.getApplicationContext(),"Failed to Fetch the Record" , Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            Log.e("On Hash Map", e.toString());
        }
        super.onPostExecute(result);
    }
}
