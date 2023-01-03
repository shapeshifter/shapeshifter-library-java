package org.lfenergy.shapeshifter.connector.common.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestFileHelper {

  public static String readXml(String fileName) {
    try {
      var url = TestFileHelper.class.getClassLoader().getResource(fileName);
      if (url == null) {
        throw new RuntimeException("Could not find: " + fileName + " on classpath");
      }
      return Files.readString(Path.of(url.toURI()), StandardCharsets.UTF_8);
    } catch (URISyntaxException | IOException ex) {
      throw new RuntimeException("Could not read test XML: " + ex.getMessage());
    }
  }
}
