package com.maxzxwd.autoruc.utils;

import org.springframework.lang.NonNull;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public final class XmlUtils {

    private static final DocumentBuilderFactory factory = JavaUtils.uncheck(() -> {

        var tmp = DocumentBuilderFactory.newInstance();
        tmp.setFeature("http://xml.org/sax/features/external-general-entities", false);
        tmp.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        return tmp;

    }, e -> new RuntimeException("Unable to create xml factory", e));

    private XmlUtils() {}

    public static @NonNull Document parse(@NonNull String content) throws ParserConfigurationException, IOException, SAXException {

        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(content)));
    }

}
