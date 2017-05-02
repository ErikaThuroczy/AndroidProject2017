package hu.uniobuda.nik.visualizer.androidproject2017.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Statistics implements Parcelable {
    private String author;
    private long total_commit;
    private String elapsed_time;
    private String most_commit_count;
    private String most_commit_size;
    private String busiest_period;
    private String other;

    public String getAuthor() {
        return author;
    }

    public long getTotal_commit() {
        return total_commit;
    }

    public String getElapsed_time() {
        return elapsed_time;
    }

    public String getMost_commit_count() {
        return most_commit_count;
    }

    public String getMost_commit_size() {
        return most_commit_size;
    }

    public String getBusiest_period() {
        return busiest_period;
    }

    public String getOther() {
        return other;
    }

    public Statistics(Parcel parcel) {
        author = parcel.readString();
        total_commit = parcel.readLong();
        elapsed_time = parcel.readString();
        most_commit_count = parcel.readString();
        most_commit_size = parcel.readString();
        busiest_period = parcel.readString();
        other = parcel.readString();
    }
    public Statistics(String author, String totalCommit, String elapsedTime, String mostCommitCount, String mostCommitSize, String busiestPeriod, String other ) {
        this.author = author;
        this.total_commit = Long.parseLong(totalCommit);
        this.elapsed_time = elapsedTime;
        this.most_commit_count = mostCommitCount;
        this.most_commit_size = mostCommitSize;
        this.busiest_period = busiestPeriod;
        this.other = other;
    }
    public static final Creator<Statistics> CREATOR = new Creator<Statistics>() {
        @Override
        public Statistics createFromParcel(Parcel in) {
            return new Statistics(in);
        }

        @Override
        public Statistics[] newArray(int size) {
            return new Statistics[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeLong(Long.parseLong(String.valueOf(total_commit)));
        dest.writeString(elapsed_time);
        dest.writeString(most_commit_count);
        dest.writeString(most_commit_size);
        dest.writeString(busiest_period);
        dest.writeString(other);
    }
}
