package org.lfenergy.shapeshifter.spring.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.lfenergy.shapeshifter.spring.service.receiving.UftpInternalController;
import org.springframework.context.annotation.Import;

/**
 * Enables scanning for shapeshifter-connector components (controllers, handlers and services).<br/> If you want to make use of the {@link UftpInternalController},
 * <code>EnableWebMvc</code> is required
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ShapeshifterConfiguration.class)
public @interface EnableShapeshifter {
}
