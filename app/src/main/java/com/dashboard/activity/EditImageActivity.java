package com.dashboard.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.mview.airtel.R;
import com.newmview.wifi.canvas.CustomDrawView;
import com.newmview.wifi.other.Utils;
import com.rtugeek.android.colorseekbar.ColorSeekBar;
import java.util.ArrayList;

public class EditImageActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {

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
    private CustomDrawView preview_Iv;
    private ColorSeekBar colorSlider;
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
    private EditText commentEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        getExtras();
        init();
    }
    private void getExtras() {

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            img=  bundle.getString("imgUri");

            if(Utils.checkifavailable(img)) {
                imageFileUri = Uri.parse(img);
            }
            System.out.println("imagefile "+imageFileUri +"path "+img);
        }

    }

    private void init() {
        fragmentManager=getSupportFragmentManager();
        pencil_Tg=findViewById(R.id.pencil_Tg);
        crop_Tg=findViewById(R.id.crop_Tg);
        preview_Iv=findViewById(R.id.preview_Iv);
        colorSlider=findViewById(R.id.colorSlider);
        progress=findViewById(R.id.progress);
        back_Iv=findViewById(R.id.back_Iv);
        transparentView=findViewById(R.id.transparentView);
        undo_Tg=findViewById(R.id. undo_Tg);
        send_LL=findViewById(R.id.send_LL);
        commentEt=findViewById(R.id.commentEt);
        pencil_Tg.setOnCheckedChangeListener(this);
        crop_Tg.setOnCheckedChangeListener(this);
        undo_Tg.setOnCheckedChangeListener(this);
        send_LL.setOnClickListener(this);

        back_Iv.setOnClickListener(this);
        setParamsForColorSlider();

        //  preview_Iv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT));
        // this.findViewById(R.id.toolbar).setVisibility(View.GONE);

        Utils.loadImage(EditImageActivity.this, preview_Iv, imageFileUri.toString(), progress, R.drawable.no_img, false);
        colorSlider.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                preview_Iv.setDrawColor(color);
            }
        });
    }

    private void setParamsForColorSlider() {colorSlider.setMaxPosition(100);
        colorSlider.setColorSeeds(R.array.material_colors); // material_colors is defalut included in res/color,just use it.
        colorSlider.setColorBarPosition(10); //0 - maxValue
        colorSlider.setAlphaBarPosition(10); //0 - 255
        colorSlider.setPosition(10,10); // An easier way to set ColorBar and AlphaBar
        colorSlider.setShowAlphaBar(true);
        colorSlider.setBarHeight(8); //5dpi
        colorSlider.setThumbHeight(30); //30dpi
        colorSlider.setBarMargin(10);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.send_LL:
                sendImage();
                break;

            case R.id.back_Iv:
                onBackPressed();
                finish();
                //  fragmentManager.popBackStack(ConstantStrings.EDIT_IMAGE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
        }


    }

    private void cropAndRotateImage() {
        if(preview_Iv.returnPaintListSize()>0)
        {
            Bitmap bitmap=Utils.getViewBitmap(preview_Iv);
            System.out.println("bitmap is "+bitmap);
            if(bitmap!=null) {
                imageFileUri = Utils.getImageUri(this, bitmap);

                tempCropFile=imageFileUri;
                System.out.println("temp crop  " + tempCropFile);
                if (imageFileUri != null) {
                    preview_Iv.resetView();
//                    Intent intent = CropImage.activity(imageFileUri)
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .setFixAspectRatio(false)
//                            .getIntent(this);
//                    startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

                } else {
                    Utils.showToast(this, "Select some picture!");
                }
            }
        }
        else
        {
//            if(imageFileUri!=null) {
//                Intent intent = CropImage.activity(imageFileUri)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setFixAspectRatio(false)
//                        .getIntent(this);
//                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
//            }
//            else {
//                Utils.showToast(this, "Select some picture!");
//            }
        }

    }
//    private String saveContent(Bitmap bitmap) {
//
//        FileOutputStream ostream = null;
//        String filepath=null;
//        try {
//            if (!CommonUtil.downloaded_root.exists()) {
//                CommonUtil.downloaded_root.mkdirs();
//            }
//            File SDCardRoot = CommonUtil.downloaded_root.getAbsoluteFile();
//
//
//            String filename = Utils.getRandomString() + ".png";
//            Log.i("DownloadLocal filename:", "" + filename);
//            File file = new File(SDCardRoot, filename);
//            if (file.createNewFile()) {
//                file.createNewFile();
//            }
//
//
//            ostream = new FileOutputStream(file);
//            if (bitmap == null) {
//                System.out.println("NULL bitmap save\n");
//            }
//            bitmap.compress(Bitmap.CompressFormat.PNG, 80, ostream);
//            filepath=file.getPath();
//            System.out.println("path 2 is "+filepath);
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//
//        }
//        return filepath;
//
//    }


    private void sendImage() {

        Bitmap bitmap=Utils.getViewBitmap(preview_Iv);
        //  String   finalPath= saveContent(bitmap);
        Uri finalUri=Utils.getImageUri(this,bitmap);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");

        intent.putExtra(Intent.EXTRA_STREAM, finalUri);
        intent.putExtra(Intent.EXTRA_TEXT, commentEt.getText().toString());
        startActivity(Intent.createChooser(intent, "Share Screenshot"));


      /*  String finalPath=null;
        fragmentManager.popBackStack(ConstantStrings.EDIT_IMAGE,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Bitmap bitmap=Util.getViewBitmap(preview_Iv);
        finalPath= saveContent(bitmap);


        if(finalPath!=null) {

            System.out.println("imagepath final is "+imageFileUri);
            //preview class check added by Sonal on 13-07-2020
            if(this instanceof FinalPreviewFromUser || this instanceof PreviewForAlbum) {
                if(tempCropFile!=null)
                {

                    String actualPath=Util.getPath(this,tempCropFile);
                    if(Util.checkifavailable(actualPath)) {


                        Util.delete(this, Uri.parse(actualPath));
                    }
                }
                if(this instanceof FinalPreviewFromUser) {
                    ((FinalPreviewFromUser) getActivity()).sendFinalResult(finalPath, bitmap);
                }
                else if(this instanceof PreviewForAlbum)//preview class check added by Sonal on 13-07-2020
                {
                    ((PreviewForAlbum) getActivity()).sendFinalResult(finalPath, bitmap);
                }
            }
        }*/
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//                //    preview_Iv.resetView();
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                Uri resultUri = result.getUri();
//                if (resultUri != null) {
//                    Utils.loadImage(this, preview_Iv, resultUri.toString(), progress, R.drawable.no_img, false);
//                    imageFileUri = resultUri;
//                } else {
//
//
//                }
//            }
        }
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {




            case R.id. undo_Tg:
                if(isChecked)
                    preview_Iv.undoPath();

                break;
            case R.id.crop_Tg:
                if(isChecked) {
                    pencil_Tg.setChecked(false);
                    cropAndRotateImage();
                }
                break;
            case R.id.pencil_Tg:
                if (isChecked) {
                    preview_Iv.callTodraw(true);
                    colorSlider.setVisibility(View.VISIBLE);
                }
                else
                {
                    preview_Iv.callTodraw(false);
                    colorSlider.setVisibility(View.INVISIBLE);
                }
                break;
        }

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
