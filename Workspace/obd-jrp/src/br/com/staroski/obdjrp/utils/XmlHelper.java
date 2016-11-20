package br.com.staroski.obdjrp.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

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

import br.com.staroski.obdjrp.Config;
import br.com.staroski.obdjrp.data.Data;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;

public final class XmlHelper {

	public static Document packageToDocument(Package dataPackage) throws IOException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document document = docBuilder.newDocument();
			Element obd2Element = document.createElement("package");

			Attr vinAttr = document.createAttribute("vehicle");
			vinAttr.setValue(dataPackage.getVehicle());
			obd2Element.setAttributeNode(vinAttr);

			Attr timeAttr = document.createAttribute("time");
			timeAttr.setValue(Config.get().formatted(new Date(dataPackage.getTime())));
			obd2Element.setAttributeNode(timeAttr);

			for (Scan scanned : dataPackage.getScans()) {
				Element scanElement = document.createElement("scan");

				Attr scanTimeAttr = document.createAttribute("time");
				scanTimeAttr.setValue(Config.get().formatted(new Date(scanned.getTime())));
				scanElement.setAttributeNode(scanTimeAttr);

				for (Data data : scanned.getData()) {
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

	public static void writeTo(OutputStream output, Package dataPackage) throws IOException {
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

	private XmlHelper() {}
}
