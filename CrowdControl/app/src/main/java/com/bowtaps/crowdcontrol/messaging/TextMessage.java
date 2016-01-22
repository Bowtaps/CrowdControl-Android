package com.bowtaps.crowdcontrol.messaging;

/**
 * @author Daniel Andrus
 * @since 2016-01-22
 */
public interface TextMessage extends Message {
    @Override
    public String getContent();
}
