package com.onmyway.utils;

import android.os.AsyncTask;
import android.support.v4.app.NavUtils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.onmyway.model.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marco on 15/05/2015.
 */

public class ServiceGateway {

    public final static String baseAPI = "http://onmywayapp.azurewebsites.net/api/";

    // api/users/login
    public static void LoginAsync(String phoneNumber, ApiCallback<User> apiCallback)
    {
        new ApiTask<User>("users/login?phoneNumber=" + phoneNumber, User.class, apiCallback).execute();
    }

    // api/appointments/base/get
    public static void GetAppointmentsPreviewAsync(String phoneNumber, ApiCallback<ArrayList<AppointmentBase>> apiCallback)
    {
        new ApiTask<User>("users/login?phoneNumber=" + phoneNumber, AppointmentBase[].class, apiCallback).execute();
    }

    // api/appointments/add
    public static void UploadAppointmentAsync(String phoneNumber, Appointment appointment, ApiCallback<Appointment> apiCallback)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("phoneNumber", phoneNumber);

        //TODO
    }

    // api/appointments/delete
    public static void RemoveAppointmentAsync(String phoneNumber, String appointmentId, ApiCallback<Boolean> apiCallback)
    {
        //TODO
    }

    // api/appointments/get
    public static void GetFullAppointmentAsync(String appointmentId, ApiCallback<Appointment> apiCallback)
    {
        //TODO
    }

    // api/appointments/users/status
    public static void GetUsersStatusAsync(String appointmentId, ApiCallback<ArrayList<UserStatus>> apiCallback)
    {
        //TODO
    }

    // api/users/status
    public static void UpdateUserStatusAsync(String phoneNumber, String status, ApiCallback<Boolean> apiCallback)
    {
        //TODO
    }

    private static class ApiTask<T> extends AsyncTask<Void, Void, T> {

        private Class deserializationClass;
        private String relativeUrl;
        private ApiCallback apiCallback;

        public ApiTask(String relativeUrl, Class deserializationClass, ApiCallback apiCallback) {
            this.relativeUrl = relativeUrl;
            this.deserializationClass = deserializationClass;
            this.apiCallback = apiCallback;
        }

        @Override
        protected T doInBackground(Void... params) {
            RestResponse<T> result;

            HttpURLConnection con = null;
            try {
                URL url = new URL(baseAPI + relativeUrl);
                con = (HttpURLConnection) url.openConnection();

                /*if (con.getResponseCode() != 200) {
                    throw new RuntimeException("HTTP error code : "+ con.getResponseCode());
                }*/

                BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
                result = (RestResponse<T>) new Gson().fromJson(br, deserializationClass);
            } catch (Exception e) {
                result = null;
            } finally {
                if (con != null)
                    con.disconnect();
            }

            if(result != null)
                return result.Data;
            else
                return null;
        }

        @Override
        protected void onPostExecute(T result) {
            apiCallback.OnComplete(result);
        }

        private class RestResponse<T>
        {
            public String Error;
            public T Data;
        }
    }
}
