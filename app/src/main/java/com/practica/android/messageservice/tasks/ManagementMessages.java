package com.practica.android.messageservice.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.practica.android.messageservice.R;
import com.practica.android.messageservice.entities.Message;
import com.practica.android.messageservice.views.ActivityGroups;
import com.practica.android.messageservice.views.ActivityMessages;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.SortedSet;


public class ManagementMessages {

    private static final String TAG = ManagementMessages.class.getSimpleName();
    private ActivityMessages activity;

    public ManagementMessages (ActivityMessages activity) {
        this.activity = activity;
    }
    private int downloadImages;

    public void sendMessageAndImage(Message message, Bitmap bitmap, String group) {
        String path = ActivityGroups.PATHGROUPS + group + ActivityMessages.PATHMESSAGES;
        DatabaseReference messagesGroupRef = FirebaseDatabase.getInstance().getReference(path);

        // Obtain new key to new message...
        message.setKey(messagesGroupRef.push().getKey());
        DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference(path + "/"  + message.getKey());

        if (bitmap != null && bitmap.isMutable()) {
            setUrlMessageAndUploadImage(bitmap, message, msgRef);
        } else {
            msgRef.setValue(message);
        }
    }

    private void setUrlMessageAndUploadImage(Bitmap bitmap,final Message message, final DatabaseReference msgRef) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesReference = storageRef.child(message.getKey());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesReference.putBytes(data);

        // Handle unsuccessful uploads.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                if (downloadUrl != null) {
                    message.setUrl(downloadUrl.toString());
                    msgRef.setValue(message);
                }
            }
        });
    }

    public void setAllViewsAllMessagesSorted(SortedSet<Message> messages) {
        setImagesMessages(new OnGetAllDataMessages() {

            @Override
            public void onStart() {
                    ;
            }

            @Override
            public void onSuccess() {
                ProgressBar loadImages = activity.findViewById(R.id.progress_msg);
                loadImages.setVisibility(View.INVISIBLE);
                loadImages.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 0));

                ScrollView viewMessages = activity.findViewById(R.id.view_messages);
                viewMessages.setVisibility(View.VISIBLE);
                viewMessages.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.8f));

                FloatingActionButton writeMessages = activity.findViewById(R.id.write_message);
                writeMessages.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                ;
            }
        }, messages);
    }

    private void setImagesMessages (final OnGetAllDataMessages listener, SortedSet<Message> messages) {
        final int sizeMessagesImage = getNumImagesMessages(messages);

        if (sizeMessagesImage == 0) {
            listener.onSuccess();
            return;
        }

        for (final Message message : messages) {
            if (!message.getUrl().equals("")) {
                downloadImage(listener, message, sizeMessagesImage);
            }
        }
    }

    private void downloadImage(final OnGetAllDataMessages listener, final Message message, final int numImages) {
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(message.getUrl());

        try {
            final File localFile = File.createTempFile("Images", "bmp");

            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot >() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ImageView imageView = searchImageView(message.getKey());

                    if (imageView != null) {
                        imageView.setImageBitmap(image);
                    }

                    if (downloadImages == numImages - 1) {
                        listener.onSuccess();
                    } else {
                        downloadImages++;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int  getNumImagesMessages(SortedSet<Message> messages) {
        int numImages = 0;

        for (Message message : messages) {
            if (!message.getUrl().equals("")) {
                numImages++;
            }
        }
        return numImages;
    }

    private ImageView searchImageView(String key) {
        TableLayout table = activity.findViewById(R.id.table_messages);
        ImageView imageView = null;
        int length = table.getChildCount();

        for(int i = 0; i < length; i++) {
            View view = table.getChildAt(i);

            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                View viewMsg = row.getChildAt(0);

                String tag = getTag(viewMsg.getTag());
                if(viewMsg instanceof ImageView && tag.equals(key)) {
                    imageView = (ImageView) viewMsg;
                    return imageView;
                }
            }
        }
        return imageView;
    }

    private String getTag(Object o) {
        String tag = "";

        if (o instanceof String) {
            tag = (String) o;
        }
        return tag;
    }
}
