package com.jorgejag.cuidaportmanoperario;

public class Upload {

    private String comment;
    private String imageUrl;
    private String timestamp;

    public Upload(String comment, String imageUrl, String timestamp) {
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public Upload() {

    }



    public Upload(String comment, String imageUrl) {

        if (comment.trim().equals("")) {
            comment = "Sin comentario";
        }

        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
