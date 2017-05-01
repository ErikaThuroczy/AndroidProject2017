package hu.uniobuda.nik.visualizer.androidproject2017.Activities;

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

import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppConfig;
import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppController;
import hu.uniobuda.nik.visualizer.androidproject2017.R;

import static android.content.ContentValues.TAG;


public class RepoAdderActivity extends AppCompatActivity {
    Button repoAddBtn;
    ProgressDialog pDialog;
    TextView gitLocalNameTextView;
    TextView gitunameTextView;
    TextView gitRepoNameTextView;
    TextView gitpswdTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.repo);

        pDialog = new ProgressDialog(this);

        gitLocalNameTextView = (TextView) findViewById(R.id.log_git_local_name);
        gitunameTextView = (TextView) findViewById(R.id.log_git_uname);
        gitRepoNameTextView = (TextView) findViewById(R.id.log_git_repo_name);
        gitpswdTextView = (TextView) findViewById(R.id.log_git_pswd);

        repoAddBtn = (Button) findViewById(R.id.log_git_add);
        repoAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idname = gitLocalNameTextView.getText().toString().trim();
                String uname = gitunameTextView.getText().toString().trim();
                String repoName = gitRepoNameTextView.getText().toString().trim();
                String pswd = gitpswdTextView.getText().toString().trim();

                //check for empty data in the form
                if (!idname.isEmpty() &&!uname.isEmpty() && !repoName.isEmpty() && !pswd.isEmpty()) {
                    checkGitLogin(idname, uname, repoName, pswd);
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

    private void checkGitLogin(final String idname, final String username, final String repoName, final String password) {
        //tag used to cancel the request
        String tag_string_req = "log_git_add";

        pDialog.setMessage("Logging in ...");
        showHideDialog();

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                AppConfig.URL_REPO_ADD,
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
                                // Now store the new item in SQLite
                                String repo_id_name = jObj.getString("repo_id_name");
                                String repo_name = jObj.getString("repo_name");

                                // Inserting row in repo table
                                //db.addRepo(repo);

                                //launch main activity
                                Intent intent = new Intent(RepoAdderActivity.this, RepoSelecterActivity.class);
                                //putextra uid ?
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
                params.put("token", "safdm786nb78jlka7895");
                params.put("uid", getIntent().getStringExtra("uid"));
                params.put("repo_id_name ", idname);
                params.put("repo_name", repoName);
                params.put("repo_user", username);
                params.put("repo_password", password);

                return params;
            }
        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
