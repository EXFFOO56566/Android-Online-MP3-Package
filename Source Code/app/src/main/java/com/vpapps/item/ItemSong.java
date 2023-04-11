package com.vpapps.item;

import android.graphics.Bitmap;

public class ItemSong {

	private String id, CategoryId, CategoryName, artist, Mp3Url,imageBig, imageSmall, Mp3Name, Duration, Description, totalRate, averageRating="0", userRating="", views, downloads;
	private Bitmap image;

	public ItemSong(String id, String CategoryId, String CategoryName, String artist, String Mp3Url,String imageBig, String imageSmall, String Mp3Name, String Duration, String Description, String totalRate, String averageRating, String views, String downloads) {
		this.id = id;
		this.CategoryId = CategoryId;
		this.CategoryName = CategoryName;
		this.artist = artist;
		this.Mp3Url = Mp3Url;
		this.imageBig = imageBig;
		this.imageSmall = imageSmall;
		this.Mp3Name = Mp3Name;
		this.Duration = Duration;
		this.Description = Description;
		this.totalRate = totalRate;
		this.averageRating = averageRating;
		this.views = views;
		this.downloads = downloads;
	}

	public ItemSong(String id, String artist, String Mp3Url, Bitmap image, String Mp3Name, String Duration, String Description) {
		this.id = id;
		this.artist = artist;
		this.Mp3Url = Mp3Url;
		this.image = image;
		this.Mp3Name = Mp3Name;
		this.Duration = Duration;
		this.Description = Description;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCategoryId() {
		return CategoryId;
	}

	public String getArtist() {
		return artist;
	}

	public void setCategoryId(String categoryid) {
		this.CategoryId = categoryid;
	}
	
	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryname) {
		this.CategoryName = categoryname;
	}
	
	public String getMp3Url() {
		return Mp3Url;
	}

	public void setMp3Url(String mp3url) {
		this.Mp3Url = mp3url;
	}
	
	public String getImageBig() {
		return imageBig;
	}

	public void setImageBig(String imageBig) {
		this.imageBig = imageBig;
	}

	public String getImageSmall() {
		return imageSmall;
	}

	public void setImageSmall(String imageSmall) {
		this.imageSmall = imageSmall;
	}
	
	public String getMp3Name() {
		return Mp3Name;
	}

	public void setMp3Name(String mp3name) {
		this.Mp3Name = mp3name;
	}
	
	public String getDuration() {
		return Duration;
	}

	public void setDuration(String duration) {
		this.Duration = duration;
	}
	
	public String getDescription() {
		return Description;
	}

	public void setDescription(String desc) {
		this.Description = desc;
	}

	public Bitmap getBitmap() {
		return image;
	}

	public String getTotalRate() {
		return totalRate;
	}

	public String getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(String averageRating) {
		this.averageRating = averageRating;
	}

	public void setTotalRate(String totalRate) {
		this.totalRate = totalRate;
	}

	public String getUserRating() {
		return userRating;
	}

	public void setUserRating(String userRating) {
		this.userRating = userRating;
	}

	public String getViews() {
		return views;
	}

	public String getDownloads() {
		return downloads;
	}

	public void setDownloads(String downloads) {
		this.downloads = downloads;
	}
}