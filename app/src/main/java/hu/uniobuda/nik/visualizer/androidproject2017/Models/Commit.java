package hu.uniobuda.nik.visualizer.androidproject2017.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Commit implements Parcelable {
    private String commit_time;
    private String committer_name;
    private String percent_of_changes;

    public String getCommitterName() {
        return committer_name;
    }
    public String getCommitTime() {
        return commit_time;
    }
    public String getPercentOfchanges() {
        return percent_of_changes;
    }

    public Commit(Parcel parcel) {
        commit_time = parcel.readString();
        committer_name = parcel.readString();
        percent_of_changes = parcel.readString();
    }

    public static final Creator<Commit> CREATOR = new Creator<Commit>() {
        @Override
        public Commit createFromParcel(Parcel in) {
            return new Commit(in);
        }

        @Override
        public Commit[] newArray(int size) {
            return new Commit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(committer_name);
        dest.writeString(commit_time);
        dest.writeString(percent_of_changes);
    }
}
