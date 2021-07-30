package com.arteriatech.emami.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.DocumentsBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by e10763 on 2/27/2017.
 */

public class BrouchersAdapter extends ArrayAdapter<DocumentsBean> {

    private Context mContext;
    private ArrayList<DocumentsBean> retDisplayValues;
    private final int[] Imageid;
    byte[] imageByteArray = null;


    public BrouchersAdapter(Context context, ArrayList<DocumentsBean> allDocumentList, int[] Imageid) {
        super(context, R.layout.layout_broucher_single, allDocumentList);
        this.Imageid = Imageid;
        this.mContext = context;
        this.retDisplayValues = allDocumentList;


    }

    @Override
    public int getCount() {
        return this.retDisplayValues != null ? this.retDisplayValues.size() : 0;
    }

    @Override
    public DocumentsBean getItem(int item) {
        DocumentsBean documentsBean;
        documentsBean = this.retDisplayValues != null ? this.retDisplayValues.get(item) : null;
        return documentsBean;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.layout_broucher_single, null);
        } else {
            grid = (View) convertView;
        }

        final DocumentsBean documentsBean = retDisplayValues.get(position);

        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
        if (!TextUtils.isEmpty(documentsBean.getFileName())) {
            textView.setText(documentsBean.getFileName().toLowerCase());
        } else {
            textView.setText("No Files to display");
        }
        if (Constants.MimeTypePDF.equalsIgnoreCase(documentsBean.getDocumentMimeType())) {
            imageView.setImageResource(Imageid[0]);
        } else if (Constants.MimeTypeDocx.equalsIgnoreCase(documentsBean.getDocumentMimeType()) || Constants.MimeTypeMsword.equalsIgnoreCase(documentsBean.getDocumentMimeType())) {
            imageView.setImageResource(Imageid[1]);
        } else if (Constants.MimeTypePPT.equalsIgnoreCase(documentsBean.getDocumentMimeType()) || Constants.MimeTypevndmspowerpoint.equalsIgnoreCase(documentsBean.getDocumentMimeType())) {
            imageView.setImageResource(Imageid[2]);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageDetails(documentsBean.getMediaLink(), documentsBean.getDocumentMimeType(), documentsBean.getFileName().toLowerCase());
            }
        });
        grid.setId(position);
        return grid;
    }

    // TODO get device merchandising image from offline store
    private void getImageDetails(String mStrImagePath, String mimeType, String filename) {
        try {
            imageByteArray = OfflineManager.getImageList(mStrImagePath);

            if (imageByteArray != null) {

                if (Constants.MimeTypePDF.equalsIgnoreCase(mimeType)) {
                    try {
                        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }

                        File data = new File(myDirectory, "/" + filename);

                        OutputStream op = new FileOutputStream(data);
                        op.write(imageByteArray);
                        System.out.println("File Created");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("Excep:" + ex.toString());
                    }

                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file), "application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                        Toast.makeText(mContext, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
                    }


                } else if (Constants.MimeTypeDocx.equalsIgnoreCase(mimeType) || Constants.MimeTypeMsword.equalsIgnoreCase(mimeType)) {
                    try {
                        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }

                        File data = new File(myDirectory, "/" + filename);

                        OutputStream op = new FileOutputStream(data);
                        op.write(imageByteArray);
                        System.out.println("File Created");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("Excep:" + ex.toString());
                    }
                    //   Toast.makeText(mContext, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file), "application/msword");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                        Toast.makeText(mContext, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
                    }
                } else if (Constants.MimeTypePPT.equalsIgnoreCase(mimeType) || Constants.MimeTypevndmspowerpoint.equalsIgnoreCase(mimeType)) {
                    try {
                        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }

                        File data = new File(myDirectory, "/" + filename);

                        OutputStream op = new FileOutputStream(data);
                        op.write(imageByteArray);
                        System.out.println("File Created");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("Excep:" + ex.toString());
                    }
                    //    Toast.makeText(mContext, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file), "application/vnd.ms-powerpoint");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                        Toast.makeText(mContext, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
                    }
                }

            }


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }
}
