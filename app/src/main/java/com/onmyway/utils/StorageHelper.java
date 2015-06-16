package com.onmyway.utils;

import android.app.Activity;
import android.content.Context;

import com.onmyway.R;
import com.onmyway.model.AppointmentBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class StorageHelper {
    private static void write(Context context, Serializable toWrite, String fileName){
        try {
            FileOutputStream stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(toWrite);
            objectStream.close();
            stream.close();
        }
        catch (Exception e){
        }
    }

    private static Object read(Context context, String fileName){
        try {
            FileInputStream stream = context.openFileInput(fileName);
            ObjectInputStream objectStream = new ObjectInputStream(stream);
            Object o = objectStream.readObject();
            objectStream.close();
            stream.close();

            return o;
        }
        catch (Exception e){
            return null;
        }
    }

    public static void writeAppointments(Context context, ArrayList<AppointmentBase> list){
        write(context, list, context.getString(R.string.appointmentsFileName));
    }

    public static ArrayList<AppointmentBase> readAppointments(Context context){
        return (ArrayList<AppointmentBase>) read(context, context.getString(R.string.appointmentsFileName));
    }
}
