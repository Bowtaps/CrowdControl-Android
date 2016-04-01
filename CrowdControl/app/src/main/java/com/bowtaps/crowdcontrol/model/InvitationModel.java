package com.bowtaps.crowdcontrol.model;

/**
 * @author Daniel Andrus
 */
public interface InvitationModel extends BaseModel {
    UserProfileModel getSender();
    UserProfileModel getRecipient();
    GroupModel getGroup();
}
