package com.application.onlineTestSeries.Chapters.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.application.onlineTestSeries.Chapters.Models.ChapterData;
import com.application.onlineTestSeries.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by mandeep on 4/9/17.
 */

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.MyViewHolder>implements Filterable {
    private Context mContext;
    private List<ChapterData> CourseList;
    private List<ChapterData> courseListFiltered;
    private chapterAdapterListner listener;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView courseDot;
        public TextView CourseName,chapterDes1,chapterDes2;
        public CardView cardView;
        //public LinearLayout llbackground;

        public MyViewHolder(View view) {
            super(view);
            courseDot= view.findViewById(R.id.courseDot);
            CourseName = view.findViewById(R.id.chapterTitle);
            chapterDes1 = view.findViewById(R.id.chapterDes1);
            chapterDes2 = view.findViewById(R.id.chapterDes2);
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
    public ChapterAdapter(Context mContext, List<ChapterData> CourseList, chapterAdapterListner listener) {
        this.mContext = mContext;
        this.CourseList = CourseList;
        this.courseListFiltered = CourseList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_chapters, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChapterData courseData = courseListFiltered.get(position);
        holder.courseDot.setBorderWidth(6);
        holder.courseDot.setBorderColor(courseData.getColorCirlce());
        //((GradientDrawable)holder.llbackground.getBackground()).setColor(courseData.getColor());
        holder.CourseName.setText(courseData.getCHAPTERTITLE());
        holder.chapterDes1.setText(courseData.getCHAPTERSUBHEAD1());
        holder.chapterDes2.setText(courseData.getCHAPTERSUBHEAD2());
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
                    List<ChapterData> filteredList = new ArrayList<>();
                    for (ChapterData row : CourseList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCHAPTERTITLE().toLowerCase().contains(charString.toLowerCase()) || row.getCHAPTERSUBHEAD1().toLowerCase().contains(charSequence)
                                || row.getCHAPTERSUBHEAD2().toLowerCase().contains(charSequence)) {
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
                courseListFiltered = (ArrayList<ChapterData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface chapterAdapterListner {
        void onContactSelected(ChapterData ChapterData);
    }
}