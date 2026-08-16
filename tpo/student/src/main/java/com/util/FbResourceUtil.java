/*
 * Class Name : This
 * v1.0
 * This file is copyrighted by Uddanda Technologies.
 * Contents of this file can not be changed with out the permission Uddanda Technologies
 */
package com.util;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Uddanda Technologies
 */
@Component
public class FbResourceUtil {

    @Value("${defaultLocal}")
    private String defaultLocal;

    private static ResourceBundle labels;
    private static Locale locale;

    @PostConstruct
    public void init() {
        locale = new Locale(defaultLocal);
        labels = ResourceBundle.getBundle("com.student.fbresource", locale);
    }

    public static String getLabel(String key) {
        if (labels == null) {
            return key;
        }

        try {
            return labels.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        if (locale == null) {
            return;
        }

        if (FbResourceUtil.locale == null ) {

            FbResourceUtil.locale = locale;
            labels = ResourceBundle.getBundle(
                "com.fb.fbresource",
                locale
            );
        }
    }
}