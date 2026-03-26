package org.lfenergy.shapeshifter.core.common.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * A custom XML filter that trims whitespace from the values of XML element attributes.
 * <p>
 * This filter extends the {@link XMLFilterImpl} class, allowing it to intercept and modify
 * the XML parsing process. Specifically, it overrides the {@code startElement} method to
 * ensure that all attribute values are stripped of leading and trailing whitespace.
 * </p>
 */
class AttributeTrimmingFilter extends XMLFilterImpl {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        var trimmedAttributes = new AttributesImpl(attributes);

        for (var i = 0; i < trimmedAttributes.getLength(); i++) {
            var value = trimmedAttributes.getValue(i);
            trimmedAttributes.setValue(i, value != null ? value.trim() : null);
        }

        super.startElement(uri, localName, qName, trimmedAttributes);
    }
}
