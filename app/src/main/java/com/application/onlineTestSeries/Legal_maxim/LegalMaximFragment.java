package com.application.onlineTestSeries.Legal_maxim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.Legal_maxim.Models.LeximsData;
import com.application.onlineTestSeries.Legal_maxim.Models.LeximsResponse;
import com.application.onlineTestSeries.Legal_maxim.adapter.legalmaximsAdapter;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.Utils.checkInternetState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

public class LegalMaximFragment extends Fragment implements legalmaximsAdapter.ContactsAdapterListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ExpandableListView expandableListView;
    private List<String> parentHeaderInformation;
    List<LeximsData> LeximsDataList,LeximsDataList1;
    private HashMap<String, List<String>> allChildItems;
    ProgressBar progressBar;
    TextView noDataFound,noSearchResultFound;
    legalmaximsAdapter adapter;
    Realm realmDB;
    Random rand;
    List<Integer> ColorArray;
    RecyclerView leximsRecycler;
    View mView;
    SearchView searchView;


    public LegalMaximFragment() {
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
    public static LegalMaximFragment newInstance(String param1, String param2) {
        LegalMaximFragment fragment = new LegalMaximFragment();
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
        // Inflate the layout for this fragment
        mView =inflater.inflate(R.layout.fragment_legal_maxims, container, false);
        ButterKnife.bind(this, mView);
        initializeView(mView);
        return mView;
    }

    private void initializeView(View v) {
        ColorArray = new ArrayList<>();
        progressBar = v.findViewById(R.id.progressBar);
        noDataFound = v.findViewById(R.id.noDataFound);
        noSearchResultFound = v.findViewById(R.id.noSearchResultFound);

        rand = new Random();
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }
        parentHeaderInformation = new ArrayList<String>();
        LeximsDataList = new ArrayList<>();
        LeximsDataList1 = new ArrayList<>();
        allChildItems= new HashMap<String, List<String>>();
        //expandableListView = (ExpandableListView)v.findViewById(R.id.expandableListView);
        adapter = new legalmaximsAdapter(getContext(),LeximsDataList,this);
        leximsRecycler = v.findViewById(R.id.recycler_view);
        leximsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
        leximsRecycler.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        leximsRecycler.setItemAnimator(new DefaultItemAnimator());
        leximsRecycler.setAdapter(adapter);

        final int[] MY_COLORS = {Color.rgb(192,0,0), Color.rgb(0,229,238), Color.rgb(255,192,0),
                Color.rgb(127,127,127), Color.rgb(146,208,80), Color.rgb(0,176,80), Color.rgb(79,129,189)
                , Color.rgb(0,128,128), Color.rgb(0,139,69),Color.rgb(255,215,0),Color.rgb(255,128,0)
                ,Color.rgb(255,106,106)};
        for (int item : MY_COLORS) {
            ColorArray.add(item);
        }
        RealmResults<LeximsData> results;

        results = realmDB.where(LeximsData.class).findAll();

        if (results.size()>0){
            updateDataFromRealm(mView);

        }else{

            getDataFromServer(v);
        }


    }

    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
    }

    private void getDataFromServer(View v) {
        progressBar.setVisibility(View.VISIBLE);
        noDataFound.setVisibility(View.GONE);
        if (!checkInternetState.Companion.getInstance(v.getContext()).isOnline()){
            showWarning("No Internet Connection",v);
            progressBar.setVisibility(View.GONE);
        }else{
            //getData(v);
            new leximsDataGetter(v).execute();

        }
    }
    private void updateDataFromRealm(View v) {
        RealmResults<LeximsData> results;

        results = realmDB.where(LeximsData.class).findAll();

        if (results.size()>0){
            noDataFound.setVisibility(View.GONE);
            for (LeximsData item: results) {
                int  n = rand.nextInt(ColorArray.size());
                if (n==ColorArray.size()){
                    n -=1;
                }

                LeximsData cd = new LeximsData();
                cd.setLMID(item.getLMID());
                cd.setLMTITLE(item.getLMTITLE());
                cd.setLMDESC(item.getLMDESC());
                cd.setColor(ColorArray.get(n));

                LeximsDataList.add(cd);
//                courseAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }
        }else{
            LeximsDataList.clear();
            adapter.notifyDataSetChanged();
            noDataFound.setVisibility(View.VISIBLE);
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
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_courses, menu);
        MenuItem item = menu.findItem(R.id.search_action);

        searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query.toLowerCase().trim() );
                if(adapter.getItemCount()<1){
                    leximsRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+query.toString().trim()+"'");
                }else {
                    leximsRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText.toLowerCase().trim());

                if(adapter.getItemCount()<1){
                    leximsRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+newText.toString().trim()+"'");
                }else{
                    leximsRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;
            }


        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                leximsRecycler.setVisibility(View.VISIBLE);
                noSearchResultFound.setVisibility(View.GONE);
                return false;
            }
        });

        //super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh_data:
                LeximsDataList.clear();
                LeximsDataList1.clear();
                getDataFromServer(mView);
                leximsRecycler.setVisibility(View.GONE);
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactSelected(LeximsData LeximsData) {
        RealmResults<subscriptionUserData> results = realmDB.where(subscriptionUserData.class).findAll();
        String status ="",isActive ="",date = "";
        for (subscriptionUserData item: results) {
            status = item.getSUBSCRIPTIONSTATUS();
            isActive = item.getSUBSCRIBERSUBSCRIPTIONSTATUS();
            date = item.getSUBSCRIBERSUBSCRIPTIONDATE();
        }
        if (LeximsData.getCounter() == 1 || LeximsData.getCounter() == 2 || LeximsData.getCounter() == 3) {
            Intent i = new Intent(mView.getContext(), LeximsPreview.class);
            i.putExtra("LMID", "" + LeximsData.getLMID());
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
                    userDate = sdf.parse(date);
                    todayDate = sdf.parse(sdf.format(new Date()));
                    if (todayDate.before(userDate) || userDate.compareTo(todayDate) == 0) {
                        Intent i = new Intent(mView.getContext(), LeximsPreview.class);
                        i.putExtra("LMID", "" + LeximsData.getLMID());
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
                        //showWarning("Plan expired please update",mView);
                        showAlertDialogue("Your Plan will expired please re-subscribe the plan.");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                //showWarning("Please subscribe to plan",mView);
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

    public class leximsDataGetter extends AsyncTask<String,String,String>{
        View v;
        leximsDataGetter(View v){
            this.v = v;
        }

        @Override
        protected String doInBackground(String... strings) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<LeximsResponse> userCall = service.get_legal_maxim();
            userCall.enqueue(new Callback<LeximsResponse>() {
                @Override
                public void onResponse(Call<LeximsResponse> call, Response<LeximsResponse> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()){
                        if (response.body().getStatus()) {
                            leximsRecycler.setVisibility(View.VISIBLE);

                            realmDB.beginTransaction();
                            realmDB.delete(LeximsData.class);
                            realmDB.commitTransaction();
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                int  n = rand.nextInt(ColorArray.size());
                                if (n==ColorArray.size()){
                                    n -=1;
                                }
                                realmDB.beginTransaction();
                                LeximsData leximDataRealm = realmDB.createObject(LeximsData.class,response.body().getData().get(i).getLMID());
                                leximDataRealm.setLMTITLE(response.body().getData().get(i).getLMTITLE());
                                leximDataRealm.setLMDESC(response.body().getData().get(i).getLMDESC());
                                leximDataRealm.setColor(ColorArray.get(n));
                                leximDataRealm.setCounter(i + 1);

                                LeximsData leximDataServer = new LeximsData();
                                leximDataServer.setLMID(response.body().getData().get(i).getLMID());
                                leximDataServer.setLMTITLE(response.body().getData().get(i).getLMTITLE());
                                leximDataServer.setLMDESC(response.body().getData().get(i).getLMDESC());
                                leximDataServer.setCounter(i + 1);
                                leximDataServer.setColor(ColorArray.get(n));

                                LeximsDataList1.add(leximDataRealm);
                                LeximsDataList.add(leximDataServer);
                                realmDB.commitTransaction();
                                adapter.notifyDataSetChanged();
                            }

                        }else{

                            showWarning("No data available yet",v);
                            noDataFound.setVisibility(View.VISIBLE);
                        }
                    }else{
                        showError("Something went wrong.",v);
                    }
                }

                @Override
                public void onFailure(Call<LeximsResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("onFailure", t.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
}
