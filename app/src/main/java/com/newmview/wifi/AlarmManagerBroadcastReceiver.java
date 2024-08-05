package com.newmview.wifi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;


import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.other.AgentDetailsModel;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import dalvik.system.DexClassLoader;


public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    public  Context context;
    public String scheduledStatus, fileName;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String TAG = "AlarmManagerBroadcastReceiver";
    private String file_checksum, filename, filesize, file_path, tcp_ip_wifi, tcp_port;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context ctx, Intent intent) {
        context = ctx;
        startRoundRobinScheduler();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startRoundRobinScheduler() {
       // Utils.appendLog("start round robin ");
        selectTasksOnTheBasisOfValidations();
    }



    private Date getCurrentDate() {
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean checkIfWeCouldRunTheAgent(AgentDetailsModel agentDetails) {
        // TODO Auto-generated method stub
        System.out.println("Agent data  is " + agentDetails.getLastScheduledTime());
       // Utils.appendLog("Agent data  is "+ agentDetails.getLastScheduledTime());
        long differenceInSeconds = getDifferenceBetweenTimeStamps(((agentDetails.getLastScheduledTime() + "")));
       // Utils.appendLog(Config.getDateTime() + " : via checkIfWeCouldRunTheAgent " + "Difference in time stamps is " + differenceInSeconds);
        System.out.println(Config.getDateTime() + " : via checkIfWeCouldRunTheAgent " + "Difference in time stamps is " + differenceInSeconds);
        String period = agentDetails.getPeriod();
        System.out.println(Config.getDateTime() + " : via checkIfWeCouldRunTheAgent " + "period  is " + period);
       // Utils.appendLog(Config.getDateTime() + " : via checkIfWeCouldRunTheAgent " + "period is" + period );
        long periodValue = -1;
        if (!Utils.checkifnull(period)) {
            if (!period.equals("-1")) {
                try {
                    periodValue = Long.parseLong(period);
                    //Utils.appendLog(" period value is "+periodValue);

                } catch (Exception e) {
                    e.printStackTrace();


                }
            } else {
                System.out.println(Config.getDateTime() + " " + ": via checkIfWeCouldRunTheAgent period value is -1");
               // Utils.appendLog(Config.getDateTime() + " via checkIfWeCouldRunTheAgent period value is -1 ");
            }
        } else {
            periodValue = Config.agentDefaultPeriod;
           // Utils.appendLog(" period value is "+periodValue);
        }
        if (differenceInSeconds >= periodValue) {
            System.out.println(Config.getDateTime() + " " + ": via checkIfWeCouldRunTheAgent yes we could run agent " + agentDetails.getFileName());
           // Utils.appendLog(Config.getDateTime() + " " + ": via checkIfWeCouldRunTheAgent yes we could run agent " + agentDetails.getFileName());
            return true;
        }
        return false;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long getDifferenceBetweenTimeStamps(String lastDateTime) {
        long diffMinutes = -1;
        long diffHours = -1;
        long diffSeconds = 0;
        try {
            String currentTime = Config.getDateTime();
           // Utils.appendLog("current time "+currentTime);
            // Log.i(TAG,"Last timestamp is "+lastDateTime +" current time "+currentTime);
            Date lastTs = getActualTimeStampFromString(lastDateTime);
            //Utils.appendLog("last date  time "+lastTs);
            //System.out.println(" get last time stamp" + lastTs);
            Date currentTs = getActualTimeStampFromString(currentTime);
           // Utils.appendLog("current  date  time "+currentTs);
           // System.out.println(" get current time stamp" + currentTs);
            if (lastTs != null && currentTs != null) {
                long milliseconds1 = lastTs.getTime();
                long milliseconds2 = currentTs.getTime();
                long diff = milliseconds2 - milliseconds1;
                diffSeconds = diff / 1000;
                diffMinutes = diff / (60 * 1000);
                diffHours = diff / (60 * 60 * 1000);
                long diffDays = diff / (24 * 60 * 60 * 1000);

		 /*  System.out.println(" : via getDifferenceBetweenTimeStamps "
		   		+ "returning difference in hours  as "+diffHours +"
		   		in minutes "+diffMinutes +" diff in seconds "+diffSeconds);*/

            }
        } catch (Exception e) {
           // Utils.appendLog("exception is "+e.getMessage());
            e.printStackTrace();
            diffSeconds = -1;
        }
        return diffSeconds;
    }

    public static Date getActualTimeStampFromString(String date) {
        Timestamp timestamp = null;
        Date date1 = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date1 = (Date) formatter.parse(date);
        } catch (Exception e) {
            //Utils.appendLog("exception is "+e.getMessage());//this generic but you can control another types of exception
            e.printStackTrace();
        }
        return date1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean runAgent(AgentDetailsModel agent) {
        boolean exceptionOccured = false;
        // TODO Auto-generated method stub
        String agentName = agent.getFileName() + "";
        //String file_path=agentName;
        // by swapnil bansal 4/6/2022
        String className ="";
                //String className = "controller.InternalTasksInitiater";
        String agentVersion = agent.getAgent_version() + "";
        String frequency = agent.getFrequnecy() + "";
        String methodName = agent.getM_name() + "";
        String data = agent.getData();
	/*	if(data!=null)
		{
		String dataArrayString[] = data.split(",");
		if(dataArrayString!=null)
		{
			if(dataArrayString.length>5)
			{

				network_type=dataArrayString[5];

			}
			if(dataArrayString.length>6)
			{
				urlsCount=dataArrayString[6];
			}
		}
		}
		*/


        System.out.print("file path is " + agentName);
       // Utils.appendLog("file path is " + agentName);

        try {
            System.out.print("entering try catch block");

            // if (className != null) {
//                String command = "unzip -p "+Environment.getExternalStorageDirectory() + "/"+agentName + " META-INF/MANIFEST.MF";

            String command = "chmod unzip -p" + Environment.getExternalStorageDirectory() + "/libs/" + agentName.trim() + " META-INF/MANIFEST.MF";
            System.out.print("entering try catch block" + command);
            //Utils.appendLog("entering try catch block" + command);
           // String sudoScript = " ";
            Process p = Runtime.getRuntime().exec(command);
          //  Utils.appendLog("p is" +p);
            BufferedReader stdInput1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
           // Utils.appendLog("stdInput1 is " +stdInput1);
            //InternalTasksInitiater a = new InternalTasksInitiater();
          // a.pingreport("", "", "", "");
//            while ((sudoScript = stdInput1.readLine()) != null) {
//                System.out.println("Line is " + sudoScript);
//                System.out.println("****while getting class name ***" + sudoScript);
//
//                if (sudoScript.contains("Main-Class:")) {
//
//                    className = sudoScript.substring(sudoScript.indexOf(":") + 1);
//                    className = className.trim();
//                }
//            }
            System.out.println(Config.getDateTime() + " : via runAgent" + " class name from mainfest file is  " + className + " with method is " + methodName);
           // Utils.appendLog(Config.getDateTime() + " : via runAgent" + " class name from mainfest file is  " + className + " with method is " + methodName);
            if (!Utils.checkifnull(agentName)) {
                Object[] timeParam = {Config.getDateTime(), agentName};
                //allRespectiveMethod("database.DatabaseClass","updateScheduledTime",timeParam);
                DB_handler db_handler = new DB_handler(context);
                db_handler.open();
                db_handler.updateScheduledTime(Config.getDateTime(), agentName);
                db_handler.close();
                try {
                    final String libPath = Environment.getExternalStorageDirectory() + "/" + agentName;
                    System.out.println(" lib path is "+libPath);
                    final File tmpDir = context.getDir("dex", 0);
                    System.out.println(" tmpDir is "+tmpDir);
                    String subClassName=agentName;
                    subClassName=subClassName.replace(".aar","");
                    System.out.println(" subClassName is "+subClassName);
                    className="com."+subClassName.toLowerCase()+".controller."+subClassName+"Class";
                   // Utils.appendLog("the method name is" + methodName + " and class name  is " + className);
                    System.out.println("the method name is" + methodName + " and class name  is " + className);
                    final DexClassLoader classloader = new DexClassLoader(libPath, tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
                      final Class<Object> classToLoad = (Class<Object>) classloader.loadClass(className);
                   // final Class<Object> classToLoad = (Class<Object>) classloader.loadClass(String.valueOf(getClass().getClassLoader().getResourceAsStream("META-INF/Main-Class: ")));
                    final Object myInstance = classToLoad.newInstance();
                    Object[] obj = {agentVersion, agentName, frequency ,data,context};
                    Class<?> params[] = new Class[obj.length];
                    for (int i = 0; i < obj.length; i++) {
                        if (obj[i] instanceof Integer) {
                            params[i] = Integer.TYPE;
                        } else if (obj[i] instanceof String) {
                            params[i] = String.class;
                        }
                        else if (obj[i] instanceof Context)
                        {
                            params[i] = Context.class;

                        }

                    }
                    final Method doSomething = classToLoad.getMethod(methodName,params);
                   // Utils.appendLog("do something" + doSomething);
                    System.out.println("do something" + doSomething);
                    doSomething.setAccessible(true);
                    doSomething.invoke(myInstance,obj);

                }
                catch (Exception e) {
                    e.printStackTrace();
                   // Utils.appendLog("Exception while calling method"+e.toString() +" for file agent name is  "+agentName);
                    System.out.print("Exception while calling method"+e.toString() +" for file agent name is  "+agentName);
                }


            }
//            }
        }
        catch (Exception e) {
            System.out.print("exception in run agent method" +e.getMessage());
            //Utils.appendLog("exception in run agent method  " +e.getMessage());
           // Utils.appendLog("exception in run agent method  " +e.getStackTrace());
            e.printStackTrace();
            exceptionOccured = true;
            if (agentName != null) {

                String eth0Status = Config.getCurrentStatusOfInterface("eth0");
              //  Utils.appendLog("eth0Status is" + eth0Status);
                if (!Utils.checkifnull(eth0Status)) {
                    if (eth0Status.equalsIgnoreCase("down")) {
                    }
                }

            }
        }
        finally {

        }

        return exceptionOccured;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void selectTasksOnTheBasisOfValidations() {
        System.out.println(Config.getDateTime() + ": Started Round robin process....");
         // Utils.appendLog(Config.getDateTime() + ": Started Round robin process....");
        ArrayList<AgentDetailsModel> agentsDetailsList = null;
        Date currentDate = null;
        Date startingDate = null;
        Date endDate = null;
        String ssdt = null;
        String sedt = null;

        try {
            DB_handler databaseClass = new DB_handler(context);
            databaseClass.open();
            agentsDetailsList = databaseClass.getAgentsWithCompletedAndReadyStatusIs();
            databaseClass.close();
            if (agentsDetailsList != null) {
                System.out.println("Size of List fetched in RRS is " + agentsDetailsList.size());
               // Utils.appendLog("Size of List fetched in RRS is " + agentsDetailsList.size());
                if (agentsDetailsList.size() > 0) {
                    for (int taskIndex = 0; taskIndex < agentsDetailsList.size(); taskIndex++) {
                        AgentDetailsModel agent = agentsDetailsList.get(taskIndex);
                        String data = agent.getData();
                        System.out.println("The data is" + data);
                       // Utils.appendLog("The data is" + data);
                       // DatabaseClass db_handler = new DatabaseClass(context);
                      // db_handler.open();
                       // db_handler.insertInLoggingAgentTable("PingWorking.jar","PingReport",data,"","init");
                        System.out.println(Config.getDateTime() + " : via selectTasksOnTheBasisOfValidations Checking if "
                                + "we can run the agent called as "
                                + agent.getFileName() + " with priority " + agent.getPriority()
                                + " and data as " + data + " period as " + agent.getPeriod());
                        String agentName = agent.getFileName();
                        System.out.println("markScheduledStatusForAgent is : " + agentName);
                       // Utils.appendLog("markScheduledStatusForAgent is : " + agentName);
                        markScheduledStatusForAgent("inprocess", agentName);
                        System.out.println("markScheduledStatusForAgent is");
                        currentDate = getCurrentDate();
                        ssdt = agent.getSsdt();
                        sedt = agent.getSedt();
                        //Utils.appendLog(" THE current  date is " + currentDate + "and ssdt is " + ssdt + " and sedt is  " + sedt);
                        System.out.println(" THE current  date is " + currentDate + "and ssdt is " + ssdt + " and sedt is  " + sedt);
                        if (!Utils.checkifnull(ssdt)) {
                            try {
                                startingDate = dateFormat.parse(ssdt);
                                System.out.println("starting date " + ssdt);
                               // Utils.appendLog("starting date " + ssdt);
                            } catch (Exception e) {
                               // Utils.appendLog("exception is " +e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        if (!Utils.checkifnull(sedt)) {
                            try {
                                endDate = dateFormat.parse(sedt);
                                //Utils.appendLog("ending date " + endDate);
                            } catch (ParseException e) {
                            }
                        }
                        if (startingDate != null && endDate != null
                                && currentDate.after(startingDate)
                                && currentDate.before(endDate)) {
                            System.out.println("before can run agent  ");
                          //  Utils.appendLog("before can run agent  ");
                            boolean canRun = checkIfWeCouldRunTheAgent(agent);
                            if (canRun) {
                                //If exception Occurs move onto next agent else break
                                boolean exceptionOccured = runAgent(agent);
                                //InternalTasksInitiater task= new InternalTasksInitiater();
                               //  task.printResult();
                                markScheduledStatusForAgent("completed", agentName);
                                if (!exceptionOccured)
                                    break;
                            } else {
                                markScheduledStatusForAgent("completed", agentName);
                                System.out.println(Config.getDateTime() + " "
                                        + ": via selectTasksOnTheBasisOfValidations no we"
                                        + " couldn't run agent " + agent.getFileName());
                              //  Utils.appendLog(Config.getDateTime() + " " + ": via selectTasksOnTheBasisOfValidations no we" + " couldn't run agent " + agent.getFileName());
                            }
                        } else {
                            markScheduledStatusForAgent("completed", agentName);
                            System.out.println(Config.getDateTime() + " : via selectTasksOnTheBasisOfValidations Agent named " + agent.getFileName()
                                    + "'s " + " running time is over now...");
                           // Utils.appendLog(Config.getDateTime() + " : via selectTasksOnTheBasisOfValidations Agent named " + agent.getFileName() + "'s " + " running time is over now...");
                        }
                    }

				/*	int count = (int) callRespectiveMethod("database.Db_connector","getCountOfAgentsWithReadyStatus",null);
					if(count==0)
					{
						Object[] objParams= {"ready",""};
						callRespectiveMethod("database.Db_connector","updateScheduledStatus",objParams);
					}*/

                } else {
                    markScheduledStatusForAgent("ready", "");

                }
            } else {
                markScheduledStatusForAgent("ready", "");

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private void markScheduledStatusForAgent(String status, String agentName) throws NoSuchMethodException {
        // TODO Auto-generated method stub
        Object[] completedParams = {status, agentName};
        DB_handler db_handler = new DB_handler(context);
        db_handler.open();
        db_handler.updateScheduledStatus(status, agentName);
        db_handler.close();


    }



}
