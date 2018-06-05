package com.application.onlineTestSeries.Legal_maxim.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.application.onlineTestSeries.Legal_maxim.Models.LeximsData;
import com.application.onlineTestSeries.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class legalmaximsAdapter extends RecyclerView.Adapter<legalmaximsAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private List<LeximsData> bookmarkList;
    private List<LeximsData> courseListFiltered;
    private ContactsAdapterListener listener;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView courseDot;
        public TextView bookmarkTitle,bookmarkDes;
        public CardView cardView;
        //public LinearLayout llbackground;

        public MyViewHolder(View view) {
            super(view);
            courseDot= view.findViewById(R.id.courseDot);
            bookmarkTitle = view.findViewById(R.id.NotesTitle);
            bookmarkDes = view.findViewById(R.id.noteDes);
            cardView = view.findViewById(R.id.card_view);
            //llbackground = view.findViewById(R.id.llBackgroundCourseRadius);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(courseListFiltered.get(getAdapterPosition()));

                }
            });
        }
    }

    public legalmaximsAdapter(Context mContext, List<LeximsData> bookmarkList, ContactsAdapterListener listener) {
        this.mContext = mContext;
        this.bookmarkList = bookmarkList;
        this.courseListFiltered = bookmarkList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notes, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LeximsData notesData = courseListFiltered.get(position);

        holder.courseDot.setBorderWidth(6);
        holder.courseDot.setBorderColor(notesData.getColor());
        holder.bookmarkTitle.setText(notesData.getLMTITLE());
        holder.bookmarkDes.setText(notesData.getLMDESC());

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
                    courseListFiltered = bookmarkList;
                } else {
                    List<LeximsData> filteredList = new ArrayList<>();
                    for (LeximsData row : bookmarkList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getLMTITLE().toLowerCase().contains(charString.toLowerCase())) {
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
                courseListFiltered = (ArrayList<LeximsData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface ContactsAdapterListener {
        void onContactSelected(LeximsData LeximsData);
    }

}