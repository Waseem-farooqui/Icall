package com.techstartegies.icall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class DoctorMenu extends Activity implements View.OnClickListener {

    ImageButton add_doctor, add_staff, view_doctor, view_staff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_menu);

        add_doctor = (ImageButton)findViewById(R.id.btn_addDoctor);
        add_staff = (ImageButton)findViewById(R.id.btn_addStaff);
        view_doctor = (ImageButton)findViewById(R.id.btn_viewDoctor);
        view_staff = (ImageButton)findViewById(R.id.btn_viewStaff);

        add_doctor.setOnClickListener(this);
        view_doctor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == add_doctor){
            startActivity(new Intent(this,AddDoctor.class ));
        }
        if(v == view_doctor){
            Intent view_doctor = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(view_doctor);
        }
    }

    /*final Dialog dialog = new Dialog(this.getApplicationContext());
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setDoctorName("Turn on Admin Mode");
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.button_red);
        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Are you sure?");
        ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.btn_yes);
        //dialog.show();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        //dialog.dismiss();
                    }
                }).start();
            }
        });*/
}