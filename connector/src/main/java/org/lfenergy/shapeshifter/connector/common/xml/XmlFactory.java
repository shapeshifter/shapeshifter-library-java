package org.lfenergy.shapeshifter.connector.common.xml;

import java.io.StringWriter;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringSource;

@Component
public class XmlFactory {

  public StringWriter newStringWriter() {
    return new StringWriter();
  }

  public StringSource newStringSource(String input) {
    return new StringSource(input);
  }
}
