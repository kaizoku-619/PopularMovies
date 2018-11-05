package net.kaizoku.popularmovies.database.model;


import android.os.Parcel;
import android.os.Parcelable;

public class MovieReview implements Parcelable {

    private String author;
    private String content;
    private String id;
    private String url;

    public MovieReview() {
    }

    public MovieReview(String author, String content, String id, String url) {
        super();
        this.author = author;
        this.content = content;
        this.id = id;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "MovieReview{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeString(this.id);
        dest.writeString(this.url);
    }

    protected MovieReview(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
        this.id = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel source) {
            return new MovieReview(source);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
}