package com.application.onlineTestSeries.Subscription_package.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.Models.SubscriptionData;


/**
 * Created by mandeep on 4/9/17.
 */

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder>implements Filterable {
    private Context mContext;
    private List<SubscriptionData> CourseList;
    private List<SubscriptionData> courseListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subscriptionName,subscriptionStatus,subscriptionDate,subscriptionPrice;

        public CardView cardView;
        public LinearLayout llBackgroundSubscriptionRadius;

        public MyViewHolder(View view) {
            super(view);
            subscriptionName = view.findViewById(R.id.subscriptionName);
            subscriptionStatus = view.findViewById(R.id.subscriptionStatus);
            subscriptionDate = view.findViewById(R.id.subscriptionDate);
            subscriptionPrice = view.findViewById(R.id.subscriptionPrice);
            cardView = view.findViewById(R.id.card_view);
            llBackgroundSubscriptionRadius = view.findViewById(R.id.llBackgroundSubscriptionRadius);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(courseListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }
    public SubscriptionAdapter(Context mContext, List<SubscriptionData> CourseList, ContactsAdapterListener listener) {
        this.mContext = mContext;
        this.CourseList = CourseList;
        this.courseListFiltered = CourseList;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subscription, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SubscriptionData SubscriptionData = courseListFiltered.get(position);
        ((GradientDrawable)holder.llBackgroundSubscriptionRadius.getBackground()).setColor(SubscriptionData.getColor());
        holder.subscriptionName.setText(SubscriptionData.getSUBSCRIPTIONNAME());
        holder.subscriptionPrice.setText(SubscriptionData.getSUBSCRIPTIONPRICE());

        if (SubscriptionData.isActive()){
            holder.subscriptionDate.setText((String)SubscriptionData.getSUBSCRIPTIONENDDATE());
        }else{
            holder.subscriptionDate.setVisibility(View.INVISIBLE);
            holder.subscriptionStatus.setVisibility(View.INVISIBLE);
        }
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
                    List<SubscriptionData> filteredList = new ArrayList<>();
                    for (SubscriptionData row : CourseList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSUBSCRIPTIONNAME().toLowerCase().contains(charString.toLowerCase())) {
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
                courseListFiltered = (ArrayList<SubscriptionData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface ContactsAdapterListener {
        void onContactSelected(SubscriptionData SubscriptionData);
    }
}