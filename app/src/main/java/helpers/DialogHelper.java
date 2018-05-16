package helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.halaat.halaat.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import Views.AnyTextView;


/**
 * Created on 5/24/2017.
 */

public class DialogHelper {
    private Dialog dialog;
    private Context context;
    private ImageLoader imageLoader;
    private RadioGroup rg;


    public DialogHelper(Context context) {
        this.context = context;
        this.dialog = new Dialog(context);
    }


    public Dialog mapDialoge(int layoutID,String title,String detail) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        AnyTextView txtTitle = (AnyTextView) dialog.findViewById(R.id.txt_title);
        AnyTextView txtDetail = (AnyTextView) dialog.findViewById(R.id.txt_detail);
       txtTitle.setText(title);
       txtDetail.setText(detail);

        return this.dialog;
    }

    public Dialog imageDialoge(int layoutID, String image, String detail, Context imageContext) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        ImageView photo = (ImageView) dialog.findViewById(R.id.image);
        AnyTextView txtDetail = (AnyTextView) dialog.findViewById(R.id.txt_detail);
      ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        imageLoader=ImageLoader.getInstance();
        imageLoader.displayImage(image,photo);
      //  Glide.with(imageContext).load(image).into(photo);
        txtDetail.setText(detail);

        return this.dialog;
    }






    public void showDialog() {

        dialog.show();
    }

    public void setCancelable(boolean isCancelable) {
        dialog.setCancelable(isCancelable);
        dialog.setCanceledOnTouchOutside(isCancelable);
    }

    public void hideDialog() {
        dialog.dismiss();
    }
}
