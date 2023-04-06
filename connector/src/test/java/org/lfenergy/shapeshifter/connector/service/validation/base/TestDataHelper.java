// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.lfenergy.shapeshifter.api.AvailableRequestedType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderISPType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.springframework.util.CollectionUtils;

public class TestDataHelper {

  public static final String TIME_ZONE_MEXICO_CITY = "America/Mexico_City";
  public static final String TIME_ZONE_AMSTERDAM = "Europe/Amsterdam";
  public static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(10.0);
  public static final BigDecimal DEFAULT_MIN_ACTIVATION_FACTOR = BigDecimal.valueOf(1.0);
  public static final OffsetDateTime TOMORROW = OffsetDateTime.now().plusDays(1);

  public static final String DEFAULT_OPTION_REFERENCE = "123";
  public static final String DEFAULT_CONTRACT_ID = "CONTRACT-ID";
  public static final String DEFAULT_BASE_LINE_REFERENCE = "BASELINE_REFERENCE";
  public static final String DEFAULT_CURRENCY = "EUR";

  /*********************************************************
   * FLEX REQUEST
   **********************************************************/
  public static FlexRequest flexRequest() {
    return flexRequest(UUID.randomUUID().toString(), OffsetDateTime.now().plusDays(1));
  }

  public static FlexRequest flexRequest(String messageId, OffsetDateTime expirationDateTime) {
    var flexRequest = new FlexRequest();
    flexRequest.setMessageID(messageId);
    flexRequest.setContractID(DEFAULT_CONTRACT_ID);
    flexRequest.setRevision(1);
    flexRequest.setExpirationDateTime(expirationDateTime);
    return flexRequest;
  }

  /*********************************************************
   * FLEX REQUEST ISP
   **********************************************************/
  public static FlexRequestISPType flexRequestIsp(AvailableRequestedType disposition, long start, long duration, long minPower, long maxPower) {
    var isp = new FlexRequestISPType();
    isp.setDisposition(disposition);
    isp.setStart(start);
    isp.setDuration(duration);
    isp.setMinPower(minPower);
    isp.setMaxPower(maxPower);
    return isp;
  }

  /*********************************************************
   * FLEX OFFER
   **********************************************************/
  public static FlexOffer flexOffer() {
    return flexOffer(DEFAULT_MIN_ACTIVATION_FACTOR);
  }

  public static FlexOffer flexOffer(BigDecimal minActivationFactor) {
    return flexOffer(UUID.randomUUID().toString(), flexOfferOptions(minActivationFactor), TOMORROW);
  }

  public static FlexOffer flexOffer(String flexRequestMessageId, List<FlexOfferOptionType> flexOfferOptions) {
    return flexOffer(flexRequestMessageId, flexOfferOptions, TOMORROW);
  }

  public static FlexOffer flexOffer(String flexRequestMessageId, List<FlexOfferOptionType> flexOfferOptions, OffsetDateTime expirationDate) {
    var flexOffer = new FlexOffer();
    flexOffer.setContractID(DEFAULT_CONTRACT_ID);
    flexOffer.setBaselineReference(DEFAULT_BASE_LINE_REFERENCE);
    flexOffer.setExpirationDateTime(expirationDate);
    flexOffer.setCurrency(DEFAULT_CURRENCY);
    flexOffer.setFlexRequestMessageID(flexRequestMessageId);
    if (!CollectionUtils.isEmpty(flexOfferOptions)) {
      flexOfferOptions.forEach(
          it -> flexOffer.getOfferOptions().add(it));
    }
    return flexOffer;
  }

  /*********************************************************
   * FLEX OFFER OPTION
   **********************************************************/
  public static FlexOfferOptionType flexOfferOption(String optionReference, BigDecimal minActivationFactor, BigDecimal price, List<FlexOfferOptionISPType> offerOptions) {
    var flexOfferOption = new FlexOfferOptionType();
    flexOfferOption.setOptionReference(optionReference);
    flexOfferOption.setPrice(price);
    flexOfferOption.setMinActivationFactor(minActivationFactor);
    flexOfferOption.getISPS().addAll(offerOptions);
    return flexOfferOption;
  }


