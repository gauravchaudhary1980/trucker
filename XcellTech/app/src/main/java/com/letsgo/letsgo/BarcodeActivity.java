package com.letsgo.letsgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.letsgo.letsgo.com.google.zxing.integration.android.IntentIntegrator;
import com.letsgo.letsgo.com.google.zxing.integration.android.IntentResult;
import org.json.JSONObject;

public class BarcodeActivity extends AppCompatActivity implements OnClickListener{
    ImageView scanBtn;
    TextView contentTxt;
    MessageBO messageBO;
    GlobalVariables globalVariable;
    User user;
    String userName;
    String newScanContent;
    ProgressDialog progressDialog;
    ServiceUtility serviceUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        progressDialog = new ProgressDialog(this);
        serviceUtility = new ServiceUtility();
        scanBtn = (ImageView)findViewById(R.id.scan_button);
        contentTxt = (TextView)findViewById(R.id.scan_content);
        scanBtn.setOnClickListener(this);
        globalVariable=GlobalVariables.getInstance();
        user=globalVariable.getUser();
        userName= user.getName();
    }
    public void onClick(View v){
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            contentTxt.setText(scanContent);
            openNextIntent(scanContent);
        }
        else{
            messageBO=new MessageBO();
            messageBO.setMessageType("fail");
            messageBO.setMessageHeader("Ooops "+ userName +" !");
            messageBO.setMessageDetail("No scan data received!");
            messageBO.setCallBackActivity("BarcodeActivity");
            ShowMessage(messageBO);
        }
    }

    private void openNextIntent(String scanContent)
    {
        if (scanContent.equals("")) {
            messageBO = new MessageBO();
            messageBO.setMessageType("fail");
            messageBO.setMessageHeader("Ooops!");
            messageBO.setMessageDetail("No scan data received!");
            messageBO.setCallBackActivity("BarcodeActivity");
            ShowMessage(messageBO);
            return;
        }
        newScanContent = scanContent;
        new CheckActivityHttpAsyncTask().execute(new String[]{scanContent});
    }
    private void openCreateTourActivity(String scanContent)
    {
        Intent intent=new Intent(this, CreateTour.class);
        intent.putExtra("activity_id",scanContent);
        startActivity(intent);
        finish();
    }
    private void openTaskDetails(ActivityBO pendingActivity)
    {
        Intent intent = new Intent(this, TaskDetail.class);
        intent.putExtra("pending_activity",pendingActivity);
        startActivity(intent);
        finish();
    }

    private void ShowMessage(MessageBO messageBO)
    {
        Intent intent=new Intent(this,Message.class);
        intent.putExtra("messageBO",messageBO);
        startActivity(intent);
        finish();
    }

    private class CheckActivityHttpAsyncTask extends AsyncTask<String, Void, String> {
        private CheckActivityHttpAsyncTask() {
        }

        protected String doInBackground(String... params) {
            return serviceUtility.GET("https://truckerservice.herokuapp.com/activity/" + params[0]);
        }

        protected void onPreExecute() {
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        protected void onPostExecute(String result) {
            try {
                progressDialog.dismiss();
                ActivityBO pendingActivity = null;
                if (!result.equals("")) {
                    JSONObject obj = new JSONObject(result);
                    if (obj.length() > 0) {
                        pendingActivity = new ActivityBO();
                        pendingActivity.setActivity_id(obj.getString("activityId"));
                        pendingActivity.setActivity_type(obj.getString("activityType"));
                        pendingActivity.setUser_id(obj.getString("userId"));
                        pendingActivity.setStart_date_time(obj.getString("startDate"));
                        pendingActivity.setEnd_date_time(obj.getString("endDate"));
                        pendingActivity.setCreated_by(obj.getString("createdBy"));
                        pendingActivity.setModified_by(obj.getString("modifiedBy"));
                        pendingActivity.setStatus(obj.getString("status"));
                    }
                }
                if (pendingActivity != null) {
                    openTaskDetails(pendingActivity);
                } else if (!BarcodeActivity.this.newScanContent.equals("")) {
                    openCreateTourActivity(newScanContent);
                }
            } catch (Exception e) {
                Toast.makeText(BarcodeActivity.this.getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}