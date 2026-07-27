package com.teamos.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LauncherModel {

    public static class AppInfo {
        public String packageName;
        public String className;
        public String label;
        public Drawable icon;

        public AppInfo(String packageName, String className, String label, Drawable icon) {
            this.packageName = packageName;
            this.className = className;
            this.label = label;
            this.icon = icon;
        }
    }

    public interface Callbacks {
        void onAppsLoaded(List<AppInfo> apps);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<AppInfo> mCachedApps = new ArrayList<>();
    private BroadcastReceiver mPackageReceiver;

    public void loadAppsAsync(final Context context, final Callbacks callbacks) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<AppInfo> apps = loadApps(context);
                synchronized (LauncherModel.this) {
                    mCachedApps = apps;
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbacks.onAppsLoaded(apps);
                    }
                });
            }
        });
    }

    private List<AppInfo> loadApps(Context context) {
        List<AppInfo> appsList = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> launchables = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : launchables) {
            String packageName = info.activityInfo.packageName;
            String className = info.activityInfo.name;
            String label = info.loadLabel(pm).toString();
            Drawable icon = info.loadIcon(pm);

            // Skip ourselves
            if (packageName.equals(context.getPackageName())) {
                continue;
            }

            appsList.add(new AppInfo(packageName, className, label, icon));
        }

        // Sort applications alphabetically by label
        Collections.sort(appsList, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return o1.label.compareToIgnoreCase(o2.label);
            }
        });

        return appsList;
    }

    public void registerPackageReceiver(Context context, final Callbacks callbacks) {
        if (mPackageReceiver != null) {
            return;
        }

        mPackageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadAppsAsync(context, callbacks);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        context.registerReceiver(mPackageReceiver, filter);
    }

    public void unregisterPackageReceiver(Context context) {
        if (mPackageReceiver != null) {
            context.unregisterReceiver(mPackageReceiver);
            mPackageReceiver = null;
        }
    }
}
