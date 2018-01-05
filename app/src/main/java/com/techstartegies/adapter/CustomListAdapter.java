package com.techstartegies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.techstartegies.icall.AppController;
import com.techstartegies.icall.CreateGroup;
import com.techstartegies.icall.GroupChat;
import com.techstartegies.icall.R;
import com.techstartegies.icall.chat;
import com.techstartegies.model.Doctor;

import java.util.List;

/**
 * Created by Waseem on 25-Nov-15.
 */
public class CustomListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Doctor> doctorsList;
    CreateGroup cg;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Doctor> doctorsList) {
        this.activity = activity;
        this.doctorsList = doctorsList;
    }

    @Override
    public int getCount() {

        return doctorsList.size();
    }

    @Override
    public Object getItem(int location) {

        return doctorsList.get(location);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);

        final TextView doctorName = (TextView) convertView.findViewById(R.id.doctorName);
        TextView doctorSpecialization = (TextView) convertView.findViewById(R.id.dctrSpecialization);
        TextView doctorStatus = (TextView) convertView.findViewById(R.id.doctorStatus);
        Button connect = (Button) convertView.findViewById(R.id.connect);

        // getting movie data for the row
        Doctor doctor = doctorsList.get(position);

        // thumbnail image
        thumbNail.setImageUrl(doctor.getThumbnailUrl(), imageLoader);

        // doctorName

        doctorName.setText(doctor.getDoctorName());
        if(doctor.getDoctorStatus()==1){
            doctorName.setTextColor(activity.getResources().getColor(R.color.activeGreen));
            doctorStatus.setText("Active");
            connect.setEnabled(true);
        }
        else{

            doctorName.setTextColor(activity.getResources().getColor(R.color.pendingOrange));
            doctorStatus.setText("Pending");
            connect.setEnabled(false);

        }

        // doctorSpecialization
        doctorSpecialization.setText(String.valueOf(doctor.getDoctorSpecialization()));



        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(activity.getApplicationContext(), chat.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);;
                i.putExtra("ChatId", doctorName.getText().toString()); //Communicating user
                //                username = getIntent().getExtras().getString("username");
                activity.startActivity(i);
            }
        });


        return convertView;
    }

    //================================ Connection Object========================================


    //============================================================================================



}
