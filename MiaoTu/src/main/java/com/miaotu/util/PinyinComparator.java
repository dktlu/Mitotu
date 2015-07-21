package com.miaotu.util;

import com.miaotu.model.NativePhoneAddress;

import java.util.Comparator;

/**
 * Created by Jayden on 2015/7/21.
 */
public class PinyinComparator implements Comparator<NativePhoneAddress> {
    @Override
    public int compare(NativePhoneAddress nativePhoneAddress, NativePhoneAddress t1) {
        if (t1.getSortLetters().equals("#")) {
            return -1;
        } else if (nativePhoneAddress.getSortLetters().equals("#")) {
            return 1;
        } else {
            return nativePhoneAddress.getSortLetters().compareTo(t1.getSortLetters());
        }
    }
}
