package com.application.onlineTestSeries.bookmarks.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.onlineTestSeries.HelperClasses.DateConverter;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.bookmarks.model.AddToBookmarks;

import java.util.List;
import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mandeep on 4/9/17.
 */

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.MyViewHolder>{
    private Context mContext;
    private List<AddToBookmarks> bookmarkList;

    public BookmarksAdapter(Context mContext, List<AddToBookmarks> bookmarkList) {
        this.mContext = mContext;
        this.bookmarkList = bookmarkList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notes, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AddToBookmarks bookmarkData = bookmarkList.get(position);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.courseDot.setBorderWidth(6);
        holder.courseDot.setBorderColor(color);
        switch (bookmarkData.getType()){
            case 0:
                holder.bookmarkTitle.setText(bookmarkData.getCourseName());
                holder.bookmarkDes.setText(bookmarkData.getChapterName());
                break;
            case 1:
                holder.bookmarkTitle.setText(bookmarkData.getCourseName());
                holder.bookmarkDes.setText("Date "+new DateConverter().convertDate(bookmarkData.getChapterName()));
                break;
            case 2:
                holder.bookmarkTitle.setText(bookmarkData.getCourseName());
                holder.bookmarkDes.setText(bookmarkData.getChapterName());
                break;
        }


    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

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
        }
    }
}