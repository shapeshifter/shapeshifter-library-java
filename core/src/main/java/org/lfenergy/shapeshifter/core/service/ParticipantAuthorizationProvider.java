package org.lfenergy.shapeshifter.core.service;

import org.lfenergy.shapeshifter.core.model.UftpParticipant;

/**
 * The ParticipantAuthorizationProvider provides a method to get the Authorization header value for a given participant.
 */
public interface ParticipantAuthorizationProvider {

    /**
     * Method that returns the complete header value of the 'Authorization' header for the given participant.
     *
     * @param participant The participant for whom the Authorization header is requested
     *
     */
    String getAuthorizationHeader(UftpParticipant participant);

}
