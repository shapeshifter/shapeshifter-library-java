package eu.uftplib.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.validation.Validator;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

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
            return domain;
        } catch (IOException e) {}
        return "DomainA";
    }

    private String getDomainFromRequest(String xml) {
        return null;
    }
}
