package com.onmyway.utils;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.onmyway.model.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Marco on 15/05/2015.
 */

public class ServiceGateway {

    public final static String baseAPI = "http://onmywayapp.azurewebsites.net/api/";
    //public final static String baseAPI = "http://localhost:1192/api/";

    // api/users/login
    public static void LoginAsync(String phoneNumber, ApiCallback<User> apiCallback)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phoneNumber", phoneNumber));

        new ApiTask<User>("user/login", ApiTask.RestMethod.POST, params, apiCallback).execute();
    }

    // api/appointments/base/get
    public static void GetAppointmentsPreviewAsync(String phoneNumber, ApiCallback<ArrayList<AppointmentBase>> apiCallback)
    {
        new ApiTask<ArrayList<AppointmentBase>>("user/appointments?phoneNumber=" + phoneNumber, ApiTask.RestMethod.GET, null, apiCallback).execute();
    }

    // api/appointments/add
    public static void UploadAppointmentAsync(String phoneNumber, Appointment appointment, ApiCallback<Appointment> apiCallback)
    {
        //format the dates
        appointment.calendarsToStrings();

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

        public enum RestMethod{
            GET,
            POST
        }

        private String relativeUrl;
        private ApiCallback apiCallback;
        private RestMethod method;
        private List<NameValuePair> postParams;

        public ApiTask(String relativeUrl, RestMethod method, @Nullable List<NameValuePair> postParams, ApiCallback apiCallback) {
            this.relativeUrl = relativeUrl;
            this.apiCallback = apiCallback;
            this.method = method;
            this.postParams = postParams;
        }

        @Override
        protected T doInBackground(Void... params) {
            if(method == RestMethod.GET)
                return executeGet();
            else
                return executePost();
        }

        private T executeGet() {
            RestResponse<T> result;

            //ClientRequest req = new ClientRequest(baseAPI);

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

        private T executePost() {
            RestResponse<T> result;

            //ClientRequest req = new ClientRequest(baseAPI);

            HttpURLConnection con = null;
            try {
                URL url = new URL(baseAPI + relativeUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");

                con.setDoOutput(true);
                con.setDoInput(true);

                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(postParams));
                writer.flush();
                writer.close();
                os.close();

                con.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));

                StringBuilder body = new StringBuilder();
                String line = null;
                while( (line = br.readLine()) != null)
                {
                    body.append(line);
                }

                result = (RestResponse<T>) new Gson().fromJson(body.toString(), new RestResponse<T>().getClass());
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

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
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
