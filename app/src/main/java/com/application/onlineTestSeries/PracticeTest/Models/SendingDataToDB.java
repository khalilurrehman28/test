package com.application.onlineTestSeries.PracticeTest.Models;

/**
 * Created by android on 16/3/18.
 */

public class SendingDataToDB {
    private int ans;
    private int userID;
    private int questionID;
    private int testID;

    public int getAns() {
        return ans;
    }

    public void setAns(int ans) {
        this.ans = ans;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }
}
