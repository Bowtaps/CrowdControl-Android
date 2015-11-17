package com.bowtaps.crowdcontrol.controllers;

import android.content.Context;

import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserModel;

import java.text.DateFormat;

/**
 * Created by 1959760 on 11/12/2015.
 */
public class LoginController {

    private UserModel model;

    public LoginController(Context app_context){
        model = new UserModel() {
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
            public DateFormat getCreatedAt() {
                return null;
            }

            @Override
            public DateFormat getUpdatedAt() {
                return null;
            }

            @Override
            public Object getLocation() {
                return null;
            }

            @Override
            public Object getPreferences() {
                return null;
            }

            @Override
            public String getStatus() {
                return null;
            }

            @Override
            public void setObjectID(String objectID) {

            }

            @Override
            public void setUserName(String userName) {

            }

            @Override
            public void setPassword(String password) {

            }

            @Override
            public void setAuthData(Object authData) {

            }

            @Override
            public void setEmailVerified(Boolean isVerified) {

            }

            @Override
            public void setEmail(String email) {

            }

            @Override
            public void setCreatedAt(DateFormat createdDate) {

            }

            @Override
            public void setUpdatedAt(DateFormat updatedDate) {

            }

            @Override
            public void setLocation(Object location) {

            }

            @Override
            public void setPreferences(Object preferences) {

            }

            @Override
            public void setStatus(String status) {

            }


        };
    }

}
