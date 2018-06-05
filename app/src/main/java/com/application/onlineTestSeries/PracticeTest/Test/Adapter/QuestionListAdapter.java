package com.application.onlineTestSeries.PracticeTest.Test.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;
import com.application.onlineTestSeries.R;

import java.util.List;

import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.*;

/**
 * Created by android on 8/2/18.
 */

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.MyViewHolder> {

    private Context mContext;
    private List<practiceTestData> ClassList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView questionNumber;
        public CardView card;
        public ImageView checked;
        public ImageView current;


        public MyViewHolder(View view) {
            super(view);
            questionNumber = view.findViewById(R.id.questionNumber);
            card = itemView.findViewById(R.id.QuestionCard);
            current = itemView.findViewById(R.id.current);
            checked = itemView.findViewById(R.id.checked);
        }
    }

    public QuestionListAdapter(Context mContext, List<practiceTestData> ClassList) {
        this.mContext = mContext;
        this.ClassList = ClassList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        practiceTestData classD = ClassList.get(position);
        holder.questionNumber.setText(""+(position+1));
        switch (classD.getProcessStart()){
            case answerGiven:
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.answerGiven));
                break;
            case answerNotViewed:
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.answerLoaded));
                break;
            case answerSkip:
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.answerSkip));
                break;
            case questionIsMarked:
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.isMarked));
                break;
        }

        if (classD.isAttempted()){
            holder.checked.setVisibility(View.VISIBLE);
        }else{
            holder.checked.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return ClassList.size();
    }
}