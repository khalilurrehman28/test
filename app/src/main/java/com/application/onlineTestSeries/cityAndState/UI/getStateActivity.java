package com.application.onlineTestSeries.cityAndState.UI;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.application.onlineTestSeries.Course.Models.state_user_model;
import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.cityAndState.Adapter.getStateAdapter;
import com.application.onlineTestSeries.cityAndState.Model.StateData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class getStateActivity extends AppCompatActivity implements getStateAdapter.StateAdapterListener{

    RecyclerView recyclerView;
    ProgressBar progressBar;
    SearchView searchView;
    private getStateAdapter adapter;
    private List<StateData> cityList;
    TextView noSearchResultFound;
    Realm realmDB;
    List<state_user_model> stateListWithChapterCount;
    Map<String, String> stateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_state);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Select Location");
        initialize();
    }


    private void initialize() {
        noSearchResultFound = findViewById(R.id.noSearchResultFound);
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }
        stateListWithChapterCount = new ArrayList<>();
        stateListWithChapterCount = getIntent().getParcelableArrayListExtra("stateList");
        stateList = new HashMap<>();
        /*Log.d("UserState",""+stateListWithChapterCount.size());
        for (state_user_model item:stateListWithChapterCount) {
            Log.d("UserState", "initialize: "+item.getStateID()+"----"+item.getChapterCount());

        }

        System.out.print(stateListWithChapterCount);*/

        progressBar = findViewById(R.id.progressBar);
        cityList = new ArrayList<>();
        adapter = new getStateAdapter(getStateActivity.this, cityList,this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getStateActivity.this, 1);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        new GetAllProducts().execute();
    }

    @Override
    public void onStateSelected(StateData students) {
        RealmResults<subscriptionUserData> results = realmDB.where(subscriptionUserData.class).findAll();
        String status ="",isActive ="",date = "";
        for (subscriptionUserData item: results) {
            status = item.getSUBSCRIPTIONSTATUS();
            isActive = item.getSUBSCRIBERSUBSCRIPTIONSTATUS();
            date = item.getSUBSCRIBERSUBSCRIPTIONDATE();
        }
        //Toast.makeText(mView.getContext(), ""+date, Toast.LENGTH_SHORT).show();
        if (isActive.equals("1") && status.equals("Active")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date userDate = null, todayDate;
            try {
                userDate = sdf.parse(date);
                todayDate = sdf.parse(sdf.format(new Date()));

                Log.d("CourseView", "onClick: "+userDate+"-----"+todayDate);

                if(todayDate.before(userDate) || userDate.compareTo(todayDate) == 0){
                    Intent intent = new Intent();
                    intent.putExtra("stateId", students.getSTATEID());
                    intent.putExtra("stateName", students.getSTATENAME());
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    showAlertDialogue("Your Plan will expired please re-subscribe the plan.");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{

            if (Integer.parseInt(students.getSTATEID())==0){
                Intent intent = new Intent();
                intent.putExtra("stateId", students.getSTATEID());
                intent.putExtra("stateName", students.getSTATENAME());
                setResult(RESULT_OK, intent);
                finish();
            }else{
                showAlertDialogue("Do you want to subscribe plan?");
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
                        startActivity(new Intent(getStateActivity.this, SubscriptionSubscribe.class));
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
    /*THis is user to get all the data in JSON file*/
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getStateActivity.this.getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        // Log.e("Error"," Json DAta"+json);
        return json;

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_state, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search State...");

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);

                if(adapter.getItemCount()<1){
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
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                if(adapter.getItemCount()<1){
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
        return true;
        //super.onCreateOptionsMenu(menu);

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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*To get all the product and set in adapter*/
    private class GetAllProducts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0)  {
            try {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(loadJSONFromAsset());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                JSONArray m_jArry = obj.getJSONArray("states");

                for (int k = 0; k < m_jArry.length(); k++) {

                    JSONObject jo_inside = m_jArry.getJSONObject(k);
                    String state_name = jo_inside.getString("STATE_NAME");
                    String state_id = jo_inside.getString("STATE_ID");

                    for (state_user_model states : stateListWithChapterCount) {
                        if (states.getStateID() == Integer.parseInt(state_id) && states.getChapterCount() > 0) {
                            stateList.put(state_id, state_name);
                        }
                    }
                }

                for (Map.Entry<String, String> entry : stateList.entrySet()) {
                    StateData cpl = new StateData();
                    cpl.setSTATEID(entry.getKey());
                    cpl.setSTATENAME(entry.getValue());
                    cityList.add(cpl);
                }
                sortlist(cityList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        private void sortlist(List<StateData> courseDataList) {
            Collections.sort(courseDataList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    StateData p1 = (StateData) o1;
                    StateData p2 = (StateData) o2;
                    return p1.getSTATEID().compareTo(p2.getSTATEID());
                }
            });
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            progressBar.setVisibility(View.GONE);
            /**
             * Updating parsed JSON data into ListView
             * */
            adapter.notifyDataSetChanged();
        }

    }

}
