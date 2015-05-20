package com.onmyway.utils;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.onmyway.model.*;
import com.onmyway.responses.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marco on 15/05/2015.
 */

public class ServiceGateway {

    public final static String baseAPI = "http://onmywayapp.azurewebsites.net/api/";
    //public final static String baseAPI = "http://localhost:1192/api/";

    // users/login
    public static void LoginAsync(String phoneNumber, ApiCallback<UserResponse> apiCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phoneNumber", phoneNumber);

        new PostApiTask<User>("user/login", params, apiCallback, UserResponse.class).execute();
    }

    // user/appointments
    public static void GetAppointmentsPreviewAsync(String phoneNumber, ApiCallback<AppointmentsPreviewResponse> apiCallback) {
        new GetApiTask<ArrayList<AppointmentBase>>("user/appointments?phoneNumber=" + phoneNumber, apiCallback, AppointmentsPreviewResponse.class).execute();
    }

    // appointment/add
    public static void UploadAppointmentAsync(String phoneNumber, Appointment appointment, ApiCallback<AppointmentResponse> apiCallback) {
        HashMap<String, String> params = new HashMap<>();

        params.put("title", appointment.getTitle());
        params.put("authorPhoneNumber", phoneNumber);
        params.put("formattedStartDateTime", appointment.getFormattedStartDateTime());
        params.put("formattedTrackingDateTime", appointment.getFormattedTrackingDateTime());

        String jsonLocation = new Gson().toJson(appointment.getLocation());
        String jsonValidUsers = new Gson().toJson(appointment.getValidUsers());
        String jsonInvalidUsers = new Gson().toJson(appointment.getInvalidUsers());

        params.put("location", jsonLocation);
        params.put("validUsers", jsonValidUsers);
        params.put("invalidUsers", jsonInvalidUsers);

        new PostApiTask<Appointment>("appointment/add", params, apiCallback, AppointmentResponse.class);
    }

    // appointment/delete
    public static void RemoveAppointmentAsync(String phoneNumber, String appointmentId, ApiCallback<BooleanResponse> apiCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("authorPhoneNumber", phoneNumber);
        params.put("id", appointmentId);

        new PostApiTask<Boolean>("appointment/delete", params, apiCallback, BooleanResponse.class).execute();
    }

    // appointment/get
    public static void GetFullAppointmentAsync(String appointmentId, ApiCallback<AppointmentResponse> apiCallback) {
        new GetApiTask<Appointment>("appointment/get?appointmentId=" + appointmentId, apiCallback, AppointmentResponse.class).execute();
    }

    // appointments/users/status
    public static void GetUsersStatusAsync(String appointmentId, ApiCallback<UsersStatusResponse> apiCallback) {
        new GetApiTask<ArrayList<UserStatus>>("appointment/users/status?appointmentId=" + appointmentId, apiCallback, UsersStatusResponse.class).execute();
    }

    // users/status
    public static void UpdateUserStatusAsync(String phoneNumber, String status, ApiCallback<BooleanResponse> apiCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phoneNumber", phoneNumber);
        params.put("status", status);

        new PostApiTask<Boolean>("user/status", params, apiCallback, BooleanResponse.class).execute();
    }

    private static class GetApiTask<T> extends ApiTask<T> {
        public GetApiTask(String relativeUrl, ApiCallback apiCallback, Class deserializationClass){
            super(relativeUrl, RestMethod.GET, null, apiCallback, deserializationClass);
        }
    }

    private static class PostApiTask<T> extends ApiTask<T> {
        public PostApiTask(String relativeUrl, HashMap<String, String> postParams, ApiCallback apiCallback, Class deserializationClass){
            super(relativeUrl, RestMethod.POST, postParams, apiCallback, deserializationClass);
        }
    }

    private static class ApiTask<T> extends AsyncTask<Void, Void, Object> {

        protected enum RestMethod{
            GET,
            POST
        }

        private String relativeUrl;
        private ApiCallback apiCallback;
        private RestMethod method;
        private Class deserializationClass;
        private HashMap<String, String> postParams;

        protected ApiTask(String relativeUrl, RestMethod method, @Nullable HashMap<String, String> postParams, ApiCallback apiCallback, Class deserializationClass) {
            this.relativeUrl = relativeUrl;
            this.apiCallback = apiCallback;
            this.method = method;
            this.postParams = postParams;
            this.deserializationClass = deserializationClass;
        }

        @Override
        protected Object doInBackground(Void... params) {
            if(method == RestMethod.GET)
                return executeGet();
            else
                return executePost();
        }

        private Object executeGet() {
            Object result;

            HttpURLConnection con = null;
            try {
                URL url = new URL(baseAPI + relativeUrl);
                con = (HttpURLConnection) url.openConnection();

                /*if (con.getResponseCode() != 200) {
                    throw new RuntimeException("HTTP error code : "+ con.getResponseCode());
                }*/

                BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
                result = new Gson().fromJson(br, deserializationClass);
            } catch (Exception e) {
                result = null;
            } finally {
                if (con != null)
                    con.disconnect();
            }

            return result;
        }

        private Object executePost() {
            Object result = null;

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
                while( (line = br.readLine()) != null) {
                    body.append(line);
                }

                result = new Gson().fromJson(body.toString(), deserializationClass);
            } catch (Exception e) {
                result = null;
            } finally {
                if (con != null)
                    con.disconnect();
            }

            return result;
        }

        private String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (String key : params.keySet())
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(params.get(key), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(Object result) {
            apiCallback.OnComplete(result);
        }
    }
}