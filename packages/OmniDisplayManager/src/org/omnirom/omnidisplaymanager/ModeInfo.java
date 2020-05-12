/*
* Copyright (C) 2018 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/

package com.qti.snapdragon.sdk.display;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ModeInfo implements Parcelable {
    public static final Creator<ModeInfo> CREATOR = new Creator<ModeInfo>() {
        public ModeInfo createFromParcel(Parcel inParcel) {
            return new ModeInfo(inParcel.readInt(), inParcel.readString(), inParcel.readInt());
        }

        public ModeInfo[] newArray(int size) {
            return new ModeInfo[size];
        }
    };
    private int id = -1;
    private int modeType = 0;
    private String name = null;

    public ModeInfo(int pId, String pName, int pType) {
        id = pId;
        name = pName;
        modeType = pType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getModeType() {
        return modeType;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeInt(id);
        destParcel.writeString(name);
        destParcel.writeInt(modeType);
    }
}
