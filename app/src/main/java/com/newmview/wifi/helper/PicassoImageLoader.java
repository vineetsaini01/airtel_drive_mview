package com.newmview.wifi.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.mview.airtel.R;
import com.newmview.wifi.other.Utils;
import com.squareup.picasso.Callback;
import com.veinhorn.scrollgalleryview.loader.MediaLoader;

import static com.newmview.wifi.other.Utils.MAX_BITMAP_SIZE;
import static com.newmview.wifi.other.Utils.resize;

public class PicassoImageLoader implements MediaLoader {
    private String url;
    private Integer thumbnailWidth;
    private Integer thumbnailHeight;

    public PicassoImageLoader(String url) {
        this.url = url;
    }


    @Override
    public boolean isImage() {
        return true;
    }

    @Override
    public void loadMedia(Context context, final ImageView imageView, final SuccessCallback callback) {
        setBitmapToView(imageView);

   //     Utils.loadImage(context,imageView,url,null,R.drawable.picture,true);


      /*  Picasso.with(context)
                .load(url)
               *//* .resize(thumbnailWidth == null ? Utils.getScreenWidth(context) : thumbnailWidth,
                        thumbnailHeight == null ? Utils.getScreenHeight(context) : thumbnailHeight)*//*
                .placeholder(R.drawable.placeholder_image)
                //.fit()
                .error(R.drawable.placeholder_image)
                .fit()
                .into(imageView, new ImageCallback(callback, context, url, imageView));*/
}

    private void setBitmapToView(ImageView imageView) {
        if(Utils.checkifavailable(url)) {
            Bitmap resource = Utils.getBitmap(url);
            if(resource!=null) {
                int bitmapSize = resource.getByteCount();
                System.out.println("bitmap size " + bitmapSize);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                if (bitmapSize > MAX_BITMAP_SIZE) {
                    //  System.out.println("bitmap resizing "+bitmapSize);
                                                  /*  throw new RuntimeException(
                                                            "Canvas: trying to draw too large(" + bitmapSize + "bytes) bitmap.");*/
                    resource = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                    System.out.println("bitmap resizing " + resource.getByteCount());
                    imageView.setImageBitmap(resource);
                } else imageView.setImageBitmap(Utils.getBitmap(url));
            }
        }
   //     imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }


    @Override
    public void loadThumbnail(Context context, final ImageView thumbnailView, final SuccessCallback callback) {
thumbnailView.setColorFilter(context.getResources().getColor(R.color.transparent_black_percent_15));
setBitmapToView(thumbnailView);
/*
        Picasso.with(context)
                .load(url)
                */
/*.resize(100,
                        100)*//*

               */
/* .resize(thumbnailWidth == null ? 100 : thumbnailWidth,
                        thumbnailHeight == null ? 100 : thumbnailHeight)*//*

                .placeholder(R.drawable.picture)
                .fit()
                .into(thumbnailView);
*/
    }

    private static class ImageCallback implements Callback {
        private final SuccessCallback callback;
        private Context with_contxt;
        private String url;
        ImageView view;

        public ImageCallback(SuccessCallback callback,Context context,String urlis,ImageView imageView) {
            this.callback = callback;
            with_contxt=context;
            url=urlis;
            view=imageView;
        }

        @Override
        public void onSuccess() {
            Log.i("PicassoImageLoaded","Successfully");
            callback.onSuccess();
        }

        @Override
        public void onError() {
            Log.i("PicassoImageLoaded","Error");
            view.setImageBitmap(Utils.getBitmap(url));
//Utils.loadImage(with_contxt,view,url,null,R.drawable.picture,true);
           // Glide.with(with_contxt).load(url).into(view);

        }
    }
}

