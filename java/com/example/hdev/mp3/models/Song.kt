package com.example.hdev.mp3.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class Song(
    private var mPath: String,
    private var mName: String,
    private var mAlbum: String,
    private var mArtist: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mPath)
        parcel.writeString(mName)
        parcel.writeString(mAlbum)
        parcel.writeString(mArtist)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun setName(name: String) {
        this.mName = name
    }

    fun getaPath(): String {
        return mPath
    }

    fun setPath(aPath: String) {
        this.mPath = aPath
    }

    fun getName(): String {
        return mName
    }

    fun getAlbum(): String {
        return mAlbum
    }

    fun setAlbum(aAlbum: String) {
        this.mAlbum = aAlbum
    }

    fun getArtist(): String {
        return mArtist
    }

    fun setArtist(aArtist: String) {
        this.mArtist = aArtist
    }

    companion object CREATOR : Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}
