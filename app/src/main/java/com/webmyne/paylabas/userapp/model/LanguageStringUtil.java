package com.webmyne.paylabas.userapp.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.webmyne.paylabas.userapp.base.PrefUtils;

/**
 * Created by Android on 10-12-2014.
 */
public class LanguageStringUtil {
    static boolean isEnglisSelected;
    static String newvalue;

    public static String languageString(Context contex,String value) {
        isEnglisSelected = PrefUtils.isEnglishSelected(contex);
        newvalue = value;

        if(isEnglisSelected) {
            // for france
            //ch = ",";
            newvalue = newvalue.replaceAll("\\.", ",");

        }
        else {
            // for english
            //ch = ".";
            newvalue = newvalue.replaceAll("\\,", ".");
        }

        return newvalue;
    }


}
