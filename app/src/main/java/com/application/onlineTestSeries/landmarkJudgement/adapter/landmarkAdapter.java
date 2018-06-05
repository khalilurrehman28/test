package com.application.onlineTestSeries.landmarkJudgement.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.application.onlineTestSeries.HelperClasses.DateConverter;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.landmarkJudgement.models.judgementData;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mandeep on 4/9/17.
 */

public class landmarkAdapter extends RecyclerView.Adapter<landmarkAdapter.MyViewHolder>implements Filterable {
    private Context mContext;
    private List<judgementData> CourseList;
    private List<judgementData> courseListFiltered;
    private ContactsAdapterListener listener;
    public landmarkAdapter(Context mContext, List<judgementData> CourseList, ContactsAdapterListener listener) {
        this.mContext = mContext;
        this.CourseList = CourseList;
        this.courseListFiltered = CourseList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_judgement, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        judgementData courseData = courseListFiltered.get(position);
        holder.judgementDot.setBorderWidth(6);
        holder.judgementDot.setBorderColor(courseData.getColor());
        //((GradientDrawable)holder.llbackground.getBackground()).setColor(courseData.getColor());
        holder.judgementTitle.setText(courseData.getSCLJPARTY1());
        holder.judgementTitle1.setText(courseData.getSCLJPARTY2());
        if (!courseData.getSCLJCASENO().equals("")){
            holder.judgementCaseNo.setText("Case No. "+courseData.getSCLJCASENO());
        }else {
            holder.judgementCaseNo.setVisibility(View.GONE);
        }

        if (!courseData.getSCLJDATE().equals("")){
            holder.judgementDate.setText("Date "+new DateConverter().convertDate(courseData.getSCLJDATE()));
        }else {
            holder.judgementDate.setText("Date (--/--/----)");
            //holder.judgementDate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return courseListFiltered.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView judgementDot;
        public TextView judgementTitle,judgementTitle1,judgementCaseNo,judgementDate;
        public CardView cardView;


        public MyViewHolder(View view) {
            super(view);
            judgementDot= view.findViewById(R.id.judgementDot);
            judgementTitle = view.findViewById(R.id.judgementTitle);
            judgementTitle1 = view.findViewById(R.id.judgementTitle1);
            judgementCaseNo = view.findViewById(R.id.judgementCaseNo);
            judgementDate = view.findViewById(R.id.judgementDate);
            cardView = view.findViewById(R.id.card_view);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(courseListFiltered.get(getAdapterPosition()));
                }
            });
            //llbackground = view.findViewById(R.id.llBackgroundCourseRadius);
        }
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
                    List<judgementData> filteredList = new ArrayList<>();
                    for (judgementData row : CourseList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSCLJPARTY1().toLowerCase().contains(charString.toLowerCase()) || row.getSCLJPARTY2().toLowerCase().contains(charString.toLowerCase()) ) {
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
                courseListFiltered = (ArrayList<judgementData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface ContactsAdapterListener {
        void onContactSelected(judgementData judgementData);
    }
}