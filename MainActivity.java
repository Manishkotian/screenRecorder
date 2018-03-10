package check.network.com.deviceinfo;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button check_permission = (Button) findViewById(R.id.device_info);
        check_permission.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.d("DeviceInfo: ", "manufacturer-->" +String.valueOf(Build.MANUFACTURER));
                Log.d("DeviceInfo: ", "product-->" +String.valueOf(Build.PRODUCT));
                Log.d("DeviceInfo: ", "brand-->" +String.valueOf(Build.BRAND));
                Log.d("DeviceInfo: ", "Data recieved-->" + TrafficStats.getTotalRxBytes());
                Log.d("DeviceInfo: ", "Data transmitted-->" + TrafficStats.getTotalTxBytes());


                getMacAddress();

                getCPUUsage();



                String[] serverPackasges = {"com.duapps.recorder","com.google.android.apps.nbu.paisa.user","com.instagram.android","com.whatsapp","com.android.cts.priv.ctsshim", "com.google.android.youtube", "com.google.android.ext.services", "com.example.android.livecubes", "com.android.providers.telephony", "com.google.android.googlequicksearchbox", "com.android.providers.calendar", "com.android.providers.media", "com.google.android.onetimeinitializer", "com.google.android.ext.shared", "com.android.protips", "com.android.documentsui", "com.android.externalstorage", "com.android.htmlviewer", "com.android.companiondevicemanager", "com.android.mms.service", "com.android.providers.downloads", "com.google.android.apps.messaging", "com.google.android.configupdater", "com.android.defcontainer", "com.android.providers.downloads.ui", "com.android.vending", "com.android.pacprocessor"};


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    boolean result = apiGreaterThenLollipop(serverPackasges);
                    Log.d("DeviceInfo:", "result-->" + result);
                } else {
                    boolean result = apiLessThenLollipop(MainActivity.this, serverPackasges);
                    Log.d("DeviceInfo:", "result-->" + result);
                }

            }
        });
    }

    public void getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    //return "";
                    Log.d("DeviceInfo: ", "mac address = null");
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                // return res1.toString();
                Log.d("DeviceInfo: ", "mac address="+res1.toString());
            }
        } catch (Exception ex) {
        }
        //return "02:00:00:00:00:00";
    }

    //sample code of usageStatsManager for testing purpose
    public boolean sampleTest(String[] serverPackages) {
        Calendar calendar = Calendar.getInstance();
        long currentLocalTime = calendar.getTimeInMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        calendar.setTimeInMillis(currentLocalTime);
        Log.d("usageStatsManager","Current Time--->"+ formatter.format(calendar.getTime()));
        calendar.setTimeInMillis(currentLocalTime - 30000);

        Log.d("usageStatsManager", "begin time-->" + formatter.format(calendar.getTime()) );

        calendar.setTimeInMillis(System.currentTimeMillis());

        Log.d("usageStatsManager"," end time-->" + formatter.format(calendar.getTime()));

        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentLocalTime - 30000, System.currentTimeMillis());

        ArrayList packageNames = new ArrayList();
        for( UsageStats xyz : queryUsageStats){
            packageNames.add(xyz.getPackageName());
        }
        Log.d("usageStatsManager", "Package names from UsageStatsManager-->" + packageNames);
        for (UsageStats abc : queryUsageStats) {
            if("com.duapps.recorder".equals(abc.getPackageName())) {
                calendar.setTimeInMillis(abc.getLastTimeUsed());
                Log.d("usageStatsManager", "Package name--> " + abc.getPackageName() + " getLastTimeUsed---> " + formatter.format(calendar.getTime()));
                calendar.setTimeInMillis(abc.getFirstTimeStamp());
                Log.d("usageStatsManager", "Package name--> " + abc.getPackageName() + " getFirstTimeStamp---> " + formatter.format(calendar.getTime()));
                calendar.setTimeInMillis(abc.getLastTimeStamp());
                Log.d("usageStatsManager", "Package name--> " + abc.getPackageName() + " getLastTimeStamp---> " + formatter.format(calendar.getTime()));
                calendar.setTimeInMillis(abc.getTotalTimeInForeground());
                Log.d("usageStatsManager", "Package name--> " + abc.getPackageName() + " getTotalTimeInForeground---> " + formatter.format(calendar.getTime()));
                Log.d("usageStatsManager", "***********");
            }
        }
            return false;

    }

    public boolean apiGreaterThenLollipop(String[] serverPackasges) {
        boolean result = false;

        Calendar calendar = Calendar.getInstance();
        long currentLocalTime = calendar.getTimeInMillis();

        //UsageStatsManager
        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentLocalTime - 30000,  System.currentTimeMillis());
        if (queryUsageStats.size() == 0) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
        System.out.println("DeviceInfo   begin time-->" + (currentLocalTime - 30000) + " end time-->" + System.currentTimeMillis());
        Log.d("DeviceInfo", "begin time-->" + (currentLocalTime - 30000) + " end time-->" + System.currentTimeMillis());
        Map<String, Long> currentRunningApps = new HashMap<>();
        for (UsageStats info : queryUsageStats) {
            currentRunningApps.put(info.getPackageName(),info.getLastTimeStamp());
            Log.d("DeviceInfo", "package name-->" + (info.getPackageName()) + " last time used-->" + info.getLastTimeStamp());
            System.out.println("DeviceInfo package name-->" + (info.getPackageName()) + " last time used-->" + info.getFirstTimeStamp());
            System.out.println("************");


        }

        //currently installed apps
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<PackageInfo> pkgAppsList = MainActivity.this.getPackageManager().getInstalledPackages(0);
        ArrayList installedApps = new ArrayList();
        for (PackageInfo pa : pkgAppsList) {
            installedApps.add(pa.packageName);
        }

        Object currentInstalledAppsArray[] = installedApps.toArray();

        //detecting whether package name found is unistalled or not
        Map<String, Long> currentRunningAndInstalledApps = new HashMap<>();
        for (Map.Entry<String, Long> entry : currentRunningApps.entrySet()) {
            String packageName = entry.getKey();
            Long lastUsed = entry.getValue();
            for (int j = 0; j < currentInstalledAppsArray.length; j++) {
                if (packageName.equals(currentInstalledAppsArray[j])) {
                    currentRunningAndInstalledApps.put(packageName,lastUsed);
                    Log.d("DeviceInfo", "running packages-->" + packageName + " installed packages-->" + currentInstalledAppsArray[j]);
                    //System.out.println("DeviceInfo running packages-->" + packageName + " installed packages-->" + currentInstalledAppsArray[j]);
                }
            }
        }
       // System.out.println("DeviceInfo Array of currently running apps-->" + currentRunningAndInstalledApps);
        Log.d("DeviceInfo:", "Array of currently running apps-->" + currentRunningAndInstalledApps);

        for (int i = 0; i < serverPackasges.length; i++) {
            for (Map.Entry<String, Long> entry : currentRunningAndInstalledApps.entrySet()) {
                String finalPackageName = entry.getKey();
                Long finalLastTimeUsed = entry.getValue();
                if (serverPackasges[i].equals(finalPackageName)) {
                    Log.d("DeviceInfo","package name-->"+finalPackageName+" finaltimeused"+finalLastTimeUsed+" current time"+(System.currentTimeMillis()-30000));
                    //System.out.println("DeviceInfo package name-->"+finalPackageName+" finaltimeused"+finalLastTimeUsed+" current time"+(System.currentTimeMillis()-30000));
                    if (finalLastTimeUsed > (System.currentTimeMillis() - 120000)) {
                        result = true;
                        //System.out.println("DeviceInfo server packages-->" + serverPackasges[i] + " local packages-->" + finalPackageName);
                        Log.d("DeviceInfo", "server packages-->" + serverPackasges[i] + " local packages-->" + finalPackageName);
                        return result;
                    }
                }
            }
        }
        return result;
    }


    public boolean apiLessThenLollipop(final Context context, String[] serverPackasges) {
        Log.d("DeviceInfo:", "Entered apiLessThenLollipop");
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        boolean result = false;
        if (procInfos != null) {
            for (int i = 0; i < serverPackasges.length; i++) {
                for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                    if (processInfo.processName.equals(serverPackasges[i])) {
                        Log.d("DeviceInfo", "server package-->" + serverPackasges[i] + " local package-->" + processInfo.processName);
                        return result;
                    }
                }
            }
        }
        Log.d("DeviceInfo:", "Array of currently running apps-->" + procInfos);
        return result;
    }

    public float getCPUUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            float cpuUsage = (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
            Log.d("Device info: ", "cpu usage="+cpuUsage);
            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

}
