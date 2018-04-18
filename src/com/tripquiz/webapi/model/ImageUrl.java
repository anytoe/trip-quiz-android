package com.tripquiz.webapi.model;

import java.io.Serializable;
import java.util.StringTokenizer;

public class ImageUrl implements Serializable {

	private static final long serialVersionUID = -8315660681874790350L;

	private String url;
	private int width;
	private int height;

	private static double GoldenRatio = 1.6180339887;

	public ImageUrl(String url, String size) {
		super();
		this.url = url;

		StringTokenizer tokenizer = new StringTokenizer(size, "x");
		width = Integer.parseInt(tokenizer.nextToken());
		height = Integer.parseInt(tokenizer.nextToken());
	}

	public String getUrl(int width, int height) {
		return url.replace("{0}", width + "x" + height);
	}

	public String getUrlGoldenRatioByWidth(int width) {
		return url.replace("{0}", width + "x" + Math.round(width / GoldenRatio));
	}

	public String getUrlGoldenRatioByHeight(int height) {
		return url.replace("{0}", Math.round(height * GoldenRatio) + "x" + height);
	}

	public String getUrl() {
		return url.replace("{0}", width + "x" + height);
	}

	public String getUrlByWidth(int width) {
		return url.replace("{0}", width + "x" + height * width / this.width);
	}

	public String getUrlByWidth(int width, int widthRatio, int heightRatio) {
		int height = Math.round(((float) width / widthRatio * heightRatio));
		return url.replace("{0}", width + "x" + height * width / this.width);
	}

	public String getUrlByHeight(int height) {
		return url.replace("{0}", width * height / this.height + "x" + height);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageUrl other = (ImageUrl) obj;
		if (height != other.height)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (width != other.width)
			return false;
		return true;
	}

}
