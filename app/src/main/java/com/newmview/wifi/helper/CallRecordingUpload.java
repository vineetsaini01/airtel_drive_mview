package com.newmview.wifi.helper;

import android.content.Context;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

public class CallRecordingUpload {
//    private static final String HOST="203.122.58.233";
private static final String HOST="198.12.250.223";
    private static final String USER_NAME="mview_ftp";
    private static final String PASSWORD="92zbVZ";
    private String source_path;
    private Session session;
    private byte[] buffer;
    private Channel channel;
    private ChannelSftp sftpChannel;
    private static final int ALPHA2_PORT=30032;
    private static final int ALPHA3_PORT=22;
    private static final String REMOTE_FILE_PATH="/home/mview_ftp/call_recording";
    private Context context;


    public CallRecordingUpload(String source_file_path, Context context)
    {
        this.source_path=source_file_path;
        this.context=context;
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(USER_NAME, HOST, ALPHA3_PORT);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(PASSWORD);
            session.connect();
            session.setTimeout(Constants.CONNECTION_TIMEOUT);
            channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            sftpChannel.put(source_path, REMOTE_FILE_PATH, new SfProgressMonitor());


        } catch (Exception e) {
            e.printStackTrace();
           // Utils.showToast(context,"Please restart your call test as some error has occured!!");

        } finally {
            if(channel!=null && channel.isConnected())
            {
                channel.disconnect();
            }
if(sftpChannel!=null && sftpChannel.isConnected()) {
    sftpChannel.exit();
}
            if(session!=null && session.isConnected()) {
                session.disconnect();
            }

        }




    }


    private class SfProgressMonitor implements SftpProgressMonitor {
        @Override
        public void init(int op, String src, String dest, long max) {

        }

        @Override
        public boolean count(long count) {
            System.out.println("call_record bytes count "+count);
            return true;
        }

        @Override
        public void end() {
            System.out.println("call_record ended...");

Utils.showToast(context,Constants.call_end_record_alert);
        }
    }
}
