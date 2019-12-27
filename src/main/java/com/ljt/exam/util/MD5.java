package com.ljt.exam.util;

import org.apache.poi.ss.formula.functions.T;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密类
 * @描述：密码加密
 * */

public final class MD5 {
    /**
     * md5加密
     *
     * */
    public final static String md5(String s){
        char hexDigits[] = {'0','1','2','3','4','5','6','7','8'
                ,'9','A','B','C','D','E','F'};
        try {
        byte[] btInput = s.getBytes();
        MessageDigest md5 = null;
        md5 = MessageDigest.getInstance("MD5");
        md5.update(btInput);
        byte[] digest = md5.digest();
        int j =digest.length;
        char str[] = new char[j*2];
        int k=0;
        for (int i=0;i<j;i++){
            byte b = digest[i];
            str[k++]=hexDigits[b>>>4&0xf];
            str[k++] = hexDigits[b & 0xf];

        }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return  null;
        }
    }
    public static void main(String[] args) {
        System.out.println(MD5.md5("qexz123456"));
    }

}
