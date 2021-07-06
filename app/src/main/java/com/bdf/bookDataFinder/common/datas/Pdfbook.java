package com.bdf.bookDataFinder.common.datas;

import com.google.gson.annotations.SerializedName;

public class Pdfbook {

    @SerializedName("dbid")
    public Long dbid;
    @SerializedName("bookname")
    public String bookname;
    @SerializedName("categoryid")
    public Long categoryid;
    @SerializedName("id")
    public String id;
    @SerializedName("filePath")
    public String filePath;
    @SerializedName("displayName")
    public String displayName;
    @SerializedName("canDownload")
    public boolean canDownload;
}