  /*********************************************************
   * FLEX OFFER ISP
   **********************************************************/
  public static FlexOfferOptionISPType flexOfferOptionIsp(long start, long duration, long power) {
    var isp = new FlexOfferOptionISPType();
    isp.setStart(start);
    isp.setDuration(duration);
    isp.setPower(power);
    return isp;
  }

  public static List<FlexOfferOptionType> flexOfferOptions() {
    return flexOfferOptions(DEFAULT_MIN_ACTIVATION_FACTOR);
  }

  public static List<FlexOfferOptionType> flexOfferOptions(BigDecimal minActivationFactor) {
    return List.of(flexOfferOption(minActivationFactor));
  }

  public static FlexOfferOptionType flexOfferOption(BigDecimal minActivationFactor) {
    return flexOfferOption(minActivationFactor, DEFAULT_PRICE);
  }

  public static FlexOfferOptionType flexOfferOption(BigDecimal minActivationFactor, BigDecimal price) {
    return flexOfferOption(UUID.randomUUID().toString(), minActivationFactor, price);
  }

  public static FlexOfferOptionType flexOfferOption(String optionReference, BigDecimal minActivationFactor, BigDecimal price) {
    return flexOfferOption(optionReference, minActivationFactor, price, flexOfferOptionIsps());
  }

  public static FlexOfferOptionType flexOfferOption(List<FlexOfferOptionISPType> flexOfferOptionIsps) {
    return flexOfferOption(DEFAULT_OPTION_REFERENCE, DEFAULT_MIN_ACTIVATION_FACTOR, DEFAULT_PRICE, flexOfferOptionIsps);
  }

  public static FlexOfferOptionType flexOfferOption(BigDecimal price, List<FlexOfferOptionISPType> flexOfferOptionIsps) {
    return flexOfferOption(DEFAULT_OPTION_REFERENCE, DEFAULT_MIN_ACTIVATION_FACTOR, price, flexOfferOptionIsps);
  }

  public static ArrayList<FlexOfferOptionISPType> flexOfferOptionIsps() {
    return new ArrayList<>(List.of(
        flexOfferOptionIsp(12, 1, 2000000),
        flexOfferOptionIsp(13, 1, 2500000),
        flexOfferOptionIsp(14, 1, 2500000),
        flexOfferOptionIsp(15, 1, 2000000),
        flexOfferOptionIsp(16, 1, 2000000),
        flexOfferOptionIsp(17, 1, 2500000),
        flexOfferOptionIsp(18, 1, 1000000)));
  }

  /*********************************************************
   * FLEX ORDER
   **********************************************************/
  public static FlexOrder flexOrder(String flexOfferMessageId) {
    return flexOrder(flexOfferMessageId, DEFAULT_PRICE, DEFAULT_OPTION_REFERENCE);
  }

  public static FlexOrder flexOrder(String flexOfferMessageId, BigDecimal price, String optionReference) {
    var flexOrder = new FlexOrder();
    flexOrder.setContractID(DEFAULT_CONTRACT_ID);
    flexOrder.setBaselineReference(DEFAULT_BASE_LINE_REFERENCE);
    flexOrder.setCurrency(DEFAULT_CURRENCY);
    flexOrder.setFlexOfferMessageID(flexOfferMessageId);
    flexOrder.setPrice(price);
    flexOrder.setOptionReference(optionReference);
    return flexOrder;
  }


  /*********************************************************
   * FLEX ORDER ISP
   **********************************************************/
  public static FlexOrderISPType flexOrderIsp(long start, long duration, long power) {
    var isp = new FlexOrderISPType();
    isp.setStart(start);
    isp.setDuration(duration);
    isp.setPower(power);
    return isp;
  }

  public static ArrayList<FlexOrderISPType> flexOrderIsps() {
    return new ArrayList<FlexOrderISPType>(List.of(
        flexOrderIsp(12, 1, 2000000),
        flexOrderIsp(13, 1, 2500000),
        flexOrderIsp(14, 1, 2500000),
        flexOrderIsp(15, 1, 2000000),
        flexOrderIsp(16, 1, 2000000),
        flexOrderIsp(17, 1, 2500000),
        flexOrderIsp(18, 1, 1000000),
        flexOrderIsp(19, 1, 1500000)
    ));
  }

  public static String messageId() {
    return UUID.randomUUID().toString();
  }
}