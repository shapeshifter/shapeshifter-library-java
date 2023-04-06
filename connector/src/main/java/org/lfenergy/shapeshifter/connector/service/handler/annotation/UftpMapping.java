// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.Mapping;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface UftpMapping {

  @AliasFor("type")
  Class<? extends PayloadMessageType> value() default PayloadMessageType.class;

  @AliasFor("value")
  Class<? extends PayloadMessageType> type() default PayloadMessageType.class;
}