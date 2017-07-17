package com.example.kstoflet.rsspodcast;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by kstoflet on 4/3/2017.
 */

public class RSSFeedModel implements Parcelable {

    public String feedTitle;
    public String feedLink;
    public Date feedDate;
    public String feedDescription;
    public String feedDownload;
    public long feedLength;

    public RSSFeedModel(String title, String link, Date date, String description, String download, long length) {
        feedTitle = title;
        feedLink = link;
        feedDate = date;
        feedDescription = description;
        feedDownload = download;
        feedLength = length;
    }

    protected RSSFeedModel(Parcel in) {
        feedTitle = in.readString();
        feedLink = in.readString();
        feedDate = (java.util.Date) in.readSerializable();
        feedDescription = in.readString();
        feedDownload = in.readString();
        feedLength = in.readLong();
    }

    public static final Creator<RSSFeedModel> CREATOR = new Creator<RSSFeedModel>() {
        @Override
        public RSSFeedModel createFromParcel(Parcel in) {
            return new RSSFeedModel(in);
        }

        @Override
        public RSSFeedModel[] newArray(int size) {
            return new RSSFeedModel[size];
        }
    };

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(feedTitle);
        dest.writeString(feedLink);
        dest.writeSerializable(feedDate);
        dest.writeString(feedDescription);
        dest.writeString(feedDownload);
        dest.writeLong(feedLength);
    }

    public String getFeedTitle() {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }

    public String getFeedLink() {
        return feedLink;
    }

    public void setFeedLink(String feedLink) {
        this.feedLink = feedLink;
    }

    public Date getFeedDate() {
        return feedDate;
    }

    public void setFeedDate(Date feedDate) {
        this.feedDate = feedDate;
    }

    public String getFeedDescription() {
        return feedDescription;
    }

    public void setFeedDescription(String feedDescription) {
        this.feedDescription = feedDescription;
    }

    public String getFeedDownload() {
        return feedDownload;
    }

    public void setFeedDownload(String feedDownload) {
        this.feedDownload = feedDownload;
    }

    public long getFeedLength() {
        return feedLength;
    }

    public void setFeedLength(long feedLength) {
        this.feedLength = feedLength;
    }
}
