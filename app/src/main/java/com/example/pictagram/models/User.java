package com.example.pictagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {

    public static final String KEY_PROFILE_PICTURE = "profilePic";

    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfilePicture(ParseFile parseFile) {
        put(KEY_PROFILE_PICTURE, parseFile);
    }

}
