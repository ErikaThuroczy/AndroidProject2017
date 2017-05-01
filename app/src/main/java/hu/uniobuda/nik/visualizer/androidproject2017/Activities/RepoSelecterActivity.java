package hu.uniobuda.nik.visualizer.androidproject2017.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppConfig;
import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppController;
import hu.uniobuda.nik.visualizer.androidproject2017.Models.Statistics;
import hu.uniobuda.nik.visualizer.androidproject2017.R;

import static android.content.ContentValues.TAG;

public class RepoSelecterActivity extends AppCompatActivity {
    ListView mainStatList;
    Button mainAddBtn;
    Button mainStatBtn;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mainStatList = (ListView) findViewById(R.id.main_list);
        pDialog = new ProgressDialog(this);

        mainAddBtn = (Button) findViewById(R.id.main_add);
        mainStatBtn = (Button) findViewById(R.id.main_more);

        mainStatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("test","clicked on list!");
                /*
                GitData data = //get gitData;

                Intent intent = new Intent(RepoSelecterActivity.this, VisualizerActivity.class);
                intent.putExtra("git_data", data);
                startActivity(intent);*/
            }
        });


        mainStatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String selectedListItem = mainStatList.getSelectedItem().toString();
                checkStatistics("teszt");
                /*/check for empty data
                if (!selectedListItem.isEmpty()) {
                    checkStatistics(selectedListItem);
                }
                if {
                    //prompt user to select a list item
                    Toast.makeText(getApplicationContext(), "Please select a repo!", Toast.LENGTH_LONG).show();
                }*/
            }
        });
        mainAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open repo adder
                Intent myIntent = new Intent(getApplicationContext(), RepoAdderActivity.class);
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
                        Log.d(TAG, "Statistics Response: " + response.toString());
                        showHideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            //check for error node in json
                            if (!error) {
                                // user successfully got statisitcs
                                // Now store the stat in SQLite
                                String uid = jObj.getString("uid");

                                Gson gson = new GsonBuilder().create();
                                Statistics stat = gson.fromJson(String.valueOf(jObj.getJSONObject("repo_stat")), Statistics.class);

                                //launch main activity
                                Intent intent = new Intent(RepoSelecterActivity.this, StatisticsActivity.class);
                                intent.putExtra("repo_stat", stat);
                                Log.d(TAG, "Statistics : " + stat.getAuthor());

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
                        Log.e(TAG, "Statistics Error: " + error.getMessage());
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
