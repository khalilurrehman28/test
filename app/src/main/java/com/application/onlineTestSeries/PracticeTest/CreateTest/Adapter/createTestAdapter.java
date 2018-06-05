package com.application.onlineTestSeries.PracticeTest.CreateTest.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.onlineTestSeries.PracticeTest.CreateTest.Filter.MinMaxFilter;
import com.application.onlineTestSeries.PracticeTest.CreateTest.Models.CategoryTestData;
import com.application.onlineTestSeries.R;

import java.util.List;


/**
 * Created by mandeep on 4/9/17.
 */

public class createTestAdapter extends RecyclerView.Adapter<createTestAdapter.MyViewHolder> {
    private Context mContext;
    private List<CategoryTestData> TestList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public EditText testCount;
        public TextView testName,QuestionCount;
        public CardView cardView;
        public LinearLayout llBackgroundCourseRadius;
        public ImageView Imagebar;
        public CategorCount categorCount;

        public MyViewHolder(View view) {
            super(view);
            testCount= view.findViewById(R.id.testCount);
            testName = view.findViewById(R.id.testName);
            QuestionCount = view.findViewById(R.id.QuestionCount);
            llBackgroundCourseRadius = view.findViewById(R.id.llBackgroundCourseRadius);
            Imagebar = view.findViewById(R.id.Imagebar);
            cardView = view.findViewById(R.id.card_view);
            categorCount = new CategorCount();
        }

    }
    public createTestAdapter(Context mContext, List<CategoryTestData> TestList) {
        this.mContext = mContext;
        this.TestList = TestList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_test_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CategoryTestData courseData = TestList.get(position);
        holder.testName.setText(courseData.getCATEGORYTITLE());
        ((GradientDrawable)holder.llBackgroundCourseRadius.getBackground()).setColor(courseData.getColorCode());
        holder.testCount.addTextChangedListener(holder.categorCount);
        holder.categorCount.updatePosition(position,courseData.getQcount()/*,holder.testCount*/);
        holder.QuestionCount.setText(String.valueOf(courseData.getQcount()));
        holder.Imagebar.setBackgroundColor(courseData.getColorCode());

        String a = "";
        if (courseData.getQcount()>100){
            a = "100";
        }else{
            a = String.valueOf(courseData.getQcount());
        }
        holder.testCount.setFilters(new InputFilter[]{ new MinMaxFilter("0", a)});
        holder.testCount.setText(String.valueOf(courseData.getQuestionCount()));
    }

    @Override
    public int getItemCount() {
        return TestList.size();
    }

    // to listen user input for product quantity
    private class CategorCount implements TextWatcher {
        private int position;
        private int maxQuestion;
        //EditText editText;

        public void updatePosition(int position, int maxQuestion/*,EditText editText*/) {
            this.position = position;
            this.maxQuestion = maxQuestion;
            // this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
            if (position <= TestList.size()) {
                int n;
                if (editable.toString().equals("")) {
                    n = 0;
                } else {
                    n = Integer.parseInt(editable.toString());
                }
                TestList.get(position).setQuestionCount(n);
            }
        }
    }
}