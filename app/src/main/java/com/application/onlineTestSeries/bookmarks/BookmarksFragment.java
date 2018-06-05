package com.application.onlineTestSeries.bookmarks;

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
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.HelperClasses.RecyclerTouchListener;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.Legal_maxim.LeximsPreview;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.SectionActivity.sectionPreviewActivity;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.Utils.userStatusCheck;
import com.application.onlineTestSeries.bookmarks.adapter.BookmarksAdapter;
import com.application.onlineTestSeries.bookmarks.model.AddToBookmarks;
import com.application.onlineTestSeries.landmarkJudgement.landmarkJudgementPreview;
import com.application.onlineTestSeries.login.Models.UserData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import io.realm.Realm;
import butterknife.ButterKnife;

import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookmarksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    BookmarksAdapter bookmarksAdapter;
    List<AddToBookmarks> bookmarkDataList;
    RecyclerView bookmarksRecycler;
    ProgressBar progressBar;
    TextView noBookmarkFound;
    Realm realmDB;
    View mView;
    private ActionMode actionMode;
    private boolean isMultiSelect = false;
    //i created List of int type to store id of data, you can create custom class type data according to your need.
    private List<String> selectedIds = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1,userID;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    userStatusCheck userStatusCheck;

    public BookmarksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DictionaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookmarksFragment newInstance(String param1, String param2) {
        BookmarksFragment fragment = new BookmarksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_bookmarks, container, false);
        ButterKnife.bind(this, mView);
        initializeView(mView);
        return mView;
    }

    // get multiselect recyclerview from github https://github.com/learnpainless/MultiSelectionRecyclerView
    private void initializeView(final View v) {

        noBookmarkFound = v.findViewById(R.id.noBookmarkFound);
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        bookmarkDataList = new ArrayList<>();
        bookmarksAdapter = new BookmarksAdapter(getContext(), bookmarkDataList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        bookmarksRecycler = v.findViewById(R.id.bookmarksRecycler);
        bookmarksRecycler.setLayoutManager(mLayoutManager);
        bookmarksRecycler.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        bookmarksRecycler.setItemAnimator(new DefaultItemAnimator());
        bookmarksRecycler.setAdapter(bookmarksAdapter);

        RealmResults<AddToBookmarks> notesResult = realmDB.where(AddToBookmarks.class).findAll();
        if (notesResult.size()>0){
            for (AddToBookmarks item : notesResult){
                //UpdateClickUi(item.getStartIndex(),item.getEndIndex());
                AddToBookmarks notes = new AddToBookmarks();
                notes.setType(item.getType());
                notes.setChapterID(item.getChapterID());
                notes.setChapterName(item.getChapterName());
                notes.setCourseName(item.getCourseName());
                bookmarkDataList.add(notes);
                bookmarksAdapter.notifyDataSetChanged();
            }
        }else{
            noBookmarkFound.setVisibility(View.VISIBLE);
        }
        bookmarksRecycler.addOnItemTouchListener(new RecyclerTouchListener(v.getContext(), bookmarksRecycler, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
               /* Intent i = new Intent(v.getContext(), sectionPreviewActivity.class);
                i.putExtra("chapterID",""+bookmarkDataList.get(position).getChapterID());
                i.putExtra("courseName",""+bookmarkDataList.get(position).getCourseName());
                startActivity(i);*/
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
                switch (bookmarkDataList.get(position).getType()){
                    case 0:
                        i = new Intent(v.getContext(), sectionPreviewActivity.class);
                        i.putExtra("chapterID",""+bookmarkDataList.get(position).getChapterID());
                        i.putExtra("courseName",""+bookmarkDataList.get(position).getCourseName());
                        startActivity(i);
                        break;
                    case 1:

                        if (checkSucscribe.equals("yes")) {
                            i = new Intent(v.getContext(), landmarkJudgementPreview.class);
                            i.putExtra("SCLJID",""+bookmarkDataList.get(position).getChapterID());
                            //i.putExtra("courseName",""+courseDataList.get(position).getCourseName());
                            startActivity(i);
                        }

                        break;
                    case 2:
                        if (checkSucscribe.equals("yes")) {
                            i = new Intent(v.getContext(), LeximsPreview.class);
                            i.putExtra("LMID", "" + bookmarkDataList.get(position).getChapterID());
                            startActivity(i);
                        }
                        break;
                }
                //Toast.makeText(v.getContext(), ""+bookmarkDataList.get(position).getType(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                final AddToBookmarks bookmarks = bookmarkDataList.get(position);
                /*final CharSequence[] options = {"Remove Bookmark"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Remove Bookmark")) {

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(getContext());
                            }
                            builder.setTitle("Remove bookmark")
                                    .setMessage("Are you sure you want to remove this bookmark?")
                                    .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            Toast.makeText(getContext(), ""+bookmarks.getChapterID()+"  "+bookmarks.getChapterName(), Toast.LENGTH_SHORT).show();
                                            realmDB.executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    final AddToBookmarks data = realm.where(AddToBookmarks.class).equalTo("chapterID", bookmarks.getChapterID()).findFirst();

                                                    if (data.getChapterID().equals(bookmarks.getChapterID())){


                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }

                    }
                });
                builder.show();*/

            }
        }));

        RealmResults<UserData> results1 = realmDB.where(UserData.class).findAll();
        for (UserData item: results1){
            userID = item.getUserId();
        }

        userStatusCheck = new userStatusCheck(realmDB,mView.getContext(),userID);
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

    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
