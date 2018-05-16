package Views;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.halaat.halaat.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import Modal.tweetEnt;

/**
 * Created by Hp on 5/6/2018.
 */

public class BinderSocialTweet extends ViewBinder<tweetEnt> {

    private ImageLoader imageLoader;

    public BinderSocialTweet() {
        super(R.layout.row_item_tweet);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(tweetEnt entity, int position, int grpPosition, View view, Activity activity) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.tv_msg.setText(entity.getText());
        viewHolder.tv_date.setText(entity.getDate());
        viewHolder.tv_time.setText(entity.getTime());

    }

    public static class ViewHolder extends BaseViewHolder {

                ImageView ivNotification;
                AnyTextView tv_msg;
                AnyTextView tv_date;
                AnyTextView tv_time;

                public ViewHolder(View view) {

                    ivNotification = (ImageView) view.findViewById(R.id.ivNotification);
                    tv_msg = (AnyTextView) view.findViewById(R.id.tv_msg);
                    tv_date = (AnyTextView) view.findViewById(R.id.tv_date);
            tv_time = (AnyTextView) view.findViewById(R.id.tv_time);
        }
    }
}
