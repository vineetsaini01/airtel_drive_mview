package com.newmview.wifi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WebViewActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final String TAG = "StatusForSS";
    JSONObject jsonObject = new JSONObject();
    String publicIp = null;
    private WebView webView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Utils.fetchPublicIpUi(ip -> {
            runOnUiThread(() -> {

                publicIp = ip;
            });
        });
        webView = findViewById(R.id.webViewTest);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page loaded: " + url);
                Toast.makeText(WebViewActivity.this, "Page loaded: " + url, Toast.LENGTH_SHORT).show();

                // Delay for 30 seconds before taking a screenshot
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        takeScreenshot(view);

                    }
                }, 25000);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)) {
            loadUrl();
        }

    }

    private void loadUrl() {
        webView.loadUrl("https://fast.com");
    }

    private void takeScreenshot(WebView view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
                saveBitmap(bitmap);
            }
        });
    }

    private void saveBitmap(Bitmap bitmap) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!path.exists()) {
            path.mkdirs();
        }

        String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
        File file = new File(path, fileName);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            jsonObject.put("screenshot_taken", "true");
            jsonObject.put("public_ip",publicIp);
            jsonObject.put("private_ip", Utils.getPIP());
            jsonObject.put("resolved_ip"," ");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String date_time = now.format(dtf);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            Utils.appendLog("ELOG_FAST_NET_TEST: final json is "+jsonArray);
            // Store all the metrics after the loop ends
            DB_handler db_handler = new DB_handler(MviewApplication.ctx);
            db_handler.open();
            db_handler.insertInLoggingAgentTable("FastNet", "fastnet_report", jsonArray.toString(), date_time, "init");
            db_handler.close();
            Log.d(TAG, "Screenshot saved: " + file.getAbsolutePath());
            Toast.makeText(WebViewActivity.this, "Screenshot saved: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error saving screenshot: " + e.getMessage());
            Toast.makeText(WebViewActivity.this, "Error saving screenshot: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadUrl();
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}