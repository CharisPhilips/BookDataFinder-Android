package com.bdf.bookDataFinder.common.treeview;

import androidx.annotation.NonNull;

import com.bdf.bookDataFinder.common.treeview.bean.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tlh on 2016/10/1 :)
 */

public class TreeNode<T extends LayoutItemType> implements Cloneable {
    private T content;
    private TreeNode parent;
    private List<TreeNode> childList;
    private boolean isExpand;
    private boolean isLocked;
    private boolean isSelected;
    //the tree high
    private int height = UNDEFINE;

    private static final int UNDEFINE = -1;

    public TreeNode(@NonNull T content) {
        this.content = content;
        this.childList = new ArrayList<>();
        isSelected = false;
    }

    public int getHeight() {
        if (isRoot())
            height = 0;
        else if (height == UNDEFINE)
            height = parent.getHeight() + 1;
        return height;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean haveChild() {
        return (childList != null && !childList.isEmpty());
    }

    public boolean isLeaf() {
        if(childList == null || childList.isEmpty()) {
            return true;
        }
        else {
            if(childList != null && childList.size() > 0) {
                boolean bExistFolder = false;
                int nChildSize = childList.size();
                for(int i = 0; i < nChildSize; i++) {
                    TreeNode child = childList.get(i);
                    if(!child.isFile()) {
                        bExistFolder = true;
                        break;
                    }
                }
                return !bExistFolder;
            }
        }
        return false;
    }

    public boolean isFile() {
        return content instanceof File;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public List<TreeNode> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeNode> childList) {
        this.childList.clear();
        for (TreeNode treeNode : childList) {
            addChild(treeNode);
        }
    }

    public TreeNode addChild(TreeNode node) {
        if (childList == null)
            childList = new ArrayList<>();
        childList.add(node);
        node.parent = this;
        return this;
    }

    public boolean toggle() {
        isExpand = !isExpand;
        return isExpand;
    }

    public void collapse() {
        if (isExpand) {
            isExpand = false;
        }
    }

    public void select() {
        this.isSelected = true;
        TreeNode parent = this.parent;
        if(parent!=null) {
            parent.select();
        }
    }

    public void deselect() {
        this.isSelected = false;
        TreeNode parent = this.parent;
        if(parent!=null) {
            parent.deselect();
        }
    }

    public boolean isSelect() {
        return isSelected;
    }

    public void collapseAll() {
        if (childList == null || childList.isEmpty()) {
            return;
        }
        for (TreeNode child : this.childList) {
            child.collapseAll();
        }
    }

    public void expand() {
        if (!isExpand) {
            isExpand = true;
        }
    }

    public void expandAll() {
        expand();
        if (childList == null || childList.isEmpty()) {
            return;
        }
        for (TreeNode child : this.childList) {
            child.expandAll();
        }
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public TreeNode getParent() {
        return parent;
    }

    public TreeNode<T> lock() {
        isLocked = true;
        return this;
    }

    public TreeNode<T> unlock() {
        isLocked = false;
        return this;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "content=" + this.content +
                ", parent=" + (parent == null ? "null" : parent.getContent().toString()) +
                ", childList=" + (childList == null ? "null" : childList.toString()) +
                ", isExpand=" + isExpand +
                '}';
    }

    @Override
    public TreeNode<T> clone() throws CloneNotSupportedException {
        TreeNode<T> clone = new TreeNode<>(this.content);
        clone.isExpand = this.isExpand;
        return clone;
    }
}
