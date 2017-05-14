package hu.uniobuda.nik.visualizer.androidproject2017.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppConfig;
import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppController;
import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.DBHandler;
import hu.uniobuda.nik.visualizer.androidproject2017.R;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity {
    Button loginBtn;
    TextView logLnk;
    ProgressDialog pDialog;
    TextView mailTextView;
    TextView pswdTextView;
    DBHandler dbHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set def screen to login
        setContentView(R.layout.login);
        dbHandler = new DBHandler(this);

        pDialog = new ProgressDialog(this);
        mailTextView = (TextView) findViewById(R.id.log_mail);
        pswdTextView = (TextView) findViewById(R.id.log_pswd);

        loginBtn = (Button) findViewById(R.id.log_log);
        //////////////////////////////////////////////////////////////

        //Check for already registered user
        Cursor c = dbHandler.loadLastRecordFromTable("Users");
        if (c.getCount() >= 1) {
            String email = c.getString(c.getColumnIndex("Email"));
            String hashdpswd = c.getString(c.getColumnIndex("Password"));

            c.close();
            if (!email.isEmpty() && !hashdpswd.isEmpty()) {
                //try logging in
                Log.d(TAG, "Users Response: fromDB: " + email + " - " + hashdpswd);
                checkLogin(email, hashdpswd);
            }
        }
        //register a new user
        else {
            Intent myIntent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(myIntent);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mailTextView.getText().toString().trim();
                String pswd = pswdTextView.getText().toString().trim();
                //check for empty data in the form
                if (!mail.isEmpty() && !pswd.isEmpty()) {
                    checkLogin(mail, makeSHA1hash(pswd));
                } else {
                    //prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
                }
            }
        });

        //listener for reg new acc link
        logLnk = (TextView) findViewById(R.id.log_link);
        logLnk.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          Intent myIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                                          startActivity(myIntent);
                                      }
                                  }
        );
    }

    private void showHideDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        } else {
            pDialog.dismiss();
        }
    }

    private String makeSHA1hash(String pswd) {

        final MessageDigest digest;
        byte[] result = new byte[0];
        try {
            digest = MessageDigest.getInstance("SHA-1");
            result = digest.digest(pswd.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // Another way to make HEX, my previous post was only the method like your solution
        StringBuilder sb = new StringBuilder();

        for (byte b : result) // Bejárom a result tömböt.
        {
            sb.append(String.format("%02X", b));
        }
        String messageDigest = sb.toString(); //Sha-1 hashed pswd
        return messageDigest;
    }

    private void checkLogin(final String email, final String password) {
        //tag used to cancel the request
        String tag_string_login = "log_login";

        pDialog.setMessage("Logging in ...");
        showHideDialog();

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                AppConfig.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response);
                        showHideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            //check for error node in json
                            if (!error) {
                                // user successfully logged in
                                String uid = jObj.getString("uid");
                                //launch selecter activity
                                Intent intent = new Intent(LoginActivity.this, RepoSelecterActivity.class);
                                intent.putExtra("uid", uid);

                                //check for users in SQLite
                                Cursor c = dbHandler.loadLastRecordFromTable("Users");
                                if (c.getCount() < 1) {
                                    dbHandler.InsertIntoUSERS(
                                            jObj.getJSONObject("user").getString("name"),
                                            email,
                                            password,
                                            Calendar.getInstance().getTime().toString()
                                    );
                                }

                                startActivity(intent);
                            } else {
                                //error in login - error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        showHideDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("token", AppConfig.TOKEN);
                Log.e(TAG, "LOG: " + params);
                return params;
            }

        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_login);
    }

}
