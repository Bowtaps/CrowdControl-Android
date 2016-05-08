package com.bowtaps.crowdcontrol.model;

/**
 * A model for group invitations.
 *
 * @author Daniel Andrus
 */
public interface InvitationModel extends BaseModel {

    /**
     * Gets the user who sent the invitation.
     *
     * @return The {@link UserProfileModel} that sent this invitation.
     */
    UserProfileModel getSender();

    /**
     * Gets the user to whom the invitation was sent.
     *
     * @return The {@link UserProfileModel} that the invitation was sent to.
     */
    UserProfileModel getRecipient();

    /**
     * Gets the group that this invitation allows the recipient user to join if accepted.
     *
     * @return The group that this invitation is for.
     */
    GroupModel getGroup();
}
