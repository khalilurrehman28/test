package com.application.onlineTestSeries.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.onlineTestSeries.FullImageView.FullImage;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Utils.UtilsApp;
import com.application.onlineTestSeries.login.Models.UserData;
import com.application.onlineTestSeries.profile.profileActivity;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    LinearLayout btnCourses,btnTests,btnLegalMaxim,btnPracticeTest,btnSCLJ,btnNotes;
    TextView studentName;
    Realm realmDB;
    CircleImageView userImage;
    ImageView userImageBackground;
    String realmuserImage;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        initializeView(v);
        return v;
    }

    private void initializeView(View v) {
        Typeface customFont1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/LatoRegular.ttf");

        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        btnCourses = v.findViewById(R.id.btnCourses);
        btnTests = v.findViewById(R.id.btnTests);
        btnLegalMaxim = v.findViewById(R.id.btnLegalMaxim);
        btnSCLJ = v.findViewById(R.id.btnSCLJ);
        btnPracticeTest = v.findViewById(R.id.btnPracticeTest);
        btnNotes = v.findViewById(R.id.btnNotes);
        userImage = v.findViewById(R.id.userImage);
        userImageBackground =v.findViewById(R.id.userImageBackground);
        studentName = v.findViewById(R.id.studentName);
        studentName.setTypeface(customFont1);
        btnCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFragment(1,"courses");
            }
        });
        btnPracticeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFragment( 6,"practice test");
            }
        });
        btnSCLJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateFragment( 8,"landmark judgement");
                startActivity(new Intent(getActivity(), profileActivity.class));

            }
        });

        btnTests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFragment(5,"tests");
            }
        });
        btnNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFragment(2,"notes");

            }
        });
        btnLegalMaxim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFragment(3,"Bookmarks");

            }
        });

        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item: results){
            studentName.setText(item.getName());
            realmuserImage = item.getImage();

            if (!realmuserImage.equals("")|| realmuserImage.equals("NoImage")){
                Glide.with(this).load(UtilsApp.webUrlHome+realmuserImage).into(userImage);
            }else {
                Glide.with(this).load(R.drawable.ic_account_circle_black_36dp).into(userImage);
            }
        }
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!realmuserImage.equals("")){
                    Intent intent =new Intent(getActivity(),FullImage.class);
                    intent.putExtra("ImagePath", UtilsApp.webUrlHome+realmuserImage);
                    startActivity(intent);
                }

            }
        });
    }

    private void updateFragment(int index, String Tag) {
        ((MainActivity)getActivity()).updateNav(index,Tag);
        ((MainActivity)getActivity()).loadHomeFragment();
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
