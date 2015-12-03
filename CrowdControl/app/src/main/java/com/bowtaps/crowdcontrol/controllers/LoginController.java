package com.bowtaps.crowdcontrol.controllers;

import android.content.Context;

import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserModel;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by 1959760 on 11/12/2015.
 */
public class LoginController {

    private UserModel model;

    public LoginController(Context app_context){
        model = new UserModel() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public Date getCreated() {
                return null;
            }

            @Override
            public Date getUpdated() {
                return null;
            }

            @Override
            public Boolean wasModified() {
                return null;
            }

            @Override
            public void save() throws Exception {

            }

            @Override
            public void saveInBackground(SaveCallback callback) {

            }

            @Override
            public void load() throws Exception {

            }

            @Override
            public void loadInBackground(LoadCallback callback) {

            }

            @Override
            public String getUsername() {
                return null;
            }

            @Override
            public String getObjectID() {
                return null;
            }

            @Override
            public String getUserName() {
                return null;
            }

            @Override
            public Object getAuthData() {
                return null;
            }

            @Override
            public Boolean getEmailVerified() {
                return null;
            }

            @Override
            public String getEmail() {
                return null;
            }

            @Override
            public void setEmail(String email) {

            }

            @Override
            public String getPhone() {
                return null;
            }

            @Override
            public void setPhone(String phone) {

            }


        };
    }

}
