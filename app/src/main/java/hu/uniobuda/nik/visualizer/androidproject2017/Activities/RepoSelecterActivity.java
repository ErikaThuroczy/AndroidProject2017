package hu.uniobuda.nik.visualizer.androidproject2017.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
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
import hu.uniobuda.nik.visualizer.androidproject2017.Models.Commit;
import hu.uniobuda.nik.visualizer.androidproject2017.Models.Repo;
import hu.uniobuda.nik.visualizer.androidproject2017.Models.StatisticsAdapter;
import hu.uniobuda.nik.visualizer.androidproject2017.R;

import static android.content.ContentValues.TAG;

public class RepoSelecterActivity extends AppCompatActivity {
    DBHandler db;
    ListView mainStatList;
    Button mainAddBtn;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecter);
        db = new DBHandler(this);

        mainStatList = (ListView) findViewById(R.id.main_list);
        pDialog = new ProgressDialog(this);

        mainAddBtn = (Button) findViewById(R.id.main_add);

        getRepoList();


        mainStatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getAdapter().getItem(position).toString();
                if (!selected.isEmpty()) {
                    getVisualizer(selected);
                }
                Log.d("test", "clicked on list!");
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
                params.put("token", AppConfig.TOKEN);
                params.put("uid", getIntent().getStringExtra("uid"));
                Log.e(TAG, "LIST: " + params);
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
                AppConfig.URL_TEST_REPO_COMMITS,
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
                                // user successfully got visualization data
                                // Now store the stat in SQLite
                                int count = jObj.getInt("count");
                                Gson gson = new GsonBuilder().create();
                                Repo repo = gson.fromJson(String.valueOf(jObj.getJSONObject("repo")), Repo.class);
                                //db.InsertIntoVISUALIZER(visualizer, selected, Calendar.getInstance().getTime().toString());

                                //launch stat activity
                                Intent intent = new Intent(RepoSelecterActivity.this, VisualizerActivity.class);
                                intent.putExtra("uid", getIntent().getStringExtra("uid"));
                                intent.putExtra("selected", selected);//repo_id_name
                                intent.putExtra("count", count);
                                intent.putExtra("repo", repo);

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
                Log.e(TAG, "VIS: " + params);
                return params;
            }
        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_visualizer);
    }

}
