package hu.uniobuda.nik.visualizer.androidproject2017;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatisticsActivity extends AppCompatActivity {
    Button statBackBtn;
    String auth;
    String totalCommit;
    String elapsedTime;
    String commitWinnerByCount;
    String commitWinnerBySize;
    String busiestPeriod;
    String other;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            auth = bundle.getString("author");
            totalCommit = String.valueOf(bundle.getLong("total_commit"));
            elapsedTime = bundle.getString("elapsed_time");
            commitWinnerByCount = bundle.getString("most_commit_count");
            commitWinnerBySize = bundle.getString("most_commit_size");
            busiestPeriod = bundle.getString("busiest_period");
            other = bundle.getString("other");
        }
        if (!auth.isEmpty()) {
            ((TextView) findViewById(R.id.stat_auth)).setText(auth);
        }
        if (!totalCommit.isEmpty()) {
            ((TextView) findViewById(R.id.stat_total_commit)).setText(totalCommit);
        }
        if (!elapsedTime.isEmpty()) {
            ((TextView) findViewById(R.id.stat_elapsed_time)).setText(elapsedTime);
        }
        if (!commitWinnerByCount.isEmpty()) {
            ((TextView) findViewById(R.id.stat_winner_count)).setText(commitWinnerByCount);
        }
        if (!commitWinnerBySize.isEmpty()) {
            ((TextView) findViewById(R.id.stat_winner_size)).setText(commitWinnerBySize);
        }
        if (!busiestPeriod.isEmpty()) {
            ((TextView) findViewById(R.id.stat_period)).setText(busiestPeriod);
        }
        if (!other.isEmpty()) {
            ((TextView) findViewById(R.id.stat_other)).setText(other);
        }

        statBackBtn = (Button) findViewById(R.id.stat_back);
        statBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
