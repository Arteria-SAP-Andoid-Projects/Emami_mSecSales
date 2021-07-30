package com.arteriatech.emami.digitalProducts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.DocumentsBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

/**
 * Created by e10769 on 04-03-2017.
 *
 */

public class DigitalProductAdapter extends RecyclerView.Adapter<DigitalProductViewHolder> {
    byte[] imageByteArray = null;
    private ArrayList<DocumentsBean> documentsBeanList;
    private Context mContext;
    ByteArrayOutputStream bytearrayoutputstream;
    byte[] BYTE;
    int[] imageId = {
            R.drawable.ic_npdf,
            R.drawable.ic_ndoc,
            R.drawable.ic_nppt,
            R.drawable.ic_njpg,
            R.drawable.ic_npng,
    };

    public DigitalProductAdapter(Context mContext, ArrayList<DocumentsBean> documentsBeanList) {
        this.documentsBeanList = documentsBeanList;
        this.mContext = mContext;
    }

    @Override
    public DigitalProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.digital_product_item, parent, false);
        return new DigitalProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DigitalProductViewHolder holder, int position) {
        DocumentsBean documentsBean = documentsBeanList.get(position);

        setImageIntoImageView(documentsBean.getMediaLink(), documentsBean.getDocumentMimeType(), documentsBean.getFileName().toLowerCase(), holder.ivThumb);


    }

    @Override
    public int getItemCount() {
        return documentsBeanList.size();
    }

    private void setImageIntoImageView(String mStrImagePath, String mimeType, String filename, ImageView ivThumb) {
        try {
            imageByteArray = OfflineManager.getImageList(mStrImagePath);
            if (imageByteArray != null) {
                if (Constants.MimeTypePng.equalsIgnoreCase(mimeType) || Constants.MimeTypeJpg.equalsIgnoreCase(mimeType) || Constants.MimeTypeJpeg.equalsIgnoreCase(mimeType)) {
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
                    final File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                    final Bitmap bitmap = Compressor.getDefault(mContext).compressToBitmap(file);


                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100, stream);
                    final byte[] imageInByte = stream.toByteArray();

                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(imageInByte,0,imageInByte.length);


                    ivThumb.setImageBitmap(bitmap2);
                    ivThumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Constants.openImageInGallery(mContext,file.getPath());
                        }
                    });
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }
}
