package com.test.recovermessages.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.recovermessages.R;
import com.test.recovermessages.adapters.AppListAdapter;
import com.test.recovermessages.db.RecentNumberDB;
import com.test.recovermessages.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Setup extends AppCompatActivity {

    private ArrayList<String> addedpackages;
    private ArrayList<String> addedpackagestocompare;
    private AppListAdapter appListAdapter;
    private ArrayList<String> apppacklist;
    private boolean asking = false;
    private Handler handler;
    private int key = 0;
    private LinearLayout main;
    private ProgressBar progress;
    private Utils utils;

    private boolean getInstalledInfo(String s) {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean notystem(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & 1) != 0;
    }

    private void saveSetup() {
        SharedPreferences.Editor edit = getSharedPreferences("SETUP", 0).edit();
        edit.putBoolean("setup", true);
        edit.apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void saveTodb() {
        new bg().execute();
    }

    private void setUpAppList() {
        main.removeAllViews();
        progress.setVisibility(View.VISIBLE);
        new AsyncLayoutInflater(this).inflate(R.layout.app_list_recycler, new LinearLayout(this), new AsyncLayoutInflater.OnInflateFinishedListener() {
            public void onInflateFinished(@NonNull View view, int i, @Nullable ViewGroup viewGroup) {
                progress.setVisibility(View.GONE);
                main.addView(view);
                setupAppListRecycler(view.findViewById(R.id.recycle), (Button) findViewById(R.id.button));
            }
        });
    }

    private void setupAppListRecycler(RecyclerView recyclerView, Button button) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String[] strArr = new String[]{"com.whatsapp", "com.whatsapp.w4b", "com.gbwhatsapp", "com.facebook.lite", "com.facebook.orca", "com.facebook.mlite", "org.telegram.messenger"};
        new Thread(new Runnable() {
            public void run() {
                List arrayList = new ArrayList();
                List arrayList2 = new ArrayList();
                PackageManager packageManager = getPackageManager();
                List installedPackages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
                int i = 0;
                int i2 = 0;
                while (true) {
                    if (i2 >= strArr.length) {
                        break;
                    }
                    if (getInstalledInfo(strArr[i2])) {
                        try {
                            arrayList.add(packageManager.getPackageInfo(strArr[i2], PackageManager.GET_PERMISSIONS));
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    i2++;
                }
                for (i2 = 0; i2 < installedPackages.size(); i2++) {
                    String str = ((PackageInfo) installedPackages.get(i2)).packageName;
                    int i3 = 0;
                    while (true) {
                        String[] strArr2 = strArr;
                        if (i3 >= strArr2.length) {
                            break;
                        }
                        if (str.contains(strArr2[i3])) {
                            installedPackages.remove(i2);
                        }
                        i3++;
                    }
                }
                while (i < installedPackages.size()) {
                    if (!(notystem((PackageInfo) installedPackages.get(i)) || arrayList.contains(installedPackages.get(i)))) {
                        arrayList.add(installedPackages.get(i));
                    }
                    i++;
                }
                if (key == 1) {
                    Iterator it = new RecentNumberDB(getApplicationContext()).getAllPackages().iterator();
                    while (it.hasNext()) {
                        String str2 = (String) it.next();
                        try {
                            arrayList2.add(packageManager.getPackageInfo(str2, PackageManager.GET_PERMISSIONS));
                            apppacklist.add(str2);
                            addedpackages.add(str2);
                            addedpackagestocompare.add(str2);
                        } catch (PackageManager.NameNotFoundException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                appListAdapter = new AppListAdapter(arrayList, Setup.this, addedpackages);
                handler.post(new Runnable() {
                    public void run() {
                        recyclerView.setAdapter(appListAdapter);
                        progress.setVisibility(View.GONE);
                        button.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                if (apppacklist.size() > 0) {
                                    saveTodb();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Add atleast one application", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void setupNotificationPermission() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setup.this);
        alertDialog.setTitle("Notification Access");
        alertDialog.setMessage(Html.fromHtml("Notification access allows " + getString(R.string.app_name) + " to read and backup deleted messages &amp; media."));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int n) {
                asking = true;
                try {
                    if (Build.VERSION.SDK_INT >= 22) {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    } else {
                        try {
                            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        } catch (Exception ex) {
                            finish();
                            startActivity(new Intent(Setup.this, HomeActivity.class));
                        }
                    }
                } catch (Exception ex2) {
                    finish();
                    startActivity(new Intent(Setup.this, HomeActivity.class));
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
    }

    private void terms_setup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setup.this);
        alertDialog.setTitle("Terms & Conditions");
        alertDialog.setMessage(Html.fromHtml(getString(R.string.app_name) + " is a backup &amp; utility app. It is designed to provide backup services for notifications, specific location of storage &amp; some utility features. This app uses common Android APIs to achieve services. It's not designed to interfere with other apps or services.<br>However the use of app may be incompatible with terms of use of other apps. If this kind of incompatibility occurs, you shall not use this app.<br><br>Using this app you accept its <a href='#'>Terms &amp; Conditions</a> and <a href='#'>Privacy Policy</a>"));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.cancel();
            }
        });
        alertDialog.setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
        ((TextView) alertDialog1.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void addtolist(String str) {
        if (apppacklist.contains(str)) {
            apppacklist.remove(str);
        } else {
            apppacklist.add(str);
        }
        String sb = " size " + apppacklist.size();
        Log.d("addlistlog", sb);
    }

    @Override
    public void onBackPressed() {
        if (key == 1) {
            finish();
            super.onBackPressed();
            startActivity(new Intent(this, HomeActivity.class));
            return;
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        main = (LinearLayout) findViewById(R.id.main);
        apppacklist = new ArrayList<String>();
        progress = (ProgressBar) findViewById(R.id.progress);
        handler = new Handler();
        utils = new Utils((Activity) this);
        addedpackages = new ArrayList<String>();
        addedpackagestocompare = new ArrayList<String>();
        key = getIntent().getIntExtra("key", 0);
        if (key == 1) {
            setUpAppList();
        } else {
            setUpAppList();
            terms_setup();
        }
    }

    public class bg extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... array) {
            RecentNumberDB recentNumberDB = new RecentNumberDB(Setup.this);
            if (key == 1) {
                String sb = "size  " + addedpackagestocompare.size();
                Log.d("keylog", sb);
                ArrayList<String> list = new ArrayList<String>();
                Iterator<String> iterator = addedpackagestocompare.iterator();
                int i;
                while ((i = (iterator.hasNext() ? 1 : 0)) != 0) {
                    String s = iterator.next();
                    if (!apppacklist.contains(s)) {
                        list.add(s);
                        Log.d("keylog", s);
                    } else {
                        String sb2 = "dont remove " + s;
                        Log.d("keylog", sb2);
                    }
                }
                while (i < apppacklist.size()) {
                    recentNumberDB.addPackages(apppacklist.get(i));
                    ++i;
                }
                recentNumberDB.removePackageAndMsg(list);
            } else {
                for (int j = 0; j < apppacklist.size(); ++j) {
                    recentNumberDB.addPackages(apppacklist.get(j));
                }
            }
            return null;
        }

        protected void onPostExecute(Void void1) {
            super.onPostExecute(void1);
            if (key == 1) {
                finish();
                Intent intent = new Intent(getString(R.string.noti_obserb));
                intent.putExtra(getString(R.string.noti_obserb), "update");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                startActivity(new Intent(Setup.this, (Class) HomeActivity.class));
            } else {
                setupNotificationPermission();
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (asking && utils.isNotificationEnabled()) {
            saveSetup();
            Intent intent = new Intent(getString(R.string.noti_obserb));
            intent.putExtra(getString(R.string.noti_obserb), "update");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }
}
