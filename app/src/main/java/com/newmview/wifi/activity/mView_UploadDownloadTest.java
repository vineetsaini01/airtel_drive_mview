package com.newmview.wifi.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.newmview.wifi.AsyncTaskTools;
import com.newmview.wifi.CommonFunctions;
import com.newmview.wifi.MySpeedTest;
import com.mview.airtel.R;
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.UploadToFtpNew;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class mView_UploadDownloadTest extends AppCompatActivity{

	public static Context mContext;
	TextView resultTextView,textview,currentspeed,statustext,resultHdr;
	TextView statustext1,resultTextView1,currentspeed1,sizetext,resultHdr1;
	TextView dwnldtime,dwnldsize,dwnldspeed;
	TextView uploadtime,uploadsize,uploadspeed;
	Float currntbnd;

	//ColorArcProgressBar progBar;
	PointerSpeedometer PointerSpeedometer;

	Button startBtn, startUploadBtn,selectBtn;
	long starttime;
	long endtime;

	public String LOG_TAG = "Download mview";
	String downloadResponse = "";
	float time,size,speed;

	public static String sourceUrl = "";


	public static String filename = "b.mp3";
	public static String desDirectory = "/upload";

	//private ProgressDialog mProgressDialog;
	ProgressBar  progBarUpload,progBar;


	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	public String downloadfileURL = "http://hi-dj.com/hidj/dj_songs/2016DJ000000010151.mp3";
	public static String ftpserver = "hi-dj.com";
	public String ftpuser ="hidjftp" , ftppwd="hidj@2015";

	public static final int FILE_SELECT = 780;
	public static final int SETTINGS_ACTIVITY = 781;

	DownloadFileAsync downloadFileAsync;
    UploadTask uploadTask;
	private float bandvalue;
	private Button uploadbtn;
	private TextView uploaddownload;
	public static float MAX_SPEED_INDIA=10;
	private String newbandvalue;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mContext = this;
        setContentView(R.layout.uploaddownload_new);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		PointerSpeedometer = (PointerSpeedometer)findViewById(R.id.speedView);
		PointerSpeedometer.setUnit("Mbps");
		PointerSpeedometer.setMaxSpeed(MAX_SPEED_INDIA);
		PointerSpeedometer.setIndicatorWidth(10);
		PointerSpeedometer.setUnitTextSize(35);
	/*	PointerSpeedometer.setTickNumber(25);
		PointerSpeedometer.setTickPadding(9);*/
		PointerSpeedometer.setWithIndicatorLight(false);


//		PointerSpeedometer.setSpeedometerColor(getResources().getColor(R.color.primary));
		//PointerSpeedometer.setIndicatorColor(getResources().getColor(R.color.primary));


		textview = (TextView) findViewById(R.id.textview);
		currentspeed = (TextView) findViewById(R.id.currentspeed);
		//resultTextView = (TextView) findViewById(R.id.result);
		//statustext = (TextView) findViewById(R.id.statustext);

        startBtn=(Button)findViewById(R.id.startdwnldBtn);
        //startUploadBtn = (Button)findViewById(R.id.startuploadBtn);
       // uploadbtn=(Button)findViewById(R.id.startuploadBtn);

		statustext1 = (TextView) findViewById(R.id.statustext1);
		resultTextView1 = (TextView) findViewById(R.id.result1);
		currentspeed1 = (TextView) findViewById(R.id.currentspeed1);
		sizetext = (TextView) findViewById(R.id.sizetext);

		//resultHdr = (TextView) findViewById(R.id.resultHdr);
		resultHdr1 = (TextView) findViewById(R.id.resultHdr1);

		selectBtn = (Button)findViewById(R.id.selectBtn);
	progBar = (ProgressBar) findViewById(R.id.loading);

		progBarUpload = (ProgressBar) findViewById(R.id.loading1);
		dwnldsize=(TextView)findViewById(R.id.dwnldsize);
		dwnldtime=(TextView)findViewById(R.id.dwnldtime);
		dwnldspeed=(TextView)findViewById(R.id.dwnldspeed);
		uploadsize=(TextView)findViewById(R.id.uploadsize);
		uploadtime=(TextView)findViewById(R.id.uploadtime);
		uploadspeed=(TextView)findViewById(R.id.uploadspeed);
		//uploaddownload=(TextView)findViewById(R.id.upload_download);
		/*if(MainActivity.ud.equalsIgnoreCase("download"))
		{
			uploaddownload.setText("Download");

		}
		else if(MainActivity.ud.equalsIgnoreCase("upload"))
		{
			uploaddownload.setText("Upload");
		}*/

		TinyDB db = new TinyDB(mView_UploadDownloadTest.this);

		String t1 = db.getString("downloadurl");
		if( t1 == null || t1.equals(""))
			downloadfileURL = "http://speedtest.tele2.net/5MB.zip"; //http://hi-dj.com/hidj/dj_songs/2016DJ000000010151.mp3";
		else
			downloadfileURL = t1;

        //downloadfileURL = "http://speedtest.tele2.net/5MB.zip";

		t1 = db.getString("ftpserverurl");
		if( t1 == null || t1.equals(""))
			ftpserver = "speedtest.tele2.net";///upload";
		else
			ftpserver = t1;

		t1 = db.getString("ftpusername");
		if( t1 == null || t1.equals(""))
			ftpuser = "anonymous";
		else
			ftpuser = t1;

		t1 = db.getString("ftppwd");
		if( t1 == null || t1.equals(""))
			ftppwd = "admin@mview.com";
		else
			ftppwd = t1;

        t1 = db.getString("ftpuploaddir");
        if( t1 == null || t1.equals(""))
            desDirectory = "upload";
        else
            desDirectory = t1;


		mView_HealthStatus.mySpeedTest.uploadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
		mView_HealthStatus.mySpeedTest.uploadtest = (MySpeedTest.UploadDownload)db.getObject("upload", MySpeedTest.UploadDownload.class );
		mView_HealthStatus.mySpeedTest.downloadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
		mView_HealthStatus.mySpeedTest.downloadtest = (MySpeedTest.UploadDownload)db.getObject("download", MySpeedTest.UploadDownload.class );

		if(mView_HealthStatus.mySpeedTest.uploadtest != null)
		{
			long tt = mView_HealthStatus.mySpeedTest.uploadtest.timeTakenInMS;
			long sz = mView_HealthStatus.mySpeedTest.uploadtest.sizeInBytes;
			float bandInbps = ((sz)/((tt)/1000));
			float band = bandInbps/(1024*1024); //Mbps
			float szInMB = sz/(1024*1024);
			float ttInSecs = tt/1000;
			String uploadResponse = "TimeTaken=" + String.format("%.2f", ttInSecs) + " sec, FileSize=" + String.format("%.2f", szInMB) + "Mb \nMeasured Speed = " + String.format("%.2f", band) + "Mbps";

			//resultTextView1.setText(uploadResponse);


			/*dwnldtime.setText(String.format("%ssec", String.format("%.2f", ttInSecs)));
			dwnldsize.setText(String.format("%sMb", String.format("%.2f", szInMB)));
			dwnldspeed.setText(String.format("%sMbps", String.format("%.2f", band)));*/


			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM hh:mm");
			Date resultdate = new Date(mView_HealthStatus.mySpeedTest.uploadtest.startTime);
			String displaydate = sdf.format(resultdate);

			resultHdr1.setText("Test Results (" + displaydate +")");
			//uploaddownload.setText(displaydate);
		}
		if(mView_HealthStatus.mySpeedTest.downloadtest != null)
		{
			long tt = mView_HealthStatus.mySpeedTest.downloadtest.timeTakenInMS;
			long sz = mView_HealthStatus.mySpeedTest.downloadtest.sizeInBytes;
			float bandInbps = ((sz)/((tt)/1000));
			float band = bandInbps/(1024*1024); //Mbps
			float szInMB = sz/(1024*1024);
			float ttInSecs = tt/1000;
			downloadResponse = "TimeTaken=" + String.format("%.2f", ttInSecs) + " sec, FileSize =" + String.format("%.2f", szInMB) + "Mb \nMeasured Speed = " + String.format("%.2f", band) + "Mbps";
			//resultTextView.setText(downloadResponse);
			time=ttInSecs;
			size=szInMB;
			speed=band;

		/*	dwnldtime.setText(String.format("%ssec", String.format("%.2f", ttInSecs)));
			dwnldsize.setText(String.format("%sMb", String.format("%.2f", szInMB)));
			dwnldspeed.setText(String.format("%sMbps", String.format("%.2f", band)));
*/

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM hh:mm");
			Date resultdate = new Date(mView_HealthStatus.mySpeedTest.downloadtest.startTime);
			String displaydate = sdf.format(resultdate);
			//uploaddownload.setText(displaydate);

		//	resultHdr.setText("Test Results (" + displaydate +")");
		}

//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//
//			Drawable wrapDrawable = DrawableCompat.wrap(progBar.getIndeterminateDrawable());
//			DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, android.R.color.holo_green_light));
//			progBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
//		} else {
//			progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light), PorterDuff.Mode.SRC_IN);
//		}
		/*GradientDrawable progressGradientDrawable = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
				0xff1e90ff,0xff006ab6,0xff367ba8});
		ClipDrawable progressClipDrawable = new ClipDrawable(
				progressGradientDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		Drawable[] progressDrawables = {
				new ColorDrawable(0xffffffff),
				progressClipDrawable, progressClipDrawable};
		LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);

		progressLayerDrawable.setId(0, android.R.id.background);
		progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
		progressLayerDrawable.setId(2, android.R.id.progress);

		progBar.setProgressDrawable(progressLayerDrawable);

		progBar.setIndeterminate(false);
		progBar.setMax(100);
		progBar.invalidate();*/
		//progBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		/*setColours(progBar,
				0xff303030,   //bgCol1  grey
				0xff909090,   //bgCol2  lighter grey
				0xff0000FF,   //fg1Col1 blue
				0xffFFFFFF,   //fg1Col2 white
				0,           //value1
				0xffFF0000,   //fg2Col1 red
				0xffFFFFFF,   //fg2Col2 white
				0);          //value2
