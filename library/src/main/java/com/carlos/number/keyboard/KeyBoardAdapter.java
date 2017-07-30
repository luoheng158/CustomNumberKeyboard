package com.carlos.number.keyboard;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by carlos on 25/07/2017.
 */

public class KeyBoardAdapter extends RecyclerArrayAdapter<Key> {

    private static final int ITEM_TEXT = 0;
    private static final int ITEM_IMAGE = 5;

    public KeyBoardAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TEXT:
                return new TextViewHolder(parent);
            case ITEM_IMAGE:
                return new ImageViewHolder(parent);
        }
        throw new IllegalArgumentException("viewType err, check you logic!");
    }

    @Override
    public int getViewType(int position) {
        Key key = getItem(position);
        switch (key.getKeyType()) {
            case Key.TYPE_IMAGE:
                return ITEM_IMAGE;
            case Key.TYPE_TEXT:
                return ITEM_TEXT;
        }
        throw new IllegalArgumentException("check you logic!");
    }


    static class TextViewHolder extends BaseViewHolder<KeyText> {

        private TextView mKeyTextButton;
        private ImageView mKeyItemView;

        public TextViewHolder(ViewGroup parent) {
            super(parent, R.layout.keyboard_text_layout);
            mKeyTextButton = (TextView) itemView.findViewById(R.id.keyboard_text);
            mKeyItemView = (ImageView) itemView.findViewById(R.id.keyboard_item_bg);
        }

        @Override
        public void setData(KeyText data) {
            Resources resources = getContext().getResources();
            itemView.setBackgroundResource(data.getKeyBackgroundRes());
            mKeyItemView.setImageResource(data.getKeyItemBackgroundRes());
            mKeyTextButton.setText(data.getKeyText());
            mKeyTextButton.setTextColor(resources.getColor(data.getKeyTextColor()));
            mKeyTextButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, data.getKeyTextSize());
        }
    }

    static class ImageViewHolder extends BaseViewHolder<KeyImage> {

        private ImageView mKeyImageView;
        private ImageView mKeyItemView;

        public ImageViewHolder(ViewGroup parent) {
            super(parent, R.layout.keyboard_image_layout);
            mKeyImageView = (ImageView) itemView.findViewById(R.id.keyboard_image);
            mKeyItemView = (ImageView) itemView.findViewById(R.id.keyboard_item_bg);
        }

        @Override
        public void setData(KeyImage data) {
            itemView.setBackgroundResource(data.getKeyBackgroundRes());
            mKeyItemView.setImageResource(data.getKeyItemBackgroundRes());
            mKeyImageView.setImageResource(data.getImageRes());
        }
    }
}
