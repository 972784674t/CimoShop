package com.example.cimoshop.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author 谭海山
 */
public class Pixabay implements Parcelable {

    /**
     * total : 搜索总数
     * totalHits : 当前返回
     * List<HitsBean> ： 图片结果集
     */
    private int total;
    private int totalHits;
    private List<HitsBean> hits;


    private Pixabay(Parcel in) {
        total = in.readInt();
        totalHits = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(total);
        dest.writeInt(totalHits);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pixabay> CREATOR = new Creator<Pixabay>() {
        @Override
        public Pixabay createFromParcel(Parcel in) {
            return new Pixabay(in);
        }

        @Override
        public Pixabay[] newArray(int size) {
            return new Pixabay[size];
        }
    };

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public List<HitsBean> getHits() {
        return hits;
    }

    public void setHits(List<HitsBean> hits) {
        this.hits = hits;
    }

    public static class HitsBean implements Parcelable{
        /**
         * id  图片ID
         * pageURL  图片页面网址
         * type  photo
         * tags  标签
         * previewURL  预览网址
         * previewWidth
         * previewHeight
         * webformatURL  图片网络格式网址
         * webformatWidth
         * webformatHeight
         * largeImageURL  大图片网址
         * imageWidth
         * imageHeight
         * imageSize
         * views  点击量
         * downloads 下载量
         * favorites 收藏量
         * likes 点赞量
         * comments 评论量
         * user_id 上传者id
         * user  上传者
         * userImageURL 上传者头像
         */

        private int id;
        private String pageURL;
        private String type;
        private String tags;
        private String previewURL;
        private int previewWidth;
        private int previewHeight;
        private String webformatURL;
        private int webformatWidth;
        private int webformatHeight;
        private String largeImageURL;
        private int imageWidth;
        private int imageHeight;
        private int imageSize;
        private int views;
        private int downloads;
        private int favorites;
        private int likes;
        private int comments;
        private int user_id;
        private String user;
        private String userImageURL;

        HitsBean(Parcel in) {
            id = in.readInt();
            pageURL = in.readString();
            type = in.readString();
            tags = in.readString();
            previewURL = in.readString();
            previewWidth = in.readInt();
            previewHeight = in.readInt();
            webformatURL = in.readString();
            webformatWidth = in.readInt();
            webformatHeight = in.readInt();
            largeImageURL = in.readString();
            imageWidth = in.readInt();
            imageHeight = in.readInt();
            imageSize = in.readInt();
            views = in.readInt();
            downloads = in.readInt();
            favorites = in.readInt();
            likes = in.readInt();
            comments = in.readInt();
            user_id = in.readInt();
            user = in.readString();
            userImageURL = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(pageURL);
            dest.writeString(type);
            dest.writeString(tags);
            dest.writeString(previewURL);
            dest.writeInt(previewWidth);
            dest.writeInt(previewHeight);
            dest.writeString(webformatURL);
            dest.writeInt(webformatWidth);
            dest.writeInt(webformatHeight);
            dest.writeString(largeImageURL);
            dest.writeInt(imageWidth);
            dest.writeInt(imageHeight);
            dest.writeInt(imageSize);
            dest.writeInt(views);
            dest.writeInt(downloads);
            dest.writeInt(favorites);
            dest.writeInt(likes);
            dest.writeInt(comments);
            dest.writeInt(user_id);
            dest.writeString(user);
            dest.writeString(userImageURL);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<HitsBean> CREATOR = new Creator<HitsBean>() {
            @Override
            public HitsBean createFromParcel(Parcel in) {
                return new HitsBean(in);
            }

            @Override
            public HitsBean[] newArray(int size) {
                return new HitsBean[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPageURL() {
            return pageURL;
        }

        public void setPageURL(String pageURL) {
            this.pageURL = pageURL;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getPreviewURL() {
            return previewURL;
        }

        public void setPreviewURL(String previewURL) {
            this.previewURL = previewURL;
        }

        public int getPreviewWidth() {
            return previewWidth;
        }

        public void setPreviewWidth(int previewWidth) {
            this.previewWidth = previewWidth;
        }

        public int getPreviewHeight() {
            return previewHeight;
        }

        public void setPreviewHeight(int previewHeight) {
            this.previewHeight = previewHeight;
        }

        public String getWebformatURL() {
            return webformatURL;
        }

        public void setWebformatURL(String webformatURL) {
            this.webformatURL = webformatURL;
        }

        public int getWebformatWidth() {
            return webformatWidth;
        }

        public void setWebformatWidth(int webformatWidth) {
            this.webformatWidth = webformatWidth;
        }

        public int getWebformatHeight() {
            return webformatHeight;
        }

        public void setWebformatHeight(int webformatHeight) {
            this.webformatHeight = webformatHeight;
        }

        public String getLargeImageURL() {
            return largeImageURL;
        }

        public void setLargeImageURL(String largeImageURL) {
            this.largeImageURL = largeImageURL;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public int getImageSize() {
            return imageSize;
        }

        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public int getDownloads() {
            return downloads;
        }

        public void setDownloads(int downloads) {
            this.downloads = downloads;
        }

        public int getFavorites() {
            return favorites;
        }

        public void setFavorites(int favorites) {
            this.favorites = favorites;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getUserImageURL() {
            return userImageURL;
        }

        public void setUserImageURL(String userImageURL) {
            this.userImageURL = userImageURL;
        }
    }



}
