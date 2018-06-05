package com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.Models.SubscriptionResponse;
import com.application.onlineTestSeries.Utils.checkInternetState;
import com.application.onlineTestSeries.login.Models.LoginData;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import com.application.onlineTestSeries.Home.Models.subscriptionResponse;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.Realm.RealmApp;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.payUenvironment.AppEnvironment;
import com.application.onlineTestSeries.login.Models.UserData;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionSubscribe extends AppCompatActivity{

    private static String TAG="";
    Realm realmDB;
    String userID,userName,userEmail,mobile,subscriptionPrice,subscriptionvalidity,subscriptionID,subscriptionName,transactionID,serverHash;
    //private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    Button payNowButton;
    ImageView closeSub;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    TextView packageAmount,subscriptionDuration,packageName,paidAmount,subscriptionDiscount;
    LinearLayout mainSubscriptioncontainer;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_subscribe);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TAG = getLocalClassName();
        payNowButton = findViewById(R.id.pay_now_button);
        closeSub = findViewById(R.id.closeSub);

        setTitle("Subscribe");
        mainSubscriptioncontainer = findViewById(R.id.mainSubscriptioncontainer);
        mainSubscriptioncontainer.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        /*subscriptionPrice = getIntent().getStringExtra("subscriptionprice");
        subscriptionID = getIntent().getStringExtra("subscriptionID");
        subscriptionvalidity = getIntent().getStringExtra("subscriptionvalidity");
        subscriptionName = getIntent().getStringExtra("subscriptionname");*/
        subscriptionDiscount = findViewById(R.id.subscriptionDiscount);
        packageAmount = findViewById(R.id.packageAmount);
        //subscriptionDuration = findViewById(R.id.subscriptionDiscount);
        packageName = findViewById(R.id.packageName);
        paidAmount = findViewById(R.id.paidAmount);

        closeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        subscriptionPrice = "1";
        subscriptionID = "1";
        subscriptionvalidity = "365";
        subscriptionPrice = "1";
        subscriptionName = "1 Year Plan";

        paidAmount.setPaintFlags(paidAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        if (!checkInternetState.Companion.getInstance(this).isOnline()){
            Toasty.info(this,"No Internet Connection").show();
        }else{
            hitApi();
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
            userName = item.getName();
            userEmail = item.getEmail();
            mobile = item.getPnone();
        }

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPayUMoneyFlow();
            }
        });
    }

    private void hitApi() {
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<SubscriptionResponse> userCall = service.getSubscriptionData();
        userCall.enqueue(new Callback<SubscriptionResponse>() {
            @Override
            public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()) {
                        mainSubscriptioncontainer.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        for(int i=0; i<response.body().getData().size();i++){
                            paidAmount.setText(response.body().getData().get(i).getSUBSCRIPTIONPRICE());
                            double discountAmt = Math.round((Integer.parseInt(response.body().getData().get(i).getSUBSCRIPTIONPRICE()) * (100 - Integer.parseInt(response.body().getData().get(i).getSUBSCRIPTION_DISCOUNT())))/100);
                            packageAmount.setText(String.format("%.0f",discountAmt));
                            subscriptionPrice = String.valueOf(discountAmt);
                            subscriptionID = response.body().getData().get(i).getSUBSCRIPTIONID();
                            packageName.setText("For "+response.body().getData().get(i).getSUBSCRIPTIONNAME());
                            subscriptionDiscount.setText(response.body().getData().get(i).getSUBSCRIPTION_DISCOUNT()+"% Off");
                            subscriptionName = response.body().getData().get(i).getSUBSCRIPTIONNAME();

                            if(response.body().getData().get(i).getSUBSCRIPTION_DISCOUNT().equals("0")){
                                paidAmount.setVisibility(View.GONE);
                                subscriptionDiscount.setVisibility(View.GONE);
                                subscriptionPrice = response.body().getData().get(i).getSUBSCRIPTIONPRICE();
                            }
                        }
                    }else{
                        showError(response.body().getMessage());
                    }
                }else{
                    showError("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<SubscriptionResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

    private void showError(String s) {
        Toasty.error(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
            // Check which object is non-null
            String Status = "";
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    Status = "true";
                } else {
                    Status = "false";
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();
                Log.d(TAG, "onActivityResult: PayuResponse"+payuResponse);
                String merchantResponse = transactionResponse.getTransactionDetails();

                Log.d(TAG, "onActivityResult: merchantResponse"+merchantResponse);

                Log.d(TAG, "onActivityResult payuResponse: "+payuResponse);
                // Response from SURl and FURL
                Log.d(TAG, "onActivityResult merchantResponse: "+merchantResponse);
                updateTransactionInDataBase(payuResponse,merchantResponse,userID,transactionID,Status);

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }
    }

    private void updateTransactionInDataBase(String payuResponse, String merchantResponse, String userID, String transactionID, String status) {
        realmDB.beginTransaction();
        realmDB.delete(subscriptionUserData.class);
        realmDB.commitTransaction();

        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<subscriptionResponse> userCall = service.updateusersubscription(userID,status,payuResponse,merchantResponse,serverHash,transactionID);
        userCall.enqueue(new Callback<subscriptionResponse>() {
            @Override
            public void onResponse(Call<subscriptionResponse> call, Response<subscriptionResponse> response) {
                if (response.isSuccessful()){
                    realmDB.beginTransaction();
                    subscriptionUserData subscriptionUserData = realmDB.createObject(subscriptionUserData.class,response.body().getData().getSUBSCRIBERSUBSCRIPTIONDATE());
                    subscriptionUserData.setSUBSCRIBERSUBSCRIPTIONSTATUS(response.body().getData().getSUBSCRIBERSUBSCRIPTIONSTATUS());
                    subscriptionUserData.setSUBSCRIPTIONSTATUS(response.body().getData().getSUBSCRIPTIONSTATUS());
                    realmDB.commitTransaction();
                }
            }

            @Override
            public void onFailure(Call<subscriptionResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

    /**
     * This function prepares the data for payment and launches payumoney plug n play sdk
     */
    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Finish");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("Subscribe to plan");

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = 0;
        try {
            amount = Double.parseDouble(subscriptionPrice);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String txnId = System.currentTimeMillis() + "";
        String phone = mobile;
        String productName = subscriptionName;
        String firstName = userName;
        String email = userEmail;
        String udf1 = userID;
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ( (RealmApp) getApplication()).getAppEnvironment();
        builder.setAmount(amount)
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            generateHashFromServer(mPaymentParams);

            /*
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
           /* mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

           if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,MainActivity.this, AppPreference.selectedTheme,mAppPreference.isOverrideResultScreen());
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,MainActivity.this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
            }*/

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            payNowButton.setEnabled(true);
        }
    }

    /**
     * This method generates hash from server.
     *
     * @param paymentParam payments params used for hash generation
     */
    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SubscriptionSubscribe.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("http://lawuniverse.co.in/api/generatehashkey");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                    Log.d(TAG, "doInBackground: "+responseStringBuffer.toString());
                }

                Log.d(TAG, "doInBackground: "+responseStringBuffer.toString());
                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        /*
                         * This hash is mandatory and needs to be generated from merchant's server side
                         *
                         */
                        case "payment_hash":
                            merchantHash = response.getString(key);
                            serverHash = response.getString(key);
                            break;
                        case "transID":
                            transactionID = response.get(key).toString();
                            //Log.d(TAG, "doInBackground: "+transactionID);
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            progressDialog.dismiss();
            payNowButton.setEnabled(true);

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(SubscriptionSubscribe.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);

                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, SubscriptionSubscribe.this, R.style.AppTheme_purple, false);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        //findMenuItems.inflate(R.menu.test_menu, menu);
        //MenuItem menugraph = menu.findItem(R.id.resetTable);
        return super.onCreateOptionsMenu(menu);
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

        /*if (id == R.id.resetTable){
            //takeScreenshot(ScreenshotType.FULL);

            clearTest();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
