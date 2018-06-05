package com.application.onlineTestSeries.PYQPTest.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.onlineTestSeries.PYQPTest.Models.PYQPTestData;
import com.application.onlineTestSeries.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mandeep on 4/9/17.
 */

public class PYQPAdapter extends RecyclerView.Adapter<PYQPAdapter.MyViewHolder>implements Filterable {
    private Context mContext;
    private List<PYQPTestData> TestList;
    private List<PYQPTestData> TestListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView testCount,testName,testType,alredayAttempted;
        public CardView cardView;
        public LinearLayout llBackgroundCourseRadius;
        public ImageView Imagebar;

        public MyViewHolder(View view) {
            super(view);
            testCount= view.findViewById(R.id.testCount);
            testName = view.findViewById(R.id.testName);
            testType = view.findViewById(R.id.testType);
            alredayAttempted = view.findViewById(R.id.alredayAttempted);
            llBackgroundCourseRadius = view.findViewById(R.id.llBackgroundCourseRadius);
            Imagebar = view.findViewById(R.id.Imagebar);
            cardView = view.findViewById(R.id.card_view);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onContactSelected(TestListFiltered.get(getAdapterPosition()));
                }
            });

        }
    }
    public PYQPAdapter(Context mContext, List<PYQPTestData> TestList, ContactsAdapterListener listener) {
        this.mContext = mContext;
        this.TestList = TestList;
        this.TestListFiltered = TestList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_test, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PYQPTestData courseData = TestListFiltered.get(position);
        ((GradientDrawable)holder.llBackgroundCourseRadius.getBackground()).setColor(courseData.getColor());
        holder.testName.setText(courseData.getPYQPTESTNAME());
        holder.testCount.setText(String.valueOf(courseData.getCount()));
        holder.Imagebar.setBackgroundColor(courseData.getColor());

        if (courseData.isStatus()){
            holder.alredayAttempted.setVisibility(View.VISIBLE);
        }else {
            holder.alredayAttempted.setVisibility(View.GONE);
        }
        if(courseData.getPYQPTESTTYPE().equals("0")) {
            holder.testType.setText(mContext.getString(R.string.free));
            holder.testType.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        }else{
            holder.testType.setText(mContext.getString(R.string.paid));
            holder.testType.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
        }
    }

    @Override
    public int getItemCount() {
        return TestListFiltered.size();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    TestListFiltered = TestList;
                } else {
                    List<PYQPTestData> filteredList = new ArrayList<>();
                    for (PYQPTestData row : TestList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getPYQPTESTNAME().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    TestListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = TestListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                TestListFiltered = (ArrayList<PYQPTestData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface ContactsAdapterListener {
        void onContactSelected(PYQPTestData PYQPTestData);
    }
}