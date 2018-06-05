package com.application.onlineTestSeries.FullImageView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.application.onlineTestSeries.R;
import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoViewAttacher;


public class FullImage extends AppCompatActivity {
    String imagepath;
    ImageView fullimage;
    PhotoViewAttacher photoViewAttacher ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_image);
        imagepath = getIntent().getStringExtra("ImagePath");
        Log.e("imagePath"," "+imagepath);
        // Toast.makeText(this, "path "+imagepath, Toast.LENGTH_SHORT).show();
        fullimage = (ImageView)findViewById(R.id.fullimageview);
        Glide.with(FullImage.this).load(imagepath).into(fullimage);
        photoViewAttacher = new PhotoViewAttacher(fullimage);

        photoViewAttacher.update();
    }
}
