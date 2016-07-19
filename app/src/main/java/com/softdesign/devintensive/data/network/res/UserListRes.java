package com.softdesign.devintensive.data.network.res;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserListRes {

    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("data")
    @Expose
    public List<UserData> data = new ArrayList<UserData>();

    public List<UserData> getData() {
        return data;
    }

    public class UserData{

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String first_name;
        @SerializedName("second_name")
        @Expose
        private String second_name;
        @SerializedName("__v")
        @Expose
        private int v;
        @SerializedName("repositories")
        @Expose
        private UserModelRes.Repositories repositories;
        @SerializedName("profileValues")
        @Expose
        private UserModelRes.ProfileValues profileValues;
        @SerializedName("publicInfo")
        @Expose
        private UserModelRes.PublicInfo publicInfo;
        @SerializedName("specialization")
        @Expose
        private String specialization;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getId() {
            return id;
        }

        public UserModelRes.PublicInfo getPublicInfo() {
            return publicInfo;
        }

        public UserModelRes.Repositories getRepositories() {
            return repositories;
        }

        public UserModelRes.ProfileValues getProfileValues() {
            return profileValues;
        }

        public String getFullName() {
            return first_name + " " + second_name;
        }
    }
}
