package com.techstartegies.icall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techstartegies.util.MySqlUtil;

public class Main extends Activity implements View.OnClickListener {

    TelephonyManager tm;
    EditText usernameField, passwordField;
    String number;
    Button register;
    TextView tv;
    static Boolean result = false;
    private static final String JSON_URL = "http://localhost/icall/app_auth.php";
    Context context = this;
    ChatConnection con;
    static int flag = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setContentView(R.layout.activity_chat2);


        register = (Button)findViewById(R.id.btn_register);
        tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        Toast.makeText(Main.this, tm.getLine1Number(), Toast.LENGTH_SHORT).show();
        usernameField = (EditText)findViewById(R.id.username);
        passwordField = (EditText)findViewById(R.id.password);
        register.setOnClickListener(this);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    /**
     * This function will verify the login credientaials
     */
    public void login(View view){

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        //username="admin";
        //password="admin";
        Log.i("Network Info", isNetworkConnected() + "");
        if(!(username.equals("") || password.equals(""))){
            if(isNetworkConnected())
                new MySqlUtil(this).execute( username, password);
            else
                Toast.makeText(context,"Login Failed! please Check Your Internet Connection",Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(context,"Login Failed! please fill the fields",Toast.LENGTH_LONG).show();


    }

    @Override
    public void onClick(View v) {
        if(v==register){

            login(v);
            if(ChatService.started==0) {
                startService(new Intent(getApplicationContext(), ChatService.class));
            }

            if(!ChatConnection.login) {
                Log.i("Open Fire", " Login from Openfire");
            }


        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
