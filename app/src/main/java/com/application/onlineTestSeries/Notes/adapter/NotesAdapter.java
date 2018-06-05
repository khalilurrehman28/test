package com.application.onlineTestSeries.Notes.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.onlineTestSeries.HelperClasses.DateConverter;
import com.application.onlineTestSeries.Notes.Models.AddToNotes;
import com.application.onlineTestSeries.R;

import java.util.List;
import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by mandeep on 4/9/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder>{
    private Context mContext;
    private List<AddToNotes> CourseList;

    public NotesAdapter(Context mContext, List<AddToNotes> CourseList) {
        this.mContext = mContext;
        this.CourseList = CourseList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notes, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AddToNotes notesData = CourseList.get(position);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.courseDot.setBorderWidth(6);
        holder.courseDot.setBorderColor(color);

        holder.noteTitle.setText(notesData.getWord());
        holder.noteDes.setText(notesData.getCourseName()+" -> "+notesData.getChapterName());
        switch (notesData.getType()){
            case 0:
                holder.noteTitle.setText(notesData.getWord());
                holder.noteDes.setText(notesData.getCourseName()+" -> "+notesData.getChapterName());
                break;
            case 1:
                holder.noteTitle.setText(notesData.getWord());
                holder.noteDes.setText("Case no. "+notesData.getChapterName()+" -> "+new DateConverter().convertDate(notesData.getCourseName()));
                break;
            case 2:
                holder.noteTitle.setText(notesData.getWord());
                holder.noteDes.setText(notesData.getCourseName()+" -> "+notesData.getChapterName());

                break;

        }

    }

    @Override
    public int getItemCount() {
        return CourseList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView courseDot;
        public TextView noteTitle,noteDes;
        public CardView cardView;
        //public LinearLayout llbackground;

        public MyViewHolder(View view) {
            super(view);
            courseDot= view.findViewById(R.id.courseDot);
            noteTitle = view.findViewById(R.id.NotesTitle);
            noteDes = view.findViewById(R.id.noteDes);
            cardView = view.findViewById(R.id.card_view);
            //llbackground = view.findViewById(R.id.llBackgroundCourseRadius);
        }
    }
}