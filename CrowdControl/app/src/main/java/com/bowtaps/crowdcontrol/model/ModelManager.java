package com.bowtaps.crowdcontrol.model;

/**
 * A class dedicated for providing model functionality that does not belong in any individual
 * model, such as logging users in, signing up new users, or getting the current user.
 *
 * @author Daniel Andrus
 * @since 2016-01-17
 */
public interface ModelManager {

    public UserModel logInUser(String username, String password) throws Exception;

    public void logInUserInBackground(String username, String password, final BaseModel.LoadCallback callback);

    public UserModel createUser(String username, String email, String password) throws Exception;

    public void createUserInBackground(String username, String email, String password, final BaseModel.SaveCallback callback);

    public UserModel getCurrentUser();

    public Boolean logOutCurrentUser() throws Exception;
}
