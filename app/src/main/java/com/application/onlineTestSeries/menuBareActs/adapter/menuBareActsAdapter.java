package com.application.onlineTestSeries.menuBareActs.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.onlineTestSeries.Course.Models.CourseData;
import com.application.onlineTestSeries.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mandeep on 4/9/17.
 */

public class menuBareActsAdapter extends RecyclerView.Adapter<menuBareActsAdapter.MyViewHolder>implements Filterable {
    private Context mContext;
    private List<CourseData> CourseList;
    private List<CourseData> courseListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView CourseName;
        public CardView cardView;
        public LinearLayout llbackground;

        public MyViewHolder(View view) {
            super(view);
            CourseName = view.findViewById(R.id.courseName);
            cardView = view.findViewById(R.id.card_view);
            llbackground = view.findViewById(R.id.llBackgroundCourseRadius);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(courseListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }
    public menuBareActsAdapter(Context mContext, List<CourseData> CourseList, ContactsAdapterListener listener) {
        this.mContext = mContext;
        this.CourseList = CourseList;
        this.courseListFiltered = CourseList;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_bare_acts, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CourseData courseData = courseListFiltered.get(position);
        holder.CourseName.setText(courseData.getCOURSETITLE());
    }

    @Override
    public int getItemCount() {
        return courseListFiltered.size();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    courseListFiltered = CourseList;
                } else {
                    List<CourseData> filteredList = new ArrayList<>();
                    for (CourseData row : CourseList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCOURSETITLE().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    courseListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = courseListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                courseListFiltered = (ArrayList<CourseData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface ContactsAdapterListener {
        void onContactSelected(CourseData courseData);
    }
}