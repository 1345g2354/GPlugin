package com.xuyongchao.gplugin.utils;

import android.app.Activity;
import android.util.Log;

import com.xuyongchao.gplugin.MainActivity_ViewBinding;
import com.xuyongchao.gplugin.activity.MainActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BindUtils {
    public static void bind(MainActivity activity) {
        //            String className=activity.getClass().getName();
//            Class c = Class.forName(className + "_ViewBinding");
//            Constructor constructor = c.getConstructor(activity.getClass());
//            constructor.newInstance(activity);
        new MainActivity_ViewBinding(activity);
    }
}
