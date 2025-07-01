// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.crypto;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.exceptions.SodiumException;
import com.goterl.lazysodium.utils.Key;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.participant.ParticipantResolutionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UftpCryptoServiceTest {

    public static final String PAYLOAD_XML = "PAYLOAD_XML";
    public static final String PRIVATE_KEY = "PRIVATE_KEY";
    public static final byte[] BODY = "BODY".getBytes(StandardCharsets.UTF_8);
    public static final String BASE_64_BODY = Base64.getEncoder().encodeToString(BODY);
    public static final String SENDER_DOMAIN = "SENDER_DOMAIN";
    public static final USEFRoleType SENDER_ROLE = USEFRoleType.DSO;
    private static final String PUBLIC_KEY = "PUBLIC_KEY";

    @Mock
    private ParticipantResolutionService participantService;
    @Mock
    private LazySodiumFactory factory;
    @Mock
    private LazySodiumBase64Pool lazySodiumInstancePool;

    @InjectMocks
    private UftpCryptoService testSubject;

    @Mock
    private LazySodiumJava lazySodium;
    @Mock
    private UftpParticipant sender;
    @Mock
    private SodiumException sodiumException;
    @Mock
    private SignedMessage signedMessage;
    @Mock
    private Key publicKey;
    @Mock
    private RuntimeException runtimeException;

    @AfterEach
    void noMore() {
        verifyNoMoreInteractions(
                participantService,
                factory,
                lazySodiumInstancePool,
                lazySodium,
                sender,
                sodiumException,
                signedMessage,
                publicKey,
                runtimeException
        );
    }

    @Test
    void signMessage() throws Exception {
        given(lazySodiumInstancePool.claim()).willReturn(lazySodium);
        given(lazySodium.cryptoSign(PAYLOAD_XML, PRIVATE_KEY)).willReturn(BASE_64_BODY);
        given(sender.domain()).willReturn(SENDER_DOMAIN);
        given(sender.role()).willReturn(SENDER_ROLE);

        var result = testSubject.signMessage(PAYLOAD_XML, sender, PRIVATE_KEY);

        assertThat(result.getSenderDomain()).isEqualTo(SENDER_DOMAIN);
        assertThat(result.getSenderRole()).isEqualTo(SENDER_ROLE);
        assertThat(result.getBody()).isEqualTo(BODY);
        verify(lazySodiumInstancePool).release(lazySodium);
    }

    @Test
    void signMessageThrows() throws Exception {
        given(lazySodiumInstancePool.claim()).willReturn(lazySodium);
        given(lazySodium.cryptoSign(PAYLOAD_XML, PRIVATE_KEY)).willThrow(sodiumException);

        assertThatThrownBy(() -> testSubject.signMessage(PAYLOAD_XML, sender, PRIVATE_KEY))
                .isInstanceOf(UftpConnectorException.class)
                .hasMessage("Failed to sign message.")
                .hasCause(sodiumException);

        verify(lazySodiumInstancePool).release(lazySodium);
    }

    @Test
    void verifySignedMessage() throws Exception {
        given(signedMessage.getSenderDomain()).willReturn(SENDER_DOMAIN);
        given(signedMessage.getSenderRole()).willReturn(SENDER_ROLE);
        given(participantService.getPublicKey(SENDER_ROLE, SENDER_DOMAIN)).willReturn(PUBLIC_KEY);
        given(signedMessage.getBody()).willReturn(BODY);
        given(lazySodiumInstancePool.claim()).willReturn(lazySodium);
        given(factory.keyFromBase64String(PUBLIC_KEY)).willReturn(publicKey);
        given(lazySodium.cryptoSignOpen(BASE_64_BODY, publicKey)).willReturn(PAYLOAD_XML);

        var result = testSubject.verifySignedMessage(signedMessage);

        assertThat(result).isEqualTo(PAYLOAD_XML);
        verify(lazySodiumInstancePool).release(lazySodium);
    }

    @Test
    void verifySignedMessageThrows() {
        given(signedMessage.getSenderDomain()).willReturn(SENDER_DOMAIN);
        given(signedMessage.getSenderRole()).willReturn(SENDER_ROLE);
        given(participantService.getPublicKey(SENDER_ROLE, SENDER_DOMAIN)).willReturn(PUBLIC_KEY);
        given(signedMessage.getBody()).willReturn(BODY);
        given(lazySodiumInstancePool.claim()).willReturn(lazySodium);
        given(factory.keyFromBase64String(PUBLIC_KEY)).willReturn(publicKey);
        given(lazySodium.cryptoSignOpen(BASE_64_BODY, publicKey)).willThrow(runtimeException);

        assertThatThrownBy(() ->
                testSubject.verifySignedMessage(signedMessage))
                .isInstanceOf(UftpCryptoException.class)
                .hasMessage("Failed to verify message.")
                .hasCause(runtimeException);

        verify(lazySodiumInstancePool).release(lazySodium);
    }

    @Test
    void verifySignedMessageReturnsNullWhenMessageNotValidlySignedWithPublicKey() {
        given(signedMessage.getSenderDomain()).willReturn(SENDER_DOMAIN);
        given(signedMessage.getSenderRole()).willReturn(SENDER_ROLE);
        given(participantService.getPublicKey(SENDER_ROLE, SENDER_DOMAIN)).willReturn(PUBLIC_KEY);
        given(signedMessage.getBody()).willReturn(BODY);
        given(lazySodiumInstancePool.claim()).willReturn(lazySodium);
        given(factory.keyFromBase64String(PUBLIC_KEY)).willReturn(publicKey);
        given(lazySodium.cryptoSignOpen(BASE_64_BODY, publicKey)).willReturn(null);

        var exception = assertThrows(UftpConnectorException.class, () ->
                testSubject.verifySignedMessage(signedMessage));

        assertThat(exception).isInstanceOf(UftpConnectorException.class);

        verify(lazySodiumInstancePool).release(lazySodium);
    }
}