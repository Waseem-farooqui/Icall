package com.techstartegies.icall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.techstartegies.adapter.CustomListAdapter;
import com.techstartegies.model.Doctor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "http://techstrategies.com.pk/icall/api/get_doctors.php";
    private ProgressDialog pDialog;
    private List<Doctor> doctorsList = new ArrayList<Doctor>();
    private ListView listView;
    private CustomListAdapter adapter;
    public Context context=this;
    CreateGroup cg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_active_doctor);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, doctorsList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // changing action bar color
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));

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

                                JSONObject obj = response.getJSONObject(i);
                                Doctor doctor = new Doctor();
                                doctor.setDoctorName(obj.getString("firstname")+" "+obj.getString("lastname"));
                                doctor.setThumbnailUrl(obj.getString("image"));
                                doctor.setDoctorSpecialization(obj.getString("specialization"));
                                doctor.setDoctorStatus(obj.getInt("status"));

                                // Genre is json array


                                // adding doctor to movies array
                                doctorsList.add(doctor);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_doctor, menu);
        return true;
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
        if (id == R.id.action_GroupChat) {

            try{

                Log.e("Creating Group", "Starting");
                cg=new CreateGroup();
                cg.CreatingGroup();

                Log.e("Creating Group:","Successfully");
            }
            catch(Exception e)
            {
                Log.e("Group Exception",e.getMessage());
            }
            return true;
        }

        if (id == R.id.action_JoinGroup) {

            try{

                cg=new CreateGroup();


                cg.joiningGroup();

                Intent i=new Intent(this,GroupChat.class);
                startActivity(i);

                Log.e("Joining Group:", "Successfully");


                Toast.makeText(this, "Starting Group Chat", Toast.LENGTH_LONG).show();
                //ChatService.joinFirstTime=1;
            }
            catch(Exception e)
            {
                Log.e("Joining Group:Exception",e.getMessage());
                Toast.makeText(this,"Joining Unsuccessful",Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.action_Invite) {

            try{

                //  cg=new CreateGroup();
                //  cg.inviteUser();

                Log.e("Inviting:","Successfully");
            }
            catch(Exception e)
            {
                Log.e("Invitation Exception", e.getMessage());
            }
            return true;
        }

      /*  if (id == R.id.action_startChat) {

            try{

                Intent i=new Intent(MainActivity.this,GroupChat.class);
                startActivity(i);
                Log.e("Chat Started:","Successfully");
            }
            catch(Exception e)
            {
                Log.e("Chat Started Exception",e.getMessage());
            }
            return true;
        }
*/

        return super.onOptionsItemSelected(item);
    }




}