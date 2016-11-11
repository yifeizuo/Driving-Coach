package com.example.yzuo.drivingcoach;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class drivingBehavior extends AppCompatActivity {

    private EditText comment;
    private Button btn_add_comment, btn_save, btn_cancel;
    private View commentContainer;
    private Toast toast;

    private String type;
    private String value;
    private String point_time;
    private String tripID, segmentStartTime;
    private static final String STOP_CONTENT = "You were at a very low speed, and it is ";
    private static final String HIGH_ACCEL = "Very high acceleration during your trip and it is ";
    private static final String HIGH_DECEL = "Very high deceleration during your trip and it is";
    private static final String HIGH_SPEED = "is a very high speed during you trip. Maybe you can consider slow down a little bit";
    private TextView bad_behavior, bad_behavior_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_behavior);

        //
        init();

        controlCommentContainer();


        getBadBehaviorType();
        getBadBehaviorTime();
        getBadBehaviorContent();

    }

    private void init() {
        commentContainer = findViewById(R.id.commentContainer);
        btn_add_comment = (Button) findViewById(R.id.addComment);
        btn_save = (Button) findViewById(R.id.save);
        btn_cancel = (Button) findViewById(R.id.cancel);

        bad_behavior = (TextView) findViewById(R.id.bad_behavior);
        bad_behavior_content = (TextView) findViewById(R.id.bad_behavior_content);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        value = intent.getStringExtra("value");
        point_time = intent.getStringExtra("point_time");
        tripID = intent.getStringExtra("tripID");
        segmentStartTime = intent.getStringExtra("segmentStartTime");

        bad_behavior.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        bad_behavior_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        if(type.equals("stop")) {
            bad_behavior.setText("Very low speed");
            bad_behavior_content.setText(STOP_CONTENT + String.format("%.2f", Double.parseDouble(value)) + "m/s");
        }
        else if (type.equals("high_accel")) {
            bad_behavior.setText("High acceleration");
            bad_behavior_content.setText(HIGH_ACCEL + String.format("%.2f", Double.parseDouble(value)) + "m/s²" );
        }
        else if (type.equals("high_decel")) {
            bad_behavior.setText("High deceleration");
            bad_behavior_content.setText(HIGH_DECEL + String.format("%.2f", Double.parseDouble(value)) + "m/s²");
        }
        else if (type.equals("high_speed")) {
            bad_behavior.setText("High speed");
            bad_behavior_content.setText( String.format("%.2f", Double.parseDouble(value)) + "m/s" + HIGH_SPEED);
        }


    }


    private void controlCommentContainer() {
        //set the size of comment window
        setSize();

        commentContainer.setVisibility(View.GONE);

        btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentContainer.setVisibility(View.VISIBLE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commHandler mHandler = new commHandler(getApplicationContext());
                commHandler.onResponseListener putCommentListener = new commHandler.onResponseListener<String>() {
                    @Override
                    public void onResponse(String str) {
                        if(str.equals("")) {
                            //It should be no response body, but only 200 OK status code
                            new AlertDialog.Builder(drivingBehavior.this)
                                    .setTitle("Status")
                                    .setMessage("Feedback sent successfully")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            commentContainer.setVisibility(View.GONE);
                                        }
                                    })
                                    .show();
                        }
                        else {
                            int status_code = Integer.parseInt(str);

                            new AlertDialog.Builder(drivingBehavior.this)
                                    .setTitle("Error: " + status_code)
                                    .setMessage("Feedback failed to be sent")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }

                    }
                };
                mHandler.put_comment(tripID, segmentStartTime, point_time, comment.getText().toString(), putCommentListener);


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentContainer.setVisibility(View.GONE);
                commentContainer.setVisibility(View.GONE);
                toast = Toast.makeText(getApplicationContext(), "Comment has been canceled", Toast.LENGTH_SHORT);
                toast.show();
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }

    private void setSize()
    {
        comment = (EditText) findViewById(R.id.comment);
        android.view.ViewGroup.LayoutParams params = comment.getLayoutParams();
        //params.width = 360;
        //params.height = 200;
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());
        params.width = width;
        params.height = height;
        comment.setLayoutParams(params);
    }

    private void getBadBehaviorType() {
    }

    private void getBadBehaviorTime() {
    }

    private void getBadBehaviorContent() {
    }

}
