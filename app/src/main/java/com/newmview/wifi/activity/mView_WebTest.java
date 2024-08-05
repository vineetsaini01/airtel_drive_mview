package com.newmview.wifi.activity;


import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mview.airtel.R;
import com.newmview.wifi.mView_HealthStatus;
import com.webservice.WebService;

public class mView_WebTest extends AppCompatActivity {

	Button startBtn;
	TextView result,textview;
	TextView url_result,speed_result;
	ImageView google,fb,twitter,wikipedia,yahoo;
	TextView google_res,fb_res,twitter_res,wiki_res,yahoo_res;
	TextView google_speed,fb_speed,twitter_speed,wiki_speed,yahoo_speed;
	LinearLayout google_layout,fb_layout,twitter_layout,wiki_layout,yahoo_layout;
	   
	   private WebView wv1;
	   
	   int currIndex;
	   
	   public static String DisplayResultString,DisplayUrl,DisplayResult;
	private boolean opened=false;
	int time=0;


	@Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.webtest_new);
		   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		  init();






		   wv1=(WebView)findViewById(R.id.webView);
	      wv1.setWebViewClient(new MyBrowser());
	      //wv1.setWebChromeClient(new MyBrowser1());
	     // result.setText(DisplayResultString);

		   if( mView_HealthStatus.connectionTypeIdentifier == 1)
		   	textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
		   else
			   textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);

	      startBtn.setOnClickListener(new View.OnClickListener() {
	         @Override
	         public void onClick(View v) {

				 if(testStarted == false)
				 {
					 testStarted = true;
					 startBtn.setText("STOP TEST");
					 google_speed.setVisibility(View.INVISIBLE);
					 fb_speed.setVisibility(View.INVISIBLE);
					 twitter_speed.setVisibility(View.INVISIBLE);
					 wiki_speed.setVisibility(View.INVISIBLE);
					 yahoo_speed.setVisibility(View.INVISIBLE);
					 running = 0;
					 currIndex = 0;
					 mView_HealthStatus.mySpeedTest.initWebtest();
					 System.out.println("currentindex initially"+currIndex);
					 final String url = mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(currIndex).url;

					 wv1.getSettings().setLoadsImagesAutomatically(true);
					 //wv1.getSettings().setJavaScriptEnabled(true);
					 wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
					 previousBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
					 totalBytes = 0;
					 Log.e("mView", "Starting bytes ==>   " + previousBytes);
					 timeStarted = System.currentTimeMillis();
					 final String url1 = url.replace("http://", "");
					/* if(opened)
					 {
					 	time=0;
					 	opened=false;
					 }
					 else

					 {
					 	time=2000;
					 }*/

					 //DisplayResultString = /*"1. " +*/ url1 ;
					  Handler handler = new Handler();
					 handler.postDelayed(new Runnable() {
						 @Override
						 public void run() {
							 wv1.loadUrl(url);
							 DisplayUrl=url1;

						 }


					 },1000);
					 System.out.println("url to be loaded on btn clck"+url);
				 }else {
					 testStarted = false;
					 startBtn.setText("START TEST");
					 wv1.stopLoading();
				 }



	         }
	      });
	   }

	private void init() {

		textview=(TextView)findViewById(R.id.textview);
		startBtn=(Button)findViewById(R.id.startBtn);
		google=(ImageView)findViewById(R.id.googlelimg);
		fb=(ImageView)findViewById(R.id.fbimg);
		twitter=(ImageView)findViewById(R.id.twitter);
		wikipedia=(ImageView)findViewById(R.id.wikiimg);
		yahoo=(ImageView)findViewById(R.id.yahooimg);

		google_res=(TextView)findViewById(R.id.googleurl);
		fb_res=(TextView)findViewById(R.id.fburl);
		twitter_res=(TextView)findViewById(R.id.twitterurl);
		yahoo_res=(TextView)findViewById(R.id.yahoourl);
		wiki_res=(TextView)findViewById(R.id.wikiurl);
		google_layout=(LinearLayout) findViewById(R.id.googlelayout);
		fb_layout=(LinearLayout) findViewById(R.id.fblayout);
		twitter_layout=(LinearLayout)findViewById(R.id.twitterlayout);
		yahoo_layout=(LinearLayout)findViewById(R.id.yahoolayout);
		wiki_layout=(LinearLayout) findViewById(R.id.wikilayout);
		google_speed=(TextView)findViewById(R.id.googlespeed);
		fb_speed=(TextView)findViewById(R.id.fbspeed);
		twitter_speed=(TextView)findViewById(R.id.twitterspeed);
		yahoo_speed=(TextView)findViewById(R.id.yahoospeed);
		wiki_speed=(TextView)findViewById(R.id.wikispeed);

	}

	boolean testStarted = false;
	   /*@Override
	   public void onBackPressed(){
		   Intent returnIntent = new Intent();
		   //returnIntent.putExtra("result",result);
		   setResult(Activity.RESULT_OK,returnIntent);
		   finish();
		   
	   }*/
	   private int running = 0; // Could be public if you want a timer to check.
	   
	   private class MyBrowser extends WebViewClient {
	      @Override
	      public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        
	         running++;
	         System.out.println("url to be loaded in should override"+url);
	         view.loadUrl(url);
			  return true;
	      }
	      
	      @Override
	      public void onPageFinished(WebView view, String url)
	  	  {
	    	  if(--running == 0) { // just "running--;" if you add a timer.
	    		  timeFinished = System.currentTimeMillis();
		    	  long timeTaken =  timeFinished - timeStarted;
		    	  int a = android.os.Process.myUid();
	              final long currentBytes = TrafficStats.getUidRxBytes(a);
	              totalBytes = currentBytes - previousBytes;
		    	  //long seconds = timeTaken/1000;
		    	  Log.e("mView", "Page Finished ==>   " + totalBytes + " " + timeTaken + " " + url +"current "+currentBytes +"previous "+previousBytes);
				  mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(currIndex).timeTakenInMS = timeTaken;
				  mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(currIndex).bytes = totalBytes;
		    	  
		    	  float f1 = (float)totalBytes;
		    	  float f2 = f1/1000.0f;
		    	  String s1 = String.format("%.2f",f2);
		    	  
		    	  f1 = (float)timeTaken;
		    	  f2 = f1/1000.0f;
		    	  String s2 = String.format("%.2f",f2);
		    	  System.out.println("s1 "+s1 +"  "+"s2" +" "+s2);
		    	  DisplayResultString = s1 + "kb " + s2 + "sec";
				  Toast.makeText(mView_WebTest.this, "index"+currIndex, Toast.LENGTH_SHORT).show();


				  final Handler handler = new Handler();
		  		  handler.postDelayed(new Runnable() {
		  		  @Override
		  		  public void run() {
		  			running = 0;
					  if(testStarted == false)

						  return;
		  			previousBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
		  			totalBytes = 0;
			    	timeStarted = System.currentTimeMillis();
			    	currIndex++;

			    	System.out.println("current index on inc  " +currIndex +" "+ "and in run block");

			    	if( currIndex < mView_HealthStatus.mySpeedTest.webtest.websiteArr.size())
			    	{

			    		
			    		String url = mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(currIndex).url;
			    		String url1 = url.replace("http://", "");
						int temp = currIndex+1;
						String ind = temp + "";
			    		//DisplayResultString += /*ind + ". " + */"<br>"+url1+"</br>";
						DisplayUrl=url1;
						System.out.println("url to be loaded in onpagefinished"+url);
			    		wv1.loadUrl(url);
			    	}else {
						testStarted = false;
						startBtn.setText("START TEST");
						WebService.API_sendWebTest("view");

					}
		  		  }
		  		}, 3000);



				  switch(currIndex) {
					  case 0:
						  google_speed.setVisibility(View.VISIBLE);
						  //google_res.setText(DisplayUrl);
						  google_speed.setText(DisplayResultString);
						  break;
					  case 1:
						  fb_speed.setVisibility(View.VISIBLE);
						  //fb_res.setText(DisplayUrl);
						  fb_speed.setText(DisplayResultString);
						  break;
					  case 2:
						  twitter_speed.setVisibility(View.VISIBLE);
						  //twitter_res.setText(DisplayUrl);
						  twitter_speed.setText(DisplayResultString);
						  break;
					  case 3:
						  wiki_speed.setVisibility(View.VISIBLE);
						  //wiki_res.setText(DisplayUrl);
						  wiki_speed.setText(DisplayResultString);
						  break;
					  case 4:
						  yahoo_speed.setVisibility(View.VISIBLE);
						  // yahoo_res.setText(DisplayUrl);
						  yahoo_speed.setText(DisplayResultString);
						  break;

				  }
		  		
	            }
	    	  
	  	  }
	      
	      @Override
	      public void onPageStarted(WebView view, String url, Bitmap favicon){
	    	  running = Math.max(running, 1);
	      }
	   }
	   
	   long timeStarted;
	   long timeFinished;
	   long previousBytes;
	   long totalBytes;
	  
	   private class MyBrowser1 extends WebChromeClient {
		     
		      
		      @Override
	          public void onProgressChanged(WebView view, int newProgress) {
	              super.onProgressChanged(view, newProgress);
	              
	              if( newProgress == 100)
	              {
	            	  int a = android.os.Process.myUid();
		              long currentBytes = TrafficStats.getUidRxBytes(a);
		              totalBytes = currentBytes - previousBytes;
	            	  timeFinished = System.currentTimeMillis();
	    	    	  long timeTaken =  timeFinished - timeStarted;
	    	    	  Log.e("mView", "chrome Page Finished ==>   " + totalBytes + " " + timeTaken + "ms" );
	    	    	  //Log.e("mView", "Current Bytes ==>   " + totalBytes
	                   //   + "   New Progress ==>   " + newProgress);
	              }
	          }
		   }
	   
	  /* @Override
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
	      if (id == R.id.action_settings) {
	         return true;
	      }
	      return super.onOptionsItemSelected(item);
	   }*/

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

		return super.onOptionsItemSelected(item);
	}
}
