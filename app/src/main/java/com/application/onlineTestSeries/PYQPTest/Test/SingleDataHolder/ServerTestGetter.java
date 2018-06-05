package com.application.onlineTestSeries.PYQPTest.Test.SingleDataHolder;

import com.application.onlineTestSeries.PYQPTest.Test.Models.PYQP_Test_Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by android on 31/1/18.
 */

public class ServerTestGetter {
    private static ServerTestGetter instance = null;

    private static List<PYQP_Test_Data> ConvertedQuestionData;

    public static synchronized ServerTestGetter getInstance() {
        if (instance == null){
            instance = new ServerTestGetter();
        }
        return instance;
    }

    protected ServerTestGetter() {
        ConvertedQuestionData = new ArrayList<>();
    }

    public void addDataToList(PYQP_Test_Data testData){
        ConvertedQuestionData.add(testData);
    }

    public List<PYQP_Test_Data> getConvertedQuestionData() {
        return ConvertedQuestionData;
    }

    public void setConvertedQuestionData(List<PYQP_Test_Data> convertedQuestionData) {
        ConvertedQuestionData = convertedQuestionData;
    }

    public void clear() {
        ConvertedQuestionData.clear();
    }
}
