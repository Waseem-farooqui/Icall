package com.techstartegies.icall;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayDialog extends Activity {

    ListView dialogList;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_dialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Color Mode");
        //builder.setView(R.layout.custom_dialog);

        text = (TextView)findViewById(R.id.dialogclick);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


}
