// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.model;

/**
 * Data transfer object for UFTP participant information.
 *
 * @param domain The domain of the participant. It is not not be confused with the 'endpoint' for UFTP communication,
 *               but rather serves as an identifier for the participant.
 * @param publicKey The public key of the participant.
 * @param endpoint The endpoint of the participant. This is the URL where the participant can be reached.
 * @param requiresAuthorization Specifies whether the participant requires authorization to communicate with it.
 */
public record UftpParticipantInformation(String domain, String publicKey, String endpoint, boolean requiresAuthorization) {}
