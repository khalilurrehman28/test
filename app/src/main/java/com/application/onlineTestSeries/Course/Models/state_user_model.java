package com.application.onlineTestSeries.Course.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class state_user_model implements Parcelable {

    public static final Creator<state_user_model> CREATOR = new Creator<state_user_model>() {
        @Override
        public state_user_model createFromParcel(Parcel in) {
            return new state_user_model(in);
        }

        @Override
        public state_user_model[] newArray(int size) {
            return new state_user_model[size];
        }
    };
    private int stateID;
    private int chapterCount;

    protected state_user_model(Parcel in) {
        stateID = in.readInt();
        chapterCount = in.readInt();
    }

    public state_user_model(int stateID, int chapterCount) {
        this.stateID = stateID;
        this.chapterCount = chapterCount;
    }

    public int getStateID() {
        return stateID;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stateID);
        dest.writeInt(chapterCount);
    }
}
