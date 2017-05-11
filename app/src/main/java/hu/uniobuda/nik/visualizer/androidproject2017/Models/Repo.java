package hu.uniobuda.nik.visualizer.androidproject2017.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Repo implements Parcelable {
    private String repo_id_name;
    private Commit[] commit;

    public String getRepoIdName() {
        return repo_id_name;
    }
    public Commit[] getCommits() {
        return commit;
    }

    public Repo(Parcel parcel) {
        repo_id_name = parcel.readString();
        commit = (Commit[]) parcel.readParcelableArray(getClass().getClassLoader());
    }

    public static final Creator<Repo> CREATOR = new Creator<Repo>() {
        @Override
        public Repo createFromParcel(Parcel in) {
            return new Repo(in);
        }

        @Override
        public Repo[] newArray(int size) {
            return new Repo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(repo_id_name);
        dest.writeParcelableArray(commit, flags);
    }
}
