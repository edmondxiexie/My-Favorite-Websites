/**
 * 08-672 Assignment 3.
 * @author Jiayi Xie
 * @id jiayix
 * 11/25/2016
 */

public class FavoriteBean {
	private int userId;
	private int favoriteId; 
	private String url;
	private String comments;
	private int clicks;
	
    public int getUserId() {
        return userId;
    }
    
    public int getFavoriteId() {
        return favoriteId;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getComments() {
        return comments;
    }
    
    public int getClicks() {
        return clicks;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
	
}
