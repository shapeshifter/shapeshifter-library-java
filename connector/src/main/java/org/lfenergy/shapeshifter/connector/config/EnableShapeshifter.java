package org.lfenergy.shapeshifter.connector.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.lfenergy.shapeshifter.connector.service.receiving.UftpInternalController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Enables scanning for shapeshifter-connector components (controllers, handlers and services)
 * If you want to make use of the {@link UftpInternalController}, {@link EnableWebMvc} is required
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(UftpConnectorConfiguration.class)
public @interface EnableShapeshifter {
}
