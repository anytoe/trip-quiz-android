package com.tripquiz.webgeneric;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.tripquiz.persistance.ImageRepositoryInMemory;

public class ImageDownloader extends AsyncTask<Void, Integer, Void> {

	private String url;
	private ImageView img;
	private Bitmap image;
	private ImageLoaderListener listener;
	private ImageRepositoryInMemory imageRepository;
	private View viewToMakeVisible;

	public ImageDownloader(ImageRepositoryInMemory imageRepository) {
		this.imageRepository = imageRepository;
	}

	public void init(String url, ImageView img, ImageLoaderListener listener, View viewToMakeVisible) {
		this.url = url;
		this.img = img;
		this.listener = listener;
		this.viewToMakeVisible = viewToMakeVisible;
	}

	public void init(String url, ImageView img, ImageLoaderListener listener) {
		this.url = url;
		this.img = img;
		this.listener = listener;
	}

	public interface ImageLoaderListener {
		void onImageDownloaded(Bitmap bmp);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... arg0) {

		if (imageRepository != null && imageRepository.hasObject(url)) {
			image = imageRepository.loadObject(url);
		} else {
			image = getBitmapFromURL(url);
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {

		if (image != null)
			imageRepository.saveObject(image, url);

		if (listener != null)
			listener.onImageDownloaded(image);

		if (image != null)
			img.setImageBitmap(image);

		// img.setVisibility(View.VISIBLE);
		if (viewToMakeVisible != null)
			viewToMakeVisible.setVisibility(View.VISIBLE);

		super.onPostExecute(result);
	}

	public static Bitmap getBitmapFromURL(String link) {
		try {
			URL url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);

			return myBitmap;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
