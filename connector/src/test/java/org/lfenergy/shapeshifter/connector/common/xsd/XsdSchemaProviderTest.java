package org.lfenergy.shapeshifter.connector.common.xsd;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lfenergy.shapeshifter.connector.common.xml.TestFileHelper.readXml;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.xml.transform.StringSource;
import org.xml.sax.SAXParseException;

@ExtendWith(MockitoExtension.class)
class XsdSchemaProviderTest {

  private static final String XXE_ATTACK = "xml/xxe/FlexRequestResponse_with_XXE_Attack.xml";
  private static final String XXE_ATTACK_SSRF = "xml/xxe/FlexRequestResponse_with_XXE_Attack_SSRF.xml";

  @Mock
  private XsdFactory xsdFactory;

  @InjectMocks
  private XsdSchemaProvider testSubject;

  @Mock
  private File xsd1File, xsd2File;
  @Mock
  private InputStream xsd1Stream, xsd2Stream;
  @Mock
  private XsdSchemaPool pool1, pool2;
  @Mock
  private Schema schema1, schema2;
  @Mock
  private Validator validator;

  @AfterEach
  void noMore() {
    Mockito.verifyNoMoreInteractions(
        xsdFactory,
        xsd1File,
        xsd2File,
        xsd1Stream,
        xsd2Stream,
        pool1,
        pool2,
        schema1,
        schema2
    );
  }

  @Test
  void multipleCalls() throws Exception {
    var xsd1 = new URL("file:///one.xsd");
    var xsd2 = new URL("file:///two.xsd");

    given(xsdFactory.newXsdSchemaPool(xsd1)).willReturn(pool1);
    given(pool1.claim()).willReturn(schema1);
    given(schema1.newValidator()).willReturn(validator);

    given(xsdFactory.newXsdSchemaPool(xsd2)).willReturn(pool2);
    given(pool2.claim()).willReturn(schema2);
    given(schema2.newValidator()).willReturn(validator);

    testSubject.getValidator(xsd1);
    testSubject.getValidator(xsd1);

    testSubject.getValidator(xsd2);
    testSubject.getValidator(xsd2);
    testSubject.getValidator(xsd2);

    verify(xsdFactory).newXsdSchemaPool(xsd1);
    verify(pool1, times(2)).claim();
    verify(schema1, times(2)).newValidator();
    verify(pool1, times(2)).release(schema1);

    verify(xsdFactory).newXsdSchemaPool(xsd2);
    verify(pool2, times(3)).claim();
    verify(schema2, times(3)).newValidator();
    verify(pool2, times(3)).release(schema2);
  }

  @Test
  void test_violate_XXE_then_fail() throws Exception {
    doTestXXE(XXE_ATTACK,
              "External Entity: Failed to read external document 'data', because 'file' access is not allowed due to restriction set by the accessExternalDTD property");
  }

  @Test
  void test_violate_XXE_SSRF_then_fail() throws Exception {
    doTestXXE(XXE_ATTACK_SSRF,
              "Failed to read external document '', because 'http' access is not allowed due to restriction set by the accessExternalDTD property");
  }

  private void doTestXXE(String fileName, String errorMessage) {
    var xsdFactory = new XsdFactory(new XsdSchemaFactoryPool());
    var xsdSchemaProvider = new XsdSchemaProvider(xsdFactory);
    var xsdUrl = this.getClass().getClassLoader().getResource("UFTP.xsd");
    var validator = xsdSchemaProvider.getValidator(xsdUrl);
    var xml = readXml(fileName);
    assertThatThrownBy(() -> validator.validate(new StringSource(xml)))
        .isInstanceOf(SAXParseException.class)
        .hasMessageContaining(errorMessage);
  }
}
