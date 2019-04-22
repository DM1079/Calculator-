package com.alin.calculator.Core;

import java.util.Locale;

public class i18n {

    String lang= Locale.getDefault().getLanguage();

    public String getDefaultLang(){
    return lang;
}

//根据读取的系统语言设定程序界面语言
//返回false--操作失败 true--操作成功
public boolean setDefaultLang(){
    switch(lang){
        case "es":
        case "zh":
        default:
    }


    return false;
}
    public String localeToString(Locale l){
        if(l.equals(Locale.SIMPLIFIED_CHINESE))
            return "zh";
        if(l.equals(Locale.ENGLISH))
            return "es";
        return null;
    }
}
