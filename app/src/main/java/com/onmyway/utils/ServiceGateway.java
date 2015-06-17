package com.onmyway.utils;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.onmyway.model.*;
import com.onmyway.requests.UploadAppointmentRequest;
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

    //public final static String baseAPI = "http://onmywayapp.azurewebsites.net/api/";
    public final static String baseAPI = "http://10.0.3.2:1192/api/";

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

        UploadAppointmentRequest body = new UploadAppointmentRequest();
        body.title = appointment.getTitle();
        body.authorPhoneNumber = phoneNumber;
        body.formattedStartDateTime = appointment.getFormattedStartDateTime();
        body.formattedTrackingDateTime = appointment.getFormattedTrackingDateTime();
        body.location = appointment.getLocation();
        body.validUsers = appointment.getValidUsers();

        new PostApiTask<Appointment>("appointment/add", body, apiCallback, AppointmentResponse.class).execute();
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

    //user/update
    public static void UpdateUser(String phoneNumber, String status, String avatar, ApiCallback<BooleanResponse> apiCallback){
        HashMap<String, String> params = new HashMap<>();

        params.put("phoneNumber", phoneNumber);
        params.put("status", status);
        params.put("avatar", avatar);

        new PostApiTask<Boolean>("user/update", params, apiCallback, BooleanResponse.class).execute();
    }


    public static void SynchronizeForeground(String appointmentId, String phoneNumber, String status, @Nullable LatLng location, ApiCallback<SyncResponse> apiCallback) {
        Synchronize(false, appointmentId, phoneNumber, status, location, apiCallback);
    }

    public static void SynchronizeBackground(String appointmentId, String phoneNumber, @Nullable LatLng location, String status, ApiCallback<SyncResponse> apiCallback) {
        Synchronize(true, appointmentId, phoneNumber, status, location, apiCallback);
    }

    // appointment/sync
    private static void Synchronize(boolean isLight, String appointmentId, String phoneNumber, String status, @Nullable LatLng location, ApiCallback<SyncResponse> apiCallback){
        HashMap<String, String> params = new HashMap<>();

        params.put("phoneNumber", phoneNumber);
        params.put("status", status);

        if (location != null)
        {
            params.put("latitude", Double.toString(location.latitude));
            params.put("longitude", Double.toString(location.longitude));
        }

        new PostApiTask<Boolean>("appointment/sync?appointmentId=" + appointmentId + "isLight=" + Boolean.toString(isLight), params, apiCallback, SyncResponse.class).execute();
    }

    private static class GetApiTask<T> extends ApiTask<T> {
        public GetApiTask(String relativeUrl, ApiCallback apiCallback, Class deserializationClass){
            super(relativeUrl, RestMethod.GET, null, apiCallback, deserializationClass, false);
        }
    }

    private static class PostApiTask<T> extends ApiTask<T> {
        public PostApiTask(String relativeUrl, Object postBody, ApiCallback apiCallback, Class deserializationClass){
            super(relativeUrl, RestMethod.POST, serialize(postBody), apiCallback, deserializationClass, true);
        }

        public PostApiTask(String relativeUrl, HashMap<String, String> postParams, ApiCallback apiCallback, Class deserializationClass){
            super(relativeUrl, RestMethod.POST, getQuery(postParams), apiCallback, deserializationClass, false);
        }

        private static String serialize(Object postBody) {
            try {
                return postBody != null ? new Gson().toJson(postBody) : "{}";
            } catch (Exception e) {
                return "{}";
            }
        }

        private static String getQuery(HashMap<String, String> params) {
            StringBuilder result = new StringBuilder();

            try {
                boolean first = true;

                for (String key : params.keySet()) {
                    if (first)
                        first = false;
                    else
                        result.append("&");

                    result.append(URLEncoder.encode(key, "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(params.get(key), "UTF-8"));
                }
            }
            catch (Exception e){}

            return result.toString();
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
        private String postBody;
        private boolean jsonBody;

        protected ApiTask(String relativeUrl, RestMethod method, @Nullable String postBody, ApiCallback apiCallback, Class deserializationClass, boolean jsonBody) {
            this.relativeUrl = relativeUrl;
            this.apiCallback = apiCallback;
            this.method = method;
            this.postBody = postBody;
            this.jsonBody = jsonBody;
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
                if(jsonBody)
                    con.setRequestProperty("Content-Type", "application/json");
                con.setRequestMethod("POST");

                con.setDoOutput(true);
                con.setDoInput(true);

                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(postBody);
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

        @Override
        protected void onPostExecute(Object result) {
            apiCallback.OnComplete(result);
        }
    }
}