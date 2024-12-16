package com.devxlabs.WhatsAppTools;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.util.Log;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.util.NougatUtil;
import com.google.appinventor.components.runtime.util.YailList;
import com.google.appinventor.components.runtime.Form;
import java.util.ArrayList;
import java.util.List;
import java.io.File;


@DesignerComponent(version = 2, description = "Created by Salman Developer",
    category = ComponentCategory.EXTENSION,
    nonVisible = true, iconName = "https://img.icons8.com/cotton/16/000000/whatsapp--v4.png")

@UsesPermissions(permissionNames = "android.permission.READ_CONTACTS, android.permission.CALL_PHONE")

public class WhatsappTools extends AndroidNonvisibleComponent {
    private ComponentContainer container;
    private Context context;
    private Form form;
    private boolean w4bEnable;
    private String packageType = "com.whatsapp";

    public WhatsappTools(ComponentContainer container) {
        super(container.$form());
        this.container = container;
        context = (Context) container.$context();
        form = (Form) container.$form();
    }


    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "False")
    @SimpleProperty(userVisible = true)
    public void WhatsAppBusiness(boolean enable) {
        w4bEnable = enable;
        if (enable) {
            packageType = "com.whatsapp.w4b";
        } else {
            packageType = "com.whatsapp";
        }
    }

    @SimpleProperty(category = PropertyCategory.APPEARANCE, description = "")
    public boolean WhatsAppBusiness() {
        return w4bEnable;
    }


    @SimpleFunction(description = "Opens the WhatsApp interface to send a message.")
    public void SendMessageDirect(String phoneNumber, String message) {
        if (!phoneNumber.isEmpty()) {
            try {
                String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + message;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage(packageType);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                Error(e.getMessage());
            }
        }
    }

    @SimpleFunction(description = "Opens a chat with the specified phone number in WhatsApp.")
    public void SendMessage(String message) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage(packageType);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(intent);
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }

    @SimpleFunction(description = "Open a chat with a specific contact.")
    public void OpenChat(String phoneNumber) {
        if (!phoneNumber.isEmpty()) {
            try {
                phoneNumber = phoneNumber.replace("+", "").replace("-", "").replace(" ", "");
                String url = "https://wa.me/" + phoneNumber;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setPackage(packageType);
                context.startActivity(intent);
            } catch (Exception e) {
                Error(e.getMessage());
            }
        }
    }

    @SimpleFunction(description = "Check if a contact exists in the device.")
    public boolean DoesContactExist(String phoneNumber) {
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME
            }, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Error(e.getMessage());
        }
        return false;
    }

    @SimpleFunction(description = "Retrieve the profile picture of a WhatsApp contact.")
    public String GetProfilePicture(String phoneNumber) {
        try {
            Uri uri = Uri.parse("content://com.android.contacts/data");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, null, ContactsContract.Data.DATA1 + " = ?", new String[] {
                phoneNumber
            }, null);
            if (cursor != null && cursor.moveToFirst()) {
                String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI));
                cursor.close();
                return (photoUri != null) ? photoUri : "No profile picture found.";
            }
        } catch (Exception e) {
            Error(e.getMessage());
        }
        return "No profile picture found.";
    }

    @SimpleFunction(description = "Share a URL directly on WhatsApp.")
    public void ShareURL(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, url);
            intent.setPackage(packageType);
            context.startActivity(intent);
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }

    @SimpleFunction(description = "Retrieve the installed WhatsApp version.")
    public String GetWhatsAppVersion() {
        try {
            return context.getPackageManager().getPackageInfo(packageType, 0).versionName;
        } catch (Exception e) {
            Error(e.getMessage());
        }
        return "WhatsApp is not installed.";
    }

    @SimpleFunction(description = "Add a new contact to the device's contacts.")
    public void AddContact(String name, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
            context.startActivity(intent);
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }


    @SimpleFunction(description = "Checks if WhatsApp is installed on the device.")
    public boolean IsAvailable() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageType);
        boolean result = (intent != null) ? true : false;
        return result;
    }

    @SimpleFunction(description = "Sends a file with an optional caption via WhatsApp.")
    public void SendFile(String caption, String filePath) {
        try {
            if (!filePath.startsWith("file://")) {
                filePath = "file://" + filePath;
            }
            Uri uri = Uri.parse(filePath);
            File allFile = new File(uri.getPath());
            if (allFile.isFile()) {
                String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String type = mime.getMimeTypeFromExtension(fileExtension);

                if (type == null) {
                    type = "application/octet-stream";
                }
                Uri shareableUri = NougatUtil.getPackageUri(form, allFile);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, caption);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_STREAM, shareableUri);
                intent.setType(type);
                intent.setPackage(packageType);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }

    @SimpleFunction(description = "Sends multiple files with an optional caption via WhatsApp.")
    public void SendFiles(String caption, YailList listFilePath) {
        ArrayList < Uri > fileUris = new ArrayList < Uri > ();
        try {
            for (Object item: listFilePath.toArray()) {
                String fileItem = item.toString();
                if (!fileItem.startsWith("file://")) {
                    fileItem = "file://" + fileItem;
                }
                Uri uri = Uri.parse(fileItem);
                File allFile = new File(uri.getPath());
                if (allFile.isFile()) {
                    Uri shareableUri = NougatUtil.getPackageUri(form, allFile);
                    fileUris.add(shareableUri);
                }
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_TEXT, caption);
            intent.setType("text/plain");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
            intent.setType("*/*");
            intent.setPackage(packageType);
            context.startActivity(intent);
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }

    @SimpleFunction(description = "Sends a file directly to a specific phone number.")
    public void SendFileDirect(String phoneNumber, String caption, String filePath) {
        if (!phoneNumber.isEmpty()) {
            phoneNumber = phoneNumber.replace("+", "").replace("-", "").replace(" ", "");
            try {
                if (!filePath.startsWith("file://")) {
                    filePath = "file://" + filePath;
                }
                Uri uri = Uri.parse(filePath);
                File allFile = new File(uri.getPath());
                if (allFile.isFile()) {
                    String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type = mime.getMimeTypeFromExtension(fileExtension);

                    if (type == null) {
                        type = "application/octet-stream";
                    }
                    Uri shareableUri = NougatUtil.getPackageUri(form, allFile);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, caption);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_STREAM, shareableUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType(type);
                    intent.setPackage(packageType);
                    intent.putExtra("jid", phoneNumber + "@s.whatsapp.net");
                    form.startActivity(intent);
                }
            } catch (Exception e) {
                Error(e.getMessage());
            }
        }
    }

    @SimpleFunction(description = "Sends multiple files directly to a specific phone number.")
    public void SendFilesDirect(String phoneNumber, String caption, YailList listFilePath) {
        if (!phoneNumber.isEmpty()) {
            ArrayList < Uri > fileUris = new ArrayList < Uri > ();
            phoneNumber = phoneNumber.replace("+", "").replace("-", "").replace(" ", "");
            try {
                for (Object item: listFilePath.toArray()) {
                    String fileItem = item.toString();
                    if (!fileItem.startsWith("file://")) {
                        fileItem = "file://" + fileItem;
                    }
                    Uri uri = Uri.parse(fileItem);
                    File allFile = new File(uri.getPath());
                    if (allFile.isFile()) {
                        Uri shareableUri = NougatUtil.getPackageUri(form, allFile);
                        fileUris.add(shareableUri);
                    }
                }
                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_TEXT, caption);
                intent.setType("text/plain");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("*/*");
                intent.setPackage(packageType);
                intent.putExtra("jid", phoneNumber + "@s.whatsapp.net");
                form.startActivity(intent);
            } catch (Exception e) {
                Error(e.getMessage());
            }
        }
    }

    @SimpleFunction(description = "Initiates a voice call with a specified contact on WhatsApp.")
    public void MakeVoiceCall(String contactName) {
        MakeCall(contactName, "vnd.android.cursor.item/vnd.com.whatsapp.voip.call");
    }

    @SimpleFunction(description = "Initiates a video call with a specified contact on WhatsApp.")
    public void MakeVideoCall(String contactName) {
        MakeCall(contactName, "vnd.android.cursor.item/vnd.com.whatsapp.video.call");
    }

    private void MakeCall(String name, String mimeString) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME);
            while (cursor.moveToNext()) {
                long _id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID));
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (displayName.equals(name)) {
                    if (mimeType.equals(mimeString)) {
                        String data = "content://com.android.contacts/data/" + _id;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(data), mimeString);
                        intent.setPackage(packageType);
                        context.startActivity(intent);
                        cursor.close();
                    }
                }
            }
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }


    @SimpleEvent(description = "Event is fired when error happens.")
    public void Error(String error) {
        EventDispatcher.dispatchEvent(this, "Error", error);
    }


}
