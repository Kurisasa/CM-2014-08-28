package com.boha.coursemaker.util;


import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class Statics {

    /*
     * REMOTE URL - bohamaker back end - production
     */
    //
    public static final String URL = "http://bohamaker.com:51490/cm/";
    public static final String IMAGE_URL = "http://bohamaker.com:51490/coursemaker_images/";
    public static final String WEBSOCKET_URL = "ws://bohamaker.com:51490/cm/";
    /*
	 * LOCAL URL - pecanwood dungeon
	 */
//    public static final String URL = "http://192.168.1.111:8050/cm/";
//    public static final String IMAGE_URL = "http://192.168.1.111:8050/coursemaker_images/";
//    public static final String WEBSOCKET_URL = "ws://192.168.1.111:8050/cm/";


    public static final String AUTHOR_ENDPOINT = "wsauthor",
            ADMIN_ENDPOINT = "wsadmin",
            INSTRUCTOR_ENDPOINT = "wsinstructor",
            TRAINEE_ENDPOINT = "wstrainee",
            TEAM_ENDPOINT = "wsteam";
    //
    /**
     * Servlet URL strings
     */
    public static final String SERVLET_INSTRUCTOR = "instructor?JSON=";
    public static final String SERVLET_AUTHOR = "author?JSON=";
    public static final String SERVLET_TRAINEE = "trainee?JSON=";
    public static final String SERVLET_ADMIN = "admin?JSON=";
    public static final String SERVLET_PLATFORM = "platform?JSON=";
    public static final String SERVLET_LOG = "log?JSON=";
    public static final String SERVLET_TEAM = "team?JSON=";

    public static final String CRASH_REPORTS_URL = URL + "crash?";
    public static final String SESSION_ID = "webSocketSessionID";


    public static final String HEADER_FONT = "fonts/DroidSerif-BoldItalic.ttf";
    public static final String DROID_FONT = "DroidSans.ttf";
    public static final String DROID_FONT_BOLD = "DroidSans-Bold.ttf";

    //
    public static void setHeaderFont(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                Statics.HEADER_FONT);
        txt.setTypeface(font);
    }

    public static void setDroidFont(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                Statics.DROID_FONT);
        txt.setTypeface(font);
    }

    public static void setDroidFontBold(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "DroidSerif-Bold");
        txt.setTypeface(font);
    }

    public static void setRobotoFontBoldCondensed(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-BoldCondensed.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoFontRegular(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Regular.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoFontLight(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Light.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoFontBold(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Bold.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoItalic(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Italic.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoRegular(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Regular.ttf");
        txt.setTypeface(font);
    }

}
