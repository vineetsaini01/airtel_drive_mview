package com.newmview.wifi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mview.airtel.R;
import com.newmview.wifi.canvas.CustomDrawView;
import com.mview.airtel.databinding.EditImageFragmentBinding;
import com.newmview.wifi.other.Utils;
/*import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;*/

import java.util.ArrayList;

public class EditImageFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "EditImageFragment";
    private View view;
    private Bitmap bmp;
    private Bitmap alteredBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    private ToggleButton pencil_Tg;
    private Uri imageFileUri;

  //  private ColorSeekBar colorSlider;
    private String img;
    private ProgressBar progress;
    private View transparentView;
    private LinearLayout send_LL;
    private static final int STATE_STILL=0;
    private static final int STATE_MOVING=1;
    private static int DEFAULT_COLOR;

    private int state=0;
    private ArrayList<Paint> paintPenList =new ArrayList<>();
    private Path latestPath;
    private Paint latestPaint;
    private ArrayList<Path> pathPenList =new ArrayList<>();

    private int lineWidth =15;
    private int currentColor;
    private CustomDrawView customDrawView;
    private ToggleButton  undo_Tg,crop_Tg;
    private FragmentManager fragmentManager;
    private ImageView back_Iv;
    private Uri tempCropFile;
    private EditImageFragmentBinding imageFragmentBinding;
    private ActivityResultLauncher<Intent> launcher;

    //private LockableScrollView lockableScrollView;
  //  public EditImageFragment(LockableScrollView scrollView) {
      /*  lockableScrollView=scrollView;
    }*/
    public EditImageFragment()
    {

    }
    public static  EditImageFragment newInstance(Bundle bundle)
    {

        EditImageFragment editImageFragment=new EditImageFragment();
        editImageFragment.setArguments(bundle);
        return editImageFragment;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       imageFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.edit_image_fragment, container,
                false);
       // view=inflater.inflate(R.layout.edit_image_fragment,container,false);
        getExtras();
        init();
        setUpLauncher();
       cropAndRotateImage();

        /*if(lockableScrollView!=null)
            lockableScrollView.setEnabled(false);*/
        return  imageFragmentBinding.getRoot();
    }

    private void getExtras() {

        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            img=bundle.getString("imgUri");

            if(Utils.checkifavailable(img)) {
                imageFileUri = Uri.parse(img);
            }
            System.out.println("imagefile "+imageFileUri +"path "+img);
        }

    }

    private void init() {
        fragmentManager = getActivity().getSupportFragmentManager();
        imageFragmentBinding.previewIv.setImageURI(imageFileUri);
    }
       
      //  send_LL.setOnClickListener(this);
       /* imageFragmentBinding.previewIv.setImageUriAsync(imageFileUri);
//        Utils.loadImage(getActivity(), imageFragmentBinding.previewIv, imageFileUri.toString(), null, R.drawable.picture, false);
        imageFragmentBinding.previewIv.setOnSetImageUriCompleteListener(this);
        imageFragmentBinding.previewIv.setOnCropImageCompleteListener(this);
    }*/



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

          /*  case R.id.send_LL:
                sendImage();
                break;

            case R.id.back_Iv:
                fragmentManager.popBackStack(ConstantStrings.EDIT_IMAGE,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;*/
        }


    }

    private void cropAndRotateImage() {

        {
            if(imageFileUri!=null) {
               /* Intent intent = CropImage.activity(imageFileUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setFixAspectRatio(false)
                        .getIntent(getActivity());
                launcher.launch(intent);*/
                /*CropImage.activity(imageFileUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getContext(), this);*/
               /* Intent intent = CropImage.activity(imageFileUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setFixAspectRatio(false)
                        .getIntent(getActivity());
                      launcher.launch(intent);*/
            }
            else {
                Utils.showToast(getActivity(), "Select some picture!");
            }
        }

    }


    private void sendImage() {

    }



/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                //    imageFragmentBinding.previewIv.resetView();
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                if(resultUri!=null) {
                    Utils.loadImage(getActivity(), imageFragmentBinding.previewIv, resultUri.toString(), progress, R.drawable.picture, false);
                    imageFileUri=resultUri;
                }
                else
                {


                }
            }
        }
    }
*/



    public void setUpLauncher() {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            if(result.getData()!=null)

                            {
                                handleCropResult(result.getData());

                            }

                        }
                    }
                });

        //Create Intent
       /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        //Launch activity to get result
        someActivityResultLauncher.launch(intent);*/
    }


   /* @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {

    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
    //    handleCropResult(result);

    }*/

    private void handleCropResult(Intent result) {

       /* CropImage.ActivityResult result1 = CropImage.getActivityResult(result);
        Uri resultUri = result1.getUri();
        if(resultUri!=null) {

            Log.i(TAG,"Result obtained..");
            imageFragmentBinding.previewIv.setImageURI(resultUri);
            // Utils.loadImage(getActivity(), imageFragmentBinding.previewIv, resultUri.toString(), progress, R.drawable.picture, false);
            imageFileUri=resultUri;
        }*/
    }
}
