package com.letsgo.letsgo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class Login extends AppCompatActivity {
    SharedPreferences settings;
    int authSetting=4;
    GlobalVariables globalVariable;
    MessageBO messageBO;
    AutoCompleteTextView user;
    String androidID;
    ProgressDialog progressDialog;
    ServiceUtility serviceUtility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        serviceUtility = new ServiceUtility();
        ImageView emailSignIn_Button = (ImageView) findViewById(R.id.email_sign_in_button);
        ImageView signUp_Button  =(ImageView)  findViewById(R.id.sign_up);

        globalVariable=GlobalVariables.getInstance();
        FindFormControls();
        androidID= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        emailSignIn_Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidateForm()) {
                    executeSignIn();
                }
            }
        });

        signUp_Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptSignUp();
                openSignUpActivity();
            }
        });

        if (isNetworkAvailable(this)) {
            Toast.makeText(this,"Internet Connected!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Internet Connection not found!", Toast.LENGTH_SHORT).show();
        }
    }
    public void FindFormControls() {
        user=(AutoCompleteTextView)findViewById(R.id.user);

    }
    private void executeSignIn()
    {
        new LoginHttpAsyncTask().execute(new String[]{this.user.getText().toString()});
    }

    private void openMainActivity()
    {
        startActivity(new Intent(this, BarcodeActivity.class));
        finish();
    }
    private void openSignUpActivity()
    {

        startActivity(new Intent(this, SignUp.class));
        finish();
    }


    private void ShowMessage(MessageBO messageBO)
    {
        Intent intent=new Intent(this,Message.class);
        intent.putExtra("messageBO",messageBO);
        startActivity(intent);
        finish();
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private boolean isValidateForm()
    {
        boolean isValid=true;
        String messageString="";

        if(user.getText().toString().equals(""))
        {
            isValid=false;
            messageString="Phone should not be blank!";
        }


        if(!isValid)
        {
            Toast.makeText(this, messageString, Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private class LoginHttpAsyncTask extends AsyncTask<String, Void, String> {
        private LoginHttpAsyncTask() {
        }

        protected String doInBackground(String... params) {
            return serviceUtility.GET("https://truckerservice.herokuapp.com/userdetails/" + params[0]);
        }

        protected void onPreExecute() {
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        protected void onPostExecute(String result) {
            try {
                Login.this.progressDialog.dismiss();
                JSONObject obj = new JSONObject(result);
                if (obj.length() > 0) {
                    User userBO = new User();
                    userBO.setUserid(obj.getString("phone"));
                    userBO.setPhone(obj.getString("phone"));
                    userBO.setName(obj.getString("name"));
                    userBO.setEmail(obj.getString("email"));
                    userBO.setPassword(obj.getString("password"));
                    JSONObject objSetting = new JSONObject(obj.getString("setting"));
                    com.letsgo.letsgo.Settings settings = new com.letsgo.letsgo.Settings();
                    settings.setPhone(objSetting.getString("phone"));
                    settings.setCityCode(objSetting.getString("cityCode"));
                    userBO.setSettings(settings);
                    JSONObject objCity = new JSONObject(obj.getString("city"));
                    CityBO city = new CityBO();
                    city.setCode(objCity.getString("cityCode"));
                    city.setName(objCity.getString("name"));
                    city.setPerson(objCity.getString("person"));
                    city.setAddress(objCity.getString("address"));
                    city.setPhone(objCity.getString("phone"));
                    city.setIscreate(objCity.getBoolean("create"));
                    city.setIsresume(objCity.getBoolean("resume"));
                    city.setIsclose(objCity.getBoolean("close"));
                    userBO.setCity(city);
                    if (Login.this.androidID.equals(userBO.getPassword())) {
                        Login.this.globalVariable.setUser(userBO);
                        switch (authSetting) {
                            case 4:
                                openMainActivity();
                                return;
                            default:
                                openMainActivity();
                                return;
                        }
                    }
                    messageBO = new MessageBO();
                    messageBO.setMessageType("fail");
                    messageBO.setMessageHeader("Ooops!");
                    messageBO.setMessageDetail("This phone no. is registerd to other phone!");
                    messageBO.setCallBackActivity("Login");
                    ShowMessage(messageBO);
                    return;
                }
                messageBO = new MessageBO();
                messageBO.setMessageType("fail");
                messageBO.setMessageHeader("Ooops!");
                messageBO.setMessageDetail("This phone no. is registerd to other phone!");
                messageBO.setCallBackActivity("Login");
                ShowMessage(messageBO);
            } catch (Exception e) {
                //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                messageBO = new MessageBO();
                messageBO.setMessageType("fail");
                messageBO.setMessageHeader("Ooops!");
                messageBO.setMessageDetail("This phone no. is not registerd!");
                messageBO.setCallBackActivity("Login");
                ShowMessage(messageBO);
            }
        }
    }
}
