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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class UftpSigningServiceImplementation implements UftpSigningService {

    private String role;

    public UftpSigningServiceImplementation(String role) {
        this.role = role;
    }

    public String sealMessage(String message, String privateKey, DomainPair domainPair) {
        LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
        try {
            var body = lazySodium.cryptoSign(message, Key.fromHexString(privateKey));
            var base64Body = Base64.getEncoder().encodeToString(body.getBytes());

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
            var base64Body = document.getDocumentElement().getAttribute("Body");
            var body = new String(Base64.getDecoder().decode(base64Body));
            LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
            return lazySodium.cryptoSignOpen(body, Key.fromHexString(publicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public KeyPair generateKeyPair() {
        LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
        try {
            var keyPair = lazySodium.cryptoSignKeypair();
            return new KeyPair(keyPair.getPublicKey().getAsHexString(), keyPair.getSecretKey().getAsHexString());
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
