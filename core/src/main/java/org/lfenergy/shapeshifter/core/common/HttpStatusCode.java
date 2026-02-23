package org.lfenergy.shapeshifter.core.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpStatusCode {

  public static final HttpStatusCode TEMPORARY_REDIRECT = new HttpStatusCode(307);
  public static final HttpStatusCode PERMANENT_REDIRECT =  new HttpStatusCode(308);

  public static final HttpStatusCode BAD_REQUEST =  new HttpStatusCode(400);
  public static final HttpStatusCode UNAUTHORIZED =  new HttpStatusCode(401);
  public static final HttpStatusCode CONFLICT =  new HttpStatusCode(409);

  public static final HttpStatusCode INTERNAL_SERVER_ERROR =  new HttpStatusCode(500);
  public static final HttpStatusCode NOT_IMPLEMENTED =  new HttpStatusCode(501);

  int value;

  @Override
  public String toString() {
    return Integer.toString(value);
  }

  public static HttpStatusCode valueOf(int value) {
    return new HttpStatusCode(value);
  }

  public boolean isSuccess() {
    return value >= 200 && value < 300;
  }

  public boolean isClientError() {
    return value >= 400 && value < 500;
  }

  public boolean isServerError() {
    return value >= 500;
  }

  public boolean isRedirect() {
    return value >= 300 && value < 400;
  }
}
