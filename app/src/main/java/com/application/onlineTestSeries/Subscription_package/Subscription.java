package com.application.onlineTestSeries.Subscription_package;

import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.Adapter.SubscriptionAdapter;
import com.application.onlineTestSeries.Subscription_package.Models.SubscriptionData;
import com.application.onlineTestSeries.Subscription_package.Models.SubscriptionResponse;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.Utils.checkInternetState;
import com.application.onlineTestSeries.login.Models.UserData;
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
 * Use the {@link Subscription#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Subscription extends Fragment implements SubscriptionAdapter.ContactsAdapterListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    View mView;
    RecyclerView subscriptionRecycler;
    List<SubscriptionData> userSubscriptionList;
    SubscriptionAdapter adapter;
    ProgressBar progressBar;
    TextView noCourseFound;
    List<Integer> ColorArray;
    Random rand;
    Realm realmDB;
    String userID;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Subscription.
     */
    // TODO: Rename and change types and number of parameters
    public static Subscription newInstance(String param1, String param2) {
        Subscription fragment = new Subscription();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_subscription, container, false);
        initializeView(mView);
        return mView;
    }

    private void initializeView(View v) {
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }
        userSubscriptionList = new ArrayList<>();
        progressBar = v.findViewById(R.id.progressBar);
        noCourseFound = v.findViewById(R.id.noCourseFound);
        adapter = new SubscriptionAdapter(v.getContext(),userSubscriptionList,this);
        subscriptionRecycler = v.findViewById(R.id.subscriptionRecycler);
        subscriptionRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        subscriptionRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
        subscriptionRecycler.setItemAnimator(new DefaultItemAnimator());
        subscriptionRecycler.setAdapter(adapter);
        ColorArray = new ArrayList<>();
        rand = new Random();
        final int[] MY_COLORS = {Color.rgb(192,0,0), Color.rgb(0,229,238), Color.rgb(255,192,0),
                Color.rgb(127,127,127), Color.rgb(146,208,80), Color.rgb(0,176,80), Color.rgb(79,129,189)
                , Color.rgb(0,128,128), Color.rgb(0,139,69),Color.rgb(255,215,0),Color.rgb(255,128,0)
                ,Color.rgb(255,106,106)};
        for (int item : MY_COLORS) {
            ColorArray.add(item);
        }
        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item : results) {
            userID = item.getUserId();
        }
        getDataFromServer(v);
    }

    private void getDataFromServer(View v) {
        if (!checkInternetState.Companion.getInstance(v.getContext()).isOnline()){
            showWarning("No Internet Connection",v);
        }else{
            updateData(v);
        }
    }

    private void updateData(final View v) {
        progressBar.setVisibility(View.VISIBLE);
        noCourseFound.setVisibility(View.GONE);
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<SubscriptionResponse> userCall = service.getsubscriptionpackage(userID);
        userCall.enqueue(new Callback<SubscriptionResponse>() {
            @Override
            public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()){
                    if (response.body().getStatus()) {
                        subscriptionRecycler.setVisibility(View.VISIBLE);

                        List<SubscriptionData> course = response.body().getData();
                        //String cATEGORYID, String cATEGORYTITLE, String cATEGORYDATE, String sTATEIDFK, String cATEGORYDELSTATUS

                        for (SubscriptionData item: course) {
                            int  n = rand.nextInt(ColorArray.size());
                            if (n==ColorArray.size()){
                                n -=1;
                            }

                            SubscriptionData cd = new SubscriptionData();
                            cd.setSUBSCRIPTIONID(item.getSUBSCRIPTIONID());
                            cd.setSUBSCRIPTIONNAME(item.getSUBSCRIPTIONNAME());
                            cd.setSUBSCRIPTIONPRICE(item.getSUBSCRIPTIONPRICE());
                            cd.setSUBSCRIPTIONVALIDITY(item.getSUBSCRIPTIONVALIDITY());
                            cd.setColor(ColorArray.get(n));
                            if (item.getSUBSCRIPTIONSTATUS()!=null){
                                if (item.getSUBSCRIPTIONSTATUS().toString().equals("Active")){
                                    cd.setActive(true);
                                }else{
                                    cd.setActive(false);
                                }
                            }else{
                                cd.setActive(false);
                            }

                            if (item.getSUBSCRIPTIONENDDATE()!=null){
                                if (item.getSUBSCRIPTIONSTATUS()!=null){
                                    if (item.getSUBSCRIPTIONSTATUS().toString().equals("Active")){
                                        cd.setSUBSCRIPTIONENDDATE(item.getSUBSCRIPTIONENDDATE().toString());
                                        //cd.setActive(true);
                                    }else{
                                        cd.setSUBSCRIPTIONENDDATE("");
                                    }
                                }else{
                                    cd.setSUBSCRIPTIONENDDATE("");
                                }
                            }else{
                                cd.setSUBSCRIPTIONENDDATE("");
                            }

                            userSubscriptionList.add(cd);

                            adapter.notifyDataSetChanged();
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
            public void onFailure(Call<SubscriptionResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void showError(String s, View v) {
        Toasty.error(v.getContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showWarning(String s, View v) {
        Toasty.warning(v.getContext(), s, Toast.LENGTH_LONG, true).show();
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

    @Override
    public void onContactSelected(SubscriptionData SubscriptionData) {
        Intent i = new Intent(mView.getContext(), SubscriptionSubscribe.class);
        i.putExtra("subscriptionID",""+SubscriptionData.getSUBSCRIPTIONID());
        i.putExtra("subscriptionprice",""+SubscriptionData.getSUBSCRIPTIONPRICE());
        i.putExtra("subscriptionvalidity",""+SubscriptionData.getSUBSCRIPTIONVALIDITY());
        i.putExtra("subscriptionname",""+SubscriptionData.getSUBSCRIPTIONNAME());
        startActivity(i);
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
