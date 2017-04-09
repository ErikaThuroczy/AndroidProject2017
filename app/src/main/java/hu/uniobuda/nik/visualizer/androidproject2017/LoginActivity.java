package hu.uniobuda.nik.visualizer.androidproject2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set def screen to login
        setContentView(R.layout.login);
        TextView regScreen = (TextView) findViewById(R.id.reg_link);
        //listener for reg new acc link
        regScreen.setOnClickListener(new View.OnClickListener()
             {
                 @Override
                 public void onClick(View v) {
                     Intent myIntent = new Intent(getApplicationContext(), RegisterActivity.class );
                     startActivity(myIntent);
                 }
             }
        );

    }
}
