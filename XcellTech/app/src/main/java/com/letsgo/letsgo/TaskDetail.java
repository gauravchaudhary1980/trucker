package com.letsgo.letsgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskDetail extends AppCompatActivity {

    //Activity List
    MessageBO messageBO;
    ActivityBO activityBO;
    TextView txtTaskDetails;
    Button  btnResume;
    Button  btnComplete;
    Button  btnCancle;
    TextView timeStamptext;
    TextView userNametext;
    String formattedDate;

    String detailsText;
    GlobalVariables globalVariable;
    User user;
    String userName;
    TourBO tourBO;
    String currentStatus;
    Gson gson;
    ProgressDialog progressDialog;
    ServiceUtility serviceUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        progressDialog=new ProgressDialog(this);
        gson = new Gson();
        serviceUtility = new ServiceUtility();
        FindFormControls();
        globalVariable=GlobalVariables.getInstance();
        user=globalVariable.getUser();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateTime = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        formattedDate = currentDateTime.format(calendar.getTime());
        timeStamptext.setText(formattedDate);

        activityBO = (ActivityBO)getIntent().getSerializableExtra("pending_activity");
         new CheckActivityHttpAsyncTask().execute(new String[]{this.activityBO.getActivity_id()});

    }
    private void FillDetials()
    {
        userName = user.getName();
        userNametext.setText("Hello! " + userName);
        detailsText = "Details not found!";
        if (tourBO != null) {
            detailsText = "Customer name : " + tourBO.getCustomerCode().toString() + "\n" +
                          "PAN Number : " + tourBO.getPanNumber().toString() + "\n" +
                          "Quantity : " + tourBO.getQuantity().toString() + "\n" +
                          "Vehicle No : " + tourBO.getVehicleNumber().toString() + "\n" +
                          "Driver No : " + tourBO.getDriverNumber().toString() + "\n" +
                          "GR No : " + tourBO.getGrNumber().toString() + "\n" +
                          "Start Date : " + tourBO.getActivityBO().getStart_date_time().toString() + "\n" +
                          "note : " + tourBO.getNote().toString() + "\n" +
                          "Status : " + tourBO.getActivityBO().getStatus().toString();

            currentStatus = tourBO.getActivityBO().getStatus().toString();

            if (currentStatus.equals("COMPLETE") || currentStatus.equals("CANCEL"))
            {
                detailsText=detailsText + "\n" + "End Date : " + tourBO.getActivityBO().getEnd_date_time().toString();
            }
        }
        txtTaskDetails.setText(detailsText);
    }
    public void FindFormControls() {
        txtTaskDetails=(TextView) findViewById(R.id.TaskDetailTextView);
        txtTaskDetails.setEnabled(false);
        btnResume=(Button) findViewById(R.id.resume_button);
        btnComplete=(Button) findViewById(R.id.complete_button);
        btnCancle=(Button) findViewById(R.id.cancle_button);
        timeStamptext=(TextView) findViewById(R.id.timeStamptextView);
        userNametext=(TextView) findViewById(R.id.userNametextView);

    }

    public void onControlClicked(View v) {
        switch (v.getId()) {
            case R.id.resume_button:
                UpdateTask("RESUME");
                break;
            case R.id.cancle_button:
                UpdateTask("CANCEL");
                break;
            case R.id.complete_button:
                UpdateTask("COMPLETE");
                break;
            default:
                break;
        }
    }
    private void UpdateTask(String operationType){
        boolean isAllowed=false;
        int i = -1;
        switch (operationType) {
            case "RESUME":
                isAllowed = this.user.getCity().isresume();
                tourBO.getActivityBO().setModified_by(this.formattedDate);
                tourBO.getActivityBO().setStatus(operationType);
                tourBO.getActivityBO().setModified_by(this.user.getUserid());
                break;
            case "CANCEL":
                isAllowed = true;
                tourBO.getActivityBO().setModified_by(this.formattedDate);
                tourBO.getActivityBO().setStatus(operationType);
                tourBO.getActivityBO().setModified_by(this.user.getUserid());
                break;
            case "COMPLETE":
                isAllowed = this.user.getCity().isclose();
                tourBO.getActivityBO().setEnd_date_time(this.formattedDate);
                tourBO.getActivityBO().setModified_by(this.formattedDate);
                tourBO.getActivityBO().setStatus(operationType);
                tourBO.getActivityBO().setModified_by(this.user.getUserid());
                break;
            default:
                break;
        }

        if (!isAllowed) {
            showWarning(operationType);
        } else if (currentStatus.equals("COMPLETE")) {
            messageBO = new MessageBO();
            messageBO.setMessageType("fail");
            messageBO.setMessageHeader("Ooops! " + this.userName);
            messageBO.setMessageDetail("Trip has been closed.");
            messageBO.setCallBackActivity("BarcodeActivity");
            ShowMessage(this.messageBO);
        } else {
            new UpdateTourHttpAsyncTask().execute(new TourBO[]{this.tourBO});
        }

    }
    private void showWarning(String operationType)
    {
            messageBO=new MessageBO();
            messageBO.setMessageType("fail");
            messageBO.setMessageHeader("Ooops! "+ userName +" !");
            messageBO.setMessageDetail("Your are not allowed to "+ operationType.toLowerCase() +" trip!");
            messageBO.setCallBackActivity("BarcodeActivity");
            ShowMessage(messageBO);
        }
    private void ShowMessage(MessageBO messageBO)
    {
        Intent intent=new Intent(this,Message.class);
        intent.putExtra("messageBO",messageBO);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent =new Intent(this,BarcodeActivity.class);
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
                if (!result.equals("")) {
                    JSONObject obj = new JSONObject(result);
                    if (obj.length() > 0) {
                        ActivityBO pendingActivity = new ActivityBO();
                        pendingActivity.setActivity_id(obj.getString("activityId"));
                        pendingActivity.setActivity_type(obj.getString("activityType"));
                        pendingActivity.setUser_id(obj.getString("userId"));
                        pendingActivity.setStart_date_time(obj.getString("startDate"));
                        pendingActivity.setEnd_date_time(obj.getString("endDate"));
                        pendingActivity.setCreated_by(obj.getString("createdBy"));
                        pendingActivity.setModified_by(obj.getString("modifiedBy"));
                        pendingActivity.setStatus(obj.getString("status"));
                        JSONObject objTour = new JSONObject(obj.getString("tour"));
                        tourBO = new TourBO();
                        tourBO.setCustomerCode(objTour.getString("customerCode"));
                        tourBO.setPanNumber(objTour.getString("panNumber"));
                        tourBO.setQuantity(objTour.getString("quantity"));
                        tourBO.setVehicleNumber(objTour.getString("vehicleNumber"));
                        tourBO.setDriverNumber(objTour.getString("driverNumber"));
                        tourBO.setGrNumber(objTour.getString("grNumber"));
                        tourBO.setNote(objTour.getString("note"));
                        tourBO.setActivityBO(pendingActivity);
                    }
                }
                TaskDetail.this.FillDetials();
            } catch (Exception e) {
                Toast.makeText(TaskDetail.this.getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class UpdateTourHttpAsyncTask extends AsyncTask<TourBO, Void, String> {
        private UpdateTourHttpAsyncTask() {
        }

        protected String doInBackground(TourBO... tour) {
            JsonObject activityJsonObject = new JsonObject();
            try {
                ActivityBO activityBO = tour[0].getActivityBO();
                activityJsonObject.addProperty("activityId", activityBO.getActivity_id());
                activityJsonObject.addProperty("activityType", activityBO.getActivity_type());
                activityJsonObject.addProperty("userId", activityBO.getUser_id());
                activityJsonObject.addProperty("startDate", activityBO.getStart_date_time());
                activityJsonObject.addProperty("endDate", activityBO.getEnd_date_time());
                activityJsonObject.addProperty("createdBy", activityBO.getCreated_by());
                activityJsonObject.addProperty("modifiedBy", activityBO.getModified_by());
                activityJsonObject.addProperty("status", activityBO.getStatus());
                JsonObject tourObject = new JsonObject();
                tourObject.addProperty("activityId", activityBO.getActivity_id());
                tourObject.addProperty("customerCode", tour[0].getCustomerCode());
                tourObject.addProperty("panNumber", tour[0].getPanNumber());
                tourObject.addProperty("quantity", tour[0].getQuantity());
                tourObject.addProperty("vehicleNumber", tour[0].getVehicleNumber());
                tourObject.addProperty("driverNumber", tour[0].getDriverNumber());
                tourObject.addProperty("grNumber", tour[0].getGrNumber());
                tourObject.addProperty("note", tour[0].getNote());
                activityJsonObject.add("tour", tourObject);
            } catch (Exception e) {
                Toast.makeText(TaskDetail.this.getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return serviceUtility.PUT("https://truckerservice.herokuapp.com/activity", activityJsonObject);
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
                messageBO = new MessageBO();
                messageBO.setMessageType("success");
                messageBO.setMessageHeader("Congratulations!");
                messageBO.setMessageDetail("Status has been changed!");
                messageBO.setCallBackActivity("BarcodeActivity");
                ShowMessage(TaskDetail.this.messageBO);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
