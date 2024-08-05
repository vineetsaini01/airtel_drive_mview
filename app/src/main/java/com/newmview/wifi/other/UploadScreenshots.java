package com.newmview.wifi.other;

import android.content.Context;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.RequestResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;


public class UploadScreenshots {
	public static boolean uploadinit=true;

	ArrayList<HashMap<String,String>> urlsDetailsList=null;			
    private Context context;
	private Connection c;
	//private Db_connector db_connector;

	private int uploadCount=0;
	private String scan_id=" ";
	public static  boolean dbCheck=true;
	public static boolean uploading=false;
	private static final String  SC_PATH="/home/pi/screenshots/";
	//private static Logger logger= LogManager.getLogger(UploadScreenshots.class);
	
public static void main(String[] args)
{
	
}
	
	public void startUploadProcess()
	{
	
		
		try {
			uploadinit=false;
			readtable();
			
				} 
		catch(Exception e)
		{
	     	//logger.error("Exception",e);
	
		}
		finally
		{
			uploadinit =true;
			
		 
		}
	
	//	}
		
		
	}
		
		
		

		
		
		
		
//		}
		
	
	
//	private void  updatePreviousStatusIfExceptionOccurs(String url_id)
//	{
//	String status=null;
//	  Class db_c;
//		try {
//
//			db_c = Class.forName("database.Db_connector");
//
//			  int paramCount =6;
//		        Method method;
//		        Object o = db_c.newInstance();
//		       System.out.println("id is "+url_id );
//		        Object[]obj= {url_id,helper.Config.manualScanUploadValue};
//		  Class<?>params[]=new Class[obj.length];
//		  for (int i = 0; i < obj.length; i++) {
//	            if (obj[i] instanceof Integer) {
//	                params[i] = Integer.TYPE;
//	            } else if (obj[i] instanceof String) {
//	                params[i] = String.class;
//	            }
//
//	        }
//
//
//		    Method insert_method =db_c.
//		    		getDeclaredMethod("getStatusOfUrl",  params);
//		    insert_method.setAccessible(true);
//		   status= (String) insert_method.invoke(o, obj);
//
//
//
//		} catch (ClassNotFoundException | NoSuchMethodException |
//				SecurityException | IllegalAccessException |
//				IllegalArgumentException | InvocationTargetException | InstantiationException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception",e);
//			System.out.println(helper.Config.getDateTime()+"  : via  updatePreviousStatusIfExceptionOccurs Exception occured in upload class in 5... "+e.getMessage());
//		}
//
//
//
//
//	if(status!=null)
//	{
//		if(status.equalsIgnoreCase("US"))
//		{
//			System.out.println(helper.Config.getDateTime() +" : via  updatePreviousStatusIfExceptionOccurs  Updating the status with url id "+url_id +" from US to completed again");
//			updatestatusofmanualscanfile(url_id,"completed");
//		}
//	}
//
//
//
//	}
//

	private void readtable(){
		DB_handler db_handler=new DB_handler(context);
		db_handler.open();

		 //System.out.println(helper.Config.getDateTime()+" : via readtable  Mutex  status is "+uploadinit);
		ArrayList<HashMap<String, String>> countList= (ArrayList<HashMap<String, String>>) db_handler.getScCountToUpload();
		if(countList!=null)
		{
			for(int i=0;i<countList.size();i++)
			{
				String count=countList.get(i).get("count");
				String scanId=countList.get(i).get("scan_id");
				sendEvent(count,scanId);
			}
		}
		hitReadQueryAndAddInList();
	 
			 }
	private void sendEvent(String count,String scan_id) {
		DB_handler db_handler=new DB_handler(context);
		db_handler.open();
		System.out.println(Config.getDateTime()+" Sc count is "+count +" for scanid "+scan_id);
		// TODO Auto-generated method stub
		 try { 
			  JSONObject jsonobj=new JSONObject();
		  jsonobj.put("desc", "Screenshot count for "
		  		+ "manual scan with scan id "+scan_id +" is "+count); 
		  jsonobj.put("date_time",Config.getDateTime());
		  jsonobj.put("additional_info", " ");
		  Object[] params = {"","sc_count", jsonobj.toString(),"init", Constants.manualScanUploadValue};
		  db_handler.insertInLoggingAgentTable("","","","","");
		 }
		  catch (JSONException e1) { // TODO
		  
		  e1.printStackTrace();
		  }
		
	}
	
