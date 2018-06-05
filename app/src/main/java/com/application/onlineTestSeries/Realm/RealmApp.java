package com.application.onlineTestSeries.Realm;

import android.app.Application;

import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.payUenvironment.AppEnvironment;

import io.realm.Realm;

public class RealmApp extends Application{

    AppEnvironment appEnvironment;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        appEnvironment = AppEnvironment.PRODUCTION;
    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }
}
