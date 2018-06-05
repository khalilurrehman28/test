package com.application.onlineTestSeries.PracticeTest.Test.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;
import com.application.onlineTestSeries.PracticeTest.Test.QuestionFragment.questionFragment;

import java.util.List;


/**
 * Created by android on 24/1/18.
 */

public class ViewPagerAdapter extends SmartFragmentPagerAdapter {

    private List<practiceTestData> questionList;

    public ViewPagerAdapter(FragmentManager fm, List<practiceTestData> question) {
        super(fm);
        this.questionList = question;
    }

    @Override
    public Fragment getItem(int position) {
        return questionFragment.create(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);

    }

    @Override
    public int getCount() {
        return questionList.size();
    }

}