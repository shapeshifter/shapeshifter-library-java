package eu.uftplib.service;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Validator;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UftpValidationServiceImplementation implements UftpValidationService {

    private String role;

    public UftpValidationServiceImplementation(String role) {
        this.role = role;
    }

    public DomainPair validateXml(String xml, MessageDirection messageDirection) {
        DomainPair domainPair = null;
        String xsdFilename = null;
        switch (role) {
            case "AGR":
                xsdFilename = "UFTP-V1.01-agr.xsd";
                break;
            case "DSO":
                xsdFilename = "UFTP-V1.01-dso.xsd";
                break;
            case "CRO":
                xsdFilename = "UFTP-V1.01-cro.xsd";
                break;
        }
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        StringReader reader = new StringReader(xml);
        try {
            Schema schema = schemaFactory.newSchema(getClass().getClassLoader().getResource(xsdFilename));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(reader));
            domainPair = getDomainPairFromRequest(xml);
        } catch (SAXException e) {
            System.out.println("Request is NOT valid reason:" + e);
        } catch (IOException e) {
        }
        return domainPair;
    }

    private DomainPair getDomainPairFromRequest(String xml) {
        try {
            Document document = loadXMLFromString(xml);
            //var messageType = document.getDocumentElement().getNodeName();
            return new DomainPair(document.getDocumentElement().getAttribute("SenderDomain"), document.getDocumentElement().getAttribute("RecipientDomain"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

        // String senderDomain = null;
        // JAXBContext jaxbContext = null;
        // try {
        //     jaxbContext = JAXBContext.newInstance(TestMessageType.class);
        //     Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        //     StringReader reader = new StringReader(xml);
        //     TestMessageType testMessageType = (TestMessageType) unmarshaller.unmarshal(reader);
        //     senderDomain = testMessageType.getSenderDomain();
        // } catch (JAXBException e) {
        //     System.out.println( "Request is NOT valid reason:" + e.getMessage());
        //     //e.printStackTrace();
        // }
        // return senderDomain;
    }

    private static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}
