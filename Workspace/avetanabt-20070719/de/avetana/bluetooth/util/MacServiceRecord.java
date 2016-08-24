package de.avetana.bluetooth.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;

import javax.bluetooth.DataElement;
import javax.bluetooth.UUID;

import de.avetana.bluetooth.sdp.LocalServiceRecord;

public class MacServiceRecord {

	  /**
	   * Transforms the service record object into an XML element and returns the string representation
	   * of this XML element. Useful for the Mac implementation
	   * @return The XML-based string representation of the service record
	   */


	  public static String getSDPRecordXML(LocalServiceRecord lsr) {
		  
		  int c = lsr.getChannelNumber();
		  if (c == 0) {
			  int prot = lsr.getProtocol();
			  if (prot == 1 || prot == 2)
				  lsr.updateChannelNumber(1);
			  else
				  lsr.updateChannelNumber(0x1001);
		  }
		  
	    try {
	      File f = null;
	      do {
	      	int counter = (int)(1000f * Math.random());
	      	f = new File (System.getProperty("java.io.tmpdir") + File.separator + "serviceRecord" + counter + ".xml");
	      } while (f.exists());

	      PElement plist = new PElement ("plist");
	      plist.setAttribute("version", "0.9");
	      PElement dict = new PElement ("dict");
	      plist.addChild(dict);
	      dict.addChild(new PElement ("key", "0000 - ServiceRecordHandle*"));
	      dict.addChild(new PElement ("integer", "65540"));
	      for (int i = 1;i < 65535;i++) {
	      	DataElement de = lsr.getAttributeValue(i);
	      	if (de == null) continue;
	      	PElement depe = makePElement (de);
	      	if (depe == null) continue;
	      	String attId = "" + Integer.toHexString(i);
	      	while (attId.length() < 4) attId = "0" + attId;
	        dict.addChild(new PElement ("key", attId));
	      	dict.addChild (depe);
	      }

	      FileOutputStream fos = new FileOutputStream (f);
	      plist.writeXML(fos);

	      return f.getAbsolutePath();
	    } catch (Exception e) {
	      e.printStackTrace();
	      return null;
	    }
	  }
	  
	  private static PElement makePElement (DataElement e) {
	  	PElement ret = null;
	  	switch (e.getDataType()) {
	  		case DataElement.STRING:
	 		case DataElement.URL:
	  			//try {
				//	return newDataElement (4, ((String)e.getValue()).getBytes("UTF-8"));
				//} catch (UnsupportedEncodingException e1) {
					return new PElement ("string", (String)e.getValue());
				//}
	 		case DataElement.U_INT_1:
	  			return newDataElement (1, 1, e.getLong());
	  		case DataElement.U_INT_2:
	  			return newDataElement (2, 1, e.getLong());
	  		case DataElement.U_INT_4:
	  			return newDataElement (4, 1, e.getLong());
	  		case DataElement.U_INT_8:
	  			return newDataElement (8, 1, e.getLong());
//	  		case DataElement.U_INT_16:
//	  			return newDataElement (16, 2, e.getLong());
	  		case DataElement.INT_1:
	  			return newDataElement (1, 2, e.getLong());
	  		case DataElement.INT_2:
	  			return newDataElement (2, 2, e.getLong());
	  		case DataElement.INT_4:
	  			return newDataElement (4, 2, e.getLong());
	  		case DataElement.INT_8:
	  			return newDataElement (8, 2, e.getLong());
//	  		case DataElement.INT_16:
//	  			return newDataElement (16, 2, e.getLong());
	  		case DataElement.UUID:
	  			return new PElement ("data", new String (Base64.encode (((UUID)e.getValue()).toByteArray())));
	  		case DataElement.DATSEQ:
	  			ret = new PElement ("array");
	  			Enumeration v = (Enumeration)e.getValue();
	  			while (v.hasMoreElements())
	  				ret.addChild(makePElement ((DataElement)v.nextElement()));
	  			
	  			return ret;
	  		default:
	  			System.err.println ("Unhandeled DataElement type" + e.getDataType());
	  	}
	  	return ret;
	  }
	  
	  private static PElement newDataElement (int size, int type, long value) {
		    PElement dict = new PElement ("dict");
		    dict.addChild(new PElement ("key", "DataElementSize"));
		    dict.addChild(new PElement ("integer", "" + size));
		    dict.addChild(new PElement ("key", "DataElementType"));
		    dict.addChild(new PElement ("integer", "" + type));
		    dict.addChild(new PElement ("key", "DataElementValue"));
		    dict.addChild(new PElement ("integer", "" + value));
		    return dict;
		  }

	  private static PElement newDataElement (int type, byte value[]) {
		      PElement dict = new PElement ("dict");
		      dict.addChild(new PElement ("key", "DataElementType"));
		      dict.addChild(new PElement ("integer", "" + type));
		      dict.addChild(new PElement ("key", "DataElementValue"));
		      dict.addChild(new PElement ("data", new String (Base64.encode(value))));
		      return dict;
		  }

}
