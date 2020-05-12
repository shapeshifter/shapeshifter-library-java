package eu.uftplib.service;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Validator;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import generated.TestMessageType;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Service
public class UftpValidationServiceImplementation implements UftpValidationService {

    public String validateXml(String xml) {
        String domain = null;
        String xsdFilename1 = "src/main/schemas/UFTP-V1.01-agr.xsd";
        String xsdFilename2 = "src/main/schemas/UFTP-V1.01-cro.xsd";
        String xsdFilename3 = "src/main/schemas/UFTP-V1.01-dso.xsd";
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        StringReader reader = new StringReader(xml);
        try {
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(xsdFilename1),
                    new StreamSource(xsdFilename2), new StreamSource(xsdFilename3)});
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(reader));
            domain = getDomainFromRequest(xml);

        } catch (SAXException e) {
            System.out.println( "Request is NOT valid reason:" + e);
        } catch (IOException e) {}
        return domain;
    }

    private String getDomainFromRequest(String xml) {
        String senderDomain = null;
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(TestMessageType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            TestMessageType testMessageType = (TestMessageType) unmarshaller.unmarshal(reader);
            senderDomain = testMessageType.getSenderDomain();
        } catch (JAXBException e) {
            System.out.println( "Request is NOT valid reason:" + e);
            e.printStackTrace();
        }
        return senderDomain;
    }
}
