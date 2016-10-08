package br.com.staroski.obdjrp.io;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.com.staroski.obdjrp.obd2.OBD2Data;
import br.com.staroski.obdjrp.obd2.OBD2Package;
import br.com.staroski.obdjrp.obd2.OBD2Scan;

public class XmlSerializer {

	public static Document packageToDocument(OBD2Package dataPackage) throws IOException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document document = docBuilder.newDocument();
			Element obd2Element = document.createElement("obd2");

			Attr vinAttr = document.createAttribute("vin");
			vinAttr.setValue(dataPackage.getVIN());
			obd2Element.setAttributeNode(vinAttr);

			Attr timeAttr = document.createAttribute("time");
			timeAttr.setValue(String.valueOf(dataPackage.getTime()));
			obd2Element.setAttributeNode(timeAttr);

			for (OBD2Scan scanned : dataPackage.getScans()) {
				Element scanElement = document.createElement("scan");

				for (OBD2Data data : scanned.getData()) {
					Element dataElement = document.createElement("data");

					Attr pidAttr = document.createAttribute("pid");
					pidAttr.setValue(data.getPID());
					dataElement.setAttributeNode(pidAttr);

					Attr resultAttr = document.createAttribute("value");
					resultAttr.setValue(data.getValue());
					dataElement.setAttributeNode(resultAttr);

					scanElement.appendChild(dataElement);
				}
				obd2Element.appendChild(scanElement);
			}
			document.appendChild(obd2Element);
			return document;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static void writeTo(OutputStream output, OBD2Package dataPackage) throws IOException {
		try {
			Document document = packageToDocument(dataPackage);
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(output);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException(e);
		}
	}

	private XmlSerializer() {}
}
