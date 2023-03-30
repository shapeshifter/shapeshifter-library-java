// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

/**
 * Annotation that defines a class or argument as an outgoing message handler.
 *
 * <pre>
 * @UftpOutgoingHandler
 * public class OutgoingMessageHandler {
 *
 *   // implement your business logic here
 *
 * }
 * </pre>
 */
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Controller
public @interface UftpOutgoingHandler {

  @AliasFor(
      annotation = Controller.class
  )
  String value() default "";
}
