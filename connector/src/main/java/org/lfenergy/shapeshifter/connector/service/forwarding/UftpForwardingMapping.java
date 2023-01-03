package org.lfenergy.shapeshifter.connector.service.forwarding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpMapping;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpOutgoingHandler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UftpForwardingMapping {

  private final Map<Class<? extends PayloadMessageType>, UftpHandlerMethod> incomingMethods = new HashMap<>();
  private final Map<Class<? extends PayloadMessageType>, UftpHandlerMethod> outgoingMethods = new HashMap<>();

  private final List<Object> incomingHandlers;
  private final List<Object> outgoingHandlers;

  public UftpForwardingMapping(@UftpIncomingHandler List<Object> incomingHandlers,
                               @UftpOutgoingHandler List<Object> outgoingHandlers) {
    this.incomingHandlers = incomingHandlers;
    this.outgoingHandlers = outgoingHandlers;
  }

  public Optional<UftpHandlerMethod> findIncomingHandler(Class<? extends PayloadMessageType> uftpType) {
    return Optional.ofNullable(incomingMethods.get(uftpType));
  }

  public Optional<UftpHandlerMethod> findOutgoingHandler(Class<? extends PayloadMessageType> uftpType) {
    return Optional.ofNullable(outgoingMethods.get(uftpType));
  }

  @PostConstruct
  protected void discover() {
    log.info("Discovering UFTP incoming methods... Found beans: {}", incomingHandlers);
    incomingHandlers.forEach(handler -> discover(handler).forEach(method -> this.registerIncoming(method, handler)));
    log.info("Finished discovering UFTP incoming methods... Result: {}", incomingMethods);

    log.info("Discovering UFTP outgoing methods... Found beans: {}", outgoingHandlers);
    outgoingHandlers.forEach(handler -> discover(handler).forEach(method -> this.registerOutgoing(method, handler)));
    log.info("Finished discovering UFTP outgoing methods... Result: {}", outgoingMethods);
  }

  private Stream<Method> discover(Object uftpController) {
    log.info("UFTP receiver {} methods... Found methods: {}", uftpController, uftpController.getClass().getDeclaredMethods());

    return Arrays.stream(uftpController.getClass().getDeclaredMethods())
          .filter(this::hasUftpMapping)
          .sorted((method1, method2) -> method1.getName().compareToIgnoreCase(method2.getName()));
  }

  private boolean hasUftpMapping(Method method) {
    return Arrays.stream(method.getAnnotations()).anyMatch(this::isUftpMapping);
  }

  private boolean isUftpMapping(Annotation annotation) {
    return annotation.annotationType().equals(UftpMapping.class) || inheritsFromUftpMapping(annotation);
  }

  private boolean inheritsFromUftpMapping(Annotation annotation) {
    return annotation.annotationType().isAnnotationPresent(UftpMapping.class);
  }

  private void registerIncoming(Method method, Object uftpController) {
    UftpMapping mapping = getUftpMapping(method);
    log.info("UFTP incoming {} register method: {} for type {}", uftpController, method.getName(), mapping.type());
    validateIncomingNotRegistered(method, mapping.type(), uftpController);
    validate(mapping, method);
    incomingMethods.put(mapping.type(), new UftpHandlerMethod(uftpController, method));
    log.info("UFTP incoming {} registered method {} successfully for type {}", uftpController, method.getName(), mapping.type());
  }

  private void registerOutgoing(Method method, Object uftpController) {
    UftpMapping mapping = getUftpMapping(method);
    log.info("UFTP outgoing {} register method: {} for type {}", uftpController, method.getName(), mapping.type());
    validateOutgoingNotRegistered(method, mapping.type(), uftpController);
    validate(mapping, method);
    outgoingMethods.put(mapping.type(), new UftpHandlerMethod(uftpController, method));
    log.info("UFTP outgoing {} registered method {} successfully for type {}", uftpController, method.getName(), mapping.type());
  }

  private void validate(UftpMapping mapping, Method method) {
    validateNotForAbstractType(mapping.type());
    validateMethodSignature(method, mapping.type());
  }

  private UftpMapping getUftpMapping(Method receivingMethod) {
    // Directly annotated with UftpMapping
    UftpMapping mapping = receivingMethod.getAnnotation(UftpMapping.class);
    if (mapping != null) {
      return mapping;
    }
    // Annotated with derived annotation
    Annotation derived = Arrays.stream(receivingMethod.getAnnotations()).filter(this::isUftpMapping).findFirst().orElseThrow(
        // Unreachable code because the presence of UftpMapping is forced earlier. Still useful in case someone fucks up.
        () -> new UftpConnectorException("No UftpMapping annotation found for method: " + receivingMethod.getName()));
    return derived.annotationType().getDeclaredAnnotation(UftpMapping.class);
  }

  private void validateNotForAbstractType(Class<? extends PayloadMessageType> type) {
    if (Modifier.isAbstract(type.getModifiers())) {
      throw new UftpConnectorException("Abstract type " + type.getName() + " is not supported");
    }
  }

  private void validateIncomingNotRegistered(Method receivingMethod, Class<? extends PayloadMessageType> type, Object uftpController) {
    if (incomingMethods.containsKey(type)) {
      var existing = incomingMethods.get(type);
      throw new UftpConnectorException(
          "Method " + existing.bean().getClass().getName() + ":" + existing.method().getName() +
              " is already registered to handle incoming type " + type.getName() +
              ". Found second method: " + uftpController.getClass().getName() + ":" + receivingMethod.getName());
    }
  }

  private void validateOutgoingNotRegistered(Method receivingMethod, Class<? extends PayloadMessageType> type, Object uftpController) {
    if (outgoingMethods.containsKey(type)) {
      var existing = outgoingMethods.get(type);
      throw new UftpConnectorException(
          "Method " + existing.bean().getClass().getName() + ":" + existing.method().getName() +
              " is already registered to handle outgoing type " + type.getName() +
              ". Found second method: " + uftpController.getClass().getName() + ":" + receivingMethod.getName());
    }
  }

  private void validateMethodSignature(Method method, Class<? extends PayloadMessageType> type) {
    if (!isCorrectMethodSignature(method, type)) {
      // Make application stop ?
      throw new UftpConnectorException(
          "Method " + method.getName() + " signature must be "
              + "\"void " + method.getName() + "(" + UftpParticipant.class.getName() + ", " + type.getName() + ")\". "
              + "Found signature: \"" + method + "\"");
    }
  }

  private boolean isCorrectMethodSignature(Method method, Class<? extends PayloadMessageType> type) {
    return method.getReturnType() == void.class
        && method.getParameterCount() == 2
        && method.getParameters()[0].getType() == UftpParticipant.class
        && method.getParameters()[1].getType() == type;
  }
}
