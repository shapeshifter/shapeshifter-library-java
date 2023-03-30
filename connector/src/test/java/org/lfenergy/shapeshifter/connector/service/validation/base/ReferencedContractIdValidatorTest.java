// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.ContractSettlementType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexReservationUpdate;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.ContractSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedContractIdValidatorTest {

  private static final String CONTRACT_ID1 = "CONTRACT_ID1";
  private static final String CONTRACT_ID2 = "CONTRACT_ID2";
  private static final String CONTRACT_ID3 = "CONTRACT_ID3";

  @Mock
  private ContractSupport contractSupport;

  @InjectMocks
  private ReferencedContractIdValidator testSubject;

  @Mock
  private UftpParticipant sender;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        contractSupport,
        sender
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexReservationUpdate.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexSettlement.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withoutParameter() {
    FlexSettlement noSubElements = new FlexSettlement();

    FlexSettlement noContractIdOnFlexOrderSettlement = new FlexSettlement();
    noContractIdOnFlexOrderSettlement.getFlexOrderSettlements().addAll(
        List.of(new FlexOrderSettlementType(), new FlexOrderSettlementType())
    );

    FlexSettlement noContractIdOnContractSettlement = new FlexSettlement();
    noContractIdOnFlexOrderSettlement.getContractSettlements().addAll(
        List.of(new ContractSettlementType(), new ContractSettlementType())
    );

    return Stream.of(
        Arguments.of(new FlexOffer()),
        Arguments.of(new FlexOrder()),
        Arguments.of(new FlexReservationUpdate()),
        Arguments.of(noSubElements),
        Arguments.of(noContractIdOnFlexOrderSettlement),
        Arguments.of(noContractIdOnContractSettlement)
    );
  }

  public static Stream<Arguments> withParameter() {

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setContractID(CONTRACT_ID1);

    FlexOrder flexOrder = new FlexOrder();
    flexOrder.setContractID(CONTRACT_ID1);

    FlexReservationUpdate flexReservationUpdate = new FlexReservationUpdate();
    flexReservationUpdate.setContractID(CONTRACT_ID1);

    FlexOrderSettlementType os1 = new FlexOrderSettlementType();
    os1.setContractID(CONTRACT_ID1);
    FlexOrderSettlementType os2 = new FlexOrderSettlementType();
    os2.setContractID(CONTRACT_ID2);
    FlexOrderSettlementType os3WithoutContractId = new FlexOrderSettlementType();

    ContractSettlementType cs1 = new ContractSettlementType();
    cs1.setContractID(CONTRACT_ID1);
    ContractSettlementType cs2 = new ContractSettlementType();
    cs2.setContractID(CONTRACT_ID3);
    ContractSettlementType cs3WithoutContractId = new ContractSettlementType();

    FlexSettlement flexSettlement = new FlexSettlement();
    flexSettlement.getFlexOrderSettlements().addAll(List.of(os1, os2, os3WithoutContractId));
    flexSettlement.getContractSettlements().addAll(List.of(cs1, cs2, cs3WithoutContractId));

    return Stream.of(
        Arguments.of(flexOffer, List.of(CONTRACT_ID1)),
        Arguments.of(flexOrder, List.of(CONTRACT_ID1)),
        Arguments.of(flexSettlement, List.of(CONTRACT_ID1, CONTRACT_ID2, CONTRACT_ID3)) // unique values are found
    );
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenNoValueIsPresent(PayloadMessageType payloadMessage) {
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_true_whenFoundValueIsSupported(PayloadMessageType payloadMessage, List<String> baselineRefs) {
    if (baselineRefs.size() >= 1) {
      given(contractSupport.isSupportedContractID(CONTRACT_ID1)).willReturn(true);
    }
    if (baselineRefs.size() == 3) {
      given(contractSupport.isSupportedContractID(CONTRACT_ID2)).willReturn(true);
      given(contractSupport.isSupportedContractID(CONTRACT_ID3)).willReturn(true);
    }

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_false_whenFoundValueIsFirstNotSupported(PayloadMessageType payloadMessage, List<String> baselineRefs) {
    given(contractSupport.isSupportedContractID(CONTRACT_ID1)).willReturn(false);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference ContractID");
  }
}