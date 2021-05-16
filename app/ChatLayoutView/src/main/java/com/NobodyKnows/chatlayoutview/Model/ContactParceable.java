package com.NobodyKnows.chatlayoutview.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactParceable implements Parcelable {
    private String name;
    private String contactNumbers;

    public ContactParceable(String name, String contactNumbers) {
        this.name = name;
        this.contactNumbers = contactNumbers;
    }

    protected ContactParceable(Parcel in) {
        name = in.readString();
        contactNumbers = in.readString();
    }

    public static final Creator<ContactParceable> CREATOR = new Creator<ContactParceable>() {
        @Override
        public ContactParceable createFromParcel(Parcel in) {
            return new ContactParceable(in);
        }

        @Override
        public ContactParceable[] newArray(int size) {
            return new ContactParceable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(contactNumbers);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumbers() {
        return contactNumbers;
    }

    public void setContactNumbers(String contactNumbers) {
        this.contactNumbers = contactNumbers;
    }
}
