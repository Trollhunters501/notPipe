package io.github.gohoski.notpipe.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.gohoski.notpipe.R;
import io.github.gohoski.notpipe.Utils;
import io.github.gohoski.notpipe.data.Comment;
import io.github.gohoski.notpipe.util.ImageLoader;

/**
 * Adapter for comments with lazy loading support.
 * Only loads images for items that have been explicitly marked as visible.
 */
public class CommentAdapter extends ArrayAdapter<Comment> {
    private Context context;

    public CommentAdapter(Context context, int resource, List<Comment> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.item_comment, parent, false);
            holder = new ViewHolder();
            holder.channelTitle = (TextView) row.findViewById(R.id.channel_title);
            holder.content = (TextView) row.findViewById(R.id.content);
            holder.date = (TextView) row.findViewById(R.id.date);
            holder.channelThumb = (ImageView) row.findViewById(R.id.channel_thumbnail);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Comment comment = getItem(position);
        holder.channelTitle.setText(comment.channel);
        holder.content.setText(comment.content);
        holder.date.setText(Utils.formatTimeAgo(context, comment.publishedAt));

        // Prepare holder for lazy loading
        holder.comment = comment;
        holder.imagesLoaded = false;

        // Reset thumbnail to prevent recycled view artifacts
        holder.channelThumb.setImageBitmap(null);

        return row;
    }

    /**
     * Called by VideoActivity when this row enters the screen.
     */
    public void loadImagesForView(View row) {
        if (row.getTag() instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) row.getTag();
            if (!holder.imagesLoaded && holder.comment != null) {
                ImageLoader.loadImage(holder.comment.channelThumbnail, holder.channelThumb);
                holder.imagesLoaded = true;
            }
        }
    }

    private static class ViewHolder {
        TextView channelTitle;
        TextView content;
        TextView date;
        ImageView channelThumb;
        Comment comment;
        boolean imagesLoaded; // Tracks if we already triggered the load for this specific view
    }
}