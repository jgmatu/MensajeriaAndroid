package com.practica.android.messageservice.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.practica.android.messageservice.R;
import com.practica.android.messageservice.views.ActivityMessages;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;


public class ManagementMessages {

    private static final String TAG = ManagementMessages.class.getSimpleName();
    private ActivityMessages activity;
    private String path;
    private ImageMessages imageMessages;

    private int readMessages = 0;
    private int sizeMessagesImage = 0;

    public ManagementMessages (ActivityMessages activity, String path) {
        this.activity = activity;
        this.path = path;
        this.imageMessages = new ImageMessages();
    }

    public void setViewAllMessagesSorted(List<String> messages) {
        getAllMessages(new OnGetAllDataMessages() {

            @Override
            public void onStart() {
                    ;
            }

            @Override
            public void onSuccess(ImageMessages imageMessages) {
                TableLayout table = activity.findViewById(R.id.table_messages);
                SortedSet<ImageMessage> sorted = imageMessages.getSorted();

                for (ImageMessage imageMessage : sorted) {
                    setTableTextMessage(table, imageMessage.getMessage());
                    if (imageMessage.getBitmap() != null) {
                        setTableImage(table, imageMessage.getBitmap());
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                ;
            }
        }, messages);
    }

    private void getAllMessages(final OnGetAllDataMessages listener, List<String> messages) {
        listener.onStart();

        mReadDataOnce(new OnGetAllDataMessages() {

            @Override
            public void onStart() {
                ;
            }

            @Override
            public void onSuccess(ImageMessages imageMessages) {
                getImagesMessages(new OnGetAllDataMessages() {
                    @Override
                    public void onStart() {
                        ;
                    }

                    @Override
                    public void onSuccess(ImageMessages imageMessages) {
                        listener.onSuccess(imageMessages);
                    }

                    @Override
                    public void onFailed(DatabaseError databaseError) {
                        Log.e(TAG, "Failed images imageMessages...");
                    }
                });
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e(TAG, "Failed ImageMessages Meta data...");
            }
        }, messages);
    }

    private void getImagesMessages (final OnGetAllDataMessages listener) {
        listener.onStart();

        sizeMessagesImage = imageMessages.getNumMessagesImage();
        if (sizeMessagesImage == 0) {
            listener.onSuccess(imageMessages);
            return;
        }

        readMessages = 0;
        for (final Map.Entry<String, ImageMessage> entry : imageMessages.getMap().entrySet()) {
            final ImageMessage imageMessage = entry.getValue();

            if (imageMessage.getUrl().equals("")) {
                continue;
            }

            StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageMessage.getUrl());
            try {
                final File localFile = File.createTempFile("Images", "bmp");

                httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot >() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        imageMessages.get(entry.getKey()).setBitmap(image);

                        if (readMessages == sizeMessagesImage - 1) {
                            listener.onSuccess(ManagementMessages.this.imageMessages);
                        } else {
                            readMessages++;
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
    }


    private void mReadDataOnce(final OnGetAllDataMessages listener, final List<String> messages) {
        listener.onStart();

        readMessages = 0;
        for (final String msg : messages) {
            FirebaseDatabase.getInstance().getReference(path + msg).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    com.practica.android.messageservice.entities.Message message = dataSnapshot.getValue(com.practica.android.messageservice.entities.Message.class);

                    if (isNewActivityMessage(message)) {
                        activity.addMessage(message);
                        ManagementMessages.this.imageMessages.put(msg, new ImageMessage(message));
                    }

                    if (readMessages == messages.size() - 1) {
                        listener.onSuccess(ManagementMessages.this.imageMessages);
                    } else {
                        readMessages++;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onFailed(databaseError);
                }
            });
        }
    }

    private boolean isNewActivityMessage(com.practica.android.messageservice.entities.Message message) {
        return message != null && !activity.isExistMessage(message);
    }

    private void setTableImage(TableLayout table, Bitmap image) {
        TableRow row = getRow();

        row.addView(getImageMessage(image));
        table.addView(row);
    }

    private ImageView getImageMessage(Bitmap image) {
        ImageView imageView = new ImageView(activity);

        imageView.setLayoutParams(new TableRow.LayoutParams(512, 512));
        imageView.setImageBitmap(image);
        return imageView;
    }

    private void setTableTextMessage(TableLayout table, com.practica.android.messageservice.entities.Message message) {
        TableRow row = getRow();

        row.addView(getTextMessage(message));
        table.addView(row);
    }

    private TableRow getRow() {
        TableRow row = new TableRow(activity);
        TableLayout.LayoutParams rows = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        row.setLayoutParams(rows);
        return row;
    }

    private TextView getTextMessage(com.practica.android.messageservice.entities.Message message) {
        TextView msg = new TextView(activity);
        TableRow.LayoutParams buttons = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1);

        msg.setLayoutParams(buttons);
        msg.setTextSize(ActivityMessages.SIZETEXT);
        msg.setText(message.toString());
        return msg;
    }
}
