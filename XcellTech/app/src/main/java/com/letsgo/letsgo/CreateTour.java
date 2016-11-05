package com.letsgo.letsgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class CreateTour extends AppCompatActivity  {
    GlobalVariables globalVariable;
    MessageBO messageBO;
    private static final String ACTIVITY_TYPE="Tour";
    TextView timeStamptext;
    TextView userNametext;
    String formattedDate;
    String userID;
    String userName;
    String activityId;
    AutoCompleteTextView vehicle_number;
    AutoCompleteTextView pan_number;
    AutoCompleteTextView quantity;
    AutoCompleteTextView drive_number;
    AutoCompleteTextView gr_number;
    AutoCompleteTextView note;
    TextView selectedcustomerTextView;
    String customerCode;
    ArrayList<CustomerBO> customerList;
    Spinner customer_name;
    Gson gson;
    ProgressDialog progressDialog;
    ServiceUtility serviceUtility;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);
        progressDialog = new ProgressDialog(this);
        ServiceUtility serviceUtility=new ServiceUtility();
        FindFormControls();
        globalVariable=GlobalVariables.getInstance();
        user=globalVariable.getUser();
        userID = user.getUserid();
        userName= user.getName();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateTime = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        formattedDate = currentDateTime.format(calendar.getTime());
        timeStamptext.setText(formattedDate);
        userNametext.setText("Hello! " + userName);
        activityId=getIntent().getExtras().getString("activity_id");
        isAllowCreation();
        new GetCustomerListHttpAsyncTask().execute(new Void[0]);
    }
    public void FindFormControls() {
        timeStamptext=(TextView) findViewById(R.id.timeStamptextView);
        userNametext=(TextView) findViewById(R.id.userNametextView);
        customer_name=(Spinner) findViewById(R.id.customer_name);
        vehicle_number=(AutoCompleteTextView) findViewById(R.id.vehicle_number);
        pan_number=(AutoCompleteTextView) findViewById(R.id.pan_number);
        quantity=(AutoCompleteTextView) findViewById(R.id.quantity);
        drive_number=(AutoCompleteTextView) findViewById(R.id.drive_number);
        gr_number=(AutoCompleteTextView) findViewById(R.id.gr_number);
        note = (AutoCompleteTextView) findViewById(R.id.note);
        selectedcustomerTextView=(TextView) findViewById(R.id.selectedcustomerTextView);
    }


    public void onControlClicked(View v) {
        switch (v.getId()) {
            case R.id.time_in_button:
                if(isValidateForm()) {
                    insertTour();
                }
                break;
            default:
                break;
        }
    }

    private void insertTour()
    {
        TourBO tourBO = new TourBO();
        ActivityBO activityBo = new ActivityBO();
        activityBo.setActivity_type(ACTIVITY_TYPE);
        activityBo.setActivity_id(activityId);
        activityBo.setUser_id(userID);
        activityBo.setStart_date_time(formattedDate);
        activityBo.setEnd_date_time(formattedDate);
        activityBo.setCreated_by(user.getUserid());
        activityBo.setModified_by(user.getUserid());
        activityBo.setStatus("CREATE");
        tourBO.setActivityBO(activityBo);
        tourBO.setCustomerCode(customerCode);
        tourBO.setVehicleNumber(vehicle_number.getText().toString());
        tourBO.setPanNumber(pan_number.getText().toString());
        tourBO.setQuantity(quantity.getText().toString());
        tourBO.setDriverNumber(drive_number.getText().toString());
        tourBO.setGrNumber(gr_number.getText().toString());
        tourBO.setNote(note.getText().toString());
        new CreateTourHttpAsyncTask().execute(new TourBO[]{tourBO});
    }
    private void isAllowCreation()
    {
        if (!user.getCity().iscreate()) {
            messageBO = new MessageBO();
            messageBO.setMessageType("fail");
            messageBO.setMessageHeader("Ooops! " + userName + " !");
            messageBO.setMessageDetail("Your are not allowed to create trip!");
            messageBO.setCallBackActivity("BarcodeActivity");
            ShowMessage(messageBO);
        }
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
    private boolean isValidateForm()
    {
        boolean isValid=true;
        String messageString="";

        if(activityId.equals(""))
        {
            isValid=false;
            messageString="Please capture bar code!";
        }

        else if(vehicle_number.getText().toString().equals(""))
        {
            isValid=false;
            messageString="Vehicle number should not be blank!";
        }

        if(!isValid)
        {
            Toast.makeText(this, messageString, Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private void setCustomer() {
        //fill data in spinner
        ArrayAdapter<CustomerBO> adapter = new ArrayAdapter<CustomerBO>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, customerList);
        customer_name.setAdapter(adapter);

        customer_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                CustomerBO customer = (CustomerBO) parent.getSelectedItem();
                selectedcustomerTextView.setText(customer.getName());
                customerCode=customer.getCustomerCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private class GetCustomerListHttpAsyncTask extends AsyncTask<Void, Void, String> {
        private GetCustomerListHttpAsyncTask() {
        }

        protected String doInBackground(Void... params) {
            return serviceUtility.GET("https://truckerservice.herokuapp.com/customers");
        }

        protected void onPreExecute() {
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        protected void onPostExecute(String result) {
            try {
                customerList = new ArrayList();
                JSONArray obj = new JSONArray(result);
                for (int i = 0; i < obj.length(); i++) {
                    JSONObject d = obj.getJSONObject(i);
                    CustomerBO customerBO = new CustomerBO();
                    customerBO.setCustomerCode(d.getString("customerCode"));
                    customerBO.setName(d.getString("name"));
                    customerBO.setAddress1(d.getString("address1"));
                    customerBO.setAddress2(d.getString("address2"));
                    customerBO.setCity(d.getString("city"));
                    customerBO.setPhone1(d.getString("phone1"));
                    customerBO.setPhone2(d.getString("phone2"));
                    customerBO.setEmail1(d.getString("email1"));
                    customerBO.setEmail2(d.getString("email2"));
                    customerList.add(customerBO);
                }
                progressDialog.dismiss();
                setCustomer();
            } catch (Exception e) {
                Toast.makeText(CreateTour.this.getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class CreateTourHttpAsyncTask extends AsyncTask<TourBO, Void, String> {
        private CreateTourHttpAsyncTask() {
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
                Toast.makeText(CreateTour.this.getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return serviceUtility.POST("https://truckerservice.herokuapp.com/activity", activityJsonObject);
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
                messageBO.setMessageDetail("Your can start journey now!");
                messageBO.setCallBackActivity("BarcodeActivity");
                CreateTour.this.ShowMessage(messageBO);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
