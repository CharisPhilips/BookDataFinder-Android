package com.bdf.bookDataFinder.common.datas;

import com.bdf.bookDataFinder.common.treeview.TreeNode;
import com.bdf.bookDataFinder.common.treeview.bean.Dir;
import com.bdf.bookDataFinder.common.treeview.bean.File;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class CategoryData {

    @SerializedName("id")
    public Long id;
    @SerializedName("categoryname")
    public String categoryname;
    @SerializedName("categorylevel")
    public Integer categorylevel;
    @SerializedName("categoryparentid")
    public Long categoryparentid;
    @SerializedName("categoryorder")
    public Integer categoryorder;
    @SerializedName("category")
    public Vector<CategoryData> category = new Vector<CategoryData>();

    public void addSubCategory(CategoryData sub) {
        this.category.add(sub);
    }

    public static CategoryData fromJson(Object obj) throws JSONException {

        CategoryData category = null;
        if(obj!=null && obj instanceof JSONObject) {
            category = new CategoryData();
            String name = (String) (((JSONObject) obj).get("categoryname"));
            category.categoryname = name;
            long id = ((Number) (((JSONObject) obj).get("id"))).longValue();
            category.id = id;
            if(((JSONObject) obj).has("category")) {
                JSONArray jsonArray = ((JSONObject) obj).getJSONArray("category");
                int nLength = jsonArray.length();
                if (nLength > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        CategoryData sub = CategoryData.fromJson(jo_inside);
                        category.addSubCategory(sub);
                    }
                }
            }
        }
        return category;
    }

   public static TreeNode toTreeNode(CategoryData categoryData) {
       TreeNode resNode = null;
       if(categoryData!=null && categoryData.categoryname!=null) {
           resNode = new TreeNode(new Dir(categoryData.categoryname, categoryData.id));
           int nSubCateLenth = categoryData.category.size();
           if(nSubCateLenth > 0) {
               for(int i = 0; i < nSubCateLenth; i++) {
                   TreeNode subTreeNode = toTreeNode(categoryData.category.get(i));
                   resNode.addChild(subTreeNode);
               }
           }
           else{
//               for(int i = 1; i <= (Math.random() * 10 + 1); i++) {
//                   resNode.addChild(new TreeNode(new File("pdf book" + String.valueOf(i))));
//               }
           }
       }
       return resNode;
   }
}
