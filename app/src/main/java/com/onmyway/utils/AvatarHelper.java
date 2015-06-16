package com.onmyway.utils;

import com.onmyway.R;

/**
 * Created by Federico on 16/06/2015.
 */
public class AvatarHelper
{
    public static final String MALE1 = "MALE1";
    public static final String MALE2 = "MALE2";
    public static final String MALE3 = "MALE3";
    public static final String MALE4 = "MALE4";
    public static final String MALE5 = "MALE5";
    public static final String MALE6 = "MALE6";
    public static final String MALE7 = "MALE7";
    public static final String MALE8 = "MALE8";
    public static final String MALE9 = "MALE9";
    public static final String MALE10 = "MALE10";

    public static final String FEMALE1 = "FEMALE1";
    public static final String FEMALE2 = "FEMALE2";
    public static final String FEMALE3 = "FEMALE3";
    public static final String FEMALE4 = "FEMALE4";
    public static final String FEMALE5 = "FEMALE5";
    public static final String FEMALE6 = "FEMALE6";
    public static final String FEMALE7 = "FEMALE7";
    public static final String FEMALE8 = "FEMALE8";
    public static final String FEMALE9 = "FEMALE9";
    public static final String FEMALE10 = "FEMALE10";

    public static final String[] AVATARS = {
            MALE1, MALE2, MALE3, MALE4, MALE5, MALE6, MALE7, MALE8, MALE9, MALE10,
            FEMALE1, FEMALE2, FEMALE3, FEMALE4, FEMALE5, FEMALE6, FEMALE7, FEMALE8, FEMALE9, FEMALE10
    };

    public static int GetDrawableAvatarFromString(String avatar){
        switch(avatar){
            default:
            case MALE1:
                return R.drawable.male1;
            case MALE2:
                return R.drawable.male2;
            case MALE3:
                return R.drawable.male3;
            case MALE4:
                return R.drawable.male4;
            case MALE5:
                return R.drawable.male5;
            case MALE6:
                return R.drawable.male6;
            case MALE7:
                return R.drawable.male7;
            case MALE8:
                return R.drawable.male8;
            case MALE9:
                return R.drawable.male9;
            case MALE10:
                return R.drawable.male10;

            case FEMALE1:
                return R.drawable.female1;
            case FEMALE2:
                return R.drawable.female2;
            case FEMALE3:
                return R.drawable.female3;
            case FEMALE4:
                return R.drawable.female4;
            case FEMALE5:
                return R.drawable.female5;
            case FEMALE6:
                return R.drawable.female6;
            case FEMALE7:
                return R.drawable.female7;
            case FEMALE8:
                return R.drawable.female8;
            case FEMALE9:
                return R.drawable.female9;
            case FEMALE10:
                return R.drawable.female10;
        }
    }
}
