package com.application.onlineTestSeries.PracticeTest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.application.onlineTestSeries.HelperClasses.RecyclerTouchListener;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.PracticeTest.Adapter.practiceTestAdapter;
import com.application.onlineTestSeries.PracticeTest.CreateTest.CreateTest;
import com.application.onlineTestSeries.PracticeTest.Models.PracticeTestListResponse;
import com.application.onlineTestSeries.PracticeTest.Models.QuestionUpdateResponse;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestListData;
import com.application.onlineTestSeries.PracticeTest.practicTestGrabber.practiceTestGrabber;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Utils.checkInternetState;
import com.application.onlineTestSeries.Utils.userStatusCheck;
import com.application.onlineTestSeries.login.Models.UserData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PracticeTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PracticeTestFragment extends Fragment implements practiceTestAdapter.ContactsAdapterListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<practiceTestListData> testList;
    RecyclerView pyqpRecycler;
    ProgressBar progressBar;
    TextView noTestFound,noSearchResultFound;
    List<Integer> ColorArray;
    Random rand;
    practiceTestAdapter adapter;
    Context ctx;
    Realm realmDB;
    String userID,status, DateOfStart,PackageEndDate;
    View mView;
    SearchView searchView;
    String instructionData;
    userStatusCheck userStatusCheck;
    int counter = 1;

    public PracticeTestFragment() {
        // Required empty public constructor
    }

    public static PracticeTestFragment newInstance(String param1, String param2) {
        PracticeTestFragment fragment = new PracticeTestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_practice_test, container, false);
        initializeView(mView);
        return mView;
    }

    private void initializeView(final View v) {
        ctx = v.getContext();
        rand = new Random();
        testList = new ArrayList<>();
        ColorArray = new ArrayList<>();
        pyqpRecycler = v.findViewById(R.id.pyqpRecycler);
        progressBar = v.findViewById(R.id.progressBar);
        noTestFound = v.findViewById(R.id.noCourseFound);
        noSearchResultFound = v.findViewById(R.id.noSearchResultFound);

        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        adapter = new practiceTestAdapter(ctx, testList,this);
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ctx, 1);
        pyqpRecycler.setLayoutManager(mLayoutManager);
        pyqpRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
        pyqpRecycler.setItemAnimator(new DefaultItemAnimator());
        pyqpRecycler.setAdapter(adapter);
        final int[] MY_COLORS = {Color.rgb(192,0,0), Color.rgb(0,229,238), Color.rgb(255,192,0),
                Color.rgb(127,127,127), Color.rgb(146,208,80), Color.rgb(0,176,80), Color.rgb(79,129,189)
                , Color.rgb(0,128,128), Color.rgb(0,139,69),Color.rgb(255,215,0),Color.rgb(255,128,0)
                ,Color.rgb(255,106,106)};
        for (int item : MY_COLORS) {
            ColorArray.add(item);
        }

        pyqpRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                //int lastVisibleItemPosition= ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                // Load more if we have reach the end to the recyclerView
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    //loadMoreItems(lastVisibleItemPosition,++counter);
                    ++counter;
                    Log.d(TAG, "onScrolled: counter" + counter);
                    getTestFromServer(v);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item: results){
            userID = item.getUserId();
            status = item.getActive();
            DateOfStart = item.getFirstInstall();
            PackageEndDate = item.getStartDate();
        }

        userStatusCheck = new userStatusCheck(realmDB,mView.getContext(),userID);

        pyqpRecycler.addOnItemTouchListener(new RecyclerTouchListener(mView.getContext(), pyqpRecycler, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                if (Integer.parseInt(testList.get(position).getCREATED_BY()) == 2) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(mView.getContext());
                    builder.setTitle("Delete test")
                        .setMessage("Are you sure you want to delete test?")
                        .setPositiveButton("Delete ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressBar.setVisibility(View.VISIBLE);
                                deleteTest(testList.get(position).getTESTID());
                                testList.clear();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_delete)
                        .show();
                }
            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();
        userStatusCheck.checkUpdatedStatus();
    }

    private void deleteTest(String testid) {
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<QuestionUpdateResponse> userCall = service.deleteTest(testid,userID);
        userCall.enqueue(new Callback<QuestionUpdateResponse>() {
            @Override
            public void onResponse(Call<QuestionUpdateResponse> call, Response<QuestionUpdateResponse> response) {
                if (response != null && response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        Log.d("userTest", response.body().getMessage());
                        getTestFromServer(mView);
                    } else {
                        Log.d("userTest", response.body().getMessage());
                    }
                } else {
                    //Toast.makeText( , "data not found", Toast.LENGTH_SHORT).show();
                    Log.d("userQuestion", "data not found");
                }
            }

            @Override
            public void onFailure(Call<QuestionUpdateResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        counter = 1;
        testList.clear();
        adapter.notifyDataSetChanged();
        getTestFromServer(mView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.test_main_menu, menu);
        MenuItem item = menu.findItem(R.id.search_action);
        MenuItem RefreshItem = menu.findItem(R.id.createTest);
        //RefreshItem.setVisible(false);

        searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Search Test...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query.toLowerCase());
                if(adapter.getItemCount()<1){
                    pyqpRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+query.toString().trim()+"'");
                }else {
                    pyqpRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query.toLowerCase().trim());
                if(adapter.getItemCount()<1){
                    pyqpRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+query.toString().trim()+"'");
                }else {
                    pyqpRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                pyqpRecycler.setVisibility(View.VISIBLE);
                noSearchResultFound.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.createTest:
                //Toast.makeText(ctx, "refresh click ", Toast.LENGTH_SHORT).show();;
                Intent i = new Intent(mView.getContext(),CreateTest.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onContactSelected(practiceTestListData PYQPTestData) {
        if (PYQPTestData.getCount()>0){
            Intent i =new Intent(mView.getContext(), practiceTestGrabber.class);
            i.putExtra("testID",PYQPTestData.getTESTID());
            i.putExtra("testName",PYQPTestData.getTESTNAME());
            i.putExtra("testPrice", PYQPTestData.getTEST_PRICE());
            i.putExtra("testQuestion",PYQPTestData.getCount());
            i.putExtra("instructionData",instructionData);
            i.putExtra("maxquestion",PYQPTestData.getTEST_MAX_QUESTION());
            i.putExtra("subscription", PYQPTestData.getSubscriptionFlag());
            i.putExtra("purschased_Status", PYQPTestData.getTEST_PURCHASE());
            startActivity(i);
        }else{
            showError("No question added for test",mView);
        }
    }

    public String getCurrentUTC(){
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd");
        outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return outputFmt.format(time);
    }

    private void getTestFromServer(final View v) {
        if (!checkInternetState.Companion.getInstance(v.getContext()).isOnline()){
            showWarning("No Internet Connection",v);
        }else{
            progressBar.setVisibility(View.VISIBLE);
            noTestFound.setVisibility(View.GONE);
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<PracticeTestListResponse> userCall = service.get_all_practice_test(Integer.parseInt(userID), counter);
            userCall.enqueue(new Callback<PracticeTestListResponse>() {
                @Override
                public void onResponse(Call<PracticeTestListResponse> call, Response<PracticeTestListResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()){
                        if (response.body().getStatus()) {
                            instructionData = response.body().getInstruction();

                            List<practiceTestListData> course = response.body().getData();

                            for (practiceTestListData item: course) {
                                int  n = rand.nextInt(ColorArray.size());
                                if (n==ColorArray.size()){
                                    n -=1;
                                }
                                int count;

                                if (!item.getTESTQUESTIONS().equals("")){
                                    count = item.getTESTQUESTIONS().split("\\|").length;
                                }else{
                                    count = 0;
                                }

                                practiceTestListData cd = new practiceTestListData();
                                cd.setCount(count);
                                cd.setTESTID(item.getTESTID());
                                cd.setTESTDURATION(item.getTESTDURATION());
                                cd.setColor(ColorArray.get(n));
                                cd.setTESTNAME(item.getTESTNAME());
                                cd.setCREATED_BY(item.getCREATED_BY());
                                cd.setTEST_MAX_QUESTION(item.getTEST_MAX_QUESTION());
                                cd.setTEST_PURCHASE(item.getTEST_PURCHASE());
                                cd.setSubscriptionFlag(response.body().getSubscriptionFlag());
                                cd.setTEST_PRICE(item.getTEST_PRICE());
                                Log.d(TAG, "onResponse: " + response.body().getSubscriptionFlag());
                                testList.add(cd);
                                adapter.notifyDataSetChanged();
                            }
                        } else {

                            if (response.body().getData().size() == 0 && testList.size() > 0) {
                                //showWarning("No more test available",v);
                            } else {
                                noTestFound.setVisibility(View.VISIBLE);
                                showWarning("No Test available yet", v);
                            }
                        }
                    }else{
                        showError("Something went wrong.",v);
                    }
                }

                @Override
                public void onFailure(Call<PracticeTestListResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }


    private void showWarning(String s,View v) {
        Toasty.warning(v.getContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showError(String s,View v) {
        Toasty.error(v.getContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
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



}
