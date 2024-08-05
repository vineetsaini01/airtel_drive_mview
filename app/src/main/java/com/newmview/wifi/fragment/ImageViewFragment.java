package com.newmview.wifi.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;


import com.mview.airtel.R;
import com.newmview.wifi.activity.BuildingTabActivity;
import com.newmview.wifi.activity.CompareMapsActivity;
import com.newmview.wifi.activity.HeatMapListActivity;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.customview.TouchImageView;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.Interfaces.Swipelisteners;
import com.newmview.wifi.helper.OnSwipeTouchListener;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.ImageViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.GONE;

public class ImageViewFragment extends Fragment implements View.OnClickListener,
        TouchImageView.OnTouchImageViewListener,  View.OnTouchListener{


    private static final String TAG ="ImageViewFragment" ;
    ImageView imgview;
    private TextView timestamp_TV;
    private String timestmap;
    private View view;
    private Bundle bundle;
    private ProgressBar progressBar;
    private TextView toolbarTitle;
    private String user_name;
    private Button addBtn;
    private ImageView edit_Album;
    private ImageView del_Note;
    private ImageView edit_Note;
    private String urlofphoto;
    private String id,title,event_id;
    private Uri uri;
    private String filepath;
    private boolean external;

    private String idgenerated;
    private String comments;
    private String orignalPath;
    private String downloadedPath;
    private String admin;
    RelativeLayout relativeLayout;
    LinearLayout shareLL;
    Swipelisteners swipelisteners;
    private String image_file;
    private String source;
    private String desc;
    private String content_type;
    private String position;
    private ArrayList<String> uriList=new ArrayList<>();
    TouchImageView imgDisplay;
    private TouchImageView photoView;
    private Uri resultUri;
    private Bitmap resultBmp;
    private String subString_Url;
    private Dialog mFullScreenDialog;
    private long mLastClickTime=0;
    private String showmenu;
    private String status;
    private String src;
    private String media;
    private View optionsLayout;
    private String like_flag;
  //  private MediaMenuOptionsView mediaMenuOptionsView;
    private String pshare;
    private String share_lock;
    private String sourceActivity;
    private String like_count;
    private String percentage;
    private String cms_sync;
    private HashMap<String,String> details;
    private Button shareBtn,backBtn;
    private String textToShare;
    private MapModel mapDetails;
    private ImageViewFragment imageFragment;
    private FragmentManager fragmentManager;
    private RelativeLayout mainRL;
    private Button compareBtn;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HeatMapListActivity) {
            swipelisteners = (Interfaces.Swipelisteners) getActivity();
        }

    }
    private ImageViewModel viewModel;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getActivity()).get(ImageViewModel.class);
        /*viewModel =new ViewModelProvider(this,
                new MainViewModelFactory(requireParentFragment()).get(WifiViewModel.class);*/

    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if ( newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


            openFullscreenDialog();


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            closeFullscreenDialog();
        }








    }

    private void closeFullscreenDialog() {
        if (photoView != null) {
            ((ViewGroup) photoView.getParent()).removeView(photoView);
            relativeLayout.addView(photoView);
            if (mFullScreenDialog!=null)
            {
                mFullScreenDialog.dismiss();
            }}


    }

    private void openFullscreenDialog() {
        mFullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ((ViewGroup) mainRL.getParent()).removeView(mainRL);
        mFullScreenDialog.addContentView(mainRL,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if(source.equalsIgnoreCase("" +
                "" +
                ""))
       // mFullScreenDialog.setContentView();

        mFullScreenDialog.show();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_image_view, container, false);

        init();
        if(getActivity()!=null) {
            requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // in here you can do logic when backPress is clicked
                    if (getActivity() != null) {
                        if (getActivity() instanceof BuildingTabActivity) {
                            compareBtn.setVisibility(GONE);
                        }
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
        }

        if (Utils.checkContext(getActivity())) {


                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);


              //  urlofphoto = bundle.getString("photoURI");

                if (Utils.checkifavailable(urlofphoto)&& (urlofphoto.contains("198.12.250.223/") ||
                        urlofphoto.contains("factabout.in"))) {

                    String remaining = urlofphoto.substring(urlofphoto.lastIndexOf("/") + 1);

                    /*if (Utils.isBase64(remaining))
                    {
                        urlofphoto=Utils.getdecodedurl(urlofphoto);
                    }*/
                   /* System.out.println("decoded url of photo is "+urlofphoto);
                    String[] paths = urlofphoto.split("/");
                    if(paths.length>0) {
                        subString_Url = paths[paths.length - 1];
                    }*/
                }

                else {
                    progressBar.setVisibility(GONE);
                    System.out.println("in else of photo");
                }
            if(Utils.checkifavailable(source))
            {
                if(source.equalsIgnoreCase("FloorPlan"))
                {
                    shareBtn.setVisibility(GONE);
                            view.findViewById(R.id.legendsLayout).setVisibility(GONE);
                    }
                else if(source.equalsIgnoreCase("HeatMap"))
                {
                    view.findViewById(R.id.legendsLayout).setVisibility(GONE);
                }
                else if(source.equalsIgnoreCase("WalkMapMini")||source.equalsIgnoreCase("HeatMapMini")||source.equalsIgnoreCase("ls_heatmap_mini"))
                {
                    view.findViewById(R.id.legendsLayout).setVisibility(GONE);
                    shareBtn.setVisibility(GONE);
                    backBtn.setVisibility(GONE);

                }


            }
                photoView.setZoomEnabled(true);
            Bitmap bmp=Utils.getBitmap(urlofphoto);
                if(bmp!=null)
                photoView.setImageBitmap(bmp);
             //   Utils.loadImage(getActivity(),photoView,urlofphoto,progressBar,R.drawable.picture, false);

               // getActivity().getWindow().setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.image_transition));
            }











        /*mediaMenuOptionsView=new MediaMenuOptionsView
                (getActivity(),view,like_flag,id, pshare,admin, " ",share_lock,details);
        return view;*/
        return view;
    }

    private void getExtras() {

         bundle = getArguments();
        if(bundle!=null) {
            urlofphoto = bundle.getString("path");
            source=bundle.getString("source");
            textToShare=bundle.getString("textToShare");
            mapDetails=(MapModel)bundle.getSerializable("mapDetails");
            Log.i(TAG,"Url obtained "+urlofphoto);
        }
        System.out.println("bundle in iv fragment "+bundle);

    }

    private void loadImage(String urlofphoto) {


    }

    private void openImageLayout() {
       /* Intent intent=new Intent(getActivity(), ImageDetailActivity.class);
        intent.putExtra("time",timestmap);
        intent.putExtra("photoURI", urlofphoto);
        intent.putExtra("username",user_name);
        intent.putExtra("title",title);
        intent.putExtra("event_id",event_id);
        intent.putExtra("admin",admin);
        intent.putExtra("id",id);
        intent.putExtra("source",source);
        intent.putExtra("image_file",image_file);
        intent.putExtra("desc",desc);
        intent.putExtra("content_type",content_type);
        intent.putExtra("position",position);
        startActivity(intent);
        System.out.println("value while sending from fragment "+admin);*/

    }
    private void init() {
       fragmentManager=getActivity().getSupportFragmentManager();
       mainRL=view.findViewById(R.id.main_relative);
        photoView=view.findViewById(R.id.photoView);

        photoView.setZoomEnabled(true);
        timestamp_TV = view.findViewById(R.id.timestamp_TV);
        relativeLayout=view.findViewById(R.id.main_relative);
        shareLL=view.findViewById(R.id.shareLL);
        progressBar=view.findViewById(R.id.thumbnailprogress);
        shareBtn=view.findViewById(R.id.shareBtn);
        backBtn=view.findViewById(R.id.backBtn);
        if(getActivity() instanceof BuildingTabActivity) {
            compareBtn = getActivity().findViewById(R.id.compareBtn);
            if(Utils.checkifavailable(source)) {
                if(!source.equalsIgnoreCase("FloorPlan")) {
                    compareBtn.setVisibility(View.VISIBLE);
                    compareBtn.setOnClickListener(this);
                }
            }
        }

        //optionsLayout=view.findViewById(R.id.optionsLayout);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.sendImage(true, getActivity(),shareLL,
                        urlofphoto,false,null, new Interfaces.SaveSuccessfullListener() {
                            @Override
                            public void saveSuccessfull(String name, String path) {

                            }
                        }, null, textToShare);

            }
        });






        photoView.setOnTouchListener(new OnSwipeTouchListener(getActivity(),media,photoView) {

            public void onSwipeRight() {
                if (photoView.getCurrentZoom() == 1) {
                    swiperightmethod();
                }

            }

            public void onSwipeLeft() {
                if (photoView.getCurrentZoom() == 1) {
                    swipeleftmethod();
                }
            }
            public void onTapMethod() {
// From another Fragment or Activity where you wish to show this
// PurchaseConfirmationDialogFragment.
                ImageFragmentDialog imageFragmentDialog=new ImageFragmentDialog();
                imageFragmentDialog.setArguments(bundle);
               imageFragmentDialog.show(
                        getChildFragmentManager(), ImageFragmentDialog.TAG);
              // viewModel.childFragmentClicked().setValue(source);
              //  ((CompareHeatMapFragment) getParentFragment()).openRespectiveImage(source,mapDetails);
              //  openCompleteView();
                //openFullscreenDialog();

            }



            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                if (photoView.getCurrentZoom() == 1) {
                    closeTheImageView();
                }
            }

            //swipe bottom added by Sonal on 23-03-2021 to close the view on swiping the image down.
            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                if (photoView.getCurrentZoom() == 1) {
                    closeTheImageView();
                }
            }
        });







        //  photoView.setOnTouchListener(new TouchImageView());

    }

    private void openCompleteView() {
        Log.i(TAG,"Mapdetails "+mapDetails);
        imageFragment=new ImageViewFragment().newInstance(getActivity(),mapDetails,source);
        fragmentManager.beginTransaction().
                replace(R.id.main_relative, imageFragment).addToBackStack(null).commit();

    }

    private void closeTheImageView() {


        if(getActivity()!=null)
        {
            Animation animSlideUp = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_down);
            System.out.println("get Activity instance of "+getActivity());

            //  ((AppCompatActivity)getActivity()).startAni
        }

    }

    private void swipeleftmethod() {
        if (swipelisteners!=null)
        {
            swipelisteners.swipeleft();
        }

    }

    private void swiperightmethod() {
        if (swipelisteners!=null)
        {
            swipelisteners.swiperight();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

    }
    private void setImageUsingUri(Uri resultUri) {
        try {
            if (resultUri != null) {
                photoView.setImageBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("exception is " + e.toString());
        }


    }

    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void saveImage(Uri uri){
        FileOutputStream fileOutputStream = null;
        try {
            Bitmap bmp=MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            fileOutputStream = new FileOutputStream(createFile());
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createFile() {

        File directory;
        if(external){
            directory = getAlbumStorageDir("Edit");
        }
        else {
            directory = getActivity().getDir("Edit", Context.MODE_PRIVATE);
        }
        if(!directory.exists() && !directory.mkdirs()){
            Log.e("ImageSaver","Error creating directory " + directory);
        }
        File SDCardRoot=Config.downloaded_root.getAbsoluteFile();
        // File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String filename=Utils.getRandomString()+".png";
        Log.i("DownloadLocal filename:",""+filename);
        return new File(SDCardRoot,filename);
    }

    private File getAlbumStorageDir(String albumName) {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
    private String saveFileToPhone(Intent data) {

        try {
            if(Utils.checkContext(getActivity())) {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                ContextWrapper cw = new ContextWrapper(getActivity());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("Edit", Context.MODE_PRIVATE);
                // Create imageDir
                File mypath = new File(directory, "profile.jpg");

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return directory.getAbsolutePath();
            }

        }
        catch (FileNotFoundException f)
        {
            f.printStackTrace();
        }
        catch (IOException ie)
        {
            ie.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (v.getId())
        {
            case R.id.compareBtn:
                Bundle bundle=new Bundle();
                bundle.putSerializable("mapDetails",mapDetails);
                bundle.putString("source",source);
                Log.i(TAG,"Sending source "+source);
                if(Utils.checkifavailable(source))
                {
                    if(source.toLowerCase().contains("ls_heatmap".toLowerCase()))
                    {
                        mapDetails.setCompareImgPath(mapDetails.getLsHeatMapPath());
                    }
                    else
                    {
                        mapDetails.setCompareImgPath(mapDetails.getFinalMapPath());
                    }
                }
                else
                {
                    mapDetails.setCompareImgPath(mapDetails.getFinalMapPath());
                }
                Log.i(TAG,"Sending bundle "+bundle);
                Utils.takeToNextActivity(getActivity(), CompareMapsActivity.class,bundle);
                break;
        }
    }


    public void upload() {
        if(Utils.checkifavailable(src)) {
            if (src.equalsIgnoreCase("Album_comments"))
            {
                if(Utils.checkifavailable(filepath)) {
               //     uploadInAlbumComments(filepath, comments);
                }
            }
            else
            {
               // uploadForRest();
            }
        }
        else

        {
        //    uploadForRest();
        }
        System.out.println("in upload method");


        // getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

    }






    private String traverse(File dir, String urlofphoto) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if(files.length>0)
            {
                for (int i = 0; i < files.length; i++) {
                    String substring=null;
                    String name = String.valueOf(files[i]);
                    if(Utils.checkifavailable(name)) {
                        String[] paths = name.split("/");
                        if (paths.length > 0) {
                            substring = paths[paths.length - 1];
                        }
                        System.out.println("url name is " + name);
                        if (Utils.checkifavailable(substring) &&Utils.checkifavailable(subString_Url)) {
                            if (subString_Url.equalsIgnoreCase(substring))
                                return name;
                        }
                    }
                }

            }
        }
        return null;
    }
    public void delete() {
        try {

            if (Utils.checkContext(getActivity())) {


                File fdelete = new File(uri.getPath());
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + uri.getPath());
                    } else {
                        System.out.println("file not Deleted :" + uri.getPath());
                    }
                }
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        }
        catch (Exception e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMove() {
        swiperightmethod();
    }

    @Override
    public void onSwipeLeft() {
        swipeleftmethod();
    }

    @Override
    public void onSwipeRight() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }

    public ImageViewFragment newInstance(Context context, MapModel mapDetails, String source) {
        //  Bundle bundle1=new Bundle();
        Bundle bundle = new Bundle();
     //   Log.i(TAG,"Map Details "+mapDetails.getMapPath() +" mapId "+mapDetails.getMapId());
        if (Utils.checkifavailable(source)) {
            String textToShare=
                    "SSID : "+mapDetails.getSsidName() +", \n"+"Time Stamp : "+Utils.getDateTime()+", \n"+"Survey Id : "+mapDetails.getMapId()
                            +", \n"+"User Id : "+Utils.getMyContactNum(MviewApplication.ctx);

            if (source.equalsIgnoreCase("FloorPlan")) {
                bundle.putString("path", mapDetails.getFloorPlanPath());
                bundle.putString("source",source);

            }

            else if(source.equalsIgnoreCase("WalkMap"))
            {


                bundle.putString("path",mapDetails.getMapPath());
                bundle.putString("source",source);
                bundle.putString("textToShare",textToShare);
            }
            else if(source.equalsIgnoreCase("WalkMapMini"))
            {

                bundle.putString("path",mapDetails.getMapPath());
                bundle.putString("source",source);
            }

            else if(source.equalsIgnoreCase("HeatMapMini"))
            {
                bundle.putString("path",mapDetails.getFinalMapPath());
                bundle.putString("source",source);
            }
            else if(source.equalsIgnoreCase("ls_heatmap_mini"))
            {
                bundle.putString("path",mapDetails.getLsHeatMapPath());
                bundle.putString("source",source);
            }
            else {
                bundle.putString("path", mapDetails.getFinalMapPath());
                bundle.putString("source",source);
            }
            bundle.putString("wifiCoordsX",mapDetails.getWifiX());
            bundle.putString("wifiCoordsY",mapDetails.getWifiY());
            bundle.putSerializable("mapDetails",mapDetails);
        }

        Log.i(TAG,"Setting path "+mapDetails.getMapPath());
        ImageViewFragment imageViewFragment=new ImageViewFragment();
        imageViewFragment.setArguments(bundle);
        return imageViewFragment;

    }





  /*  @Override
    public void onViewTap(View view, float x, float y) {





    }
*/





}

