// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.DPrognosis;
import org.lfenergy.shapeshifter.api.DPrognosisISPType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderISPType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.lfenergy.shapeshifter.api.FlexReservationUpdate;
import org.lfenergy.shapeshifter.api.Metering;
import org.lfenergy.shapeshifter.api.MeteringISPType;
import org.lfenergy.shapeshifter.api.MeteringProfileType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IspListValidatorBaseTest {

  private static final LocalDate WINTER_TIME_DAY = LocalDate.parse("2022-11-22");
  private static final Duration DURATION_15_MINUTES = Duration.ofMinutes(15);
  private static final String TIME_ZONE_AMSTERDAM = "Europe/Amsterdam";

  @RequiredArgsConstructor
  private static class TestImplementation extends IspListValidatorBase {

    private record CallData(Long maxNumberIsps, List<IspInfo> isps) {

    }

    private final boolean configuredResult;
    private final List<CallData> calls = new ArrayList<>();

    @Override
    public int order() {
      return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public String getReason() {
      return "TestImplementation reason";
    }

    @Override
    protected boolean validateIsps(long maxNumberIsps, List<IspInfo> isps) {
      calls.add(new CallData(maxNumberIsps, isps));

      return configuredResult;
    }
  }

  @Mock
  private UftpParticipant sender;
  @Mock
  private List<IspInfo> listIsps;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        sender,
        listIsps
    );
  }

  @Test
  void appliesTo() {
    TestImplementation testSubject = new TestImplementation(true);
    assertThat(testSubject.appliesTo(DPrognosis.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexReservationUpdate.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexRequest.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
    assertThat(testSubject.appliesTo(Metering.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    TestImplementation testSubject = new TestImplementation(true);
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withParameters() {
    DPrognosis dPrognosis = new DPrognosis();
    dPrognosis.setPeriod(WINTER_TIME_DAY);
    dPrognosis.setISPDuration(DURATION_15_MINUTES);
    dPrognosis.setTimeZone(TIME_ZONE_AMSTERDAM);
    dPrognosis.getISPS().addAll(List.of(
        setStartAndDuration(new DPrognosisISPType(), 1, 1),
        setStartAndDuration(new DPrognosisISPType(), 2, 2)
    ));

    FlexReservationUpdate flexReservationUpdate = new FlexReservationUpdate();
    flexReservationUpdate.setPeriod(WINTER_TIME_DAY);
    flexReservationUpdate.setISPDuration(DURATION_15_MINUTES);
    flexReservationUpdate.setTimeZone(TIME_ZONE_AMSTERDAM);
    // No ISPs (not allowed bij XSD)

    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setPeriod(WINTER_TIME_DAY);
    flexRequest.setISPDuration(DURATION_15_MINUTES);
    flexRequest.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexRequest.getISPS().addAll(List.of(
        setStartAndDuration(new FlexRequestISPType(), 1, 1),
        setStartAndDuration(new FlexRequestISPType(), 2, 2)
    ));

    FlexOfferOptionType flexOffer1 = new FlexOfferOptionType();
    flexOffer1.getISPS().addAll(List.of(
        setStartAndDuration(new FlexOfferOptionISPType(), 1, 1),
        setStartAndDuration(new FlexOfferOptionISPType(), 2, 2)
    ));

    FlexOfferOptionType flexOffer2 = new FlexOfferOptionType();
    flexOffer2.getISPS().addAll(List.of(
        setStartAndDuration(new FlexOfferOptionISPType(), 3, 3),
        setStartAndDuration(new FlexOfferOptionISPType(), 4, 4)
    ));

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setPeriod(WINTER_TIME_DAY);
    flexOffer.setISPDuration(DURATION_15_MINUTES);
    flexOffer.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexOffer.getOfferOptions().addAll(List.of(flexOffer1, flexOffer2));

    FlexOrder flexOrder = new FlexOrder();
    flexOrder.setPeriod(WINTER_TIME_DAY);
    flexOrder.setISPDuration(DURATION_15_MINUTES);
    flexOrder.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexOrder.getISPS().addAll(List.of(
        setStartAndDuration(new FlexOrderISPType(), 1, 1),
        setStartAndDuration(new FlexOrderISPType(), 2, 2)
    ));

    MeteringProfileType meteringProfile1 = new MeteringProfileType();
    meteringProfile1.getISPS().addAll(List.of(
        setStart(new MeteringISPType(), 1),
        setStart(new MeteringISPType(), 2)
    ));

    MeteringProfileType meteringProfile2 = new MeteringProfileType();
    meteringProfile2.getISPS().addAll(List.of(
        setStart(new MeteringISPType(), 3),
        setStart(new MeteringISPType(), 4)
    ));

    Metering meteringMessage = new Metering();
    meteringMessage.setPeriod(WINTER_TIME_DAY);
    meteringMessage.setISPDuration(DURATION_15_MINUTES);
    meteringMessage.setTimeZone(TIME_ZONE_AMSTERDAM);
    meteringMessage.getProfiles().addAll(List.of(meteringProfile1, meteringProfile2));

    return Stream.of(
        Arguments.of(dPrognosis, List.of(
            // One call to abstract method validateIsps
            List.of(new IspInfo(1, 1), new IspInfo(2, 2))
        )),
        Arguments.of(flexReservationUpdate, List.of(
            // One call to abstract method validateIsps with empty list
            List.of()
        )),
        Arguments.of(flexRequest, List.of(
            // One call to abstract method validateIsps
            List.of(new IspInfo(1, 1), new IspInfo(2, 2))
        )),
        Arguments.of(flexOffer, List.of(
            // Two calls to abstract method validateIsps
            List.of(new IspInfo(1, 1), new IspInfo(2, 2)),
            List.of(new IspInfo(3, 3), new IspInfo(4, 4))
        )),
        Arguments.of(flexOrder, List.of(
            // One call to abstract method validateIsps
            List.of(new IspInfo(1, 1), new IspInfo(2, 2))
        )),
        Arguments.of(meteringMessage, List.of(
            // Two calls to abstract method validateIsps
            List.of(new IspInfo(1, 1), new IspInfo(2, 1)),
            List.of(new IspInfo(3, 1), new IspInfo(4, 1))
        ))
    );
  }

  private static DPrognosisISPType setStartAndDuration(DPrognosisISPType instance, long start, long duration) {
    instance.setStart(start);
    instance.setDuration(duration);
    return instance;
  }


  private static FlexRequestISPType setStartAndDuration(FlexRequestISPType instance, long start, long duration) {
    instance.setStart(start);
    instance.setDuration(duration);
    return instance;
  }

  private static FlexOfferOptionISPType setStartAndDuration(FlexOfferOptionISPType instance, long start, long duration) {
    instance.setStart(start);
    instance.setDuration(duration);
    return instance;
  }

  private static FlexOrderISPType setStartAndDuration(FlexOrderISPType instance, long start, long duration) {
    instance.setStart(start);
    instance.setDuration(duration);
    return instance;
  }

  private static MeteringISPType setStart(MeteringISPType instance, long start) {
    instance.setStart(start);
    return instance;
  }

  @ParameterizedTest
  @MethodSource("withParameters")
  <T extends PayloadMessageType> void valid(T msg, List<List<IspInfo>> expectedIspsInCalls) {
    TestImplementation testSubject = new TestImplementation(true);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, msg))).isTrue();

    assertThat(testSubject.calls).hasSize(expectedIspsInCalls.size());
    forEach(testSubject.calls, (call, i, n) -> {
      assertThat(call.maxNumberIsps).isEqualTo(96);
      assertThat(call.isps).containsExactlyElementsOf(expectedIspsInCalls.get(i));
    });
  }

  @Test
  void validateIsps_true() {
    TestImplementation testSubject = new TestImplementation(true);

    assertThat(testSubject.validateIsps(100, listIsps)).isTrue();

    assertThat(testSubject.calls).hasSize(1);
    assertThat(testSubject.calls.get(0).maxNumberIsps).isEqualTo(100);
    assertThat(testSubject.calls.get(0).isps).isSameAs(listIsps);
  }

  @Test
  void validateIsps_false() {
    TestImplementation testSubject = new TestImplementation(false);

    assertThat(testSubject.validateIsps(100, listIsps)).isFalse();

    assertThat(testSubject.calls).hasSize(1);
    assertThat(testSubject.calls.get(0).maxNumberIsps).isEqualTo(100);
    assertThat(testSubject.calls.get(0).isps).isSameAs(listIsps);
  }

  @FunctionalInterface
  public interface LoopWithIndexAndSizeConsumer<T> {

    void accept(T t, int i, int n);
  }

  public static <T> void forEach(Collection<T> collection,
                                 LoopWithIndexAndSizeConsumer<T> consumer) {
    int index = 0;
    for (T object : collection) {
      consumer.accept(object, index++, collection.size());
    }
  }
}
