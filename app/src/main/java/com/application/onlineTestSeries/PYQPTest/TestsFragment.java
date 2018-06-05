package com.application.onlineTestSeries.PYQPTest;

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
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.PYQPTest.Adapter.PYQPAdapter;
import com.application.onlineTestSeries.PYQPTest.Adapter.getPYQPStateAdapter;
import com.application.onlineTestSeries.PYQPTest.Models.PYQPStates;
import com.application.onlineTestSeries.PYQPTest.Models.PYQPTestData;
import com.application.onlineTestSeries.PYQPTest.Models.PYQPTestResponse;
import com.application.onlineTestSeries.PYQPTest.Models.State;
import com.application.onlineTestSeries.PYQPTest.Test.TestResult.resultTestActivity;
import com.application.onlineTestSeries.PracticeTest.Models.QuestionUpdateResponse;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.Utils.userStatusCheck;
import com.application.onlineTestSeries.cityAndState.UI.getStateActivity;
import com.application.onlineTestSeries.login.Models.UserData;
import com.application.onlineTestSeries.termsAndConditions.termsConditionActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.Utils.checkInternetState;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestsFragment extends Fragment implements getPYQPStateAdapter.StateAdapterListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    List<PYQPTestData> testList;
    List<State> stateList;
    RecyclerView pyqpRecycler;
    ProgressBar progressBar;
    TextView noTestFound, noSearchResultFound;
    List<Integer> ColorArray;
    Random rand;
    PYQPAdapter adapter;
    Context ctx;
    Realm realmDB;
    String userID, status, DateOfStart, PackageEndDate;
    View mView;
    SearchView searchView;
    userStatusCheck userStatusCheck;
    getPYQPStateAdapter getPYQPStateAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TestsFragment() {
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
    public static TestsFragment newInstance(String param1, String param2) {
        TestsFragment fragment = new TestsFragment();
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
        mView = inflater.inflate(R.layout.fragment_tests, container, false);
        initializeView(mView);
        return mView;
    }

    private void initializeView(final View v) {
        ctx = v.getContext();
        testList = new ArrayList<>();
        rand = new Random();
        ColorArray = new ArrayList<>();
        stateList = new ArrayList<>();
        pyqpRecycler = v.findViewById(R.id.pyqpRecycler);
        progressBar = v.findViewById(R.id.progressBar);
        noTestFound = v.findViewById(R.id.noCourseFound);
        noSearchResultFound = v.findViewById(R.id.noSearchResultFound);
        getPYQPStateAdapter = new getPYQPStateAdapter(ctx,stateList,this);
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        //adapter = new PYQPAdapter(ctx, testList, this);
        /*RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ctx, 1);
        pyqpRecycler.setLayoutManager(mLayoutManager);
        pyqpRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
        pyqpRecycler.setItemAnimator(new DefaultItemAnimator());
        pyqpRecycler.setAdapter(adapter);*/

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ctx, 1);
        //pyqpRecycler = v.findViewById(R.id.recycler_view);
        pyqpRecycler.setLayoutManager(mLayoutManager);
        pyqpRecycler.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        pyqpRecycler.setItemAnimator(new DefaultItemAnimator());
        pyqpRecycler.setAdapter(getPYQPStateAdapter);

        final int[] MY_COLORS = {Color.rgb(192, 0, 0), Color.rgb(0, 229, 238), Color.rgb(255, 192, 0),
                Color.rgb(127, 127, 127), Color.rgb(146, 208, 80), Color.rgb(0, 176, 80), Color.rgb(79, 129, 189)
                , Color.rgb(0, 128, 128), Color.rgb(0, 139, 69), Color.rgb(255, 215, 0), Color.rgb(255, 128, 0)
                , Color.rgb(255, 106, 106)};
        for (int item : MY_COLORS) {
            ColorArray.add(item);
        }


        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item : results) {
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
                if (testList.get(position).isStatus()) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(mView.getContext());
                    builder.setTitle("Reset test")
                            .setMessage("Are you sure you want to reset test?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    resetTest(testList.get(position).getPYQPTESTID());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }));

        if (!checkInternetState.Companion.getInstance(v.getContext()).isOnline()) {
            showWarning("No Internet Connection", v);
        } else {
            getTestFromServer(mView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        userStatusCheck.checkUpdatedStatus();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.test_main_menu, menu);
        MenuItem item = menu.findItem(R.id.search_action);
        MenuItem createTest = menu.findItem(R.id.createTest);
        createTest.setVisible(false);
        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search State...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query.toLowerCase());
                if (adapter.getItemCount() < 1) {
                    pyqpRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '" + query.toString().trim() + "'");
                } else {
                    pyqpRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                getPYQPStateAdapter.getFilter().filter(query.toLowerCase().trim());
                if (getPYQPStateAdapter.getItemCount() < 1) {
                    pyqpRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '" + query.toString().trim() + "'");
                } else {
                    pyqpRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }                return false;
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

        //super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.createTest:
                //Toast.makeText(ctx, "refresh click ", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onContactSelected(PYQPTestData PYQPTestData) {
        if (PYQPTestData.getCount() > 0) {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-mm-dd");

            if (!PYQPTestData.isStatus()) {

                if (PYQPTestData.getPYQPTESTTYPE().equals("0")){
                    Intent i = new Intent(mView.getContext(), termsConditionActivity.class);
                    i.putExtra("testID", PYQPTestData.getPYQPTESTID());
                    i.putExtra("description", PYQPTestData.getPYQPTESTDESC());
                    i.putExtra("testName", PYQPTestData.getPYQPTESTNAME());
                    i.putExtra("testDuration", PYQPTestData.getPYQPTESTDURATION());
                    startActivity(i);
                }else{
                    RealmResults<subscriptionUserData> results = realmDB.where(subscriptionUserData.class).findAll();
                    String status ="",isActive ="",date = "";
                    for (subscriptionUserData item: results) {
                        status = item.getSUBSCRIPTIONSTATUS();
                        isActive = item.getSUBSCRIBERSUBSCRIPTIONSTATUS();
                        date = item.getSUBSCRIBERSUBSCRIPTIONDATE();
                    }

                    if (isActive.equals("1") && status.equals("Active")){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date userDate = null, todayDate;
                        try {
                            userDate = sdf.parse(date);
                            todayDate = sdf.parse(sdf.format(new Date()));
                            if(todayDate.before(userDate) || userDate.compareTo(todayDate) == 0){
                                Intent i = new Intent(mView.getContext(), termsConditionActivity.class);
                                i.putExtra("testID", PYQPTestData.getPYQPTESTID());
                                i.putExtra("description", PYQPTestData.getPYQPTESTDESC());
                                i.putExtra("testName", PYQPTestData.getPYQPTESTNAME());
                                i.putExtra("testDuration", PYQPTestData.getPYQPTESTDURATION());
                                startActivity(i);
                            }else{
                                //showWarning("Plan expired please update",mView);
                                showAlertDialogue("Your Plan will expired please re-subscribe the plan.");
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //showWarning("Please subscribe to plan",mView);
                        showAlertDialogue("Do you want to subscribe the plan?");
                    }
                }

            } else {
                //showInfo("You already attempt this test", mView);
                Intent i = new Intent(mView.getContext(), resultTestActivity.class);
                i.putExtra("correct", PYQPTestData.getCORRECT_ANSWER());
                i.putExtra("wrong", PYQPTestData.getWRONG_ANSWER());
                i.putExtra("attempt", PYQPTestData.getATTEMPTED_ANSWER());
                i.putExtra("total", PYQPTestData.getTOTAL_QUESTION());
                i.putExtra("testID", PYQPTestData.getPYQPTESTID());
                i.putExtra("testName", PYQPTestData.getPYQPTESTNAME());

                startActivity(i);

            }

            try {
                Date date1 = myFormat.parse(PackageEndDate);
                Date date2 = myFormat.parse(getCurrentUTC());
                long diff = date2.getTime() - date1.getTime();
                System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //showSuccess("more question",mView);

        } else {
            showError("No question added for test", mView);
        }
    }*/

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

    public String getCurrentUTC() {
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd");
        outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return outputFmt.format(time);
    }

    private void getTestFromServer(final View v) {
        if (!checkInternetState.Companion.getInstance(v.getContext()).isOnline()) {
            showWarning("No Internet Connection", v);
        } else {
            //testList.clear();
            stateList.clear();
           // adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.VISIBLE);
            noTestFound.setVisibility(View.GONE);
            /*ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<PYQPTestResponse> userCall = service.getpreviousyearquestionpaper(userID);
            userCall.enqueue(new Callback<PYQPTestResponse>() {
                @Override
                public void onResponse(Call<PYQPTestResponse> call, Response<PYQPTestResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        if (response.body().getStatus()) {
                            List<PYQPTestData> course = response.body().getData();

                            for (PYQPTestData item : course) {
                                int n = rand.nextInt(ColorArray.size());
                                if (n == ColorArray.size()) {
                                    n -= 1;
                                }
                                int count;

                                if (!item.getPYQPTESTQUESTIONS().equals("")) {
                                    count = item.getPYQPTESTQUESTIONS().split("\\|").length;
                                } else {
                                    count = 0;
                                }

                                PYQPTestData cd = new PYQPTestData();
                                cd.setCount(count);
                                cd.setPYQPSTATEIDFK(item.getPYQPSTATEIDFK());
                                cd.setPYQPTESTDATE(item.getPYQPTESTDATE());
                                cd.setColor(ColorArray.get(n));
                                cd.setPYQPTESTDESC(item.getPYQPTESTDESC());
                                cd.setPYQPTESTDURATION(item.getPYQPTESTDURATION());
                                cd.setPYQPTESTNAME(item.getPYQPTESTNAME());
                                cd.setPYQPTESTID(item.getPYQPTESTID());
                                cd.setPYQPTESTTYPE(item.getPYQPTESTTYPE());
                                cd.setStatus(item.isStatus());
                                cd.setCORRECT_ANSWER(item.getCORRECT_ANSWER());
                                cd.setATTEMPTED_ANSWER(item.getATTEMPTED_ANSWER());
                                cd.setWRONG_ANSWER(item.getWRONG_ANSWER());
                                cd.setTOTAL_QUESTION(item.getTOTAL_QUESTION());
                                cd.setCREATED_BY(item.getCREATED_BY());

                                testList.add(cd);
                                adapter.notifyDataSetChanged();
                            }
                            //showSuccess("Test Updated", v);
                        } else {
                            noTestFound.setVisibility(View.VISIBLE);
                            showWarning("No Test available yet", v);
                        }
                    } else {
                        showError("Something went wrong.", v);
                    }
                }

                @Override
                public void onFailure(Call<PYQPTestResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                    progressBar.setVisibility(View.GONE);

                }
            });*/

            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<PYQPStates> userCall = service.getpyqpstate();
            userCall.enqueue(new Callback<PYQPStates>() {
                @Override
                public void onResponse(Call<PYQPStates> call, Response<PYQPStates> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        //if (response.body().get()) {
                            List<State> course = response.body().getStates();
                            for (State item : course) {
                                State st = new State();
                                st.setName(item.getName());
                                st.setCountryIdFk(item.getCountryIdFk());
                                st.setPYQPSTATEIDFK(item.getPYQPSTATEIDFK());
                                st.setStatesId(item.getStatesId());
                                stateList.add(st);
                                getPYQPStateAdapter.notifyDataSetChanged();
                            }
                            //showSuccess("Test Updated", v);
                        /*} else {
                            noTestFound.setVisibility(View.VISIBLE);
                            showWarning("No Test available yet", v);
                        }*/
                    } else {
                        showError("Something went wrong.", v);
                    }
                }

                @Override
                public void onFailure(Call<PYQPStates> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                    progressBar.setVisibility(View.GONE);

                }
            });
        }
    }


    private void showWarning(String s, View v) {
        Toasty.warning(v.getContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showSuccess(String s, View v) {
        Toasty.success(v.getContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showError(String s, View v) {
        Toasty.error(v.getContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showInfo(String s, View v) {
        Toasty.info(v.getContext(), s, Toast.LENGTH_LONG, true).show();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        //pyqpRecycler.setVisibility(View.GONE);
        getTestFromServer(mView);
    }*/

    public void resetTest(String testID) {
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<QuestionUpdateResponse> userCall = service.resetTest(userID, testID);
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
    public void onStateSelected(State students) {
        if (!students.getStatesId().equals("")){
            Intent i = new Intent(mView.getContext(), PYQPByState.class);
            i.putExtra("stateID", students.getStatesId());
            startActivity(i);
        }
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
