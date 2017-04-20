package hu.uniobuda.nik.visualizer.androidproject2017;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity {
    ListView mainStatList;
    Button mainAddBtn;
    Button mainStatBtn;
    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        mainStatList = (ListView) findViewById(R.id.main_list);
        pDialog = new ProgressDialog(this);

        mainAddBtn = (Button) findViewById(R.id.main_add);
        mainStatBtn = (Button) findViewById(R.id.main_more);
        mainStatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedListItem = mainStatList.getSelectedItem().toString();

                //check for empty data
                if (!selectedListItem.isEmpty()) {
                    checkStatistics(selectedListItem);
                } else {
                    //prompt user to select a list item
                    Toast.makeText(getApplicationContext(), "Please select a repo!", Toast.LENGTH_LONG).show();
                }
            }
        });
        mainAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open repo adder
                Intent myIntent = new Intent(getApplicationContext(), RepoSelecterActivity.class);
                startActivity(myIntent);
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

    private void checkStatistics(final String selected) {
        //tag used to cancel the request
        String tag_string_req = "main_statistics";

        pDialog.setMessage("Getting statistics ...");
        showHideDialog();

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                AppConfig.URL_REPO_STAT,
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
                                // user successfully got statisitcs
                                // Now store the stat in SQLite
                                String uid = jObj.getString("uid");

                                JSONObject stat = jObj.getJSONObject("repo_stat");
                                String auth = stat.getString("author");
                                long totalCommit = stat.getLong("total_commit");
                                String elapsedTime = stat.getString("elapsed_time");
                                String commitWinnerByCount = stat.getString("most_commit_count");
                                String commitWinnerBySize = stat.getString("most_commit_size");
                                String busiestPeriod = stat.getString("busiest_period");
                                String other = stat.getString("other");

                                // Inserting row in users table
                                //db.addStat(statistics..);

                                //launch main activity
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.putExtra("author",auth);
                                intent.putExtra("total_commit",totalCommit);
                                intent.putExtra("elapsed_time",elapsedTime);
                                intent.putExtra("most_commit_count",commitWinnerByCount);
                                intent.putExtra("most_commit_size",commitWinnerBySize);
                                intent.putExtra("busiest_period",busiestPeriod);
                                intent.putExtra("other",other);

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
                params.put("token", "safdm786nb78jlka7895");
                params.put("uid", getIntent().getStringExtra("uid"));
                params.put("repo_id_name", selected);

                return params;
            }
        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
