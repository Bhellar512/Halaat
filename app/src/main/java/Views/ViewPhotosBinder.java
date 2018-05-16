package Views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.halaat.halaat.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import Modal.Image;
import helpers.DialogHelper;

public class ViewPhotosBinder extends ViewBinder<Image> {

    private ImageLoader imageLoader;
    Context applicationContext;
    Context context;

    public ViewPhotosBinder(Context context, Context applicationContext) {
        super(R.layout.row_item_photo);
        imageLoader = ImageLoader.getInstance();
        this.applicationContext=applicationContext;
        this.context=context;
    }
    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final Image entity, int position, int grpPosition, View view, Activity activity) {
       ViewHolder viewHolder = (ViewHolder) view.getTag();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
     /*   Glide.with(applicationContext)
                .load(entity.getUri())
                .into(viewHolder.image);*/
     imageLoader.displayImage(entity.getUri(),viewHolder.image);
     //viewHolder.image.setImageResource(R.drawable.logo);

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper dialogHelper=new DialogHelper(applicationContext);
                dialogHelper.imageDialoge(R.layout.image_dialoge,entity.getUri(),entity.getDescription(),context);
                dialogHelper.showDialog();
            }
        });


    }

    public static class ViewHolder extends BaseViewHolder {

        ImageView image;


        public ViewHolder(View view) {

            image = (ImageView) view.findViewById(R.id.image);

        }
    }
}
