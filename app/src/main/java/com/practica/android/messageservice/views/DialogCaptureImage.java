package com.practica.android.messageservice.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.practica.android.messageservice.R;


public class DialogCaptureImage {

    private ActivityMessages activity;

    DialogCaptureImage(ActivityMessages activity) {
        this.activity = activity;
    }

    void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Select Image....");

        builder.setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                captureImageGallery();
            }
        });
        builder.setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                captureImageCamera();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void captureImageGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        activity.startActivityForResult(chooser, ActivityMessages.ACTION_REQUEST_GALLERY);
    }

    private void captureImageCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, ActivityMessages.REQUEST_IMAGE_CAPTURE);
        }
    }
}


