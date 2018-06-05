package com.application.onlineTestSeries.landmarkJudgement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.Utils.checkInternetState;
import com.application.onlineTestSeries.landmarkJudgement.FilterActivity.judgementFilter;
import com.application.onlineTestSeries.landmarkJudgement.adapter.landmarkAdapter;
import com.application.onlineTestSeries.landmarkJudgement.models.JudgementResponse;
import com.application.onlineTestSeries.landmarkJudgement.models.judgementData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import static android.content.ContentValues.TAG;

public class LandmarkJudgementFragment extends Fragment implements landmarkAdapter.ContactsAdapterListener{
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private landmarkAdapter landmarkAdapter;
    List<judgementData> judgementDataList,judgementDataList1;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView noSCLJFound,noSearchResultFound,loadPrevious;
    List<Integer> ColorArray;
    Random rand;
    View mView;
    SearchView searchView;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 101;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Realm realm;
    int counter = 1;
    int listCounter = 0;
    public LandmarkJudgementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LandmarkJudgementFragment newInstance(String param1, String param2) {
        LandmarkJudgementFragment fragment = new LandmarkJudgementFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_landmark_judgement, container, false);
        ButterKnife.bind(this, mView);
        initializeView(mView);
        return mView;
    }

    private void initializeView(final View v) {
        progressBar = v.findViewById(R.id.progressBar);
        noSCLJFound = v.findViewById(R.id.noSCLJFound);
        noSearchResultFound = v.findViewById(R.id.noSearchResultFound);
        loadPrevious = v.findViewById(R.id.loadPrevious);
        rand = new Random();

        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realm = Realm.getInstance(config);
        }

        ColorArray = new ArrayList<>();
        judgementDataList = new ArrayList<>();
        judgementDataList1 = new ArrayList<>();
        landmarkAdapter = new landmarkAdapter(getContext(), judgementDataList,this);
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView = v.findViewById(R.id.landmarkJudgementRecycler);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(landmarkAdapter);
        final int[] MY_COLORS = {Color.rgb(192,0,0), Color.rgb(0,229,238), Color.rgb(255,192,0),
                Color.rgb(127,127,127), Color.rgb(146,208,80), Color.rgb(0,176,80), Color.rgb(79,129,189)
                , Color.rgb(0,128,128), Color.rgb(0,139,69),Color.rgb(255,215,0),Color.rgb(255,128,0)
                ,Color.rgb(255,106,106)};
        for (int item : MY_COLORS) {
            ColorArray.add(item);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int visibleItemCount        = mLayoutManager.getChildCount();
                int totalItemCount          = mLayoutManager.getItemCount();
                int firstVisibleItemPosition= ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                int lastVisibleItemPosition= ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                /*Log.d(TAG, "onScrolled: visible"+visibleItemCount);
                Log.d(TAG, "onScrolled: total"+totalItemCount);
                Log.d(TAG, "onScrolled: first"+firstVisibleItemPosition);
                Log.d(TAG, "onScrolled: last"+lastVisibleItemPosition);*/
                //Log.d(TAG, "onScrolled: 4"+counter);

                // Load more if we have reach the end to the recyclerView
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    //loadMoreItems(lastVisibleItemPosition,++counter);
                    ++counter;
                    Log.d(TAG, "onScrolled: counter"+counter);
                    getDataFromServer(v);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        loadPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter =1;
                getDataFromServer(v);
            }
        });

        getDataFromServer(v);
    }

   /* private void loadMoreItems(int firstVisibleItemPosition, int counter) {
        //showSuccess("i am at "+firstVisibleItemPosition+"-----"+counter,mView);
        Toast.makeText(mView.getContext(), "i am at "+firstVisibleItemPosition+"-----"+counter, Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menusclj, menu);
        MenuItem item = menu.findItem(R.id.search_action);

        searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Search chapter...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                landmarkAdapter.getFilter().filter(query.toLowerCase().trim());
                if(landmarkAdapter.getItemCount()<1){
                    recyclerView.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+query.toString().trim()+"'");
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String query) {
                landmarkAdapter.getFilter().filter(query.toLowerCase().trim());
                if(landmarkAdapter.getItemCount()<1){
                    recyclerView.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+query.toString().trim()+"'");
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setVisibility(View.VISIBLE);
                noSearchResultFound.setVisibility(View.GONE);
                return false;
            }
        });

        //super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.filter:
                Intent intent = new Intent(getContext(), judgementFilter.class);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);

                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactSelected(judgementData judgementData) {
        RealmResults<subscriptionUserData> results = realm.where(subscriptionUserData.class).findAll();
        String status ="",isActive ="",date = "";
        for (subscriptionUserData item: results) {
            status = item.getSUBSCRIPTIONSTATUS();
            isActive = item.getSUBSCRIBERSUBSCRIPTIONSTATUS();
            date = item.getSUBSCRIBERSUBSCRIPTIONDATE();
        }
        Log.d(TAG, "onContactSelected: "+date);

        if (judgementData.getCounter() == 1 || judgementData.getCounter() == 2 || judgementData.getCounter() == 3) {
            Intent i = new Intent(mView.getContext(), landmarkJudgementPreview.class);
            i.putExtra("SCLJID", "" + judgementData.getSCLJID());
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
        } else {
            if (isActive.equals("1") && status.equals("Active")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date userDate = null, todayDate;
                try {
                    todayDate = sdf.parse(sdf.format(new Date()));

                    userDate = sdf.parse(date);
                    Log.d(TAG, "onContactSelected: " + userDate + "-----" + todayDate);
                    if (todayDate.before(userDate) || userDate.compareTo(todayDate) == 0) {
                        Intent i = new Intent(mView.getContext(), landmarkJudgementPreview.class);
                        i.putExtra("SCLJID", "" + judgementData.getSCLJID());
                   /* i.putExtra("SCLJDate",""+judgementData.getSCLJDATE());
                    i.putExtra("SCLJCaseNo",""+judgementData.getSCLJCASENO());
                    i.putExtra("SCLJParty1",""+judgementData.getSCLJPARTY1());
                    i.putExtra("SCLJParty2",""+judgementData.getSCLJPARTY2());
                    i.putExtra("SCLJJudgement",""+judgementData.getSCLJJUDGEMENT());*/
                        startActivity(i);
                        //Toast.makeText(mView.getContext(), ""+judgementData.getSCLJID(), Toast.LENGTH_SHORT).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!searchView.isIconified()) {
                                    searchView.onActionViewCollapsed();
                                }
                            }
                        }, 140);
                    } else {
                        showAlertDialogue("Your Plan will expired please re-subscribe the plan.");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
//            showWarning("Please subscribe to plan",mView);
                showAlertDialogue("Do you want to subscribe the plan?");
            }
        }
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
        noSCLJFound.setVisibility(View.GONE);
        loadPrevious.setVisibility(View.GONE);
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<JudgementResponse> userCall = service.get_judgement(counter);
        userCall.enqueue(new Callback<JudgementResponse>() {
            @Override
            public void onResponse(Call<JudgementResponse> call, Response<JudgementResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    if (response.body().getStatus()) {
                        if (response.body().getData() != null){
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                int  n = rand.nextInt(ColorArray.size());
                                if (n==ColorArray.size()){
                                    n -=1;
                                }

                                realm.beginTransaction();

                                String judgementDate;
                                if (!response.body().getData().get(i).getSCLJDATE().equals("1970-01-01 00:00:00")){
                                    judgementDate = response.body().getData().get(i).getSCLJDATE();
                                }else{
                                    judgementDate ="";
                                }

                                judgementData judgementDatarealm = new judgementData();
                                judgementDatarealm.setSCLJID(response.body().getData().get(i).getSCLJID());
                                judgementDatarealm.setColor(ColorArray.get(n));
                                judgementDatarealm.setSCLJCASENO(response.body().getData().get(i).getSCLJCASENO());
                                judgementDatarealm.setSCLJDATE(judgementDate);
                                judgementDatarealm.setSCLJJUDGEMENT(response.body().getData().get(i).getSCLJJUDGEMENT());
                                judgementDatarealm.setSCLJPARTY1(response.body().getData().get(i).getSCLJPARTY1());
                                judgementDatarealm.setSCLJPARTY2(response.body().getData().get(i).getSCLJPARTY2());
                                judgementDatarealm.setSCLJTITLE(response.body().getData().get(i).getSCLJTITLE());
                                judgementDatarealm.setCounter(listCounter += 1);

                                Log.d(TAG, "onResponse: " + listCounter);

                                judgementDataList.add(judgementDatarealm);
                                landmarkAdapter.notifyDataSetChanged();
                                realm.insertOrUpdate(judgementDatarealm);
                                realm.commitTransaction();

                            }
                        }else{
                            showWarning("No SCLJ found Here.",v);
                        }
                        //showSuccess("Course Updated",v);
                    }else{
                        if (judgementDataList.size()<=0){
                            loadPrevious.setVisibility(View.VISIBLE);
                            noSCLJFound.setVisibility(View.VISIBLE);
                            showWarning("No SCLJ found.",v);
                        }
                    }
                }else{
                    showError("Something went wrong.",v);
                }
            }

            @Override
            public void onFailure(Call<JudgementResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SECOND_ACTIVITY_REQUEST_CODE:
                if (resultCode==RESULT_OK){
                    String date = data.getStringExtra("date");
                    String caseno = data.getStringExtra("caseno");
                    updateDateUsingFilter(date,caseno);
                }

                break;
        }


    }

    private void updateDateUsingFilter(String date, String caseno) {
//        Toast.makeText(mView.getContext(), date+"----"+caseno, Toast.LENGTH_SHORT).show();

        progressBar.setVisibility(View.VISIBLE);
        //recyclerView.setVisibility(View.GONE);
        noSCLJFound.setVisibility(View.GONE);
        judgementDataList.clear();
        landmarkAdapter.notifyDataSetChanged();
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<JudgementResponse> userCall = service.searchJudgement(date,caseno);
        userCall.enqueue(new Callback<JudgementResponse>() {
            @Override
            public void onResponse(Call<JudgementResponse> call, Response<JudgementResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    if (response.body().getStatus()) {
                        //recyclerView.setVisibility(View.GONE);
                        //String cATEGORYID, String cATEGORYTITLE, String cATEGORYDATE, String sTATEIDFK, String cATEGORYDELSTATUS
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            int  n = rand.nextInt(ColorArray.size());
                            if (n==ColorArray.size()){
                                n -=1;
                            }

                            String judgementDate;
                            Log.d(TAG, "onResponse: "+response.body().getData().get(i).getSCLJDATE());
                            if (!response.body().getData().get(i).getSCLJDATE().equals("1970-01-01 00:00:00")){
                                judgementDate = response.body().getData().get(i).getSCLJDATE();
                            }else{
                                judgementDate ="";
                            }

                            realm.beginTransaction();
                            judgementData judgementDatarealm = new judgementData();
                            judgementDatarealm.setSCLJID(response.body().getData().get(i).getSCLJID());
                            judgementDatarealm.setColor(ColorArray.get(n));
                            judgementDatarealm.setSCLJCASENO(response.body().getData().get(i).getSCLJCASENO());
                            judgementDatarealm.setSCLJDATE(judgementDate);
                            //judgementData.setSCLJID(response.body().getData().get(i).getSCLJID());
                            judgementDatarealm.setSCLJJUDGEMENT(response.body().getData().get(i).getSCLJJUDGEMENT());
                            judgementDatarealm.setSCLJPARTY1(response.body().getData().get(i).getSCLJPARTY1());
                            judgementDatarealm.setSCLJPARTY2(response.body().getData().get(i).getSCLJPARTY2());
                            judgementDatarealm.setSCLJTITLE(response.body().getData().get(i).getSCLJTITLE());
                            realm.insertOrUpdate(judgementDatarealm);
                            realm.commitTransaction();


                            /*judgementData judgementData = new judgementData();
                            judgementData.setColor(ColorArray.get(n));
                            judgementData.setSCLJCASENO(response.body().getData().get(i).getSCLJCASENO());
                            judgementData.setSCLJDATE(judgementDate);
                            judgementData.setSCLJID(response.body().getData().get(i).getSCLJID());
                            judgementData.setSCLJJUDGEMENT(response.body().getData().get(i).getSCLJJUDGEMENT());
                            judgementData.setSCLJPARTY1(response.body().getData().get(i).getSCLJPARTY1());
                            judgementData.setSCLJPARTY2(response.body().getData().get(i).getSCLJPARTY2());
                            judgementData.setSCLJTITLE(response.body().getData().get(i).getSCLJTITLE());
*/
                            //judgementDataList.add(judgementData);
                            judgementDataList.add(judgementDatarealm);
                            //adapter.notifyDataSetChanged();
                            landmarkAdapter.notifyDataSetChanged();
                        }

                        //showSuccess("Course Updated",v);
                    }else{
                        loadPrevious.setVisibility(View.VISIBLE);
                        noSCLJFound.setVisibility(View.VISIBLE);
                        showWarning("No SCLJ found.",mView);
                    }
                }else{
                    showError("Something went wrong.",mView);
                }
            }

            @Override
            public void onFailure(Call<JudgementResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
                progressBar.setVisibility(View.GONE);

            }
        });
    }
}
