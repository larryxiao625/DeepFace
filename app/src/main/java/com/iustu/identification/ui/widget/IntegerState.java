package com.iustu.identification.ui.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;

public class IntegerState extends Preference.BaseSavedState {
        public int state;

        public IntegerState(Parcelable superState) {
            super(superState);
        }

        private IntegerState(Parcel source) {
            super(source);
            state = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Parcelable.Creator<IntegerState> CREATOR = new Creator<IntegerState>() {
            @Override
            public IntegerState createFromParcel(Parcel source) {
                return new IntegerState(source);
            }

            @Override
            public IntegerState[] newArray(int size) {
                return new IntegerState[size];
            }
        };
}