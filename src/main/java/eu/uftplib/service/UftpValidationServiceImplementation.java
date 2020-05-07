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

import org.xml.sax.SAXException;

public class UftpValidationServiceImplementation implements UftpValidationService {
    private String role;

    public UftpValidationServiceImplementation(String role) {
        this.role = role;
    }

    public boolean validateXml(String xml) {
		String xsdFilename = "";
		switch (this.role) {
			case "AGR":
				xsdFilename = "/UFTP-V1.01-agr.xsd"; 
				break;
			case "CRO":
				xsdFilename = "/UFTP-V1.01-cro.xsd"; 
				break;
			case "DSO":
				xsdFilename = "/UFTP-V1.01-dso.xsd"; 
				break;
		
			default:
				break;
		}
		File schemaFile = new File(xsdFilename);
		Source xmlFile = new StreamSource(new File("web.xml"));
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		StringReader reader = new StringReader(xml);
		try {
  			Schema schema = schemaFactory.newSchema(schemaFile);
  			Validator validator = schema.newValidator();
  			validator.validate(new StreamSource(reader));
			System.out.println(xmlFile.getSystemId() + " is valid");
			return true;
		} catch (SAXException e) {
  			System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
		} catch (IOException e) {}
		return false;
    }
}
