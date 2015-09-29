package com.alg.flibusta.latest.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewItemJson {
    private String updated;
    private int idTagBook;
    private String title;
    private String author;
    private String[] categories;
    private String content;

    public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public int getIdTagBook() {
		return idTagBook;
	}
	public void setIdTagBook(int idTagBook) {
		this.idTagBook = idTagBook;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String[] getCategories() {
		return categories;
	}
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

    public NewItems cast() throws ParseException {
    	NewItems ni = new NewItems();
    	ni.setAuthor(this.getAuthor());
    	ni.setCategories(String.join("; ", this.getCategories()));
    	ni.setContent(this.getContent());
    	ni.setIdTagBook(this.getIdTagBook());
    	ni.setTitle(this.getTitle());
    	ni.setUpdated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH).parse(this.getUpdated()));
    	return ni;
	}
}
