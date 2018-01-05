package com.techstartegies.icall;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.techstartegies.util.MySqlUtil;

import java.util.ArrayList;

public class AddDoctor extends Activity implements View.OnClickListener {

    public static EditText cnic_field, first_name, last_name, contact, password, special;
    int len = 0;
    Button reg_doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_doctor);

        cnic_field = (EditText) findViewById(R.id.cnic);
        first_name = (EditText) findViewById(R.id.first_name);
        first_name.setEnabled(false);
        last_name = (EditText) findViewById(R.id.last_name);
        last_name.setKeyListener(null);
        contact = (EditText) findViewById(R.id.contact);
        password = (EditText) findViewById(R.id.password);
        special = (EditText) findViewById(R.id.doctorSpecialization);
        special.setKeyListener(null);
        reg_doc = (Button) findViewById(R.id.dtr_reg_btn);

        cnic_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String str = cnic_field.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = cnic_field.getText().toString();
                if ((str.length() == 5 || str.length() == 13) && len < str.length())//len check for backspace
                    cnic_field.append("-");
                if (str.length() == 15)
                    new MySqlUtil(AddDoctor.this).execute(cnic_field.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        reg_doc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == reg_doc) {
            new MySqlUtil(AddDoctor.this).execute(first_name.getText().toString().toLowerCase() + "." + last_name.getText().toString().toLowerCase(), password.getText().toString(), cnic_field.getText().toString(), "0", "doctor");

            String msg = "Dear " + first_name.getText().toString() + " ! \nYou are registered by admin for iCall \n"
                    +"Following are your Crediential to login to your app please don't share them with any one\n" +
                    "Username :" + first_name.getText().toString().toLowerCase() + "." + last_name.getText().toString().toLowerCase() +
                    "\nPassword : " + password.getText().toString();
            try{
                sendSms(contact.getText().toString(), msg);
            } catch (IllegalArgumentException e){
                Log.e("SMS Destination", "Invalid Destination for the Sms");
            }
            AddDoctor.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Message Send", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void sendSms(String number, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(message);
        smsManager.sendMultipartTextMessage(number, null, parts, null, null);
    }


}
