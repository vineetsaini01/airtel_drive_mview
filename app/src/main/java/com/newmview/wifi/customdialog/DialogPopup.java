package com.newmview.wifi.customdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.services.MyJobService;

public class DialogPopup {

    private String title;

    public DialogPopup(String title) {
        this.title = title;
    }

    public void showClearLogDialog(Context context) {
        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
        db_handler.open();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage("Agent count with init status is: "+db_handler.countInitStatusRows()+" \nPress OK to mark all completed except last 10 rows in Table.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                db_handler.updateStatusOfAgentsEvents("completed");
                db_handler.close();
                Toast.makeText(context,"Data updated Successfuly in Table",Toast.LENGTH_SHORT).show();
               ;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void sendEvtServerManually(Context context) {
        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
        db_handler.open();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage("Sending EVT to Server Manually.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                db_handler.send_evt_to_server();
                db_handler.close();
                Toast.makeText(context,"Data sent to Server",Toast.LENGTH_SHORT).show();
                ;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void startTestManually(Context context) {
        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
        db_handler.open();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage("Start Test Manually.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                MyJobService.scheduleOneTimeTest();
                Toast.makeText(context,"All Tests started Manually",Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
