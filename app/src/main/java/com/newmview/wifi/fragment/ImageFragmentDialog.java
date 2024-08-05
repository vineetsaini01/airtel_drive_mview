package com.newmview.wifi.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mview.airtel.R;
import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.bean.TestResults;
import com.newmview.wifi.customdialog.MyAlertDialog;
import com.mview.airtel.databinding.FragmentImageViewBinding;
import com.newmview.wifi.enumtypes.AlertType;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.DialogManager;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.DialogViewModel;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.TestResultsVM;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class ImageFragmentDialog extends DialogFragment implements Interfaces.MarkerTouchListener, MyAlertDialog.AlertDialogInterface {
    private Button shareBtn,backBtn;
    private String textToShare;
    private MapModel mapDetails;

    private String timestmap;
    private View view;
    private Bundle bundle;

    private String urlofphoto;
    RelativeLayout relativeLayout;

    Interfaces.Swipelisteners swipelisteners;
    private String image_file;
    private String source;
    private ArrayList<String> uriList=new ArrayList<>();

    public static final String TAG = "ImageFragmentDialog";
    private FragmentManager fragmentManager;
    private TextView darkGreenTv,lightGreenTv,redTv,yellowTv;
    private FragmentImageViewBinding fragmentImageViewBinding;
    private TestResultsVM viewModel;
    private List<TestResults> testResults=new ArrayList<>();
    private DialogViewModel alertDialogViewModel;
    private TestResultsVM activityVM;


    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment

         //view=inflater.inflate(R.layout.fragment_image_view, container, false);
        fragmentImageViewBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_image_view, container,
                false);
         getExtras();
         init();
         setImage();
        return fragmentImageViewBinding.getRoot();
    }

    private void setImage() {
        if (Utils.checkContext(getActivity())) {





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
                fragmentImageViewBinding.thumbnailprogress.setVisibility(GONE);
                System.out.println("in else of photo");
            }
            if(Utils.checkifavailable(source))
            {
           if(source.equalsIgnoreCase("WalkMapMini")||source.equalsIgnoreCase("HeatMapMini"))
                {
                  //  view.findViewById(R.id.legendsLL).setVisibility(GONE);
                    fragmentImageViewBinding.shareBtn.setVisibility(View.VISIBLE);
                    fragmentImageViewBinding.backBtn.setVisibility(View.VISIBLE);
                    fragmentImageViewBinding.legendsLayout.legendsLL.setVisibility(View.VISIBLE);

                }



            }
            if(Utils.checkifavailable(source))
            {
                if(!source.toLowerCase().contains("walkmap"))
                {
                    setViewModel();
                    initializeDialogVM();
                }
            }
            Log.i(TAG,"Url of photo "+urlofphoto);

            fragmentImageViewBinding.photoView.setZoomEnabled(true);
            if(Utils.checkifavailable(urlofphoto))
            fragmentImageViewBinding.photoView.setImageBitmap(Utils.getBitmap(urlofphoto));


            //   Utils.loadImage(getActivity(),fragmentImageViewBinding.photoView,urlofphoto,fragmentImageViewBinding.thumbnailprogress,R.drawable.picture, false);

            // getActivity().getWindow().setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.image_transition));
        }

    }

    private void setViewModel() {
        viewModel=new ViewModelProvider(this,new MainViewModelFactory()).get(TestResultsVM.class);
        activityVM=new ViewModelProvider(requireActivity(),new MainViewModelFactory()).get(TestResultsVM.class);
        fragmentImageViewBinding.setViewModel(new TestResults());
fragmentImageViewBinding.photoView.setMarkerTouchListener(this);
int mapId=Integer.parseInt(mapDetails.getMapId());
viewModel.getTestResultsObservableAtId(mapId).observe(getActivity(), new Observer<List<TestResults>>() {
    @Override
    public void onChanged(List<TestResults> testResults) {
        ImageFragmentDialog.this.testResults=testResults;
        fragmentImageViewBinding.photoView.setMarkers(testResults);
    }
});


    }

    private void initializeDialogVM() {
        alertDialogViewModel = DialogManager.initializeViewModel(this);
        DialogManager.initializeViewModel(this);
    }
    private void init() {
        {
            fragmentManager=getActivity().getSupportFragmentManager();

            /*fragmentImageViewBinding.photoView=view.findViewById(R.id.fragmentImageViewBinding.photoView);

            fragmentImageViewBinding.photoView.setZoomEnabled(true);
            fragmentImageViewBinding.timestamp_TV = view.findViewById(R.id.fragmentImageViewBinding.timestamp_TV);
            fragmentImageViewBinding.shareLL=view.findViewById(R.id.fragmentImageViewBinding.shareLL);
            relativeLayout=view.findViewById(R.id.main_relative);
            fragmentImageViewBinding.thumbnailprogress=view.findViewById(R.id.thumbnailprogress);
            shareBtn=view.findViewById(R.id.shareBtn);
            backBtn=view.findViewById(R.id.backBtn);
            legendsLayout=view.findViewById(R.id.legendsLayout);
            ls_legendsLayout=view.findViewById(R.id.ls_legendsLayout);*/

            Log.i(TAG,"Source is "+source);
            DecimalFormat decimalFormat=new DecimalFormat("0.0");
            try {
                if(Utils.checkifavailable(source))
                {
                    if(source.toLowerCase().contains("ls_heatmap".toLowerCase()))
                    {
                        fragmentImageViewBinding.legendsLayout.legendsLL.setVisibility(GONE);
                        fragmentImageViewBinding.lsLegendsLayout.legendsMainLL.setVisibility(View.VISIBLE);
                        /* darkGreenTv=fragmentImageViewBinding.lsLegendsLayout.lfindViewById(R.id.darkGreenTv);
                         lightGreenTv=ls_legendsLayout.findViewById(R.id.lightGreenTv);
                         yellowTv=ls_legendsLayout.findViewById(R.id.yellowTv);
                         redTv=ls_legendsLayout.findViewById(R.id.redTv);*/
                        fragmentImageViewBinding.lsLegendsLayout.darkGreenTv.setText(String.format("%s%%", decimalFormat.format(Double.parseDouble(mapDetails.getLsExcellentCoveragePercentage()))));
                        fragmentImageViewBinding.lsLegendsLayout.lightGreenTv.setText(String.format("%s%%", decimalFormat.format(Double.parseDouble(mapDetails.getLsGoodCoveragePercentage()))));
                        fragmentImageViewBinding.lsLegendsLayout.yellowTv.setText(String.format("%s%%", decimalFormat.format(Double.parseDouble(mapDetails.getLsFairCoveragePercentage()))));
                        fragmentImageViewBinding.lsLegendsLayout.redTv.setText(String.format("%s%%", decimalFormat.format(Double.parseDouble(mapDetails.getLsPoorCoveragePercentage()))));
                        fragmentImageViewBinding.lsLegendsLayout.darkGreenTv24.setText(String.format("%s%%", decimalFormat.format(Double.parseDouble(mapDetails.getLsExcellentCoveragePercentage()))));
                        fragmentImageViewBinding.lsLegendsLayout.lightGreenTv24.setText(String.format("%s%%", decimalFormat.format(Double.parseDouble(mapDetails.getLsGoodCoveragePercentage()))));
                        fragmentImageViewBinding.lsLegendsLayout.yellowTv24.setText(String.format("%s%%", decimalFormat.format(Double.parseDouble(mapDetails.getLsFairCoveragePercentage()))));
                        fragmentImageViewBinding.lsLegendsLayout.redTv24.setText(String.format("%s%%", decimalFormat.format(Double.parseDouble(mapDetails.getLsPoorCoveragePercentage()))));

                    }
                    else
                    {
                        setValuesToSSHeatMapLegends(decimalFormat);

                    }
                }
                else
                {
                   setValuesToSSHeatMapLegends(decimalFormat);

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                fragmentImageViewBinding.legendsLayout.legendsLL.setVisibility(GONE);
                fragmentImageViewBinding.lsLegendsLayout.legendsMainLL.setVisibility(GONE);
            }

            //optionsLayout=view.findViewById(R.id.optionsLayout);

            fragmentImageViewBinding.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   dismiss();
                }
            });
            fragmentImageViewBinding.shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.sendImage(true, getActivity(),fragmentImageViewBinding.shareLL,
                            urlofphoto,false,null, new Interfaces.SaveSuccessfullListener() {
                                @Override
                                public void saveSuccessfull(String name, String path) {

                                }
                            }, null, textToShare);

                }
            });













            //  fragmentImageViewBinding.photoView.setOnTouchListener(new TouchImageView());

        }

    }

    private void setValuesToSSHeatMapLegends(DecimalFormat decimalFormat) {
        fragmentImageViewBinding.legendsLayout.legendsLL.setVisibility(View.VISIBLE);
        fragmentImageViewBinding.lsLegendsLayout.legendsMainLL.setVisibility(GONE);
        fragmentImageViewBinding.legendsLayout.darkGreenTv.setText(decimalFormat.format(Double.parseDouble(mapDetails.getExcellentCoveragePercentage()))+"%");
        fragmentImageViewBinding.legendsLayout.lightGreenTv.setText(decimalFormat.format(Double.parseDouble(mapDetails.getGoodCoveragePercentage()))+"%");
        fragmentImageViewBinding.legendsLayout.yellowTv.setText(decimalFormat.format(Double.parseDouble(mapDetails.getFairCoveragePercentage()))+"%");
        fragmentImageViewBinding.legendsLayout.redTv.setText(decimalFormat.format(Double.parseDouble(mapDetails.getPoorCoveragePercentage()))+"%");

    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       /* dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        return dialog;
    }
    private void getExtras() {

        Bundle bundle = getArguments();
        if(bundle!=null) {
            urlofphoto = bundle.getString("path");
            source=bundle.getString("source");
            textToShare=bundle.getString("textToShare");
            mapDetails=(MapModel)bundle.getSerializable("mapDetails");
            Log.i(TAG,"Url obtained "+urlofphoto);
        }
        System.out.println("bundle in iv fragment "+bundle);

    }

    @Override
    public void onMarkerTouched(String id) {

        int idNum=Integer.parseInt(id);
        TestResults testResult= viewModel.getTestResultsObservableAtLocation(idNum).getValue();
        activityVM.selectMarker(testResult);

    }


    @Override
    public void alertDialogPositiveButtonClicked(AlertType type, Object details) {

    }

    @Override
    public void alertDialogNegativeButtonClicked(AlertType type) {

    }

    @Override
    public void listOptionClicked(String text) {

    }

    @Override
    public void finishActivity() {

    }

   /* public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.order_confirmation))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {} )
                .create();
    }

    public static String TAG = "PurchaseConfirmationDialog";*/
}
