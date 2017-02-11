/**
 * 08-672 Assignment 3.
 * @author Jiayi Xie
 * @id jiayix
 * 11/25/2016
 */

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class FavoriteForm {
	private String url;
	private String comments;
	private String favoriteId;

	private String button;

	public FavoriteForm(HttpServletRequest request) {
		url = request.getParameter("url");
		comments = request.getParameter("comments");
		button = request.getParameter("favoritebutton");
		favoriteId = request.getParameter("favoriteId");
	}
	
	public String getUrl() { 
	    return url; 
    }
	
	public String getComments() { 
	    return comments;
    }
	
	public String getFavoriteId() {
		return favoriteId;
	}
	
	public String getButton() {
		return button;
	}

	public boolean isPresent() { 
	    return button != null;
    }
	
	public List<String> getValidationErrors() {
		List<String> errors = new ArrayList<String>();

		if(button != null && button.equals("Add Favorite")) {
			if (url == null || url.length() == 0) {
				errors.add("Url is required");
			}
		}
		return errors;
	}

}
