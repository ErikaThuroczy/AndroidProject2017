package hu.uniobuda.nik.visualizer.androidproject2017.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppConfig;
import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppController;
import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.DBHandler;
import hu.uniobuda.nik.visualizer.androidproject2017.Models.Repo;
import hu.uniobuda.nik.visualizer.androidproject2017.Models.StatisticsAdapter;
import hu.uniobuda.nik.visualizer.androidproject2017.Models.Statistics;
import hu.uniobuda.nik.visualizer.androidproject2017.R;

import static android.content.ContentValues.TAG;

public class RepoSelecterActivity extends AppCompatActivity {
    DBHandler db;
    ListView mainStatList;
    Button mainAddBtn;
    Button mainStatBtn;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecter);
        db = new DBHandler(this);

        mainStatList = (ListView) findViewById(R.id.main_list);
        pDialog = new ProgressDialog(this);

        mainAddBtn = (Button) findViewById(R.id.main_add);
        mainStatBtn = (Button) findViewById(R.id.main_more);

        getRepoList();


        mainStatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("test", "clicked on list!");
                /*
                getVisualizer(mainStatList.getSelectedItem().toString());
                GitData data = //get gitData;

                Intent intent = new Intent(RepoSelecterActivity.this, VisualizerActivity.class);
                intent.putExtra("git_data", data);
                startActivity(intent);*/
            }
        });


        mainStatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedListItem = "androidos";//mainStatList.getSelectedItem().toString();
                //checkStatistics("teszt");
                //check for empty data
                if (true) {//!selectedListItem.isEmpty()) {
                    //db has stat info
                    Log.d(TAG, "Statistics Response: fromDB");
                    Statistics stat = new Statistics("", "0", "", "", "", "", "");
                    Cursor c = db.getStatByIdName(selectedListItem);
                    while (c.moveToNext()) {
                        String author = c.getString(c.getColumnIndex("Author"));
                        String totalCommit = c.getString(c.getColumnIndex("TotalCommit"));
                        String elapsedTime = c.getString(c.getColumnIndex("ElapsedTime"));
                        String mostCommitCount = c.getString(c.getColumnIndex("MostCommitCount"));
                        String mostCommitSize = c.getString(c.getColumnIndex("MostCommitSize"));
                        String busiestPeriod = c.getString(c.getColumnIndex("BusiestPeriod"));
                        String other = c.getString(c.getColumnIndex("Other"));
                        stat = new Statistics(author, totalCommit, elapsedTime, mostCommitCount, mostCommitSize, busiestPeriod, other);
                    }
                    c.close();
                    if (!stat.getAuthor().isEmpty()) {
                        //launch stat activity
                        Intent intent = new Intent(RepoSelecterActivity.this, StatisticsActivity.class);
                        intent.putExtra("repo_stat", stat);
                        Log.d(TAG, "Statistics fromDBauth: " + stat.getAuthor());

                        startActivity(intent);
                        finish();
                    } else {
                        checkStatistics(selectedListItem);
                    }
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
                Intent myIntent = new Intent(getApplicationContext(), RepoAdderActivity.class);
                myIntent.putExtra("uid", getIntent().getStringExtra("uid"));
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

    private void getRepoList() {
        //tag used to cancel the request
        String tag_string_req = "main_repo_list";

        pDialog.setMessage("Loading repository list ...");
        showHideDialog();

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                AppConfig.URL_REPO_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "RepoList Response: " + response);
                        showHideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            //check for error node in json
                            if (!error) {
                                // user successfully got repolist
                                // Now store the repolist in SQLite

                                JSONArray repos = jObj.getJSONArray("repos");
                                int count = jObj.getInt("count");

                                ArrayList<String> arrayList = new ArrayList<>();
                                for (int i = 0; i < count; i++) {
                                    Log.d("repo", repos.getJSONObject(i).getString("repo_id_name"));
                                    db.InsertIntoREPOLIST(repos.getJSONObject(i).getString("repo_id_name"), Calendar.getInstance().getTime().toString());
                                    arrayList.add(repos.getJSONObject(i).getString("repo_id_name"));
                                }
                                StatisticsAdapter adapter = new StatisticsAdapter(arrayList);
                                mainStatList.setAdapter(adapter);
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
                        Log.e(TAG, "RepoList Error: " + error.getMessage());
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

                return params;
            }
        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
                        Log.d(TAG, "Statistics Response: " + response);
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
                                db.InsertIntoSTAT(stat, selected, Calendar.getInstance().getTime().toString());

                                //launch stat activity
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
                params.put("token", AppConfig.TOKEN);
                params.put("uid", getIntent().getStringExtra("uid"));
                params.put("repo_id_name", selected);

                return params;
            }
        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getVisualizer(final String selected) {
        //tag used to cancel the request
        String tag_string_visualizer = "main_visualizer";

        pDialog.setMessage("Getting visualization data ...");
        showHideDialog();

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                AppConfig.URL_REPO_COMMITS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Visualizer Response: " + response);
                        showHideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            //check for error node in json
                            if (!error) {
                                // user successfully got statisitcs
                                // Now store the stat in SQLite
                                int count = jObj.getInt("count");
                                /** > Success
                                {
                                    "tag":"repo_commits",
                                    "error":false,
                                    "repo_id_name":<repo_id_name>,
                                    "from":"2017-01-01 00:00:00",
                                    "to":"2017-01-01 01:00:00",
                                    "count":7,
                                    "commits":
                                        [
                                            {<commit data...>},
                                            {<commit data...>},
                                        ]
                                }**/


                                Gson gson = new GsonBuilder().create();
                                Repo visualizer = gson.fromJson(String.valueOf(jObj.getJSONObject("commits")), Repo.class);
                                //db.InsertIntoVISUALIZER(visualizer, selected, Calendar.getInstance().getTime().toString());

                                //launch stat activity
                                Intent intent = new Intent(RepoSelecterActivity.this, VisualizerActivity.class);
                                intent.putExtra("visualizer", visualizer);
                                Log.d(TAG, "Visualizer : " + visualizer.getRepoIdName());

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
                        Log.e(TAG, "Visualizer Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        showHideDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(0L)); // mindate
                //posting parameters to url
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", AppConfig.TOKEN);
                params.put("uid", getIntent().getStringExtra("uid"));
                params.put("repo_id_name", selected);
                params.put("from", cal.getTime().toString());
                params.put("to", Calendar.getInstance().getTime().toString());

                return params;
            }
        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_visualizer);
    }

}
