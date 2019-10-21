package ec.tec.ami.views.activities;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import ec.tec.ami.R;

public class DialogSelectImage extends Dialog {

    ImageView imgGallery, imgPicture;

    public DialogSelectImage(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_image_selector);
        imgGallery = findViewById(R.id.imgGallery);
        imgPicture = findViewById(R.id.imgPicture);
    }

    public void setOnGalleryListener(final View.OnClickListener listener){
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                DialogSelectImage.this.dismiss();
            }
        });
    }

    public void setOnPictureListener(final View.OnClickListener listener){
        imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                DialogSelectImage.this.dismiss();
            }
        });
    }
}
