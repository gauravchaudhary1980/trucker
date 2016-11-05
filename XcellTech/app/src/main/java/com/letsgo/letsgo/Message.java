package com.letsgo.letsgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.Class;
public class Message extends AppCompatActivity {
    ImageView messageImage;
    TextView messageHeaderTextView;
    TextView messageDetailTextView;
    MessageBO messageBO;
    Thread background;
    Intent callBackIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FindFormControls();
        getExtraValues();
        setFormValues();
        background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 3 seconds
                    sleep(3*1000);

                    // After 5 seconds redirect to another intent
                    startActivity(callBackIntent);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
    }
    private void FindFormControls() {
        messageImage = (ImageView) findViewById(R.id.imgMessage);
        messageHeaderTextView=(TextView)findViewById(R.id.txtHeader);
        messageDetailTextView=(TextView)findViewById(R.id.txtDetails);
    }
    private void getExtraValues()
    {
       messageBO = (MessageBO) getIntent().getSerializableExtra("messageBO");
    }
    private void setFormValues()
    {
        int taskImgResId;
        switch (messageBO.getMessageType())
        {
            case "success":
                taskImgResId = getResources().getIdentifier("success", "drawable", "com.letsgo.letsgo");
                break;
            case "fail":
                taskImgResId = getResources().getIdentifier("fail", "drawable", "com.letsgo.letsgo");
                break;
            case "information":
                taskImgResId = getResources().getIdentifier("information", "drawable", "com.letsgo.letsgo");
                break;
            default:
                taskImgResId = getResources().getIdentifier("information", "drawable", "com.letsgo.letsgo");
                break;
        }
        messageImage.setImageResource(taskImgResId);
        messageHeaderTextView.setText(messageBO.getMessageHeader());
        messageDetailTextView.setText(messageBO.getMessageDetail());
        switch (messageBO.getCallBackActivity())
        {
            case "Login":
                callBackIntent=new Intent(this,Login.class);
                break;
            case "CreateTour":
                callBackIntent=new Intent(this,CreateTour.class);
                break;
           case "BarcodeActivity":
                callBackIntent=new Intent(this,BarcodeActivity.class);
                break;
            default:
                callBackIntent=new Intent(this,Login.class);
                break;
        }

    }
    public void onControlClicked(View v) {
        switch (v.getId()) {
            case R.id.imgMessage:
                startActivity(callBackIntent);
                finish();
                break;
            default:
                break;
        }

    }
    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        Intent intent =new Intent(this,Login.class);
        startActivity(intent);
        finish();
    }
}
