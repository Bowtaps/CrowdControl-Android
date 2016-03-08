package com.bowtaps.crowdcontrol.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The interface for group models.
 *
 * @author Joseph Mowry
 * @since 2015-11-28
 */
public interface GroupModel extends BaseModel {

    /**
     * Gets the {@link UserProfileModel} of the leader of the group or {@code null} if there is no
     * leader.
     *
     * @return The {@link UserProfileModel} of the leader of the group or {@code null} if there is
     *         no leader.
     */
    public UserProfileModel getGroupLeader();

    /**
     * Sets the given user as this group's designated leader.
     *
     * @param leader The profile of the user to make the leader.
     */
    public void setGroupLeader(UserProfileModel leader);

    /**
     * Gets the name of the group.
     *
     * @return The name of the group.
     */
    public String getGroupName();

    /**
     * Sets the name of the group.
     *
     * @param name The new name of the group.
     */
    public void setGroupName(String name);

    /**
     * Gets the description of the group.
     *
     * @return The description of the group.
     */
    public String getGroupDescription();

    /**
     * Sets the description of the group.
     *
     * @param description The new description of the group.
     */
    public void setGroupDescription(String description);

    /**
     * Gets the list of users associated with the current group.
     *
     * @return An {@link ArrayList} of {@link UserProfileModel} objects that belong to the group.
     */
    public List<? extends UserProfileModel> getGroupMembers();

    /**
     * Clears all members from this group.
     *
     * @return {@code true} if the operation was successful, {@code false} if not.
     */
    public Boolean clearGroupMembers();

    /**
     * Adds multiple user profiles to the group.
     *
     * @param profiles The {@link Collection} of users to add as members to this group.
     *
     * @return {@code true} if the operation was successful, {@code false} if not.
     */
    public Boolean addGroupMembers(Collection<? extends UserProfileModel> profiles);

    /**
     * Adds a new member to the group.
     *
     * @param profile The {@link UserProfileModel} of the user to be added.
     *
     * @return True if the user was successfully added to the group, false if not or if the user is
     *         already a member of the group.
     */
    public Boolean addGroupMember(UserProfileModel profile);

    /**
     * Removes a user from the group.
     *
     * @param profile The {@link UserProfileModel} of the user to be removed.
     *
     * @return True if the user was successfully removed from the group, false if not or if the user
     *         was already not a member of the group.
     */
    public Boolean removeGroupMember(UserProfileModel profile);
}