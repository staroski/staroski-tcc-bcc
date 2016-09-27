package br.com.staroski.obdjrp.obd2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

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

public final class OBD2DataPackage {

	public static OBD2DataPackage readFrom(InputStream input) throws IOException {
		OBD2DataPackage dataPackage = new OBD2DataPackage();
		// TODO
		return dataPackage;
	}

	private final List<OBD2Data> dataPackage = new LinkedList<>();
	private String vin;

	private long time;

	private OBD2DataPackage() {
		this(null, -1);
	}

	OBD2DataPackage(String vin, long time) {
		this.vin = vin == null ? "UNKNOWN" : vin;
		this.time = time;
	}

	public List<OBD2Data> getDataList() {
		return dataPackage;
	}

	public long getTime() {
		return time;
	}

	public String getVIN() {
		return vin;
	}

	public Document toXML() throws IOException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document document = docBuilder.newDocument();
			Element obd2Element = document.createElement("obd2");
			document.appendChild(obd2Element);

			Attr vinAttr = document.createAttribute("vin");
			vinAttr.setValue(getVIN());
			obd2Element.setAttributeNode(vinAttr);

			Attr timeAttr = document.createAttribute("time");
			timeAttr.setValue(String.valueOf(getTime()));
			obd2Element.setAttributeNode(timeAttr);

			for (OBD2Data data : getDataList()) {
				Element dataElement = document.createElement("data");
				obd2Element.appendChild(dataElement);

				Attr pidAttr = document.createAttribute("pid");
				pidAttr.setValue(data.getPID());
				dataElement.setAttributeNode(pidAttr);

				Attr resultAttr = document.createAttribute("result");
				resultAttr.setValue(data.getResult());
				dataElement.setAttributeNode(resultAttr);
			}
			return document;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void writeTo(OutputStream output) throws IOException {
		try {
			Document xml = toXML();
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

	void add(OBD2Data data) {
		this.dataPackage.add(data);
	}
}
