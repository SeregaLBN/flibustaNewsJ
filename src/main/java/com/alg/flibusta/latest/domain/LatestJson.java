package com.alg.flibusta.latest.domain;

public class LatestJson {
	private String title;
	private String updated;
	private String icon;
	private String logo;
	private NewItemJson[] entries;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public NewItemJson[] getEntries() {
		return entries;
	}

	public void setEntries(NewItemJson[] entries) {
		this.entries = entries;
	}
}
