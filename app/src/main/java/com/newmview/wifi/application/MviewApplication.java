package com.newmview.wifi.application;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDexApplication;
import androidx.work.Configuration;
import androidx.work.WorkManager;


import com.google.android.exoplayer2.BuildConfig;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.services.WifiService;
import com.newmview.wifi.AlarmManagerBroadcastReceiver;
import com.newmview.wifi.helper.LatLong;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.services.ImupService;
import com.services.RegistrationIntentService;

import java.io.File;
import java.util.ArrayList;

import static com.newmview.wifi.helper.CommonUtil.headersList;

public class MviewApplication extends MultiDexApplication implements Configuration.Provider{
    public static Context ctx;
    private SimpleCache downloadCache;
    private File downloadDirectory;
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    private DatabaseProvider databaseProvider;
   public static ArrayList<Float> batteryList=new ArrayList<Float>();
   public static ArrayList<String> timeStamp=new ArrayList<String>();
    private ApplicationInfo applicationInfo;
    private Bundle bundle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
       // StrictMode.setVmPolicy(builder.build());

     //   String simOperator= listenService.telMgr.getSimOperatorName();
       // long cid=268435455;
        //String cidString =String.valueOf(cid);
        //String mcc = Utils.CIDMccValue(cidString);
        //System.out.println(" substring mcc in  mview  "+mcc);

        FirebaseApp.initializeApp(this);

        // Enable Crashlytics collection
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);


        try {
            applicationInfo = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            bundle = applicationInfo.metaData;
            Config.PRODUCT_VERSION=bundle.getString("product_version");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            Intent intent1 = new Intent(ctx, RegistrationIntentService.class);
            ctx.startService(intent1);
//sendGetDashboardDataRequest();
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Constants.IMSI = Utils.getImsi(ctx);
                System.out.println("imsi from app class " + Constants.IMSI);
         /*   Intent intent = new Intent(ctx, Background_service.class);
            ctx.startService(intent);
*/
            }
            headersList = new ArrayList<>();
          //  headersList.add(0, "Dashboards");
            headersList.add(0, Config.RunDiagnostics);
            // headersList.add(2,"My Network");
            LatLong.addStates();


            if (Constants.IMSI != null) {
    /*Intent intent = new Intent(ctx, Periodic_Background_service.class);
    ctx.startService(intent);*/
               // JobSchedulerUtil.scheduleImupJob(ctx);
                if (!Utils.isMyServiceRunning(ImupService.class))
                {

                    System.out.println("starting service "+"flag "+Constants.service_started);

                    Intent allIntent = new Intent(ctx, ImupService.class);
                    //Toast.makeText(MainActivity.this, "calling alarm bckgtnd dervice from home", //Toast.LENGTH_SHORT).show();
                    startService(allIntent);

                }
                // by swapnil 09/1/2023
                if (!Utils.isMyServiceRunning(WifiService.class))
                {

                    System.out.println("starting service WifiService "+"flag "+Constants.service_started);
                    Intent allIntent = new Intent(ctx, WifiService.class);
                    //Toast.makeText(MainActivity.this, "calling alarm bckgtnd dervice from home", //Toast.LENGTH_SHORT).show();
                    startService(allIntent);

                }

                 // by  swapnil 20/10/2022
                if (!Utils.isMyServiceRunning(AlarmManagerBroadcastReceiver.class))
                {

                    System.out.println("starting service "+"flag "+Constants.service_started);

                    Intent allIntent = new Intent(ctx, AlarmManagerBroadcastReceiver.class);
                    //Toast.makeText(MainActivity.this, "calling alarm bckgtnd dervice from home", //Toast.LENGTH_SHORT).show();
                    startService(allIntent);

                }


            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public DataSource.Factory buildDataSourceFactory() {
        DefaultDataSourceFactory upstreamFactory =
                new DefaultDataSourceFactory(this, buildHttpDataSourceFactory());
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
    }
    protected static CacheDataSourceFactory buildReadOnlyCacheDataSource(
            DataSource.Factory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(
                cache,
                upstreamFactory,
                new FileDataSourceFactory(),
                /* cacheWriteDataSinkFactory= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                /* eventListener= */ null);
    }
    public HttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSourceFactory("Peacock");
    }
    protected synchronized Cache getDownloadCache() {
        if (downloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache =
                    new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor(), getDatabaseProvider());
        }
        return downloadCache;
    }
    private File getDownloadDirectory() {
        if (downloadDirectory == null) {
            downloadDirectory = getExternalFilesDir(null);
            if (downloadDirectory == null) {
                downloadDirectory = getFilesDir();
            }
        }
        return downloadDirectory;
    }

    private DatabaseProvider getDatabaseProvider() {
        if (databaseProvider == null) {
            databaseProvider = new ExoDatabaseProvider(this);
        }
        return databaseProvider;
    }

    public boolean useExtensionRenderers() {
        return "withExtensions".equals(BuildConfig.FLAVOR);
    }

    public RenderersFactory buildRenderersFactory(boolean preferExtensionRenderer) {
        @DefaultRenderersFactory.ExtensionRendererMode
        int extensionRendererMode =
                useExtensionRenderers()
                        ? (preferExtensionRenderer
                        ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                        : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                        : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
        return new DefaultRenderersFactory(/* context= */ this)
                .setExtensionRendererMode(extensionRendererMode);
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(Log.VERBOSE)
                .build();
    }
}
