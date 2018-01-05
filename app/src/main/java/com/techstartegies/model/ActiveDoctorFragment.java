package com.techstartegies.model;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.techstartegies.adapter.CustomListAdapter;
import com.techstartegies.icall.AppController;
import com.techstartegies.icall.R;
import com.techstartegies.icall.chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ActiveDoctorFragment extends Fragment implements View.OnClickListener {

    // Log tag
    private static final String TAG = ActiveDoctorFragment.class.getSimpleName();

    // Movies json url
    private static final String url = "http://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<Doctor> movieList = new ArrayList<Doctor>();
    private ListView listView;
    private CustomListAdapter adapter;
    View rootView;
    Button connect;
    int flag = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_active_doctor, container, false);
        //connect = (Button) rootView.findViewById(R.id.connect);
        connect.setOnClickListener(this);

        listView = (ListView) rootView.findViewById(R.id.list);
        //adapter = new CustomListAdapter(rootView.this, movieList);
        listView.setAdapter(adapter);

        //pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // changing action bar color
        /*getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));
*/
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = null;
                                try {
                                    obj = response.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Doctor movie = new Doctor();
                                movie.setDoctorName(obj.getString("title"));
                                movie.setThumbnailUrl(obj.getString("image"));
                              /*  movie.setDoctorSpecialization(((Number) obj.get("rating"))
                                        .doubleValue());
                              *///  movie.setYear(obj.getInt("releaseYear"));

                                // Genre is json array
                                JSONArray genreArry = obj.getJSONArray("genre");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                            //    movie.setDoctorStatus(genre);

                                // adding movie to movies array
                                movieList.add(movie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    @Override
    public void onClick(View v) {
        if(v == connect){
            Intent i = new Intent(getActivity(), chat.class);
            i.putExtra("ChatId","ali"); //Communicating user
            flag = 2;
            startActivity(i);
        }
    }
}
