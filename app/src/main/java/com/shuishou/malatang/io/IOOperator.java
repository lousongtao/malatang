package com.shuishou.malatang.io;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shuishou.malatang.InstantValue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/6/8.
 */

public class IOOperator {

    public static void saveServerURL(String url){
        FileWriter writer = null;
        try {
            writer = new FileWriter(InstantValue.FILE_SERVERURL);
            writer.write(url);
            writer.close();
        } catch (IOException e) {
            Log.e("IOException", "error to save ServerURL +\n"+e.getStackTrace());
        } finally {
            try {
                if (writer != null)
                    writer.close();
            }catch (IOException e) {}
        }
    }

    public static void saveDishName(String dishName){
        FileWriter writer = null;
        try {
            writer = new FileWriter(InstantValue.FILE_DISHNAME);
            writer.write(dishName);
            writer.close();
        } catch (IOException e) {
            Log.e("IOException", "error to save DishName +\n"+e.getStackTrace());
        } finally {
            try {
                if (writer != null)
                    writer.close();
            }catch (IOException e) {}
        }
    }

    public static String loadServerURL(){
        File file = new File(InstantValue.FILE_SERVERURL);
        if (!file.exists())
            return "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            return line;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException", "error to load ServerURL +\n"+e.getStackTrace());
        } finally {
            try {
                if (in != null)
                    in.close();
            }catch (IOException e) {}
        }
        return null;
    }

    public static String loadDishName(){
        File file = new File(InstantValue.FILE_DISHNAME);
        if (!file.exists())
            return "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            return line;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException", "error to load Dish name from local file +\n"+e.getStackTrace());
        } finally {
            try {
                if (in != null)
                    in.close();
            }catch (IOException e) {}
        }
        return null;
    }
}
