package com.application.onlineTestSeries.Notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.HelperClasses.RecyclerTouchListener;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.Legal_maxim.LeximsPreview;
import com.application.onlineTestSeries.Notes.Models.AddToNotes;
import com.application.onlineTestSeries.Notes.adapter.NotesAdapter;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.SectionActivity.sectionPreviewActivity;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.Utils.userStatusCheck;
import com.application.onlineTestSeries.landmarkJudgement.landmarkJudgementPreview;
import com.application.onlineTestSeries.login.Models.UserData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

public class NotesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    NotesAdapter notesAdapter;
    List<AddToNotes> courseDataList;
    RecyclerView notesRecycler;
    ProgressBar progressBar;
    TextView noNoteFound;
    Realm realmDB;
    View mView;
    // TODO: Rename and change types of parameters
    private String mParam1,userID;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    userStatusCheck userStatusCheck;
    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_notes, container, false);
        ButterKnife.bind(this, mView);
        initializeView(mView);
        return mView;
    }

    private void initializeView(final View v) {

        noNoteFound = v.findViewById(R.id.noNotesFound);
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        courseDataList = new ArrayList<>();
        notesAdapter = new NotesAdapter(getContext(), courseDataList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        notesRecycler = v.findViewById(R.id.notesRecycler);
        notesRecycler.setLayoutManager(mLayoutManager);
        notesRecycler.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        notesRecycler.setItemAnimator(new DefaultItemAnimator());
        notesRecycler.setAdapter(notesAdapter);

        RealmResults<UserData> results1 = realmDB.where(UserData.class).findAll();
        for (UserData item: results1){
            userID = item.getUserId();
        }

        userStatusCheck = new userStatusCheck(realmDB,mView.getContext(),userID);

        RealmResults<AddToNotes> notesResult = realmDB.where(AddToNotes.class).findAll();
        if (notesResult.size()>0){
            for (AddToNotes item : notesResult){
                //UpdateClickUi(item.getStartIndex(),item.getEndIndex());
                AddToNotes notes = new AddToNotes();
                notes.setChapterID(item.getChapterID());
                notes.setChapterName(item.getChapterName());
                notes.setWord(item.getWord());
                notes.setDescription(item.getDescription());
                notes.setCourseName(item.getCourseName());
                notes.setType(item.getType());
                courseDataList.add(notes);
                notesAdapter.notifyDataSetChanged();
            }
        }else{
            noNoteFound.setVisibility(View.VISIBLE);
        }

        notesRecycler.addOnItemTouchListener(new RecyclerTouchListener(v.getContext(), notesRecycler, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String checkSucscribe = "";

                RealmResults<subscriptionUserData> results = realmDB.where(subscriptionUserData.class).findAll();
                String status ="",isActive ="",date = "";
                for (subscriptionUserData item: results) {
                    status = item.getSUBSCRIPTIONSTATUS();
                    isActive = item.getSUBSCRIBERSUBSCRIPTIONSTATUS();
                    date = item.getSUBSCRIBERSUBSCRIPTIONDATE();
                }
                Log.d(TAG, "onContactSelected: "+date);
                if (isActive.equals("1") && status.equals("Active")){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date userDate = null,todayDate;
                    try {
                        todayDate = sdf.parse(sdf.format(new Date()));

                        userDate = sdf.parse(date);
                        Log.d(TAG, "onContactSelected: "+userDate+"-----"+todayDate);
                        if(todayDate.before(userDate) || userDate.compareTo(todayDate) == 0){
                            checkSucscribe = "yes";
                        }else{
                            showAlertDialogue("Your Plan will expired please re-subscribe the plan.");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
//            showWarning("Please subscribe to plan",mView);
                    showAlertDialogue("Do you want to subscribe the plan?");
                }

                Intent i;
                switch (courseDataList.get(position).getType()){
                    case 0:
                        i = new Intent(v.getContext(), sectionPreviewActivity.class);
                        i.putExtra("chapterID",""+courseDataList.get(position).getChapterID());
                        i.putExtra("courseName",""+courseDataList.get(position).getCourseName());
                        startActivity(i);
                        break;
                    case 1:
                        if (checkSucscribe.equals("yes")){
                            i = new Intent(v.getContext(), landmarkJudgementPreview.class);
                            i.putExtra("SCLJID",""+courseDataList.get(position).getChapterID());
                            //i.putExtra("courseName",""+courseDataList.get(position).getCourseName());
                            startActivity(i);
                        }

                        break;
                    case 2:
                        if (checkSucscribe.equals("yes")) {
                            i = new Intent(v.getContext(), LeximsPreview.class);
                            i.putExtra("LMID", "" + courseDataList.get(position).getChapterID());
                            //i.putExtra("courseName",""+courseDataList.get(position).getCourseName());
                            startActivity(i);
                        }
                        break;

                }

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();
        userStatusCheck.checkUpdatedStatus();
    }

    private void showAlertDialogue(String msg) {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(mView.getContext());
        builder.setTitle("Subscribe Plan")
                .setMessage(msg)
                .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(mView.getContext(), SubscriptionSubscribe.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_subscribe)
                .show();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
