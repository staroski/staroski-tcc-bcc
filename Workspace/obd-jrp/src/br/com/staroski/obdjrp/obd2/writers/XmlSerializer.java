package br.com.staroski.obdjrp.obd2.writers;

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
import br.com.staroski.obdjrp.obd2.OBD2DataPackage;
import br.com.staroski.obdjrp.obd2.OBD2DataScan;

public class XmlSerializer {

	public static Document toXML(OBD2DataPackage dataPackage) throws IOException {
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

			for (OBD2DataScan scanned : dataPackage.getScannedData()) {
				Element scanElement = document.createElement("scanned");

				for (OBD2Data data : scanned.getDataList()) {
					Element dataElement = document.createElement("data");

					Attr pidAttr = document.createAttribute("pid");
					pidAttr.setValue(data.getPID());
					dataElement.setAttributeNode(pidAttr);

					Attr resultAttr = document.createAttribute("result");
					resultAttr.setValue(data.getResult());
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

	public static void writeTo(OutputStream output, OBD2DataPackage dataPackage) throws IOException {
		try {
			Document xml = toXML(dataPackage);
			DOMSource source = new DOMSource(xml);
			StreamResult result = new StreamResult(output);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException(e);
		}
	}

	private XmlSerializer() {}
}
