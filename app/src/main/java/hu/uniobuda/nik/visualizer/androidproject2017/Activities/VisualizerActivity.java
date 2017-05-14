package hu.uniobuda.nik.visualizer.androidproject2017.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppConfig;
import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.AppController;
import hu.uniobuda.nik.visualizer.androidproject2017.Helpers.DBHandler;
import hu.uniobuda.nik.visualizer.androidproject2017.Models.Repo;
import hu.uniobuda.nik.visualizer.androidproject2017.Models.Statistics;
import hu.uniobuda.nik.visualizer.androidproject2017.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import hu.uniobuda.nik.visualizer.androidproject2017.Models.LineChartView;

import static android.content.ContentValues.TAG;

public class VisualizerActivity extends AppCompatActivity {
    Button mainStatBtn;
    ProgressDialog pDialog;
    DBHandler db;
    Repo data;

    int counter = 0;
    int total = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualizer);

        db = new DBHandler(this);
        pDialog = new ProgressDialog(this);
        mainStatBtn = (Button) findViewById(R.id.visualizer_stat);

        ((TextView) findViewById(R.id.visualizer_text)).setText(getIntent().getStringExtra("selected"));
        total = getIntent().getIntExtra("count", 0);
        data = getIntent().getParcelableExtra("repo");

        //getDataFromInterval >>if start = end >>restart simulation >>else sart&end+10
        final LineChartView lineChart = (LineChartView) findViewById(R.id.visualizer_chart);
        lineChart.setChartData(getDataFromInterval(0, 9));

        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (counter * 10 < total) {
                            lineChart.setChartData(getDataFromInterval(counter * 10, (counter + 1) * 10 - 1));
                        } else {
                            counter = 0;
                            lineChart.setChartData(getDataFromInterval(0, 9));
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);

        mainStatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedListItem = getIntent().getStringExtra("selected");
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
                    Intent intent = new Intent(VisualizerActivity.this, StatisticsActivity.class);
                    intent.putExtra("repo_stat", stat);

                    startActivity(intent);
                    finish();
                } else {
                    checkStatistics(selectedListItem);
                }
            }
        });
    }

    private float[] getDataFromInterval(int start, int end) {
        int count = 10;
        Float checkSum = 0f;
        int times10 = 0;
        float[] ret = new float[count];

        ((TextView) findViewById(R.id.visualizer_from)).setText(data.getCommits()[start].getCommitTime());
        ((TextView) findViewById(R.id.visualizer_to)).setText(data.getCommits()[end].getCommitTime());

        for (int i = 0; i < count; i++) {
            ret[i] = total > i ? Float.parseFloat(data.getCommits()[start + i].getPercentOfchanges()) : 0;
        }
        for (float f : ret) {
            checkSum += f;
        }
        while (checkSum.toString().startsWith("0")) {
            checkSum *= 10;
            times10 += 1;
        }
        for (int j = 0; j < count; j++) {
            ret[j] = (float) (ret[j] * (Math.pow(10, (times10 + 2))));
        }
        counter += 1;
        return ret;
    }

    private float[] getRandomData() {
        int min = 1;
        int max = 100;

        Random r = new Random();
        int num = r.nextInt(max - min + 1) + min;

        return new float[]{
                r.nextInt(max - min + 1) + min,
                r.nextInt(max - min + 1) + min,
                r.nextInt(max - min + 1) + min,
                r.nextInt(max - min + 1) + min,
                r.nextInt(max - min + 1) + min,
                r.nextInt(max - min + 1) + min,
                r.nextInt(max - min + 1) + min,
                r.nextInt(max - min + 1) + min,
                r.nextInt(max - min + 1) + min,
                r.nextInt(max - min + 1) + min};
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
                                Intent intent = new Intent(VisualizerActivity.this, StatisticsActivity.class);
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
                Log.e(TAG, "STAT: " + params);
                return params;
            }
        };
        //adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}