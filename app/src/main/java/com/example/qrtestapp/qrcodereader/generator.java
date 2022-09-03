package com.example.qrtestapp.qrcodereader;

import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by sagar on 10/8/2017.
 */

public interface generator {
    void  setMessage(String message);
    void generateCode();
    ImageView getGeneratedCodeImage();
    void setImageView(ImageView view);
    Uri getUri();

}
