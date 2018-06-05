package com.application.onlineTestSeries.menuBareActs;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.application.onlineTestSeries.Course.Models.CourseData;
import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.menuBareActs.adapter.menuBareActsAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import com.application.onlineTestSeries.Chapters.ChapterActivity;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class menuBareActs extends AppCompatActivity implements menuBareActsAdapter.ContactsAdapterListener{
    menuBareActsAdapter courseAdapter;
    List<CourseData> courseDataList;
    RecyclerView courseRecycler;
    ProgressBar progressBar;
    TextView noCourseFound,noSearchResultFound;
    List<Integer> ColorArray;
    Random rand;
    Realm realmDB;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_bare_acts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Bare Acts");

        initilize();

    }

    private void initilize() {
        progressBar = findViewById(R.id.progressBar);
        noCourseFound = findViewById(R.id.noCourseFound);
        noSearchResultFound = findViewById(R.id.noSearchResultFound);

        rand = new Random();
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }
        ColorArray = new ArrayList<>();
        courseDataList = new ArrayList<>();
        courseAdapter = new menuBareActsAdapter(menuBareActs.this, courseDataList,this);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        courseRecycler =findViewById(R.id.courseRecycler);
        courseRecycler.setLayoutManager(new GridLayoutManager(menuBareActs.this, 1));
        courseRecycler.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        courseRecycler.setItemAnimator(new DefaultItemAnimator());
        courseRecycler.setAdapter(courseAdapter);
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
        }

    }
    @Override
    public void onContactSelected(CourseData courseData) {
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
                    Intent i = new Intent(menuBareActs.this, ChapterActivity.class);
                    i.putExtra("courseID",""+courseData.getCOURSEID());
                    i.putExtra("courseName",""+courseData.getCOURSETITLE());

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
                    showAlertDialogue("Your plan is expired please re-subscribe the plan.");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            if (Integer.parseInt(courseData.getcOURSESTATE())==0){
                Intent i = new Intent(menuBareActs.this, ChapterActivity.class);
                i.putExtra("courseID",""+courseData.getCOURSEID());
                i.putExtra("courseName",""+courseData.getCOURSETITLE());

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
                showAlertDialogue("Do you want to subscribe the plan?");
            }
        }
    }

    private void showAlertDialogue(String msg) {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Subscribe Plan")
                .setMessage(msg)
                .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(menuBareActs.this, SubscriptionSubscribe.class));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_courses, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_action)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search Bare Acts...");
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                courseAdapter.getFilter().filter(query);
                if(courseAdapter.getItemCount()<1){
                    courseRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+query.toString().trim()+"'");
                }else {
                    courseRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                courseAdapter.getFilter().filter(query);
                if(courseAdapter.getItemCount()<1){
                    courseRecycler.setVisibility(View.GONE);
                    noSearchResultFound.setVisibility(View.VISIBLE);
                    noSearchResultFound.setText("No results found '"+query.toString().trim()+"'");
                }else {
                    courseRecycler.setVisibility(View.VISIBLE);
                    noSearchResultFound.setVisibility(View.GONE);
                }return false;
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void updateDataFromRealm(String s) {
        RealmResults<CourseData> results = null;

        if (s.equals("0")){
            results = realmDB.where(CourseData.class).findAll();
        }

        if (results.size()>0){
            noCourseFound.setVisibility(View.GONE);
            for (CourseData item: results) {
                int  n = rand.nextInt(ColorArray.size());
                if (n==ColorArray.size()){
                    n -=1;
                }

                CourseData cd = new CourseData();
                cd.setCOURSEDATE(item.getCOURSEDATE());
                cd.setCOURSETITLE(item.getCOURSETITLE());
                cd.setCOURSEID(item.getCOURSEID());
                cd.setCOURSEDELSTATUS(item.getCOURSEDELSTATUS());
                cd.setColor(ColorArray.get(n));
                cd.setCOURSEDELSTATUS(item.getCOURSEDELSTATUS());
                cd.setcOURSESTATE(item.getcOURSESTATE());

                courseDataList.add(cd);

//                courseAdapter.notifyDataSetChanged();
            }
            courseAdapter.notifyDataSetChanged();
        }
    }


    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }
}
