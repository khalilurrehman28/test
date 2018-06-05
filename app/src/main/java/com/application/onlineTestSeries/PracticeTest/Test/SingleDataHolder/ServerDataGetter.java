package com.application.onlineTestSeries.PracticeTest.Test.SingleDataHolder;

import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 31/1/18.
 */

public class ServerDataGetter {
    private static ServerDataGetter instance = null;

    private static List<practiceTestData> ConvertedQuestionData;

    public static synchronized ServerDataGetter getInstance() {
        if (instance == null){
            instance = new ServerDataGetter();
        }
        return instance;
    }

    protected ServerDataGetter() {
        ConvertedQuestionData = new ArrayList<>();
    }

    public void getQuestionData(practiceTestData userQuestion){
        ConvertedQuestionData.add(userQuestion);
    }

    public List<practiceTestData> getConvertedQuestionData() {
        return ConvertedQuestionData;
    }

    public void setConvertedQuestionData(List<practiceTestData> convertedQuestionData) {
        ConvertedQuestionData = convertedQuestionData;
    }

    public void clearPreviousRecords(){
        ConvertedQuestionData.clear();
    }

}
