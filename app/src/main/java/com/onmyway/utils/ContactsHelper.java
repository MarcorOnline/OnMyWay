package com.onmyway.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import com.onmyway.model.User;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ContactsHelper {

    public static void ResolveContactsNames(ContextWrapper context, List<User> users) {

        ContentResolver contentResolver = context.getContentResolver();

        String name;
        for (User u : users) {
            name = getContactDisplayNameByNumber(contentResolver, u.getPhoneNumber());
            u.setName(name);
        }
    }

    private static String getContactDisplayNameByNumber(ContentResolver contentResolver, String number) {
        String name = "";

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

    private static final int CONTACT_PICKER_RESULT = 1001;

    public static void startPickContact(Activity activity){
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        activity.startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    //return phone number
    public static User getPhoneNumberFromPickResponse(Activity activity, int requestCode, Intent data){
        String phoneNumber = "";
        String userName = "";

        if (requestCode == CONTACT_PICKER_RESULT) {

            //contact uri
            Uri result = data.getData();

            //contact id
            String id = result.getLastPathSegment();

            ContentResolver contentResolver = activity.getContentResolver();

            Cursor phones = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds
                            .Phone.CONTACT_ID + "=?",
                    new String[]{id},
                    null);

            while (phones.moveToNext()) {
                int phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                {
                    phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                            ));
                    break;
                }
            }

            phones.close();

            if(!StringUtils.isNullOrWhiteSpaces(phoneNumber)) {
                userName = getContactDisplayNameByNumber(contentResolver, phoneNumber);
            }
        }

        User user = new User();
        user.setName(userName);
        user.setPhoneNumber(phoneNumber);
        return user;
    }
}
