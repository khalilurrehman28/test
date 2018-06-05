package com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.QuestionFragment.questionPYQPFragment;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;

/**
 * Created by android on 24/1/18.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    List<practiceTestData> questionList;
    public ViewPagerAdapter(FragmentManager fm, List<practiceTestData> question) {
        super(fm);
        this.questionList = question;
    }

    @Override
    public Fragment getItem(int position) {
        return questionPYQPFragment.create(position);
    }

    @Override
    public int getCount() {
        return questionList.size();
    }

}