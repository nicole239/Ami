package ec.tec.ami.data.dao;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ec.tec.ami.data.event.ImageEvent;

public class ImageDAO {

    private static ImageDAO imageDAO;
    private FirebaseStorage storage;
    private ImageDAO(){
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized ImageDAO  getInstance(){
        if(imageDAO == null){
            imageDAO = new ImageDAO();
        }
        return imageDAO;
    }

    public void uploadImage(Uri uri, String name, final ImageEvent event){
        if(uri == null){
            event.onSuccess("");
            return;
        }
        final StorageReference reference = storage.getReference().child(name);
        UploadTask uploadTask = reference.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw  task.getException();
                }
                return  reference.getDownloadUrl();
            }
        })
       .addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    event.onSuccess(task.getResult().toString());
                }else{
                    event.onFailure(task.getException().getMessage());
                }

            }
        });
    }
}
