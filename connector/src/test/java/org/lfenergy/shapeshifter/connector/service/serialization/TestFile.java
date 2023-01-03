package org.lfenergy.shapeshifter.connector.service.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;

public class TestFile {

  private static File resourceAsFile(Class<?> testClass, String testName, String postFix) {
    File dir = new File("src/test/resources/data", toFolder(testClass));
    dir.mkdirs();
    return new File(dir, testName + postFix);
  }

  private static String toFolder(Class<?> testClass) {
    return testClass.getName().replace(".", "/");
  }

  public static String readResourceFileAsString(Class<?> testClass, String testName, String postFix) throws IOException {
    File file = resourceAsFile(testClass, testName, postFix);
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }

  public static void compareAsXml(Class<?> testClass, String testName, String actualXml) throws IOException {
    storeInActualXmlFile(testClass, testName, actualXml);
    compareWithExpectedTextFile(testClass, testName, "xml", actualXml);
  }

  private static void storeInActualXmlFile(Class<?> testClass, String testName, String actualXml) throws IOException {
    File file = resourceAsFile(testClass, testName, ".actual.xml");
    Files.writeString(file.toPath(), actualXml);
  }

  private static void compareWithExpectedTextFile(Class<?> testClass, String testName, String fileExt, String actualText) throws IOException {
    String expectedText = readResourceFileAsString(testClass, testName, ".expected." + fileExt);
    assertThat(actualText).isEqualToIgnoringWhitespace(expectedText);
  }
}
