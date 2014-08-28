package com.boha.coursemaker.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Used to load fonts from the assets directory
 *
 * @author Catalin Prata
 *         Date: 04/22/13
 */
public class FontUtil {
 
    // font file name
    public static final String FONT_ANGRY_BIRDS = "angrybirds-regular.ttf";
 
    // store the opened typefaces(fonts)
    private static final Hashtable<String, Typeface>  mCache = new Hashtable<String, Typeface>();
 
    public static Typeface getRobotoBold(Context ctx) {
		Typeface tf = loadFontFromAssets("Roboto-Bold.ttf", ctx);
		return tf;
	}
    public static Typeface getRobotoItalic(Context ctx) {
		Typeface tf = loadFontFromAssets("Roboto-Italic.ttf", ctx);
		return tf;
	}
    public static Typeface getRobotoRegular(Context ctx) {
		Typeface tf = loadFontFromAssets("Roboto-Regular.ttf", ctx);
		return tf;
	}
    public static Typeface getRobotoBoldItalic(Context ctx) {
		Typeface tf = loadFontFromAssets("Roboto-BoldItalic.ttf", ctx);
		return tf;
	}
    
    /**
     * Load the given font from assets
     *
     * @param fontName font name
     * @return Typeface object representing the font painting
     * 
     */
    
    public static Typeface loadFontFromAssets(String fontName, Context ctx) {
        // make sure we load each font only once
        synchronized (mCache) {
 
            if (! mCache.containsKey(fontName)) {
                Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), fontName);
                mCache.put(fontName, typeface);
            }
 
            return mCache.get(fontName);
 
        }
 
    }
 
}