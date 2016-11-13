package com.keiskeismartsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.Toast;

import com.keiskeismartsystem.dbsql.NotifTransact;
import com.keiskeismartsystem.dbsql.WhereHelper;
import com.keiskeismartsystem.fragment.AboutFragment;
import com.keiskeismartsystem.fragment.DashboardFragment;
import com.keiskeismartsystem.fragment.DetailNotificationFragment;
import com.keiskeismartsystem.fragment.EditProfilFragment;
import com.keiskeismartsystem.fragment.NotificationFragment;
import com.keiskeismartsystem.fragment.ProductList;
import com.keiskeismartsystem.fragment.ProfileFragment;
import com.keiskeismartsystem.fragment.SettingFragment;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.model.Notif;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private FragmentTabHost mTabHost;
    private static Context _context;
    private static UserSession _userSession;
    private NotifTransact _notifTransact;
    Boolean flagR = false;
    GCMReceiverChat gcmReceiverChat = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = getApplicationContext();
        _notifTransact = new NotifTransact(_context);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        _userSession = new UserSession(getApplicationContext());
        gcmReceiverChat = new GCMReceiverChat();
        if(!_userSession.isUserLoggedIn()) {
            Toast toast = Toast.makeText(getApplicationContext(),"Anda harus login terlebih dahulu.", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);

            finish();
        }
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content_container);
        Bundle b = new Bundle();
        /*b.putString("key", "Dashboard");
        mTabHost.addTab(mTabHost.newTabSpec("dashboard").setIndicator(null, ContextCompat.getDrawable(this, R.drawable.home)),
                DashboardFragment.class, b);
        b = new Bundle();*/
        b.putString("key", "Products");
        mTabHost.addTab(mTabHost.newTabSpec("products")
                .setIndicator(null, ContextCompat.getDrawable(this, R.drawable.home)), ProductList.class, b);
        b = new Bundle();
        b.putString("key", "Notification");
        mTabHost.addTab(mTabHost.newTabSpec("notification")
                .setIndicator(null, ContextCompat.getDrawable(this, R.drawable.notif)), NotificationFragment.class, b);
        b = new Bundle();
        b.putString("key", "Profile");
        mTabHost.addTab(mTabHost.newTabSpec("profile")
                .setIndicator(null, ContextCompat.getDrawable(this, R.drawable.profil)), ProfileFragment.class, b);
        b = new Bundle();
        b.putString("key", "Setting");
        mTabHost.addTab(mTabHost.newTabSpec("setting")
                .setIndicator(null, ContextCompat.getDrawable(this, R.drawable.opsi)), SettingFragment.class, b);

        mTabHost.getTabWidget().setStripEnabled(false);
        Bundle notif_bundle = getIntent().getExtras();

        final TabWidget widget = mTabHost.getTabWidget();
        for(int i = 0; i < widget.getChildCount(); i++) {
            View v = widget.getChildAt(i);

            // Look for the title view to ensure this is an indicator and not a divider.
//            TextView tv = (TextView)v.findViewById(android.R.id.title);
//            if(tv == null) {
//                continue;
//            }
            v.setBackgroundResource(R.drawable.notif_navigation);
            final int a = i;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v = widget.getChildTabViewAt(a);
                    v.setActivated(true);
                    Log.v("keiskeidebug", "idinya " + a);
                    String tabId = "dashboard";
                    switch (a){
                        case 0:
                            tabId = "products";
                            break;
                        case 1:
                            tabId = "notification";
                            break;
                        case 2:
                            tabId = "profile";
                            break;
                        case 3:
                            tabId = "setting";
                            break;
                    }
                    changeBigFragment(tabId);
                    mTabHost.setCurrentTab(a);
                }
            });

        }
        ArrayList<WhereHelper> whDB = new ArrayList<WhereHelper>();
        WhereHelper wh = new WhereHelper("read_flag", "0");
        whDB.add(wh);
        List<Notif> lp = _notifTransact.get(whDB);
        if (lp.size() > 0){
            notifActive();
        }
