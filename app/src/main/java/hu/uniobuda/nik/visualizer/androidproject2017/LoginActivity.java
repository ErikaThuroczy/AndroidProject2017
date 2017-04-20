package hu.uniobuda.nik.visualizer.androidproject2017;

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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mailTextView.getText().toString().trim();
                String pswd = pswdTextView.getText().toString().trim();

                //check for empty data in the form
                if (!mail.isEmpty() && !pswd.isEmpty()) {
                    checkLogin(mail, pswd);
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

    private void checkLogin(final String email, final String password) {
        //tag used to cancel the request
        String tag_string_req = "log_login";

        pDialog.setMessage("Logging in ...");
        showHideDialog();

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                AppConfig.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        showHideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            //check for error node in json
                            if (!error) {
                                // user successfully logged in
                                // Now store the user in SQLite
                                String uid = jObj.getString("uid");

                                JSONObject user = jObj.getJSONObject("user");
                                String name = user.getString("name");
                                String email = user.getString("email");
                                String created_at = Calendar.getInstance().getTime().toString();

                                // Inserting row in users table
                                //db.addUser(name, email, uid, created_at);
                                /*if(!name.isEmpty() && !uid.isEmpty()){
                                    long id = dbHandler.insertUser(name,uid); //add date too
                                    Log.d("DB", "New user: " + id);
                                }*/

                                //launch main activity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("uid",uid);

                                startActivity(intent);
                                finish();
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
                params.put("token", "safdm786nb78jlka7895");

                return params;
            }

        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