*/
		setColours(progBarUpload,
				0xff303030,   //bgCol1  grey
				0xff909090,   //bgCol2  lighter grey
				0xff0000FF,   //fg1Col1 blue
				0xffFFFFFF,   //fg1Col2 white
				0,           //value1
				0xffFF0000,   //fg2Col1 red
				0xffFFFFFF,   //fg2Col2 white
				0);          //value2

		if( mView_HealthStatus.connectionTypeIdentifier == 1)
			textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
		else
			textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);

		startBtn.setOnClickListener(new View.OnClickListener() {
	         @Override
	         public void onClick(View v) {
	         	if(MainActivity.ud.equalsIgnoreCase("download")) {
				String dataconnectivity=CommonFunctions.chkDataConnectivity(mView_UploadDownloadTest.this);


					//new DownloadTask().execute();


					if (downloadstatus == 0) {
						// statustext.setText("Download In progress");
						// currentspeed.setText( "0kbps (0%)" );
						dwnldtime.setText("");
						dwnldsize.setText("");
						dwnldspeed.setText("");
						startBtn.setText("Stop");
					//	downloadFileAsync = new DownloadFileAsync();
						AsyncTaskTools.execute(downloadFileAsync, downloadfileURL);

						// downloadFileAsync.execute(downloadfileURL);
					} else {
						if (downloadFileAsync != null) {
							sendMessageToActivity(5, "");

						} else {
							downloadstatus = 0;
							currentspeed.setText("");
							PointerSpeedometer.speedTo(0);
							progBar.setProgress(0);
						}
					}
				}
				else if(MainActivity.ud.equalsIgnoreCase("upload"))
				{
					String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath();;

					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_GET_CONTENT);
					Uri myUri = Uri.parse(folderPath);
					intent.setDataAndType(myUri, "*/*");
					startActivityForResult(intent, FILE_SELECT);

				}
	         }
	      });
        



		selectBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath();;

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_GET_CONTENT);
				Uri myUri = Uri.parse(folderPath);
				intent.setDataAndType(myUri, "*/*");
				startActivityForResult(intent, FILE_SELECT);
			}
		});

		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
		File file = new File(path, "mview" );
		if (!file.exists()) {
			file.mkdirs();
		}

		sourceUrl = path + "/mview/b.mp3";
    }

	public void setColours(ProgressBar progressBar,
						   int bgCol1, int bgCol2,
						   int fg1Col1, int fg1Col2, int value1,
						   int fg2Col1, int fg2Col2, int value2)
	{
		//If solid colours are required for an element, then set
		//that elements Col1 param s the same as its Col2 param
		//(eg fg1Col1 == fg1Col2).

		//fgGradDirection and/or bgGradDirection could be parameters
		//if you require other gradient directions eg LEFT_RIGHT.

		GradientDrawable.Orientation fgGradDirection
				= GradientDrawable.Orientation.TOP_BOTTOM;
		GradientDrawable.Orientation bgGradDirection
				= GradientDrawable.Orientation.TOP_BOTTOM;

		//Background
		GradientDrawable bgGradDrawable = new GradientDrawable(
				bgGradDirection, new int[]{bgCol1, bgCol2});
		bgGradDrawable.setShape(GradientDrawable.RECTANGLE);
		bgGradDrawable.setCornerRadius(5);
		ClipDrawable bgclip = new ClipDrawable(
				bgGradDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		bgclip.setLevel(10000);

		//SecondaryProgress
		GradientDrawable fg2GradDrawable = new GradientDrawable(
				fgGradDirection, new int[]{fg2Col1, fg2Col2});
		fg2GradDrawable.setShape(GradientDrawable.RECTANGLE);
		fg2GradDrawable.setCornerRadius(5);
		ClipDrawable fg2clip = new ClipDrawable(
				fg2GradDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

		//Progress
		GradientDrawable fg1GradDrawable = new GradientDrawable(
				fgGradDirection, new int[]{fg1Col1, fg1Col2});
		fg1GradDrawable.setShape(GradientDrawable.RECTANGLE);
		fg1GradDrawable.setCornerRadius(5);
		ClipDrawable fg1clip = new ClipDrawable(
				fg1GradDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

		//Setup LayerDrawable and assign to progressBar
		Drawable[] progressDrawables = {bgclip, fg2clip, fg1clip};
		LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
		progressLayerDrawable.setId(0, android.R.id.background);
		progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
		progressLayerDrawable.setId(2, android.R.id.progress);

		//Copy the existing ProgressDrawable bounds to the new one.
		Drawable aa = progressBar.getProgressDrawable();
		if( aa != null) {
			Rect bounds = progressBar.getProgressDrawable().getBounds();
			progressBar.setProgressDrawable(progressLayerDrawable);
			progressBar.getProgressDrawable().setBounds(bounds);
		}

		// setProgress() ignores a change to the same value, so:
		if (value1 == 0)
			progressBar.setProgress(1);
		else
			progressBar.setProgress(0);
		progressBar.setProgress(value1);

		// setSecondaryProgress() ignores a change to the same value, so:
		if (value2 == 0)
			progressBar.setSecondaryProgress(1);
		else
			progressBar.setSecondaryProgress(0);
		progressBar.setSecondaryProgress(value2);

		//now force a redraw
		progressBar.invalidate();
	}
	////////////////////



	public static int uploadstatus = 0;

    UploadToFtpNew uploadToFtp;

	public class UploadTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog pDialog;
        int uploadStat;
        UploadToFtpNew utp = new UploadToFtpNew();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
			uploadstatus = 1;
			progBarUpload.setProgress(0);
			progBar.setProgress(0);

        }

        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("source url -> " + sourceUrl);
            System.out.println("filename -> " + filename);
            System.out.println("desDirectory -> " + desDirectory);
            uploadToFtp = new UploadToFtpNew();
			uploadstatus = uploadToFtp.ftpUpload1(selectedFilePath, filename,
                    desDirectory, ftpserver, ftpuser, ftppwd, progBar, currentspeed1,PointerSpeedometer );

            return null;
        }

		/*@Override
		protected void onProgressUpdate(Integer... values) {
			//super.onProgressUpdate(values);
			PointerSpeedometer.speedTo(2);
		}*/

		@Override
        protected void onPostExecute(Void result) {
//            if (pDialog != null && pDialog.isShowing()) {
//                pDialog.dismiss();
//            }
            System.out.println("Result-->" + result);
            super.onPostExecute(result);
            try {
                if (uploadstatus == 1) {
                    uploadstatus = 0;
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            mView_UploadDownloadTest.this);

                    // Setting Dialog Message
                    alertDialog.setTitle("Upload Test");
                    if (uploadstatus == -3)
                        alertDialog.setMessage("Unknown FTP host. Please check Settings!");
                    else if (uploadstatus == -1) {
                        alertDialog.setMessage("The file to be uploaded not found, please try again!");
                    } else if (uploadstatus == -2) {
                        alertDialog.setMessage("Connection timed out error, please try again!");
                    } else if (uploadstatus == -4) {
                        alertDialog.setMessage("FTP username/pwd may not be correct. Please check Settings!");
                    }

                    uploadstatus = 0;
                    statustext1.setText("");
                    alertDialog.setCancelable(true);
					currentspeed1.setText("");
					progBarUpload.setProgress(0);
					progBar.setProgress(0);
                    // Setting Icon to Dialog

                    // Setting OK Button
                    alertDialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }
            }catch(Exception e)
            {

            }
            finally {
                uploadstatus = 0;
                startBtn.setText("Start");

                //startUploadBtn.setText("Start");
            }
        }//end onPostExecute
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (downloadFileAsync != null)
                downloadFileAsync.cancel(true);
        }catch(Exception e)
        {

        }

        try {
            if( uploadTask != null) {
                uploadTask.cancel(true);
                if (uploadToFtp != null)
                    uploadToFtp.abortFTP();
            }
        }catch(Exception e)
        {

        }
    }
	////////////////new sharad1010///////////////////////

	float currentband;
	int downloadstatus = 0;

	//this is our download file asynctask
	class DownloadFileAsync extends AsyncTask<String, Integer, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			PointerSpeedometer.speedTo(0);
			progBar.setProgress(0);
			downloadstatus = 1;
			//showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}


		@Override
		protected Void doInBackground(String... aurl) {

			try {
				starttime = System.currentTimeMillis();
				//connecting to url
				URL u = new URL(downloadfileURL);
                String [] arr = downloadfileURL.split("/");
                String fname = "b.mp3";
                if(arr.length > 0) {
                    fname = arr[arr.length - 1];
                }

				HttpURLConnection c = (HttpURLConnection) u.openConnection();
				c.setRequestMethod("GET");
				c.setDoOutput(true);
				c.connect();


				//lenghtOfFile is used for calculating download progress
				int lenghtOfFile = c.getContentLength();

				String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
				File file = new File(path, "mview" );
				if (!file.exists()) {
					file.mkdirs();
				}

				sourceUrl = path + "/mview/"+fname; //b.mp3";
				//file = new File(path, "mview" + "/" + "b.mp3");

				//this is where the file will be seen after the download
				FileOutputStream f = new FileOutputStream(new File(path + "/mview/", fname));
				//file input is from the url
				InputStream in = c.getInputStream();

				//here’s the download code
				byte[] buffer = new byte[1024];
				int len1 = 0;
				long total = 0;

				while ((len1 = in.read(buffer)) > 0) {
					total += len1; //total = total + len1
					int val1 = (int)((total*100)/lenghtOfFile);

					long tt = System.currentTimeMillis() - starttime;
					try {


						float bandInbps = ((total) / ((tt) / 1000));
						currentband = bandInbps / (1024); //kbps
						bandvalue = bandInbps/(1024*1024);
						//currentspeed.setText(band + "kbps");
						publishProgress(val1);
					}
					catch (ArithmeticException ae)
					{

					}
					f.write(buffer, 0, len1);
				}
				f.close();

				endtime = System.currentTimeMillis();
				long tt = endtime-starttime;
				long sz = lenghtOfFile;//localFile.length();
				float bandInbps = ((sz)/((endtime-starttime)/1000));
				float band = bandInbps/(1024*1024); //Mbps
				float szInMB = sz/(1024*1024);
				float ttInSecs = tt/1000;
				downloadResponse = "TimeTaken=" + String.format("%.0f", ttInSecs) + " sec, \nFileSize =" + String.format("%.2f", szInMB) + "Mb \nMeasured Speed = " + String.format("%.2f", band) + "Mbps";
				time=ttInSecs;
				size=szInMB;
				speed=band;

				mView_HealthStatus.mySpeedTest.downloadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
				mView_HealthStatus.mySpeedTest.downloadtest.isRoaming = mView_HealthStatus.roaming;
				mView_HealthStatus.mySpeedTest.downloadtest.lat = listenService.gps.getLatitude();
				mView_HealthStatus.mySpeedTest.downloadtest.lon = listenService.gps.getLongitude();
				mView_HealthStatus.mySpeedTest.downloadtest.networkType = mView_HealthStatus.iCurrentNetworkState;
				mView_HealthStatus.mySpeedTest.downloadtest.sizeInBytes = lenghtOfFile;
				//mView_HealthStatus.mySpeedTest.downloadtest.startTime = starttime;
				mView_HealthStatus.mySpeedTest.downloadtest.timeTakenInMS = tt;
				mView_HealthStatus.mySpeedTest.downloadtest.type = 2;
				mView_HealthStatus.mySpeedTest.downloadtest.protocol = mView_HealthStatus.connectionType;

				TinyDB db = new TinyDB(mView_UploadDownloadTest.this);
				db.putObject("download",mView_HealthStatus.mySpeedTest.downloadtest);
				downloadstatus = 2;


			}catch(MalformedURLException e1 ) {
				downloadstatus = -2;
			}catch(UnknownHostException e2) {
				downloadstatus = -3;
			}
			catch (Exception e) {
				Log.d(LOG_TAG, e.getMessage());
				downloadstatus = -1;
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			Log.d(LOG_TAG, progress[0] + "");
            try {
                Object o = progress[0];
                progBar.setProgress(Integer.parseInt(o.toString()));
				PointerSpeedometer.speedTo(Float.parseFloat(String.format("%.2f",bandvalue)));


               // currentspeed.setText(String.format("%.2f", currentband) + "kbps (" + o.toString() + "%)");
				//currentspeed.setText(String.format("%.2f", bandvalue) + "Mbps (" + o.toString() + "%)");


			}catch(Exception e)
            {

            }
			//mProgressDialog.setProgress(Integer.parseInt(o.toString()));
		}

		@Override
		protected void onPostExecute(Void unused) {
			//dismiss the dialog after the file was downloaded
			//dismissDialog(DIALOG_DOWNLOAD_PROGRESS);

            try {
                if (downloadstatus == 2) {
                 //   new WebService.Async_SendUporDownloadtestResults().execute(2);
                  //  resultTextView.setText(downloadResponse);
					dwnldtime.setText(String.format("%ssec", String.format("%.2f", time)));
					dwnldsize.setText(String.format("%sMb", String.format("%.2f", size)));
					dwnldspeed.setText(String.format("%sMbps", String.format("%.2f", speed)));
                //    resultHdr.setText("Test Results");

                } else if (downloadstatus < 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            mView_UploadDownloadTest.this);
                    alertDialog.setTitle("Download Test");
                    alertDialog.setMessage("Please check the Download Settings. The download URL is not correct.");
                    alertDialog.setCancelable(true);

                    alertDialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }
                downloadstatus = 0;
               // statustext.setText("");
                currentspeed.setText("");
                startBtn.setText("Start");
            }catch(Exception e)
            {

            }
            finally {
                downloadstatus = 0;
            }
		}//end onPostExecute
	}

	String message = "";

	public void sendMessageToActivity(int msg, String msgShow)
	{
		Message m = new Message();
		m.what = msg;
		message = msgShow;
		updateHandler.sendMessage(m);
		System.out.println("string is "+msgShow);
		//this.currntbnd=currentband;

	}



	public Handler updateHandler = new Handler(){

		// @Override
		public void handleMessage(Message msg) {

			int event = msg.what;
			switch(event){
				case 1:
					//ACM.myWebView.loadUrl("file:///android_asset/" + urlToLoad);
					//Toast.makeText(mView_UploadDownloadTest.this, "case "+ event, Toast.LENGTH_SHORT).show();
					currentspeed1.setText(message);

					break;
				case 2:
				//	Toast.makeText(mView_UploadDownloadTest.this, "case "+ event, Toast.LENGTH_SHORT).show();
					statustext1.setText("");
					resultTextView1.setText(message);
					float tt=  mView_HealthStatus.mySpeedTest.uploadtest.timeTakenInMS;
					long totalbytestransferred=mView_HealthStatus.mySpeedTest.uploadtest.sizeInBytes;
					long sz = totalbytestransferred;
					float bandInbps = ((sz)/((tt)/1000));
					float band = bandInbps/(1024*1024); //Mbps
					float szInMB = (float)sz/(1024*1024);
					float ttInSecs = tt/1000;
					dwnldtime.setText(String.format("%ssec", String.format("%.2f", ttInSecs)));
					dwnldsize.setText(String.format("%sMb", String.format("%.2f", szInMB)));
					dwnldspeed.setText(String.format("%sMbps", String.format("%.2f", band)));
					currentspeed1.setText("");
					resultHdr1.setText("Test Results");
					break;
				case 3:
					Toast.makeText(mView_UploadDownloadTest.this, "case "+ event, Toast.LENGTH_SHORT).show();
					System.out.println("in case 3 the message is"+message);
					sizetext.setText(message);
					break;
                case 4:
					Toast.makeText(mView_UploadDownloadTest.this, "case "+ event, Toast.LENGTH_SHORT).show();
                    statustext1.setText("");
                    startBtn.setText("Start");
                  //  startUploadBtn.setText("Start");
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                uploadstatus = 0;

                                uploadTask.cancel(true);

                                uploadTask = null;
                                if (uploadToFtp != null)
                                    uploadToFtp.abortFTP();
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                                int a =0;
                            }
                        }
                    });

                    thread.start();

                    break;
                case 5:
					Toast.makeText(mView_UploadDownloadTest.this, "case "+ event, Toast.LENGTH_SHORT).show();
                   // statustext.setText("");
                    currentspeed.setText("");
                    startBtn.setText("Start");
                    Thread thread1 = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                downloadstatus = 0;

                                downloadFileAsync.cancel(true);

                                downloadFileAsync = null;
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                                int a =0;
                            }
							downloadstatus =0;
                        }
                    });

                    thread1.start();

                    break;
			}//end of switch
		}
	}; //end of updateHandler

	String selectedFilePath = "";
	int selected_file_sizeInKB = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case FILE_SELECT:
				{
					Uri uri = data.getData();

					String path="";
					try {
						path = getPathOfSelectedFile(uri);
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if( path == null) {
						path = getPathOfThisUri(this, uri);
					}

					if( path != null) {
						File file = new File(path);
						long t = file.length();
						selected_file_sizeInKB = Integer.parseInt(String.valueOf(t / 1024));
						String msg = "Selected file size is " +selected_file_sizeInKB +"kb";
						sendMessageToActivity(3, msg);
					}

					selectedFilePath = path;
					if(selectedFilePath.equals(""))
					{
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								mView_UploadDownloadTest.this);
						alertDialog.setTitle("Upload Test");
						alertDialog.setMessage("Please select the file to upload!");
						alertDialog.setCancelable(true);

						alertDialog.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int id) {
										dialog.cancel();
									}
								});
						alertDialog.show();
					}else {
						if( uploadstatus == 0) {
							statustext1.setText("Upload In progress");
						//	startUploadBtn.setText("Stop");
							dwnldtime.setText("");
							dwnldsize.setText("");
							dwnldspeed.setText("");
							startBtn.setText("Stop");
							uploadTask = new UploadTask();
							AsyncTaskTools.execute( uploadTask );
							//
							// uploadTask.execute();
						}else {
							if(uploadTask != null) {
								try {

									sendMessageToActivity(4,"");

								}catch(Exception e)
								{
									e.printStackTrace();
									int a =0;
								}
							}
						}
					}
					break;
				}
				case SETTINGS_ACTIVITY:
				{
					TinyDB db = new TinyDB(mView_UploadDownloadTest.this);

					String t1 = db.getString("downloadurl");
					if( t1 == null || t1.equals(""))
						downloadfileURL = "http://hi-dj.com/hidj/dj_songs/2016DJ000000010151.mp3";
					else
						downloadfileURL = t1;

					t1 = db.getString("ftpserverurl");
					if( t1 == null || t1.equals(""))
						ftpserver = "hi-dj.com";
					else
						ftpserver = t1;

					t1 = db.getString("ftpusername");
					if( t1 == null || t1.equals(""))
						ftpuser = "hidjftp";
					else
						ftpuser = t1;

					t1 = db.getString("ftppwd");
					if( t1 == null || t1.equals(""))
						ftppwd = "hidj@2015";
					else
						ftppwd = t1;
					break;
				}
			}//end switch
		}//end if
	}//end function

	public static String getPathOfSelectedFile(Uri uri) throws URISyntaxException {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = mView_UploadDownloadTest.mContext.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		}
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static String getPathOfThisUri(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	//our progress bar settings
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//			case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
//				mProgressDialog = new ProgressDialog(this);
//				mProgressDialog.setMessage("Downloading file…");
//				mProgressDialog.setIndeterminate(false);
//				mProgressDialog.setMax(100);
//				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//				mProgressDialog.setCancelable(true);
//
//
//				GradientDrawable progressGradientDrawable = new GradientDrawable(
//						GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
//						0xff1e90ff,0xff006ab6,0xff367ba8});
//				ClipDrawable progressClipDrawable = new ClipDrawable(
//						progressGradientDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
//				Drawable[] progressDrawables = {
//						new ColorDrawable(0xffffffff),
//						progressClipDrawable, progressClipDrawable};
//				LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
//
//				progressLayerDrawable.setId(0, android.R.id.background);
//				progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
//				progressLayerDrawable.setId(2, android.R.id.progress);
//
//				mProgressDialog.setProgressDrawable(progressLayerDrawable);
//
//				mProgressDialog.show();
//				return mProgressDialog;
//			default:
//				return null;
//		}
//	}
	/////////////////////////////////////////

	////////////////
	class DownloadTask extends AsyncTask<Void, Integer, Void> {
		ProgressDialog pDialog;
		Boolean uploadStat;
		UploadToFtpNew utp = new UploadToFtpNew();

		@Override
		protected void onPreExecute() {
//			pDialog = new ProgressDialog(mView_UploadDownloadTest.this);
//			pDialog.setMessage("Uploading...");
//			pDialog.setCancelable(false);
//			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			pDialog.show();

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			File file = new File(path, "mview" );
			if (!file.exists()) {
				file.mkdirs();
			}

			sourceUrl = path + "/mview/b.mp3";
			file = new File(path, "mview" + "/" + "b.mp3");
			try {
				//2016DJ000000010150.mp3 - 4.67 mb
				downloadAndSaveFile("hi-dj.com", 21, "hidjftp", "hidj@2015", "/hidj/dj_songs/2016DJ000000010151.mp3", file );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
//			if (pDialog != null && pDialog.isShowing()) {
//				pDialog.dismiss();
//			}
//			System.out.println("Result-->" + result);
		//	resultTextView.setText(downloadResponse);
			dwnldtime.setText(String.format("%ssec", String.format("%.2f", time)));
			dwnldsize.setText(String.format("%sMb", String.format("%.2f", size)));
			dwnldspeed.setText(String.format("%sMbps", String.format("%.2f", speed)));
			super.onPostExecute(result);
		}
	}


	/////////////////

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		// show menu only when home fragment is selected
		//sharad - commented to hide teh top right menu
//        if (navItemIndex == 0) {
		getMenuInflater().inflate(R.menu.settings, menu);
//        }

		// when fragment is notifications, load the menu created for notifications
//		if (navItemIndex == 3) {
//			getMenuInflater().inflate(R.menu.notifications, menu);
//		}
		return true;
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
		if (id == R.id.up_down_settings) {
			Intent intent3 = new Intent(this, UploadDownloadSettings.class);
			startActivityForResult(intent3, SETTINGS_ACTIVITY);
			//Toast.makeText(getApplicationContext(), "UpLoad/Download Settings", Toast.LENGTH_LONG).show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	///////////////////

	private Boolean downloadAndSaveFile(String server, int portNumber,
										String user, String password, String filename, File localFile)
			throws IOException {
		FTPClient ftp = null;
		Boolean commandOK = false;

		try {
			starttime = System.currentTimeMillis();
			ftp = new FTPClient();
			ftp.setControlEncoding("UTF-8");
			ftp.connect(server, portNumber);
			Log.d(LOG_TAG, "Connected. Reply: " + ftp.getReplyString());

			ftp.login(user, password);
			Log.d(LOG_TAG, "Logged in");
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			Log.d(LOG_TAG, "Downloading");
			ftp.enterLocalPassiveMode();
			//ftp.setAutodetectUTF8(true);
			//ftp.enterLocalPassiveMode();
			// OutputStream outputStream = null;
			boolean success = false;
			String errmsg = "";

			try {

				InputStream inputStream = ftp.retrieveFileStream(filename);
				FileOutputStream fileOutputStream = new FileOutputStream(localFile);
				//Using org.apache.commons.io.IOUtils
				IOUtils.copy(inputStream, fileOutputStream);
				fileOutputStream.flush();
				IOUtils.closeQuietly(fileOutputStream);
				IOUtils.closeQuietly(inputStream);
				commandOK = ftp.completePendingCommand();

				//outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
				//success = ftp.retrieveFile(filename, outputStream);
			} catch(Exception e)
			{
				errmsg = e.getMessage();
			}

			finally {
				if (commandOK == true) { //outputStream != null) {
					//outputStream.close();
					endtime = System.currentTimeMillis();
					long tt = endtime-starttime;
					long sz = localFile.length();
					float bandInbps = ((sz)/((endtime-starttime)/1000));
					float band = bandInbps/(1024*1024); //Mbps
					float szInMB = sz/(1024*1024);
					float ttInSecs = tt/1000;
					downloadResponse = "TimeTaken=" + String.format("%.2f", ttInSecs) + "sec, Size =" + String.format("%.2f", szInMB) + "Mb \nMeasured Speed = " + String.format("%.2f", band) + "Mbps";

				}else {
					String error = ftp.getReplyString();
					Log.e("ddd", error);
				}
			}

			return success;
		} finally {
			if (ftp != null) {
				ftp.logout();
				ftp.disconnect();
			}
		}
	}



}
