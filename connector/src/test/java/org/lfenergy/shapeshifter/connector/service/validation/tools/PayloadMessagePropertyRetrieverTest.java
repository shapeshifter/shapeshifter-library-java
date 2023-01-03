package org.lfenergy.shapeshifter.connector.service.validation.tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.allMessageTypes;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertException;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.flexMessageTypes;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.lfenergy.shapeshifter.api.DPrognosis;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexReservationUpdate;
import org.lfenergy.shapeshifter.api.Metering;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PayloadMessagePropertyRetrieverTest {

  private static final String TIME_ZONE = "TIME_ZONE";

  private final PayloadMessagePropertyRetriever<PayloadMessageType, String> testSubject = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexMessageType.class, (m) -> ((FlexMessageType) m).getTimeZone(),
          Metering.class, (m) -> ((Metering) m).getTimeZone()
      )
  );

  @ParameterizedTest
  @ValueSource(classes = {
      FlexMessageType.class,
      Metering.class
  })
  void typeInMap_matches_sameType(Class<? extends PayloadMessageType> type) {
    assertThat(testSubject.typeInMap(type)).isTrue();
  }

  public static Stream<Arguments> flexMessageTypesInMap() {
    return flexMessageTypes().stream().map(Arguments::of);
  }

  @ParameterizedTest
  @MethodSource("flexMessageTypesInMap")
  void typeInMap_matches_derivedType(Class<? extends PayloadMessageType> type) {
    assertThat(testSubject.typeInMap(type)).isTrue();
  }

  public static Stream<Class<? extends PayloadMessageType>> appliesToStream() {
    return Stream.concat(
        flexMessageTypes().stream(),
        Stream.of(Metering.class, FlexMessageType.class)
    );
  }

  public static Stream<Arguments> notAppliesToTypes() {
    var allowed = appliesToStream().collect(Collectors.toSet());
    return allMessageTypes().stream()
                            .filter(t -> !allowed.contains(t))
                            .map(Arguments::of);
  }

  @ParameterizedTest
  @MethodSource("notAppliesToTypes")
  void typeInMap_notMatches_otherTypes(Class<? extends PayloadMessageType> type) {
    assertThat(testSubject.typeInMap(type)).isFalse();
  }

  @Test
  void getSameTypeMappingSecondCall() {
    // See code coverage in getTypeMapping()
    assertThat(testSubject.typeInMap(FlexMessageType.class)).isTrue();
    assertThat(testSubject.typeInMap(FlexMessageType.class)).isTrue();
  }

  public static Stream<Arguments> validInstances() {
    FlexMessageType flexMessage = new DPrognosis();
    flexMessage.setTimeZone(TIME_ZONE);

    DPrognosis dPrognosis = new DPrognosis();
    dPrognosis.setTimeZone(TIME_ZONE);

    FlexReservationUpdate flexReservationUpdate = new FlexReservationUpdate();
    flexReservationUpdate.setTimeZone(TIME_ZONE);

    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setTimeZone(TIME_ZONE);

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setTimeZone(TIME_ZONE);

    FlexOrder flexOrder = new FlexOrder();
    flexOrder.setTimeZone(TIME_ZONE);

    Metering meteringMessage = new Metering();
    meteringMessage.setTimeZone(TIME_ZONE);

    return Stream.of(
        Arguments.of(flexMessage),
        Arguments.of(dPrognosis),
        Arguments.of(flexReservationUpdate),
        Arguments.of(flexRequest),
        Arguments.of(flexOffer),
        Arguments.of(flexOrder),
        Arguments.of(meteringMessage)
    );
  }

  @ParameterizedTest
  @MethodSource("validInstances")
  void getParameter_knownType(PayloadMessageType message) {
    assertThat(testSubject.getOptionalProperty(message)).isPresent().contains(TIME_ZONE);
  }

  @Test
  void getParameter_knownType_noValueOnParameter() {
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setTimeZone(null);

    assertThat(testSubject.getOptionalProperty(flexRequest)).isEmpty();
  }

  @Test
  void getParameter_unknownType_throws() {
    TestMessage test = new TestMessage();

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.getOptionalProperty(test));

    assertException(actual, "Unexpected payload message type in validation: class org.lfenergy.shapeshifter.api.TestMessage");
  }

  @Test
  void getParameter_null_throws() {
    FlexRequest nill = null;

    assertThrows(NullPointerException.class, () -> testSubject.getOptionalProperty(nill));
  }

  @ParameterizedTest
  @MethodSource("validInstances")
  void getMethodResult_knownType(PayloadMessageType message) {
    assertThat(testSubject.getProperty(message)).isEqualTo(TIME_ZONE);
  }

  @Test
  void getMethodResult_knownType_noValueOnParameter() {
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setTimeZone(null);

    UftpConnectorException thrown = assertThrows(UftpConnectorException.class,
                                                 () -> testSubject.getProperty(flexRequest));

    assertException(thrown, "Lambda returned null for: class org.lfenergy.shapeshifter.api.FlexRequest");
  }

  @Test
  void getMethodResult_unknownType_throws() {
    TestMessage test = new TestMessage();

    UftpConnectorException thrown = assertThrows(UftpConnectorException.class, () -> testSubject.getProperty(test));

    assertException(thrown, "Unexpected payload message type in validation: class org.lfenergy.shapeshifter.api.TestMessage");
  }

  @Test
  void getMethodResult_null_throws() {
    FlexRequest nill = null;

    assertThrows(NullPointerException.class, () -> testSubject.getProperty(nill));
  }
}