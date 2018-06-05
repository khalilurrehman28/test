package com.application.onlineTestSeries.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.application.onlineTestSeries.Home.MainActivity;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.login.Models.LoginData;
import com.application.onlineTestSeries.login.Models.UserData;
import com.application.onlineTestSeries.login.View.loginActivity;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowUuidUpdateView {
    AlertDialog.Builder builder;
    Context ctx;
    ShowUuidUpdateView(Context ctx){
        this.ctx =ctx;
    }

    public void showAlertDialogue(String message, final String username, final String password){
        builder = new AlertDialog.Builder(ctx);
        builder.setTitle("More than 1 device is detected")
                .setMessage(message+" Logout from other device")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateUuidTDB(username,password);
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

    private void updateUuidTDB(String username, String password) {
        String uuid = UtilsApp.id(ctx);

        /*ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<LoginData> userCall = service.getData(username,password);
        userCall.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    if (response.body().getStatus()) {
                        RealmResults<UserData> result = realmDB.where(UserData.class).equalTo("userId", response.body().getUserData().getUserId()).findAll();
                        if (result.size() == 0){
                            realmDB.beginTransaction();
                            UserData data = realmDB.createObject(UserData.class,response.body().getUserData().getUserId());
                            data.setValidity(response.body().getUserData().getValidity());
                            data.setStartDate(response.body().getUserData().getStartDate());
                            data.setActive(response.body().getUserData().getActive());
                            data.setEmail(response.body().getUserData().getEmail());
                            data.setDateofbirth(response.body().getUserData().getDateofbirth());
                            data.setGender(response.body().getUserData().getGender());
                            data.setFirstInstall(response.body().getUserData().getFirstInstall());
                            data.setPnone(response.body().getUserData().getPnone());
                            data.setImage(response.body().getUserData().getImage());
                            data.setName(response.body().getUserData().getName());
                            realmDB.commitTransaction();
                        }
                        //showSuccess(response.body().getMessage());
                        countDownTimer.cancel();
                        Intent i = new Intent(loginActivity.this,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }else{
                        showError(response.body().getMessage());
                    }
                }else{
                    showError("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                Log.d("onFailure", t.toString());
                progressDialog.dismiss();
            }
        });*/
    }

}
