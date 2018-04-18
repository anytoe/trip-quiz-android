package com.tripquiz.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.ImageView;

import com.tripquiz.R;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webgeneric.ImageDownloader;

public class LayoutChangedListener implements OnLayoutChangeListener {

	private final Context context;
	private final Challenge challenge;
	private final ImageDownloader imageDownload;
	private final ImageView imageView;
	private final View contentView;
	private final View container;

	public LayoutChangedListener(Context context, ImageDownloader imageDownload, Challenge challenge, ImageView imageView, View contentView, View container) {
		this.context = context;
		this.challenge = challenge;
		this.imageDownload = imageDownload;
		this.imageView = imageView;
		this.contentView = contentView;
		this.container = container;
	}

	@Override
	public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		if (challenge.hasImage()) {
			if (v.getWidth() > 0 && v.getHeight() > 0) {
				String imageUrl = challenge.getImageUrl().getUrl(v.getWidth(), v.getHeight());
				imageDownload.init(imageUrl, imageView, null, v);
				imageDownload.execute();
			}
		} else {
			contentView.setVisibility(View.VISIBLE);
			container.setBackgroundColor(context.getResources().getColor(R.color.transparent_medium));
			contentView.setBackgroundColor(challenge.getColor());
			// contentView.setBackgroundColor(context.getResources().getColor(R.color.transparent_medium));
			// Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.location_imageplaceholder_background);
			// float scale = (float) v.getWidth() / bmp.getWidth();
			// int newWidth = Math.round(scale * bmp.getWidth());
			// int newHeight = Math.round(scale * bmp.getHeight());
			// bmp = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
			// bmp = Bitmap.createBitmap(bmp, 0, 0, v.getWidth(), v.getHeight());
			// imageView.setImageBitmap(bmp);
		}
		contentView.removeOnLayoutChangeListener(this);
	}
}