package eu.uftplib.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.utils.Key;
import com.goterl.lazycode.lazysodium.utils.Base64MessageEncoder;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class UftpSigningServiceImplementation implements UftpSigningService {

    private String role;
    private LazySodiumJava lazySodium;

    public UftpSigningServiceImplementation(String role) {
        this.role = role;
        this.lazySodium = new LazySodiumJava(new SodiumJava(), new Base64MessageEncoder());
    }

    public String sealMessage(String message, String privateKey, DomainPair domainPair) {
        try {
            String base64Body = lazySodium.cryptoSign(message, privateKey);
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document signedMessage = documentBuilder.newDocument();

            // root element
            Element root = signedMessage.createElement("SignedMessage");
            Attr senderDomainAttribute = signedMessage.createAttribute("SenderDomain");
            senderDomainAttribute.setValue(domainPair.getSenderDomain());
            root.setAttributeNode(senderDomainAttribute);
            Attr senderRoleAttribute = signedMessage.createAttribute("SenderRole");
            senderRoleAttribute.setValue(role);
            root.setAttributeNode(senderRoleAttribute);
            Attr bodyAttribute = signedMessage.createAttribute("Body");
            bodyAttribute.setValue(base64Body);
            root.setAttributeNode(bodyAttribute);
            signedMessage.appendChild(root);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(signedMessage), new StreamResult(writer));
            return writer.toString();

        } catch (SodiumException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String unsealMessage(String message, String publicKey) {
        try {
            var document = loadXMLFromString(message);
            String base64Body = document.getDocumentElement().getAttribute("Body");
            return lazySodium.cryptoSignOpen(base64Body, Key.fromBase64String(publicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public KeyPair generateKeyPair() {
        LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
        try {
            com.goterl.lazycode.lazysodium.utils.KeyPair keyPair = lazySodium.cryptoSignKeypair();
            String base64Public = Base64.getEncoder().encodeToString(keyPair.getPublicKey().getAsBytes());
            String base64Secret = Base64.getEncoder().encodeToString(keyPair.getSecretKey().getAsBytes());
            return new KeyPair(base64Public, base64Secret);
        } catch (SodiumException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

}
