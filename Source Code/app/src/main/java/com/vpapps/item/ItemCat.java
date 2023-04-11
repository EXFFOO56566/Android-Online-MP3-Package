package com.vpapps.item;

public class ItemCat {
	
	private String id, name, image;

	public ItemCat(String id, String name, String image) {
		this.id = id;
		this.name = name;
		this.image = image;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}
}
