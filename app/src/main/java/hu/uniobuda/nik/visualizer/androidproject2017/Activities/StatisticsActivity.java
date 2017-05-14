package hu.uniobuda.nik.visualizer.androidproject2017.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import hu.uniobuda.nik.visualizer.androidproject2017.Models.Statistics;
import hu.uniobuda.nik.visualizer.androidproject2017.R;

import static android.content.ContentValues.TAG;

public class StatisticsActivity extends AppCompatActivity {
    TextView statBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        Statistics stat = getIntent().getParcelableExtra("repo_stat");

        if (stat != null) {
            if (!stat.getAuthor().isEmpty()) {
                ((TextView) findViewById(R.id.stat_auth)).setText(stat.getAuthor());
            }
            if (stat.getTotal_commit() >= 0) {
                ((TextView) findViewById(R.id.stat_total_commit)).setText(Long.toString(stat.getTotal_commit()));
            }
            if (!stat.getElapsed_time().isEmpty()) {
                ((TextView) findViewById(R.id.stat_elapsed_time)).setText(stat.getElapsed_time());
            }
            if (!stat.getMost_commit_count().isEmpty()) {
                ((TextView) findViewById(R.id.stat_winner_count)).setText(stat.getMost_commit_count());
            }
            if (!stat.getMost_commit_size().isEmpty()) {
                ((TextView) findViewById(R.id.stat_winner_size)).setText(stat.getMost_commit_size());
            }
            if (!stat.getBusiest_period().isEmpty()) {
                ((TextView) findViewById(R.id.stat_period)).setText(stat.getBusiest_period());
            }
            if (!stat.getOther().isEmpty()) {
                ((TextView) findViewById(R.id.stat_other)).setText(stat.getOther());
            }
        }

        statBack = (TextView) findViewById(R.id.stat_title);
        statBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
