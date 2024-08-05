package com.newmview.wifi.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.newmview.wifi.application.MviewApplication;
import com.aykuttasil.callrecord.CallRecord;
import com.aykuttasil.callrecord.service.CallRecordService;
import com.newmview.wifi.other.Utils;

import java.io.File;

public class Call_State_Helper {


    public static int call_state;
    public static String call_source;
    private static CallRecord callRecord;
    private static String call_recording_path;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".amr";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";

    private MediaRecorder recorder = null;
    private static int currentFormat = 0;
    private static int[] output_formats = { MediaRecorder.OutputFormat.AMR_NB };
    private static String[] file_exts = {AUDIO_RECORDER_FILE_EXT_MP4 };

    @SuppressLint("WrongConstant")
    public static void startCallService(Context context) {
        try {
            System.out.println(" entering call record option");
//            callRecord = new CallRecord.Builder(context)
//
//                    .setRecordFileName("Record_" + new SimpleDateFormat("ddMMyyyyHHmmss", Locale.US).format(new Date()))
//                    .setRecordDirName("Call Recordings")
//                    .setRecordDirPath(Environment.getExternalStorageDirectory().getPath() + "/" + "Download" + "/" + "mview")
//                    .setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//                    .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//                    .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
//                    .setShowSeed(true)
//                    .build();
//            callRecord.startCallRecordService();
         //   System.out.println("get file path " + callRecord.getRecordDirPath());
        //    Utils.deletefileFromFileManager(callRecord.getRecordDirPath()  + "/" + "CallRecord");
            //AudioManager audioManager;
            MediaRecorder recorder = new MediaRecorder();

            //audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            ///audioManager.setMode(AudioManager.MODE_IN_CALL);
           // audioManager.setSpeakerphoneOn(true);
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
           // recorder.setAudioSamplingRate(8000);
            recorder.setOutputFile(getFilename());
            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);
            recorder.prepare();
            recorder.start();
            System.out.println("get file path " + recorder);



        } catch (Exception e) {
            System.out.println(" Exception in call record is"+e.getMessage());
            e.printStackTrace();
        }


    }

    private static MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(MviewApplication.ctx,
                    "Error:  in listener is" + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private static MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(MviewApplication.ctx,
                    "Warning: in listener is " + what + ", " + extra, Toast.LENGTH_SHORT)
                    .show();
        }
    };


    public static String getFilename() {

        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
    }


    public static void stopCallService(Context context) {
        try {
            if (callRecord != null) {
                callRecord.stopCallReceiver();
                Intent intent = new Intent(context, CallRecordService.class);
                context.stopService(intent);
                String path = callRecord.getRecordDirPath() + "/" + callRecord.getRecordDirName();
                File folder = new File(path);
                File[] files = folder.listFiles();
                Log.d("Files", "Size: " + files.length);
                call_recording_path = path + "/" + files[files.length - 1].getName();


                if (Utils.checkifavailable(call_recording_path)) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("call_record path " + call_recording_path);
                            new CallRecordingUpload(call_recording_path,context);
                        }
                    });

                } else {
                    Log.i("Call_Record", "path not available!!");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