	//throws JSONException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, IOExceptionthrows JSONException, InstantiationException, IllegalAccessException, NoSuchMethodException,
	//SecurityException, IllegalArgumentException, InvocationTargetException, IOException
	
	private void checkStatusAndUpload() 
	{
		 for (int i=0;i<urlsDetailsList.size();i++)
			 
		 {
			 String status=urlsDetailsList.get(i).get("status");
			 String scan_details=urlsDetailsList.get(i).get("scan_details");
				
				String scan_id=urlsDetailsList.get(i).get("scan_id");
				 String upload_id=urlsDetailsList.get(i).get("upload_id");
				 
				

				 String url_id=urlsDetailsList.get(i).get("url_id");
			//	 System.out.println(helper.Config.getDateTime()+ " :  via  checkStatusAndUpload SCAN_STATUS : UploadScreenshots class "+"status for id "+url_id +" is "+status);
			 if(status!=null)
			 {
			if(status.equalsIgnoreCase("completed"))
			{
				
				
				/*
				 * if (uploadinit) {
				 * 
				 * uploadinit=false;
				 */
					/* System.out.println(helper.Config.getDateTime()+ " : via  checkStatusAndUpload Value of upload init from 1 changed to"
					 		+ " "+uploadinit);
					
					 
					 System.out.println(helper.Config.getDateTime() +" : via  checkStatusAndUpload scan_details are "+scan_details);
					 */
					
					 JSONObject object;
					try {
						object = new JSONObject(scan_details);
					
					
						JSONObject scan_detailsobj=object.optJSONObject("scan_details");
					 String file_name=scan_detailsobj.optString("file_name");
	         		String file_size=scan_detailsobj.optString("file_size");
				
	         		String media_type=scan_detailsobj.optString("media_type");
					 
	         		
					
					
					Object[] fileParams= {SC_PATH+file_name};
					boolean fileExists=(boolean) Constants.checkIfFileExistsOrNot(file_name);
					if(fileExists)
			   //	if(helper.Utility.checkIfFileExistsOrNot(file_name))
			   			{
				 
				 
			/*	 System.out.println(helper.Config.getDateTime() +" : via  checkStatusAndUpload  from upload class  "+scan_details+" "
				 		+ "scan id is "+scan_id+" upload id is "+upload_id+" "
				 				+ "and url id is "+url_id+" file name is "
				 		+file_name+" and file size is "+file_size+"media type is "
				 				+ ""+media_type +"status is" +status);*/
				 
				 
				 
				  if (file_size.equals("0")) {

//                      adp.updateuploaddata(category_id, " ", "err", "0");
                      uploadinit = true;
                     // System.out.println(" : via  checkStatusAndUpload Value of upload init from 2 changed to "+uploadinit);
                     //code commented by Sonal on 24-12-2021
                      updatestatusofmanualscanfile(url_id,"uploaded");
                      
						/*
						 * updatestatusofmanualscanfile(url_id,"error"); return;
						 */

//                      adp.updatestatusandcountineventmediaimformation(idoffile, "err", upload_id, "0");

                  } 
				  else
				  {
					 
					
					  
//					  adp.updateuploaddata(category_id, " ", "US", "0");
				      updatestatusofmanualscanfile(url_id,"US");
				      
				      Object[] params= {upload_id,SC_PATH+file_name,file_size,url_id};
//				      RequestResponse a=new RequestResponse();
//				      a.getCountNumber(upload_id,file_name,file_size,url_id);
				     // String responseOne = parseGCNData();

				    if(Constants.responseOne!=null)
				    {
				    	JSONObject responseobj=new JSONObject(Constants.responseOne);

						String statusVal=responseobj.optString("status");
						if(statusVal.equalsIgnoreCase("0"))
						{

							String count=responseobj.optString("count");
							if(!count.equalsIgnoreCase("end"))
							{


							Uploadfile uploadfile=new Uploadfile(file_name,
									file_size,upload_id,count,url_id);
							uploadfile.run();
							}
							else
							{
								Object param[] = {SC_PATH+file_name};
								Constants.removeFile(file_name);
								/*helper.Utility.removeFile(file_name);*/
								updatestatusofmanualscanfile(url_id, "uploaded");
								//uploadinit=true;
								//System.out.println("Value of upload init from 5 changed to "+uploadinit);
							}

						}
				    }
					 // getcountnumber(upload_id,file_name,file_size,url_id);
				  
				
			//}
				 }
			   			}
			   	else
			   	{
			   		updatestatusofmanualscanfile(url_id,"status_uploaded");
			   	}
			   	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//updatePreviousStatusIfExceptionOccurs(url_id);
				//logger.error("Exception",e);
				System.out.println(Config.getDateTime()+" : via  checkStatusAndUpload Exception occured in upload class in 4... "+e.getMessage());
				//uploadinit=true;
				
			}
			   	
			}
			 
				 }
		 }
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void hitReadQueryAndAddInList() 
				{
		//ArrayList<HashMap<String,String>> urlsDetailsList=null
					DB_handler db_handler=new DB_handler(context);
					db_handler.open();
		urlsDetailsList= (ArrayList<HashMap<String, String>>) db_handler.readManualScanUrlsWithCompletedStatus();
		if(urlsDetailsList!=null) {
		    		//System.out.println(helper.Config.getDateTime() +" : via hitReadQueryAndAddInList Screenshots count to be uploaded "+urlsDetailsList.size());
		    	uploadCount=urlsDetailsList.size();
		    		if(urlsDetailsList.size()>0)
		    	{
		    		checkStatusAndUpload();
		    	}
		    	}
		    
		    	
		    
		
		
	}

	private void updatestatusofmanualscanfile(String id,String status) {
		// TODO Auto-generated method stub
		
		//
		
		
		
		
		
		  Class db_c;
			try {
				 
				db_c = Class.forName("database.Db_connector");
			
				  int paramCount =6;
			        Method method;
			        Object o = db_c.newInstance();
			       //System.out.println(": via updatestatusofmanualscanfile id is "+id );
			        Object[]obj= {id,status,Constants.manualScanUploadValue};
			  Class<?>params[]=new Class[obj.length];
			  for (int i = 0; i < obj.length; i++) {
		            if (obj[i] instanceof Integer) {
		                params[i] = Integer.TYPE;
		            } else if (obj[i] instanceof String) {
		                params[i] = String.class;
		            }
		           
		        }
			 
			        
			    Method insert_method =db_c.
			    		getDeclaredMethod("updatestatusofmanualscan",  params);
			    insert_method.setAccessible(true);  
			    insert_method.invoke(o, obj);

	
		    	
			} catch (ClassNotFoundException | NoSuchMethodException
					| SecurityException | IllegalAccessException |
					IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalStateException e) {
				// TODO Auto-generated catch block
				//logger.error("Exception",e);
			//	System.out.println(helper.Config.getDateTime()+" : via updatestatusofmanualscanfile  Exception occured in upload class in 6... "+e.getMessage());
			} 
			
		
	}



	private class Uploadfile /*extends Thread*/
	{
		
		String file_name;
		String file_size;
		String count1;
		String upload_id;
		private FileInputStream fileInputStream;
		private int bytesAvailable1;
		private String countstr1;
		private String countstr;
		private String astring;
		private int percentagecal;
		String url_id;
		public Uploadfile(String filename, String filesize, String uploadid, String count,String urlid) {
			// TODO Auto-generated constructor stub
			file_name=filename;
			file_size=filesize;
			count1=count;
			upload_id=uploadid;
			url_id=urlid;
			//System.out.println(" : via Uploadfile file size in constructor is "+file_size);
			
			
			
		}

		public void run(){  
			//System.out.println(" : via run Inner thread to upload file");
		
		     
//		        int pos = filename.lastIndexOf("/");
		        HttpURLConnection conn = null;
		        DataOutputStream dos = null;
		        String lineEnd = "\r\n";
		        String twoHyphens = "--";
		        String boundary = "*****";
		        int bytesRead, bufferSize;
		        byte[] buffer;
		        int maxBufferSize = 1 * 1024 * 1024;
		       //logger.debug(" : checking for screenshot file with path "+SC_PATH+file_name);
		        File sourceFile = new File(SC_PATH+file_name);
		        
		        if(sourceFile.exists())
		        {
		        	  int fi = Integer.valueOf(file_size);
		              int co = Integer.valueOf(count1);
		              int maxcount1 = fi / maxBufferSize;
		            // logger.debug(" " + ": via run current count and max count is "+co+" and "+maxcount1 );
		             
		              
	            	  try {
		                  fileInputStream = new FileInputStream(sourceFile);
		              } catch (FileNotFoundException e) {
		                 System.out.println(" : via run file not found...");
		                //  logger.error("Exception",e);
		              }
						
		               
		              
		              if (!count1.equals("end") ) {
		                  for (; co <= maxcount1; ) {

//		                      Log.i("UploadTest", "Going  count is" + co + "and max count is " + maxcount1);


		                      if (co == maxcount1) {
		                          count1 = "end";
//		                  


		                      } else {
		                          count1 = String.valueOf(co);
		                          float percentagecalf = ((float) co) / ((float) maxcount1);

		                          float percentag = percentagecalf * 100;
		                          percentagecal = Math.round(percentag);
//		                 

		                      }
		                      try {
		                       //   URL url = new URL("http://198.12.250.223/mtantu/uploaddataurlfile");
//		                    	  
	                    	  //URL url = new URL("http://10.107.146.130:12202/mtantu/uploaddataurlfile");
		                    	 URL url = new URL("http://10.107.146.130/mtantu/uploaddataurlfile");
		                          conn = (HttpURLConnection) url.openConnection();
		                          conn.setDoInput(true);
		                          conn.setDoOutput(true);
		                          conn.setUseCaches(false);
		                          conn.setChunkedStreamingMode(1024);
		                          conn.setConnectTimeout(300000);
		                          conn.setRequestMethod("POST");
		                          conn.setRequestProperty("Accept-Encoding", "");
		                          conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		                          conn.setRequestProperty("myFile", file_name);
		                          conn.setRequestProperty("imsi",Utils.getImsi(MviewApplication.ctx));
		                          conn.setRequestProperty("description", " ");
		                          conn.setRequestProperty("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
		                          conn.setRequestProperty("status", "Unpublish");
		                          conn.setRequestProperty("media_type", "image");
		                          conn.setRequestProperty("category_name", " ");
		                          conn.setRequestProperty("title", file_name);
		                          conn.setRequestProperty("id", upload_id);
		                          conn.setRequestProperty("amount", "0");
		                          conn.setRequestProperty("total_size", file_size);
		                          conn.setRequestProperty("count", count1);
		                          dos = new DataOutputStream(conn.getOutputStream());
		                 
		      //=================Adding imsi=================
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"imsi\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes("10000000dad1938e");
		                          dos.writeBytes(lineEnd);

		                          //=================Adding DESC=================
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes(" "); // desc is String variable
		                          dos.writeBytes(lineEnd);

		                          //=================Adding status=================
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"status\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes("Unpublish"); // msisdn is String variable
		                          dos.writeBytes(lineEnd);

		                          //=================Adding count=================
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"count\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes(count1); //count
		                          dos.writeBytes(lineEnd);


		                          //=================Adding Media_type=================
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"media_type\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes("image"); // msisdn is String variable
		                          dos.writeBytes(lineEnd);

		                          //=================Adding Total_File_Size=================
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"total_size\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes(file_size); // msisdn is String variable
		                          dos.writeBytes(lineEnd);
		      //=================Adding categoryname=================
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"category_name\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes(" "); // msisdn is String variable
		                          dos.writeBytes(lineEnd);

		                          //=================Adding title=================
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"title\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes(file_name); // msisdn is String variable
		                          dos.writeBytes(lineEnd);


		                          //===============Adding video id========
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"id\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes(upload_id); // msisdn is String variable
		                          dos.writeBytes(lineEnd);

		                          //===============Adding amount=======
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"amount\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes(" "); // msisdn is String variable
		                          dos.writeBytes(lineEnd);
		      //============Adding msisdn==========
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"msisdn\"" + lineEnd);
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes("9999999999"); // msisdn is String variable
		                          dos.writeBytes(lineEnd);

		      //=============Adding paramter media file(video)============
		                          dos.writeBytes(twoHyphens + boundary + lineEnd);
		                          dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + file_name + "\"" + lineEnd);
		                          dos.writeBytes(lineEnd);

		                          System.out.println(" : via run file input stream here next is "+fileInputStream);
		                          bytesAvailable1 = fileInputStream.available();
		                          System.out.println(" : via run bytes available to write are  "+bytesAvailable1);
		                          buffer = new byte[bytesAvailable1];
		                          bufferSize = Math.min(bytesAvailable1, maxBufferSize);
		                          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		                          if (bytesRead > 0) {
		                              dos.write(buffer, 0, bufferSize);
		                          }
		                          dos.writeBytes(lineEnd);
		                          dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		                          int serverResponseCode;
		                          String serverResponsemsg;
		                          serverResponseCode = conn.getResponseCode();
		                       //   System.out.println(" : via run Server response code is "+serverResponseCode);
		                          serverResponsemsg = conn.getResponseMessage();

		                          if (serverResponseCode == 200) {


		                              StringBuilder sb = null;
		                              BufferedReader rd;
		                              InputStream is;
		                              sb = null;

		                              sb = new StringBuilder();
		                              try {

		                                  is = conn.getInputStream();
		                                  rd = new BufferedReader(new InputStreamReader(is));
		                                  while ((countstr1 = rd.readLine()) != null) {

		                                      sb.append(countstr1);
		                                  }
		                                  rd.close();

		                                  countstr = String.valueOf(sb);
		                            //      System.out.println(" : via run UploadScreenshots respponse is "+countstr);
		                                  try {
		                                      JSONObject jsonObject = new JSONObject(countstr);
		                                      astring = jsonObject.getString("count");

		                                  } catch (Exception e) {
		                                      //logger.error("Exception",e);
//		                                      Config.callEventUF(id, this, "Exception catched after reponse code 200","ugc");
//
//		                                      UPLOAD_STATUS = false;
//		                                      uploadsuccessfull = "notuploaded";
//		                                      return uploadsuccessfull;
												/*
												 * Object[] params= {file_name}; helper.Utility.callRespectiveMethod(
												 * "helper.Utility","removeFile",params);
												 */
		                                     // helper.Utility.removeFile(file_name);
		                                      updatestatusofmanualscanfile(url_id, "uploaded");
		                                      uploadinit=true;
		                                  //    System.out.println(" : via run Value of upload init from 7 changed to "+uploadinit);
		                                      return;
		                                      
		                                      
		                                  }
		                                  if (astring.equals("404 page not found")) {
//		                                      Config.callEventUF(id, this, "Page bot found 4040 after reponse code 200","ugc");
//
//
//		                                      UPLOAD_STATUS = false;
//
//		                                      uploadsuccessfull = "notuploaded";
//		                                      return uploadsuccessfull;

		                                	  
												/*
												 * uploadinit=true;
												 * System.out.println("Value of upload init from 8 changed to "
												 * +uploadinit);
												 */		                                	  return;

		                                  } else if (astring != null && astring == count1 && !astring.equals("404 page not found")) {
		                                      if (astring.equals("end")) {
		                                         
//		                                          adp.updateuploaddata1(category_id, "uploaded", id, "end");
//		                                          adp.updatecountandstatus("uploaded", categoryid, id, "end");
//		                                         
//		                                          UPLOAD_STATUS = false;
//		                                          uploadsuccessfull = "uploaded";
//
//
//		                                          return uploadsuccessfull;
													/*
													 * System.out.println("Value of upload init from 9 changed to "
													 * +uploadinit); uploadinit=true;
													 */
		                                    	  return;

		                                      } else {

//		                                          UPLOAD_STATUS = false;
//		                                          uploadsuccessfull = "notuploaded";
//
//		                                          return uploadsuccessfull;

		                                      }

		                                  } else if (astring != null && !astring.equals("") && !astring.equals("end")) {
		                                     
//		                                      adp.updateuploaddata1(categoryid, "started", id, astring);
		                                     
		                                      try {
		                                          co = Integer.valueOf(astring);
		                                      } catch (NumberFormatException e) {

		                                        //  logger.error("Exception",e);
		                                          
		                                              
													/*
													 * uploadinit=true;
													 * System.out.println("Value of upload init from 10 changed to "
													 * +uploadinit);
													 */
		                                          return;
//		                                          UPLOAD_STATUS = false;
//		                                          uploadsuccessfull = "notuploaded";
//		                                          return uploadsuccessfull;

		                                      }

		                                  } else if (astring.equals("end")) {

//		                                      adp.updateuploaddata1(categoryid, "uploaded", id, astring);
//		                                      UPLOAD_STATUS = false;
//		                                      uploadsuccessfull = "uploaded";
//
//		                                      return uploadsuccessfull;
		                                	  Object[] params= {SC_PATH+file_name};
		                                	  Constants.removeFile(file_name);
		                                	  updatestatusofmanualscanfile(url_id, "uploaded");
												/*
												 * uploadinit=true;
												 * System.out.println("Value of upload init from 11 changed to "
												 * +uploadinit);
												 */
		                                	  return;
		                                	  
		                                	  
		                                  }
		                              } catch (IOException ioex) {

//
//		                                  UPLOAD_STATUS = false;
//		                                  uploadsuccessfull = "notuploaded";
//
//		                                  return uploadsuccessfull;

											/*
											 * uploadinit=true;
											 * System.out.println("Value of upload init from 12 changed to "+uploadinit)
											 * ;
											 */
		                            	  return;
		                            	  
		                              }

		                          } else {

//		                              Config.callEventUF(id, this, "Server response code is not 200","ugc");
//
//		                              uploadsuccessfull = "notuploaded";
//		                              UPLOAD_STATUS = false;
//
//		                              return uploadsuccessfull;
										/*
										 * uploadinit=true;
										 * System.out.println("Value of upload init from 13 changed to "+uploadinit);
										 */
		                        	  return;


		                          }
		                      } catch (SocketTimeoutException e) {
//		                          Config.callEventUF(id, this, "Socket timeoutexception while sending chunks","ugc");

//		                          UPLOAD_STATUS = false;


		                         // logger.error("Exception",e);
		                          System.out.println(Config.getDateTime()+"  : via run Exception occured in upload class in 7... "+e.getMessage());
									/*
									 * uploadinit=true;
									 * System.out.println("Value of upload init from 14 changed to "+uploadinit);
									 */
	                        	  return;
//		                          uploadsuccessfull = "notuploaded";
//
//
//		                          return uploadsuccessfull;

		                      } catch (FileNotFoundException e) {
		                    	//  System.out.println(helper.Config.getDateTime()+" : via run  Exception occured in upload class in 8... "+e.getMessage());
//		                          Config.callEventUF(id, this, "File not found  while sending chunks","ugc");
//
//
//		                          UPLOAD_STATUS = false;


		                         // logger.error("Exception",e);
									/*
									 * uploadinit=true;
									 * System.out.println("Value of upload init from 15 changed to "+uploadinit);
									 */
	                        	  return;
//		                          uploadsuccessfull = "notuploaded";
//
//		                          return uploadsuccessfull;

		                      } catch (MalformedURLException e) {
		                    	  //System.out.println(helper.Config.getDateTime()+"  : via run Exception occured in upload class in 9... "+e.getMessage());
//		                          Config.callEventUF(id, this, "MalformedURLException while sending chunks","ugc");
//
//		                          UPLOAD_STATUS = false;


		                         // logger.error("Exception",e);
									/*
									 * uploadinit=true;
									 * System.out.println("Value of upload init from 16 changed to "+uploadinit);
									 */
	                        	  return;
//		                          uploadsuccessfull = "notuploaded";
//
//		                          return uploadsuccessfull;

		                      } catch (ProtocolException e) {
		                    	//  System.out.println(helper.Config.getDateTime()+" : via run Exception occured in upload class in 10... "+e.getMessage());
//		                          Config.callEventUF(id, this, "Protocol not found  while sending chunks","ugc");

//		                          UPLOAD_STATUS = false;


		                         // logger.error("Exception",e);
									/*
									 * uploadinit=true;
									 * System.out.println("Value of upload init from 17 changed to "+uploadinit);
									 */
	                        	  return;
//		                          uploadsuccessfull = "notuploaded";

//		                          return uploadsuccessfull;

		                      } catch (IOException e) {
		                    	 // System.out.println(helper.Config.getDateTime()+" : via run  Exception occured in upload class in 11... "+e.getMessage());
//		                          Config.callEventUF(id, this, "IO found  while sending chunks","ugc");


//		                          UPLOAD_STATUS = false;

// uploadsuccessfull;

		                      } catch (Exception e) {
		                    	//  System.out.println(helper.Config.getDateTime()+" : via run  Exception occured in upload class in 12... "+e.getMessage());
//		                          Config.callEventUF(id, this, "Some other exception   found  while sending chunks and exception is "+e.toString(),"ugc");
//
//		                          UPLOAD_STATUS = false;
		                         // logger.error("Exception",e);
		                        
									/*
									 * uploadinit=true;
									 * System.out.println("Value of upload init from 19 changed to "+uploadinit);
									 */
	                        	  return;

		                      }


		                  }

		              
		              
		              
		              
		              
		              
		              
		              
		              
		              
		              
		              
		              
		              
		              
		              
		              
		              }
		        }
		        else
		        {
		        	//logger.debug(" : via run Screenshot File doesn't exist for "+file_name );
		        
		        	 updatestatusofmanualscanfile(url_id,"error");
		        
		        }

		}
		
		
		
		
		
	}
	
}
