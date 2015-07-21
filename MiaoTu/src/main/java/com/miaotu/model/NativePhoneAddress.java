package com.miaotu.model;

/**
 * Created by Jayden on 2015/5/29.
 * 通讯录
 */
public class NativePhoneAddress {
    private String name;
    private String number;
    private String sortLetters;
    private boolean isSelected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}

