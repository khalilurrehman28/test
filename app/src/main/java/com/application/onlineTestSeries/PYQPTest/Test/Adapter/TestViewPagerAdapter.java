package com.application.onlineTestSeries.PYQPTest.Test.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.application.onlineTestSeries.PYQPTest.Test.Models.PYQP_Test_Data;
import com.application.onlineTestSeries.PYQPTest.Test.QuestionFragment.questionTestFragment;

import java.util.List;

/**
 * Created by android on 24/1/18.
 */

public class TestViewPagerAdapter extends FragmentStatePagerAdapter {

    List<PYQP_Test_Data> questionList;
    public TestViewPagerAdapter(FragmentManager fm, List<PYQP_Test_Data> question) {
        super(fm);
        this.questionList = question;
    }

    @Override
    public Fragment getItem(int position) {
        return questionTestFragment.create(position);
    }

    @Override
    public int getCount() {
        return questionList.size();
    }

}