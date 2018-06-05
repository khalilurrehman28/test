package com.application.onlineTestSeries.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.Utils.checkInternetState;

import com.application.onlineTestSeries.AboutUs.AboutUsActivity;
import com.application.onlineTestSeries.Course.CourseView;
import com.application.onlineTestSeries.Home.Models.subscriptionResponse;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.Legal_maxim.LegalMaximFragment;
import com.application.onlineTestSeries.Notes.NotesFragment;
import com.application.onlineTestSeries.PYQPTest.TestsFragment;
import com.application.onlineTestSeries.PracticeTest.PracticeTestFragment;
import com.application.onlineTestSeries.PrivacyPolicy.PrivacyPolicyActivity;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Subscription_package.Subscription;
import com.application.onlineTestSeries.Utils.UtilsApp;
import com.application.onlineTestSeries.Utils.userStatusCheck;
import com.application.onlineTestSeries.bookmarks.BookmarksFragment;
import com.application.onlineTestSeries.features.Features;
import com.application.onlineTestSeries.fragment.DictionaryFragment;
import com.application.onlineTestSeries.landmarkJudgement.LandmarkJudgementFragment;
import com.application.onlineTestSeries.login.Models.UserData;
import com.application.onlineTestSeries.login.View.loginActivity;
import com.application.onlineTestSeries.profile.profileActivity;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_COURSES = "courses";
    private static final String TAG_NOTES = "notes";
    private static final String TAG_DICTIONARY= "dictionary";
    private static final String TAG_TESTS = "tests";
    private static final String TAG_PRACTICE_TEST = "practice test";
    private static final String TAG_LEGAL_MAXIM = "legal maxim";
    private static final String TAG_BOOKMARKS = "Bookmarks";
    private static final String TAG_LANDMARK_JUDGEMENT = "landmark judgement";
    private static final String TAG_SUBSCRIPTION = "Subscription";


    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;
    CircleImageView imgProfile;
    Realm realmDB;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    String image,UserID;
    userStatusCheck userStatusCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        mHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName =  navHeader.findViewById(R.id.profile_name);
        //txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        //imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile =  navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();


        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }


    }

    public void updateNav(int index,String fragmentTag){
        navItemIndex = index;
        CURRENT_TAG = fragmentTag;
    }



    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        //txtName.setText("Test User");
        //txtWebsite.setText("Law Universe");

        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item: results){
            txtName.setText("Welcome "+item.getName());
            image = item.getImage();
            UserID = item.getUserId();
            if (!image.equals("")|| image.equals("NoImage")){
                Glide.with(this).load(UtilsApp.webUrlHome+image).into(imgProfile);
            }else {
                Glide.with(this).load(R.drawable.ic_account_circle_black_36dp).into(imgProfile);
            }
        }
        userStatusCheck  = new userStatusCheck(realmDB,this,UserID);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, profileActivity.class));
            }
        });
        // loading header background image
        //Glide.with(this).load(R.drawable.nav_header).into(imgNavHeaderBg);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    public void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // set toolbar title
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                return new HomeFragment();
            case 1:
                // movies fragment
                return new CourseView();
            case 2:
                // photos
                return  new NotesFragment();
            case 3:
                // photos
                return  new BookmarksFragment();

            case 4:
                // notifications fragment
                return new DictionaryFragment();
            case 5:
                // settings fragment
                return new TestsFragment();
            case 6:
                // settings fragment
                return new PracticeTestFragment();
            case 7:
                // settings fragment
                return new LegalMaximFragment();
            case 8:
                return new LandmarkJudgementFragment();
            case 9:
                return new Subscription();
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;

                    case R.id.nav_courses:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_COURSES;
                        break;

                    case R.id.nav_notes:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_NOTES;
                        break;
                    case R.id.nav_bookmarks:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_BOOKMARKS;
                        break;
                    case R.id.nav_dictionary:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_DICTIONARY;
                        break;
                    case R.id.nav_tests:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_TESTS;
                        break;
                    case R.id.nav_practice_test:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_PRACTICE_TEST;
                        break;
                    case R.id.nav_legal_maxim:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_LEGAL_MAXIM;
                        break;
                    case R.id.nav_landmark_judgement:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_LANDMARK_JUDGEMENT;
                        break;
                    case R.id.nav_subscription:
                        navItemIndex = 9;
                        CURRENT_TAG = TAG_SUBSCRIPTION;
                        break;
                    case R.id.nav_profile:
                        startActivity(new Intent(MainActivity.this, profileActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_app_share:
                        // launch new intent instead of loading fragment
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                getString(R.string.applink));
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_features_list:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, Features.class));
                        drawer.closeDrawers();
                        return true;


                    case R.id.nav_logout:
                        logout_function();
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void logout_function() {

        realmDB.beginTransaction();
        // delete all realm objects
        realmDB.deleteAll();
        //commit realm changes
        realmDB.commitTransaction();
        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Logging Out");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                progress.cancel();
                Intent intent = new Intent(MainActivity.this,loginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1500);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userStatusCheck.checkUpdatedStatus();
    }
}
