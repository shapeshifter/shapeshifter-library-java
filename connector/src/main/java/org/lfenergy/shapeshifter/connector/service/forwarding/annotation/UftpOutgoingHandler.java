package org.lfenergy.shapeshifter.connector.service.forwarding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

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
