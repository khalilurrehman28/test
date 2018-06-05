package com.application.onlineTestSeries.PYQPTest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.BuyTest.Buy_test;
import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.PYQPTest.Adapter.PYQPAdapter;
import com.application.onlineTestSeries.PYQPTest.Models.PYQPTestData;
import com.application.onlineTestSeries.PYQPTest.Models.PYQPTestResponse;
import com.application.onlineTestSeries.PYQPTest.Test.TestResult.resultTestActivity;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.Utils.checkInternetState;
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

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PYQPByState extends AppCompatActivity implements PYQPAdapter.ContactsAdapterListener {

    RecyclerView pyqpState;
    PYQPAdapter PYQPAdapter;
    List<PYQPTestData> testList;
    Realm realmDB;
    String userID, status, DateOfStart, PackageEndDate;
    List<Integer> ColorArray;
    Random rand;

    ProgressBar progressBar;
    TextView noTestFound, noSearchResultFound;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyqpby_state);
        testList = new ArrayList<>();
        rand = new Random();
        ColorArray = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        noTestFound = findViewById(R.id.noCourseFound);
        noSearchResultFound = findViewById(R.id.noSearchResultFound);

        pyqpState = findViewById(R.id.pyqpRecycler);
        PYQPAdapter = new PYQPAdapter(this, testList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        pyqpState.setLayoutManager(mLayoutManager);
        pyqpState.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
        pyqpState.setItemAnimator(new DefaultItemAnimator());
        pyqpState.setAdapter(PYQPAdapter);
        setTitle("Previous year exams");

        final int[] MY_COLORS = {Color.rgb(192, 0, 0), Color.rgb(0, 229, 238), Color.rgb(255, 192, 0),
                Color.rgb(127, 127, 127), Color.rgb(146, 208, 80), Color.rgb(0, 176, 80), Color.rgb(79, 129, 189)
                , Color.rgb(0, 128, 128), Color.rgb(0, 139, 69), Color.rgb(255, 215, 0), Color.rgb(255, 128, 0)
                , Color.rgb(255, 106, 106)};
        for (int item : MY_COLORS) {
            ColorArray.add(item);
        }

        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item : results) {
            userID = item.getUserId();
            status = item.getActive();
            DateOfStart = item.getFirstInstall();
            PackageEndDate = item.getStartDate();
        }
        if (!checkInternetState.Companion.getInstance(this).isOnline()) {
            showWarning("No Internet Connection");
        } else {
            getTestFromServer(getIntent().getStringExtra("stateID"), userID);
        }
    }

    private void getTestFromServer(String stateID, String userID) {
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<PYQPTestResponse> userCall = service.getpreviousyearquestionpaper(userID,stateID);
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
                            cd.setUSER_STATUS(item.getUSER_STATUS());
                            cd.setPurchaseStatus(item.getPurchaseStatus());
                            cd.setTestPrice(item.getTestPrice());

                            testList.add(cd);
                            PYQPAdapter.notifyDataSetChanged();
                        }
                        //showSuccess("Test Updated", v);
                    } else {
                        noTestFound.setVisibility(View.VISIBLE);
                        showWarning("No Test available yet");
                    }
                } else {
                    showError("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<PYQPTestResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
    }

    private void showWarning(String s) {
        Toasty.warning(PYQPByState.this, s, Toast.LENGTH_LONG, true).show();
    }

    private void showSuccess(String s) {
        Toasty.success(PYQPByState.this, s, Toast.LENGTH_LONG, true).show();
    }

    private void showError(String s) {
        Toasty.error(PYQPByState.this, s, Toast.LENGTH_LONG, true).show();
    }

    private void showInfo(String s) {
        Toasty.info(PYQPByState.this, s, Toast.LENGTH_LONG, true).show();
    }

    @Override
    public void onContactSelected(PYQPTestData PYQPTestData) {
        if (PYQPTestData.getCount() > 0) {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-mm-dd");
            Intent i;
            if (!PYQPTestData.isStatus()) {

                if (PYQPTestData.getPYQPTESTTYPE().equals("0")){
                    i = new Intent(PYQPByState.this, termsConditionActivity.class);
                    i.putExtra("testID", PYQPTestData.getPYQPTESTID());
                    i.putExtra("description", PYQPTestData.getPYQPTESTDESC());
                    i.putExtra("testName", PYQPTestData.getPYQPTESTNAME());
                    i.putExtra("testDuration", PYQPTestData.getPYQPTESTDURATION());
                    startActivity(i);
                }else{
                    /*RealmResults<subscriptionUserData> results = realmDB.where(subscriptionUserData.class).findAll();
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
                            if(todayDate.before(userDate) || userDate.compareTo(todayDate) == 0){*/
                               /* Intent i = new Intent(PYQPByState.this, termsConditionActivity.class);
                                i.putExtra("testID", PYQPTestData.getPYQPTESTID());
                                i.putExtra("description", PYQPTestData.getPYQPTESTDESC());
                                i.putExtra("testName", PYQPTestData.getPYQPTESTNAME());
                                i.putExtra("testDuration", PYQPTestData.getPYQPTESTDURATION());
                                startActivity(i);*/
                            /*}else{
                                //showWarning("Plan expired please update",mView);
                                showAlertDialogue("Your Plan will expired please re-subscribe the plan.");
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //showWarning("Please subscribe to plan",mView);
                        showAlertDialogue("Do you want to subscribe the plan?");
                    }*/

                    if (PYQPTestData.getPurchaseStatus() || PYQPTestData.getUSER_STATUS().equals("Active")) {
                        i = new Intent(PYQPByState.this, termsConditionActivity.class);
                        i.putExtra("testID", PYQPTestData.getPYQPTESTID());
                        i.putExtra("description", PYQPTestData.getPYQPTESTDESC());
                        i.putExtra("testName", PYQPTestData.getPYQPTESTNAME());
                        i.putExtra("testDuration", PYQPTestData.getPYQPTESTDURATION());
                        startActivity(i);
                    } else {
                        showAlertDialogue("Subscribe for plan or Buy this paper", PYQPTestData);
                    }
                }
            } else {
                //showInfo("You already attempt this test", mView);
                i = new Intent(PYQPByState.this, resultTestActivity.class);
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
            showError("No question added for test");
        }
    }

    private void showAlertDialogue(String msg, final PYQPTestData PYQPTestData) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Subscribe Plan")
                .setMessage(msg)
                .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(PYQPByState.this, SubscriptionSubscribe.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNeutralButton("Buy Test", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!PYQPTestData.getTestPrice().equals("") && !PYQPTestData.getTestPrice().equals("0")) {
                    Intent i = new Intent(PYQPByState.this, Buy_test.class);
                    i.putExtra("testPrice", PYQPTestData.getTestPrice());
                    i.putExtra("testName", PYQPTestData.getPYQPTESTNAME());
                    i.putExtra("testType", 1);
                    i.putExtra("testID", PYQPTestData.getPYQPTESTID());
                    startActivity(i);
                } else {
                    Toast.makeText(PYQPByState.this, "Test is not available for purchase", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflater.inflate(R.menu.test_main_menu, menu);
        getMenuInflater().inflate(R.menu.test_main_menu,menu);
        MenuItem item = menu.findItem(R.id.search_action);
        MenuItem createTest = menu.findItem(R.id.createTest);
        createTest.setVisible(false);
        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search State...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                PYQPAdapter.getFilter().filter(query.toLowerCase());
                if (PYQPAdapter.getItemCount() < 1) {
                    pyqpState.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '" + query.toString().trim() + "'");
                } else {
                    pyqpState.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                PYQPAdapter.getFilter().filter(query.toLowerCase().trim());
                if (PYQPAdapter.getItemCount() < 1) {
                    pyqpState.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '" + query.toString().trim() + "'");
                } else {
                    pyqpState.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                pyqpState.setVisibility(View.VISIBLE);
                noSearchResultFound.setVisibility(View.GONE);
                return false;
            }
        });

        //super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
