package com.example.tomipc.foodzye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomipc.foodzye.model.User;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class loginActivity extends AppCompatActivity  {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    UserLocalStore userLocalStore;
    TextInputEditText EmailText, PasswordText;
    Button LoginButton;
    TextView SignupLink, ResetPasswordLink;
    ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        EmailText = (TextInputEditText) findViewById(R.id.input_email);
        PasswordText = (TextInputEditText) findViewById(R.id.input_password);
        LoginButton = (Button) findViewById(R.id.btn_login);
        SignupLink = (TextView) findViewById(R.id.link_signup);
        ResetPasswordLink = (TextView) findViewById(R.id.link_reset_password);

        userLocalStore = new UserLocalStore(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        SignupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        ResetPasswordLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        LoginButton.setEnabled(false);

        progressDialog = new ProgressDialog(loginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = EmailText.getText().toString();
        String password = PasswordText.getText().toString();

        HashMap postData = new HashMap();
        postData.put("email", email);
        postData.put("password", password);

        PostResponseAsyncTask task = new PostResponseAsyncTask(loginActivity.this, postData , new AsyncResponse() {
            @Override
            public void processFinish(String result) {
                if(result.equals("fail")) {
                    progressDialog.dismiss();
                    onLoginFailed();
                }
                else{
                    try {
                        JSONArray obj = new JSONArray(result);
                        JSONObject jObject = obj.getJSONObject(0);
                        String name = jObject.getString("name");
                        String email = jObject.getString("email");
                        int role = jObject.getInt("role");
                        String user_slug = jObject.getString("slug");
                        String user_picture = jObject.getString("user_picture");
                        String location = jObject.getString("location");
                        String phone = jObject.getString("phone");
                        String work_time = jObject.getString("work_time");
                        String description = jObject.getString("description");
                        float rate = (float)jObject.getDouble("rate_total");
                        int user_id = jObject.getInt("id");
                        User user = new User(user_id, name, user_slug, email, role, location, phone, work_time, user_picture, description, rate);
                        logUserIn(user);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    Toast.makeText(loginActivity.this, "You have successfully logged in.", Toast.LENGTH_LONG).show();
                    onLoginSuccess();
                }
            }
        });

        task.execute(Database.URL+"login");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }


    public void onLoginSuccess() {
        LoginButton.setEnabled(true);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        LoginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = EmailText.getText().toString();
        String password = PasswordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EmailText.setError("Enter a valid email address.");
            valid = false;
        } else {
            EmailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 5 || password.length() > 15) {
            PasswordText.setError("Your password must be between 5 and 15 alphanumeric characters.");
            valid = false;
        } else {
            PasswordText.setError(null);
        }

        return valid;
    }

    private void logUserIn(User user) {
        userLocalStore.storeUserData(user);
        userLocalStore.setUserLoggedIn(true);
    }

}