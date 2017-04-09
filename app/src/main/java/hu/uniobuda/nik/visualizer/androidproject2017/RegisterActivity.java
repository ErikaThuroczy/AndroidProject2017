package hu.uniobuda.nik.visualizer.androidproject2017;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity {
    //final ProgressDialog pDialog = new ProgressDialog(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        TextView regLink = (TextView) findViewById(R.id.reg_link);
        regLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //login btn click
        Button regBtn = (Button) findViewById(R.id.reg_reg);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //req url .. submit name, email and pswd as request parameters
                String tag_json_obj = "json_obj_req";
                String url = "http://www.myUrl.com/";

                //pDialog.setMessage("Loading...");
                //pDialog.show();
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                              //  pDialog.hide();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d(TAG, "Error: " + error.getMessage());
                               // pDialog.hide();
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name", "Eri");
                        params.put("email", "eri@gmail.com");
                        params.put("password", "eripswd");

                        return params;
                    }

                    //Passing some request headers
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("apiKey", "xxxxx");
                        return headers;
                    }

                };
                Log.d("mylog", "getParams() missing");
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


                //Toast - success(go b to login scrn) or denied(stay here) >> as response from server


                finish();
            }
        });
    }
}
