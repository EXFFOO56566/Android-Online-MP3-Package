package com.vpapps.item;

import java.io.Serializable;

public class ItemPlayList implements Serializable{

	private String id, name;

	public ItemPlayList(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
