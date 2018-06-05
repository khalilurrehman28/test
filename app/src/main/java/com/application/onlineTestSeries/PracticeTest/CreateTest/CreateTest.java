package com.application.onlineTestSeries.PracticeTest.CreateTest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.PracticeTest.CreateTest.Adapter.createTestAdapter;
import com.application.onlineTestSeries.PracticeTest.CreateTest.Models.CategoryTest;
import com.application.onlineTestSeries.PracticeTest.CreateTest.Models.CategoryTestData;
import com.application.onlineTestSeries.PracticeTest.Models.QuestionUpdateResponse;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.login.Models.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

public class CreateTest extends AppCompatActivity {

    RecyclerView createTestRecyclerView;
    createTestAdapter adapter;
    List<CategoryTestData> categoryList;
    ProgressBar progressBar;
    TextView noCourseFound,btnFinish,btnCancel;
    List<Integer> ColorArray;
    Random rand;
    //List<ServerDataSender> questionCategoryList;
    HashMap<Integer,Map<Integer,Integer>> questionCategoryList;
    Realm realmDB;
    String userID;
    EditText testName;
    //64-5A-04-54-e5-b5
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        createTestRecyclerView = findViewById(R.id.createTestRecyclerView);
        categoryList = new ArrayList<>();
        ColorArray = new ArrayList<>();
        rand = new Random();
        //questionCategoryList = new ArrayList<>();
        questionCategoryList = new HashMap<>();
        btnCancel = findViewById(R.id.btnCancel);
        btnFinish = findViewById(R.id.btnFinish);
        testName = findViewById(R.id.testName);
        noCourseFound = findViewById(R.id.noCourseFound);
        progressBar = findViewById(R.id.progressBar);
        adapter = new createTestAdapter(this,categoryList);
        createTestRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        createTestRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        createTestRecyclerView.setItemAnimator(new DefaultItemAnimator());
        createTestRecyclerView.setAdapter(adapter);

        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item: results){
            userID = item.getUserId();
        }


        final int[] MY_COLORS = {Color.rgb(192,0,0), Color.rgb(0,229,238), Color.rgb(255,192,0),
                Color.rgb(127,127,127), Color.rgb(146,208,80), Color.rgb(0,176,80), Color.rgb(79,129,189)
                , Color.rgb(0,128,128), Color.rgb(0,139,69),Color.rgb(255,215,0),Color.rgb(255,128,0)
                ,Color.rgb(255,106,106)};
        for (int item : MY_COLORS) {
            ColorArray.add(item);
        }

        getServerdata();

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //JSONArray array = new JSONArray();
                JSONObject item1 = new JSONObject();
                //JSONObject json = new JSONObject();
                int count = 0;
                for (CategoryTestData item: categoryList){


                    if (item.getQuestionCount()>0){
                        count = count+item.getQuestionCount();
                        //questionCategoryList.put(Integer.parseInt(item.getCATEGORYID()),item.getQuestionCount());
                        try {
                            item1.put(item.getCATEGORYID(),item.getQuestionCount());

                            //ja.put(Integer.parseInt(item.getCATEGORYID()),item.getQuestionCount());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /* ServerDataSender sd = new ServerDataSender();
                        sd.setCateID(Integer.parseInt(item.getCATEGORYID()));
                        sd.setQuestionTot(item.getQuestionCount());
                        questionCategoryList.add(sd);*/
                        Log.d("subscriptionUserData",""+item.getCATEGORYID()+"----"+item.getQuestionCount());
                    }
                }

                /*if (testName.getText().toString().trim()==""){
                    testName.setError("enter test name");
                }else{
                    testName.setError(null);
                }*/


               // array.put(item1);

               /* try {
                   // json.put("course", array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                Log.d("Message", "onClick: "+item1.toString());
                if (count>100){
                    showMessage("Plase select less than 100 question ",2);
                }else{
                    if (count<10){
                        showMessage("Plase select more than 10 question ",2);
                    }else{
                        createTest(item1.toString(),testName.getText().toString());
                    }
                }
            }
        });

    }

    private void createTest(String message,String testName) {
        if (!checkInternetState.Companion.getInstance(this).isOnline()){
            showMessage("No Internet Connection",1);
        }else{
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<QuestionUpdateResponse> userCall = service.topic_question_test(message,userID,testName);
            userCall.enqueue(new Callback<QuestionUpdateResponse>() {
                @Override
                public void onResponse(Call<QuestionUpdateResponse> call, Response<QuestionUpdateResponse> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()){
                        if (response.body().getStatus()) {
                            showMessage(response.body().getMessage(),1);
                            /*Intent i = new Intent(CreateTest.this, MainActivity.class);
                            startActivity(i);*/
                            finish();
                        }
                    }else{
                        showMessage("Something went wrong.",2);
                    }
                }

                @Override
                public void onFailure(Call<QuestionUpdateResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                    progressBar.setVisibility(View.GONE);

                }
            });
        }
    }

    private void getServerdata() {
        if (!checkInternetState.Companion.getInstance(this).isOnline()){
            showMessage("No Internet Connection",1);
        }else{
            updateData();
        }
    }

    private void updateData() {
        progressBar.setVisibility(View.VISIBLE);
        noCourseFound.setVisibility(View.GONE);
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<CategoryTest> userCall = service.get_question_category();
        userCall.enqueue(new Callback<CategoryTest>() {
            @Override
            public void onResponse(Call<CategoryTest> call, Response<CategoryTest> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()){
                    if (response.body().getStatus()) {
                        createTestRecyclerView.setVisibility(View.VISIBLE);

                        List<CategoryTestData> course = response.body().getData();
                        for (CategoryTestData item: course) {
                            int n = rand.nextInt(ColorArray.size());
                            if (n==ColorArray.size()){
                                n -=1;
                            }
                            CategoryTestData cd = new CategoryTestData();
                            cd.setCATEGORYDATE(item.getCATEGORYDATE());
                            cd.setCATEGORYDELSTATUS(item.getCATEGORYDELSTATUS());
                            cd.setCATEGORYTITLE(item.getCATEGORYTITLE());
                            cd.setCATEGORYID(item.getCATEGORYID());

                            int value = 0;
                            if(item.getQcount()== 0||item.getQcount()==null){
                                value = 0;
                            }else{
                                value = item.getQcount();
                            }

                            cd.setQcount(value);
                            cd.setColorCode(ColorArray.get(n));
                            cd.setQuestionCount(0);
                            categoryList.add(cd);
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        noCourseFound.setVisibility(View.VISIBLE);
                        showMessage("No course available yet",1);
                    }
                }else{
                    showMessage("Something went wrong.",2);
                }
            }

            @Override
            public void onFailure(Call<CategoryTest> call, Throwable t) {
                Log.d("onFailure", t.toString());
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void showMessage(String msg, int type) {
        switch (type){
            case 1:
                Toasty.warning(CreateTest.this, msg, Toast.LENGTH_LONG, true).show();
               break;
            case 2:
                Toasty.error(CreateTest.this, msg, Toast.LENGTH_LONG, true).show();
               break;
            case 3:
                Toasty.success(CreateTest.this, msg, Toast.LENGTH_LONG, true).show();
               break;
        }
    }

    private int dpToPx(int i) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, getResources().getDisplayMetrics()));
    }
}
