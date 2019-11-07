package ec.tec.ami.views.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ec.tec.ami.views.activities.DialogSelectImage;

public class ImageSelector {
    public static final int GALLERY = 0, CAMERA = 1;
    private Activity activity;

    public ImageSelector(Activity activity) {
        this.activity = activity;
    }

    public void selectImage(){
        DialogSelectImage dialogSelectImage = new DialogSelectImage(activity);
        dialogSelectImage.setOnPictureListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionCamera();
            }
        });
        dialogSelectImage.setOnGalleryListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionsGallery();
            }
        });
        dialogSelectImage.show();
    }

    private void getPermissionCamera() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA);
        else
            takePicture();
    }

    private void getPermissionsGallery() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY);
        else
            chooseFromGallery();
    }

    public void chooseFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPhoto,GALLERY);
    }

    public void takePicture(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(takePicture,CAMERA);
    }

}
