package com.gpsTracker;


import android.content.Context;

public class GPSTrackingActivity {
	GPSTracker gps;
	Context mContext;
	String longitude_latitude;
	String longitude;
	String latitude;
	public GPSTrackingActivity(Context context) {
		// TODO Auto-generated constructor stub
		
		this.mContext=context;
	}
	
	
	public String getLongitude() {
		longitude = gps.getLongitude()+"";
		return longitude;
	}


	public String getLatitude() {
		latitude = gps.getLatitude()+"";
		return latitude;
	}


	public String getLongitudeAndLatitude(){
		gps = new GPSTracker(mContext);

		// check if GPS enabled
		if (gps.canGetLocation()) {
//			longitude_latitude="Lat: " +  gps.getLatitude() + "\nLong: "
//					+ gps.getLongitude();
			 
			longitude_latitude= +  gps.getLatitude() + "+"+ gps.getLongitude();

//			gpsLocationEditText.setText("Lat: " + latitude + "\nLong: "
//					+ longitude);

			// \n is for new line
			// Toast.makeText(getApplicationContext(),
			// "Your Location is - \nLat: " + latitude + "\nLong: " + longitude,
			// Toast.LENGTH_LONG).show();

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}

		try {
//			Geocoder geo = new Geocoder(
//					mContext.getApplicationContext(),
//					Locale.getDefault());
//			List<Address> addresses = geo.getFromLocation(gps.getLatitude(), gps.getLongitude(),
//					1);
//			if (addresses.isEmpty()) {
//				Toast.makeText(mContext, "Waiting for Location", Toast.LENGTH_LONG)
//						.show();
//
//			} else {
//				if (addresses.size() > 0) {
//
//					Toast.makeText(
//							mContext,
//							addresses.get(0).getFeatureName() + ", "
//									+ addresses.get(0).getLocality() + ", "
//									+ addresses.get(0).getAdminArea() + ", "
//									+ addresses.get(0).getCountryName(),
//							Toast.LENGTH_LONG).show();
//					// yourtextfieldname.setText(addresses.get(0).getFeatureName()
//					// + ", " + addresses.get(0).getLocality() +", " +
//					// addresses.get(0).getAdminArea() + ", " +
//					// addresses.get(0).getCountryName());
//					// Toast.makeText(getApplicationContext(), "Address:- " +
//					// addresses.get(0).getFeatureName() +
//					// addresses.get(0).getAdminArea() +
//					// addresses.get(0).getLocality(),
//					// Toast.LENGTH_LONG).show();
//				}
//				
//			}
		} catch (Exception e) {
			e.printStackTrace(); // getFromLocation() may sometimes fail
			longitude_latitude="Fail to get Location";
		}
		return longitude_latitude;
	}
}
