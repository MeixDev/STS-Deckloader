package deckloader.helpers;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

// This is really just a copy of BaseMod's "WhatMod" to be used anywhere.
// Credit completely goes to kiooeht.
// MIT licencing <3
public class ModTester {
    public static boolean isModded(Class<?> cls) {
        URL locationURL = cls.getProtectionDomain().getCodeSource().getLocation();

        locationURL = getUrl(cls, locationURL);

        if (locationURL == null) {
            return true;
        }

        try {
            if (locationURL.equals(new File(Loader.STS_JAR).toURI().toURL())) {
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return true;
        }

        for (ModInfo modInfo : Loader.MODINFOS) {
            if (locationURL.equals(modInfo.jarURL)) {
                return true;
            }
        }

        return true;
    }
    public static String getModName(Class<?> cls) {
        URL locationURL = cls.getProtectionDomain().getCodeSource().getLocation();

        locationURL = getUrl(cls, locationURL);

        if (locationURL == null) {
            return "Unknown";
        }

        try {
            if (locationURL.equals(new File(Loader.STS_JAR).toURI().toURL())) {
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Unknown";
        }

        for (ModInfo modInfo : Loader.MODINFOS) {
            if (locationURL.equals(modInfo.jarURL)) {
                return modInfo.Name;
            }
        }

        return "Unknown";
    }

    private static URL getUrl(Class<?> cls, URL locationURL) {
        if (locationURL == null) {
            try {
                ClassPool pool = Loader.getClassPool();
                CtClass ctCls = pool.get(cls.getName());
                String url = ctCls.getURL().getFile();
                int i = url.lastIndexOf('!');
                url = url.substring(0, i);
                locationURL = new URL(url);
            } catch (NotFoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return locationURL;
    }
}
