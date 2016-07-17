package in.lemonco.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie Class
 */
public class Movie implements Parcelable {
    private String mOriginalTitle;
    private String mOverview;
    private String mUserRating;
    private String mReleaseDate;
    private String mPosterPath;

    public Movie(String originalTitle,String overview,String userRating,String releaseDate,String posterPath){
        this.mOriginalTitle = originalTitle;
        this.mOverview = overview;
        this.mUserRating = userRating;
        this.mReleaseDate = releaseDate;
        this.mPosterPath = posterPath;
    }
    //Constructor that takes a parcel and gives us a populated Movie object
    private Movie(Parcel in){
        mOriginalTitle = in.readString();
        mOverview = in.readString();
        mUserRating = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
    }
    //Methods to implement the parceable interface, for moving object across activities using intent
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mOriginalTitle);
        out.writeString(mOverview);
        out.writeString(mUserRating);
        out.writeString(mReleaseDate);
        out.writeString(mPosterPath);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public String getmOverview() {
        return mOverview;
    }

    public String getmUserRating() {
        return mUserRating;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public String getmPosterPath() {
            final String BASE_URL = "http://image.tmdb.org/t/p/";
            final String SIZE = "w185";
            return BASE_URL+SIZE+"/"+mPosterPath ;

    }
}
