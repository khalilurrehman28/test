package com.application.onlineTestSeries.bookmarks.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by android on 13/3/18.
 */

public class AddToBookmarks extends RealmObject{

    @PrimaryKey
    private int id;
    private String chapterID;
    private String ChapterName;
    private String courseName;
    private int type;

    public String getChapterID() {
        return chapterID;
    }

    public void setChapterID(String chapterID) {
        this.chapterID = chapterID;
    }

    public String getChapterName() {
        return ChapterName;
    }

    public void setChapterName(String chapterName) {
        ChapterName = chapterName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
