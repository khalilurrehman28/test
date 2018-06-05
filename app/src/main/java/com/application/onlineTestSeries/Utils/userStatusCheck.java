package com.application.onlineTestSeries.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.application.onlineTestSeries.Home.Models.subscriptionResponse;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.login.View.loginActivity;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class userStatusCheck {

    Realm realm;
    Context ctx;
    String userID;

    public userStatusCheck(Realm realm, Context ctx, String userID){
        this.realm = realm;
        this.ctx = ctx;
        this.userID = userID;
    }

    public void checkUpdatedStatus(){
        if (checkInternetState.Companion.getInstance(ctx).isOnline()){
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<subscriptionResponse> userCall = service.getUserSubscription(userID);
            userCall.enqueue(new Callback<subscriptionResponse>() {
                @Override
                public void onResponse(Call<subscriptionResponse> call, Response<subscriptionResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body().getData().getActiveStatus().equals("0")){
                            if (response.body().getCode().equals("OTHER_DEVICE")){

                            }else {
                                realm.beginTransaction();
                                subscriptionUserData subscriptionUserData = new subscriptionUserData();
                                subscriptionUserData.setSUBSCRIBERSUBSCRIPTIONDATE(response.body().getData().getSUBSCRIBERSUBSCRIPTIONDATE());
                                subscriptionUserData.setSUBSCRIBERSUBSCRIPTIONSTATUS(response.body().getData().getSUBSCRIBERSUBSCRIPTIONSTATUS());
                                subscriptionUserData.setSUBSCRIPTIONSTATUS(response.body().getData().getSUBSCRIPTIONSTATUS());
                                subscriptionUserData.setActiveStatus(response.body().getData().getActiveStatus());
                                realm.insertOrUpdate(subscriptionUserData);
                                realm.commitTransaction();
                            }
                        }else{
                            Toasty.error(ctx,"You are currently inactive by admin", 1500).show();
                            logout_function();
                        }
                    }
                }

                @Override
                public void onFailure(Call<subscriptionResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                }
            });
        }
    }



    private void logout_function() {
        realm.beginTransaction();
        // delete all realm objects
        realm.deleteAll();
        //commit realm changes
        realm.commitTransaction();
        final ProgressDialog progress = new ProgressDialog(ctx);
        progress.setTitle("Logging Out");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                progress.cancel();
                Intent intent = new Intent(ctx,loginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ctx.startActivity(intent);
            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1500);
    }
}
