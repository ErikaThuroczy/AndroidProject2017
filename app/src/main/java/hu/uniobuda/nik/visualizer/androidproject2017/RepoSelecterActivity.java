package hu.uniobuda.nik.visualizer.androidproject2017;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class RepoSelecterActivity extends AppCompatActivity {
    Button repoAddBtn;
    ProgressDialog pDialog;
    TextView gitunameTextView;
    TextView gitUrlTextView;
    TextView gitpswdTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.repo);

        pDialog = new ProgressDialog(this);

        gitunameTextView = (TextView) findViewById(R.id.log_git_uname);
        gitUrlTextView = (TextView) findViewById(R.id.log_git_url);
        gitpswdTextView = (TextView) findViewById(R.id.log_git_pswd);

        repoAddBtn = (Button) findViewById(R.id.log_git_add);
        repoAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = gitunameTextView.getText().toString().trim();
                String url = gitUrlTextView.getText().toString().trim();
                String pswd = gitpswdTextView.getText().toString().trim();

                //check for empty data in the form
                if (!uname.isEmpty() && !url.isEmpty() && !pswd.isEmpty()) {
                    checkGitLogin(uname, url, pswd);
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

    private void checkGitLogin(final String username, final String url, final String password) {
        //tag used to cancel the request
        String tag_string_req = "log_git_add";

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
                                // Create login session
                                //session.setLogin(true);

                                // Now store the repo in SQLite
                                String uid = jObj.getString("uid");

                                JSONObject repo = jObj.getJSONObject("repo");
                                String name = repo.getString("name");

                                // Inserting row in repo table
                                //db.addRepo(repo);

                                //launch main activity
                                Intent intent = new Intent(RepoSelecterActivity.this, MainActivity.class);
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
                //posting parameters to git login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("url", url);
                params.put("password", password);

                return params;
            }
        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
