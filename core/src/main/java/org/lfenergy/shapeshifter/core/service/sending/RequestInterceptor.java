package org.lfenergy.shapeshifter.core.service.sending;

import java.net.http.HttpRequest;
import java.util.function.Consumer;

/**
 * Interceptor that can modify an {@link HttpRequest.Builder} before the request is built and sent.
 * <p>
 * Example usage:
 * <pre>{@code
 * service.addRequestInterceptor(request -> request.timeout(Duration.ofSeconds(30)));
 * }</pre>
 */
@FunctionalInterface
public interface RequestInterceptor extends Consumer<HttpRequest.Builder> {

}
