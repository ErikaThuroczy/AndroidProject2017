package hu.uniobuda.nik.visualizer.androidproject2017.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Repo implements Parcelable {
    private String repo_id_name;
    private Commit[] commits;

    public String getRepoIdName() {
        return repo_id_name;
    }
    public Commit[] getCommits() {
        return commits;
    }

    public Repo(Parcel parcel) {
        repo_id_name = parcel.readString();

        //NEVER DELETE THIS COMMENT:(Commit[]) parcel.readParcelableArray(getClass().getClassLoader());>>fix:
        Parcelable[] cp = parcel.readParcelableArray(getClass().getClassLoader());
        commits = new Commit[cp.length];
        for (int i = 0; i < cp.length;i++)
        {
            commits[i] = (Commit)cp[i];
        }
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
        dest.writeParcelableArray(commits, flags);
    }
}
