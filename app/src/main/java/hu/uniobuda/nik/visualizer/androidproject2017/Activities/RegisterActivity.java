package hu.uniobuda.nik.visualizer.androidproject2017.Activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class RegisterActivity extends Activity {
    Button regBtn;
    TextView regLink;
    ProgressDialog pDialog;

    DBHandler dbHandler;

    TextView unameTextView;
    TextView mailTextView;
    TextView pswdTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        dbHandler = new DBHandler(this);

        unameTextView = (TextView) findViewById(R.id.reg_uname);
        mailTextView = (TextView) findViewById(R.id.reg_mail);
        pswdTextView = (TextView) findViewById(R.id.reg_pswd);

        pDialog = new ProgressDialog(this);

        regLink = (TextView) findViewById(R.id.reg_link);
        regBtn = (Button) findViewById(R.id.reg_reg);


        pDialog.setCancelable(false);

        regLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = mailTextView.getText().toString();
                String mail = mailTextView.getText().toString().trim();
                String pswd = pswdTextView.getText().toString().trim();

                //check for empty data in the form
                if (!uname.isEmpty() && !mail.isEmpty() && !pswd.isEmpty()) {
                    checkRegister(uname, mail, pswd);
                } else {
                    //prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void showHideDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        } else {
            pDialog.dismiss();
        }
    }

    private String makeSHA1hash(String pswd){

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

    private void checkRegister(final String username, final String email, final String password) {
        //tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showHideDialog();

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                AppConfig.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Register Response: " + response.toString());
                        showHideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            //check for error node in json
                            if (!error) {
                                // user successfully registered in
                                // Now store the user in SQLite
                                String uid = jObj.getString("uid");

                                JSONObject user = jObj.getJSONObject("user");
                                String name = user.getString("name");
                                String email = user.getString("email");
                                String created_at = Calendar.getInstance().getTime().toString();

                                // Inserting row in users table
                                //db.addUser(name, email, uid, created_at);
                                /*if (!name.isEmpty() && !uid.isEmpty()) {
                                    long id = dbHandler.insertUser(name, uid); //add date too
                                    Log.d("DB", "New user: " + id);
                                }*/

                                //launch selecter activity
                                //Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                Intent intent = new Intent(RegisterActivity.this, RepoSelecterActivity.class);
                                intent.putExtra("uid",uid);

                                startActivity(intent);
                                finish();
                            } else {
                                //error in register - error message
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
                        Log.e(TAG, "Register Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        showHideDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "safdm786nb78jlka7895");
                params.put("name", username);
                params.put("email", email);
                params.put("password", makeSHA1hash(password));

                return params;
            }

        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