//        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            @Override
//            public void onTabChanged(String tabId) {
//                changeBigFragment(tabId);
//            }
//        });
//        mTabHost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        if (notif_bundle!= null){
            char flag = notif_bundle.getChar("FLAG");
            switch (flag){
                case 'N':
                    DetailNotificationFragment _dnf = new DetailNotificationFragment();
                    Notif notif = (Notif) notif_bundle.getSerializable(Notif.KEY);
                    Bundle temp = new Bundle();
                    temp.putSerializable(Notif.KEY, notif);
                    _dnf.setArguments(temp);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_container, _dnf).commit();
                    break;
                case 'C':
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    public void changeBigFragment(String type){
        switch (type) {
            case "dashboard":
                DashboardFragment dfragment = new DashboardFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, dfragment ).commit();
                break;
            case "products":
                ProductList fragment = new ProductList();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, fragment ).commit();
                break;
            case "notification":
                NotificationFragment nfragment = new NotificationFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, nfragment ).commit();
                break;
            case "profile":
                ProfileFragment pfragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, pfragment ).commit();
                break;
            case "setting":
                SettingFragment sfragment = new SettingFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, sfragment ).commit();
                break;
            case "cart":
                startActivity(new Intent(this,CartActivity.class));
                break;
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            changeBigFragment("cart");
        }

        return super.onOptionsItemSelected(item);
    }
    public void editprofile(View v){
        Bundle bundle = new Bundle();
        changeFragment("edit_profile", v, bundle);
    }
    public void changeFragment(String type, View v, Bundle bundle){
        switch (type){
            case "edit_profile":
                EditProfilFragment edit_profile_fragment = new EditProfilFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, edit_profile_fragment ).commit();
                break;
            case "profile_fragment":
                ProfileFragment profile_fragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, profile_fragment ).commit();
                break;
            case "detail_notif":
                DetailNotificationFragment _dnf = new DetailNotificationFragment();
                Notif notif = (Notif) bundle.getSerializable(Notif.KEY);
                Bundle temp = new Bundle();
                temp.putSerializable(Notif.KEY, notif);
                _dnf.setArguments(temp);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, _dnf).commit();
                break;
            case "voice_box":
                try{
                    Intent intent = new Intent(MainActivity.this, VoiceBoxActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }catch (IllegalStateException e){

                }
                break;
            case "chat":
                try{
                    Intent cintent = new Intent(MainActivity.this, ChatActivity.class);
                    cintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(cintent);
                    finish();
                }catch (IllegalStateException e){

                }
                break;
            case "transaction":
                try {
                    Intent tintent = new Intent(MainActivity.this, ReportActivity.class);
                    tintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(tintent);
                    finish();
                }catch (IllegalStateException e){

                }
                break;
            case "about":
                AboutFragment about = new AboutFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, about ).commit();
                break;
            case "notification":
                NotificationFragment nfragment = new NotificationFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_container, nfragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
        }
    }
    @SuppressWarnings("deprecation")
    public void notifActive(){
        ImageView notif = (ImageView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notif.setImageDrawable(getResources().getDrawable(R.drawable.notif_active, getApplicationContext().getTheme()));
        }else{
            notif.setImageDrawable(getResources().getDrawable(R.drawable.notif_active));
        }
    }
    @SuppressWarnings("deprecation")
    public void notifNonActive(){
        ImageView notif = (ImageView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notif.setImageDrawable(getResources().getDrawable(R.drawable.notif, getApplicationContext().getTheme()));
        }else{
            notif.setImageDrawable(getResources().getDrawable(R.drawable.notif));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!flagR){
            IntentFilter filter = new IntentFilter("com.keiskeismartsystem.GCMMESSAGE.Notification");
            registerReceiver(gcmReceiverChat, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(flagR) {
            unregisterReceiver(gcmReceiverChat);
            flagR = false;
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            changeBigFragment("dashboard");
        } else if (id == R.id.nav_notif) {
            changeBigFragment("notification");
        } else if (id == R.id.nav_kontak) {
            startActivity(new Intent(this, VoiceBoxActivity.class));
            finish();
        } else if (id == R.id.nav_chat) {
            startActivity(new Intent(this,ChatActivity.class));
            finish();
        } else if (id == R.id.nav_produk) {
            changeBigFragment("products");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class GCMReceiverChat extends BroadcastReceiver {
        public GCMReceiverChat() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            Bundle results = intent.getExtras();
            char flag = results.getChar("FLAG");
            Log.v("keiskeidebug", "Nerima pesan baru FLAG" + flag);
            switch (flag) {
                case 'N':
                    notifActive();
                    break;
            }
        }
    }
    public void logout(View view){
        _userSession.logout();
        startActivity(new Intent(this, SplashScreen.class));
        finish();
    }
}
