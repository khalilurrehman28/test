package com.application.onlineTestSeries.Course;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.Chapters.ChapterActivity;
import com.application.onlineTestSeries.Course.Adapter.CourseAdapter;
import com.application.onlineTestSeries.Course.Models.CourseData;
import com.application.onlineTestSeries.Course.Models.CourseResponse;
import com.application.onlineTestSeries.Course.Models.state_user_model;
import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Utils.checkInternetState;
import com.application.onlineTestSeries.Utils.userStatusCheck;
import com.application.onlineTestSeries.cityAndState.UI.getStateActivity;
import com.application.onlineTestSeries.login.Models.UserData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class CourseView extends Fragment implements CourseAdapter.ContactsAdapterListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 101;
    CourseAdapter courseAdapter;
    List<CourseData> courseDataList,courseDataList1;
    RecyclerView courseRecycler;
    ProgressBar progressBar;
    TextView noCourseFound,noSearchResultFound;
    List<Integer> ColorArray;
    Random rand;
    Realm realmDB;
    TextView tvState,btnChangeState;
    String intentStateId,intentStateName,userID;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    View mView;
    SearchView searchView;
    userStatusCheck userStatusCheck;
    List<state_user_model> stateData;

    public CourseView() {
        // Required empty public constructor
    }

    public static CourseView newInstance(String param1, String param2) {
        CourseView fragment = new CourseView();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        ButterKnife.bind(this, view);
        initializeView(view);
    }

    private void initializeView(final View v) {
        noCourseFound = v.findViewById(R.id.noCourseFound);
        progressBar = v.findViewById(R.id.progressBar);
        noSearchResultFound = v.findViewById(R.id.noSearchResultFound);
        tvState =v.findViewById(R.id.tvState);
        btnChangeState =v.findViewById(R.id.btnChangeState);
        stateData = new ArrayList<>();

        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }
        courseDataList = new ArrayList<>();
        courseDataList1 = new ArrayList<>();
        courseAdapter = new CourseAdapter(getContext(), courseDataList,this);
        courseRecycler = v.findViewById(R.id.courseRecycler);
        courseRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        courseRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
        courseRecycler.setItemAnimator(new DefaultItemAnimator());
        courseRecycler.setAdapter(courseAdapter);

        ColorArray = new ArrayList<>();
        rand = new Random();
        final int[] MY_COLORS = {Color.rgb(192,0,0), Color.rgb(0,229,238), Color.rgb(255,192,0),
                Color.rgb(127,127,127), Color.rgb(146,208,80), Color.rgb(0,176,80), Color.rgb(79,129,189)
                , Color.rgb(0,128,128), Color.rgb(0,139,69),Color.rgb(255,215,0),Color.rgb(255,128,0)
                ,Color.rgb(255,106,106)};
        for (int item : MY_COLORS) {
            ColorArray.add(item);
        }

        /*Realm data check */
        RealmResults<CourseData> results = realmDB.where(CourseData.class).findAll();
        if (results.size()>0){
            updateDataFromRealm("0");
        }else{
            getDataFromServer(v);
        }

        btnChangeState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), getStateActivity.class);
                intent.putParcelableArrayListExtra("stateList", (ArrayList<? extends Parcelable>) stateData);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_courses, menu);
        MenuItem item = menu.findItem(R.id.search_action);
        searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Search Bare Acts...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                courseAdapter.getFilter().filter(query.toLowerCase().trim() );
                if(courseAdapter.getItemCount()<1){
                    courseRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+query.toString().trim()+"'");
                }else {
                    courseRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                courseAdapter.getFilter().filter(newText.toLowerCase().trim());
                if(courseAdapter.getItemCount()<1){
                    courseRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+newText.toString().trim()+"'");
                }else{
                    courseRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                courseRecycler.setVisibility(View.VISIBLE);
                noSearchResultFound.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh_data:
                courseDataList.clear();
                courseDataList1.clear();
                getDataFromServer(mView);
                courseRecycler.setVisibility(View.GONE);
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onContactSelected(CourseData getCourseData) {

        if(getCourseData.getTOTAL_CHAPTERS()>0){
            Intent i = new Intent(mView.getContext(), ChapterActivity.class);
            i.putExtra("courseID",""+getCourseData.getCOURSEID());
            i.putExtra("courseName",""+getCourseData.getCOURSETITLE());

            startActivity(i);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!searchView.isIconified()) {
                        searchView.onActionViewCollapsed();
                    }
                }
            }, 140);
        }else{
            showError("No chapter is added in this",mView);
        }
    }

    private void updateDataFromRealm(String i) {
        //showSuccess("Updated from Realm",v);
        RealmResults<CourseData> results;
        results = realmDB.where(CourseData.class).findAll();

        /*if (i.equals("0")){
            results = realmDB.where(CourseData.class).equalTo("cOURSESTATE",String.valueOf(0)).findAll();
        }else{
            results = realmDB.where(CourseData.class).equalTo("cOURSESTATE",i).findAll();
        }*/
        if (results.size()>0){
            noCourseFound.setVisibility(View.GONE);
            for (CourseData item: results) {
                int  n = rand.nextInt(ColorArray.size());
                if (n==ColorArray.size()){
                    n -=1;
                }

                if (item.getcOURSESTATE().equals(i)) {
                    if (item.getTOTAL_CHAPTERS() > 0) {
                        CourseData cd = new CourseData();
                        cd.setCOURSEDATE(item.getCOURSEDATE());
                        cd.setCOURSETITLE(item.getCOURSETITLE());
                        cd.setCOURSEID(item.getCOURSEID());
                        cd.setCOURSEDELSTATUS(item.getCOURSEDELSTATUS());
                        cd.setColor(ColorArray.get(n));
                        cd.setCOURSEDELSTATUS(item.getCOURSEDELSTATUS());
                        cd.setcOURSESTATE(item.getcOURSESTATE());
                        cd.setTOTAL_CHAPTERS(item.getTOTAL_CHAPTERS());
                        courseDataList.add(cd);
                    }
                }
                stateData.add(new state_user_model(Integer.parseInt(item.getcOURSESTATE()), item.getTOTAL_CHAPTERS()));
            }
            sortlist(courseDataList);
            courseAdapter.notifyDataSetChanged();
        }else{
            courseDataList.clear();
            courseAdapter.notifyDataSetChanged();
            noCourseFound.setVisibility(View.VISIBLE);
            noCourseFound.setText("No act available for "+intentStateName);
        }
    }

    private void getDataFromServer(View v) {
        if (!checkInternetState.Companion.getInstance(v.getContext()).isOnline()){
            showWarning("No Internet Connection",v);
        }else{
            updateData(v);
        }
    }

    private void showWarning(String s,View v) {
        Toasty.warning(v.getContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showSuccess(String s,View v) {
        Toasty.success(v.getContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showError(String s,View v) {
        Toasty.error(v.getContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void updateData(final View v) {
        progressBar.setVisibility(View.VISIBLE);
        noCourseFound.setVisibility(View.GONE);
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<CourseResponse> userCall = service.getcourseList();
        userCall.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    if (response.body().getStatus()) {
                        courseRecycler.setVisibility(View.VISIBLE);
                        List<CourseData> course = response.body().getData();
                        realmDB.beginTransaction();
                        realmDB.delete(CourseData.class);
                        realmDB.commitTransaction();

                        for (CourseData item: course) {
                            int  n = rand.nextInt(ColorArray.size());
                            if (n==ColorArray.size()){
                                n -=1;
                            }
                            realmDB.beginTransaction();
                            CourseData cdRealm = realmDB.createObject(CourseData.class,item.getCOURSEID());
                            cdRealm.setCOURSEDATE(item.getCOURSEDATE());
                            cdRealm.setCOURSETITLE(item.getCOURSETITLE());
                            cdRealm.setCOURSEDELSTATUS(item.getCOURSEDELSTATUS());
                            cdRealm.setcOURSESTATE(item.getcOURSESTATE());
                            cdRealm.setColor(ColorArray.get(n));
                            cdRealm.setCOURSEICON(item.getCOURSEICON());
                            cdRealm.setTOTAL_CHAPTERS(item.getTOTAL_CHAPTERS());
                            stateData.add(new state_user_model(Integer.parseInt(item.getcOURSESTATE()), item.getTOTAL_CHAPTERS()));

                            courseDataList1.add(cdRealm);

                            if (item.getcOURSESTATE().equals(String.valueOf(0))){
                                if (item.getTOTAL_CHAPTERS() > 0) {
                                    CourseData cd = new CourseData();
                                    cd.setCOURSEID(item.getCOURSEID());
                                    cd.setCOURSEDATE(item.getCOURSEDATE());
                                    cd.setCOURSEDELSTATUS(item.getCOURSEDELSTATUS());
                                    cd.setCOURSETITLE(item.getCOURSETITLE());
                                    cd.setcOURSESTATE(item.getcOURSESTATE());
                                    cd.setColor(ColorArray.get(n));
                                    cd.setCOURSEICON(item.getCOURSEICON());
                                    cd.setTOTAL_CHAPTERS(item.getTOTAL_CHAPTERS());
                                    courseDataList.add(cd);
                                }
                            }
                            realmDB.commitTransaction();
                            sortlist(courseDataList);
                            courseAdapter.notifyDataSetChanged();
                        }
                    }else{
                        noCourseFound.setVisibility(View.VISIBLE);
                        showWarning("No course available yet",v);
                    }
                }else{
                    showError("Something went wrong.",v);
                }
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void sortlist(List<CourseData> courseDataList) {
        Collections.sort(courseDataList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                CourseData p1 = (CourseData) o1;
                CourseData p2 = (CourseData) o2;
                return p2.getTOTAL_CHAPTERS().compareTo(p1.getTOTAL_CHAPTERS());
            }
        });
    }

    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
    }

    public void
    onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SECOND_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // get String data from Intent
                    intentStateId = data.getStringExtra("stateId");
                    intentStateName = data.getStringExtra("stateName");
                    tvState.setText(intentStateName);
                    courseDataList.clear();
                    updateDataFromRealm(intentStateId);
                }
                break;
            default:

                break;
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}