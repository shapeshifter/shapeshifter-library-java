package eu.uftplib.web;

import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.validation.Validator;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class UftpController {

	@RequestMapping(value = "/api/messages", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String uftp(HttpEntity<String> httpEntity) {
		String xml = httpEntity.getBody();
		System.out.println(xml);
		if (!validateXML(xml)) return "ERROR";
		return "OK";
	}

	private boolean validateXML(String xml) {
		String xsdFilename = "";
		switch ("AGR") {
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