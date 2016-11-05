package com.letsgo.letsgo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {
    //SQLiteDatabaseHelper helper=new SQLiteDatabaseHelper(this);
    Button signUp_Button;
    AutoCompleteTextView name;
    AutoCompleteTextView phone;
    AutoCompleteTextView email;
    MessageBO messageBO;
    TextView imeiNumber;
    String androidID;
    Spinner cityCodeSpinner;
    TextView selectedCityTextView;
    String cityCode;
    ArrayList<CityBO> cityList;
    ServiceUtility serviceUtility;
    Gson gson;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressDialog=new ProgressDialog(this);
        gson = new Gson();
        serviceUtility = new ServiceUtility();
        if (isConnected()) {
            FindFormControls();
            androidID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            String warningText = " Hey! Your phone number will be register to this phone only.";
            imeiNumber.setText(warningText);
            signUp_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //attemptSignUp();
                    createSignUp();
                }
            });
            //setCity();
            new GetCityListHttpAsyncTask().execute(new Void[0]);
        }
    }
    public void FindFormControls() {
        signUp_Button = (Button) findViewById(R.id.lets_go_button);
        name=(AutoCompleteTextView)findViewById(R.id.name);
        phone=(AutoCompleteTextView)findViewById(R.id.phone);
        email=(AutoCompleteTextView)findViewById(R.id.email);
        imeiNumber = (TextView) findViewById(R.id.imeiNumberTextView);
        cityCodeSpinner=(Spinner) findViewById(R.id.cityCodeSpinner);
        selectedCityTextView=(TextView) findViewById(R.id.selectedCityTextView);
    }
    public void createSignUp()
    {
        String name_txt=name.getText().toString();
        String phone_txt=phone.getText().toString();
        String email_txt=email.getText().toString();

        if (isValidateForm())
        {
            User user = new User();
            user.setName(name_txt);
            user.setPhone(phone_txt);
            user.setEmail(email_txt);
            user.setPassword(androidID);
            user.setActive(Boolean.valueOf(true));
            com.letsgo.letsgo.Settings settings=new com.letsgo.letsgo.Settings();
            settings.setPhone(phone_txt);
            settings.setCityCode(cityCode);
            user.setSettings(settings);
            new CreateUserHttpAsyncTask().execute(new User[]{user});
        }

    }
    private void ShowMessage(MessageBO messageBO)
    {
        Intent intent=new Intent(this,Message.class);
        intent.putExtra("messageBO",messageBO);
        startActivity(intent);
        finish();
    }
    private boolean isValidateForm()
    {
        boolean isValid=true;
        String messageString="";

        if(name.getText().toString().equals(""))
        {
            isValid=false;
            messageString="Name should not be blank!";
        }
        else if(phone.getText().toString().equals(""))
        {
            isValid=false;
            messageString="Phone should not be blank!";
        }
        else if(email.getText().toString().equals(""))
        {
            isValid=false;
            messageString="Email should not be blank!";
        }
        if(!isValid)
        {
            Toast.makeText(this, messageString, Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private void setCity() {

        //fill data in spinner
        ArrayAdapter<CityBO> adapter = new ArrayAdapter<CityBO>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, cityList);
        cityCodeSpinner.setAdapter(adapter);

        cityCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                CityBO city = (CityBO) parent.getSelectedItem();
                selectedCityTextView.setText(city.getName());
                cityCode=city.getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent =new Intent(this,Login.class);
        startActivity(intent);
        finish();
    }

    public boolean isConnected() {
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return false;
        }
        return true;
    }
    private class GetCityListHttpAsyncTask extends AsyncTask<Void, Void, String> {
        private GetCityListHttpAsyncTask() {
        }

        protected String doInBackground(Void... params) {
            //ServiceUtility serviceUtility = serviceUtility;
            return serviceUtility.GET("https://truckerservice.herokuapp.com/cities");
        }

        protected void onPreExecute() {
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
            SignUp.this.progressDialog.show();
        }

        protected void onPostExecute(String result) {
            try {
                cityList = new ArrayList();
                JSONArray obj = new JSONArray(result);
                for (int i = 0; i < obj.length(); i++) {
                    JSONObject d = obj.getJSONObject(i);
                    CityBO cityBO = new CityBO();
                    cityBO.setCode(d.getString("cityCode"));
                    cityBO.setName(d.getString("name"));
                    cityBO.setPerson(d.getString("person"));
                    cityBO.setAddress(d.getString("address"));
                    cityBO.setPhone(d.getString("phone"));
                    cityBO.setIscreate(d.getBoolean("create"));
                    cityBO.setIsresume(d.getBoolean("resume"));
                    cityBO.setIsclose(d.getBoolean("close"));
                    cityList.add(cityBO);
                }
                progressDialog.dismiss();
                setCity();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreateUserHttpAsyncTask extends AsyncTask<User, Void, String> {
        private CreateUserHttpAsyncTask() {
        }

        protected String doInBackground(User... user) {
            JsonObject userJsonObject = new JsonObject();
            try {
                userJsonObject.addProperty("phone", user[0].getPhone());
                userJsonObject.addProperty("name", user[0].getName());
                userJsonObject.addProperty("email", user[0].getEmail());
                userJsonObject.addProperty("password", user[0].getPassword());
                JsonObject settingObject = new JsonObject();
                com.letsgo.letsgo.Settings settings = user[0].getSettings();
                settingObject.addProperty("phone", settings.getPhone());
                settingObject.addProperty("cityCode", settings.getCityCode());
                userJsonObject.add("setting", settingObject);
            } catch (Exception e) {
                Toast.makeText(SignUp.this.getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            ServiceUtility serviceUtility = SignUp.this.serviceUtility;
            return ServiceUtility.POST("https://truckerservice.herokuapp.com/user", userJsonObject);
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
                messageBO.setMessageDetail("Your login has been created.");
                messageBO.setCallBackActivity("Login");
                ShowMessage(messageBO);
            } catch (Exception e) {
                Toast.makeText(SignUp.this.getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}


