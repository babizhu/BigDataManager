package com.bbz.bigdata.platform.rrdtool.resultparser;

import com.alibaba.fastjson.JSON;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.xmlmodel.DataXMLModel;
import com.bbz.bigdata.platform.rrdtool.xmlmodel.FullXMLModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class XMLResultParser {

	private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	/**
	 * 解析查询到的rrd数据的xml格式字符串
	 * @param xml
	 * @return
	 */
	public static FullXMLModel parse(String xml) {
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document dom = builder.parse(new ByteArrayInputStream(xml.getBytes()) );
			Element root = dom.getDocumentElement();
			FullXMLModel resultModel=new FullXMLModel();
			NodeList nodeList = root.getChildNodes();
			Node metaNode=null,dataNode=null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String name = node.getNodeName();
				if (name.equals("meta")) {
					metaNode=node;
				}else if(name.equals("data")){
					dataNode=node;
				}
			}
			Node legendNode=null,gprintsNode=null;
			for (int i = 0; i < metaNode.getChildNodes().getLength(); i++) {
				Node node = metaNode.getChildNodes().item(i);
				if (!(node instanceof Element)) {
					continue;
				}
				String name = node.getNodeName();
				if (name.equals("start")) {
					resultModel.setStart(Long.parseLong(node.getTextContent()));
				}else if(name.equals("end")){
					resultModel.setEnd(Long.parseLong(node.getTextContent()));
				}else if(name.equals("step")){
					resultModel.setStep(Integer.parseInt(node.getTextContent()));
				}else if(name.equals("rows")){
					resultModel.setRows(Integer.parseInt(node.getTextContent()));
				}else if(name.equals("columns")){
					resultModel.setColumns(Integer.parseInt(node.getTextContent()));
//					resultModel.setDatas(new ArrayList<>(resultModel.getColumns()));
				}else if(name.equals("legend")) {
					legendNode=node;
				}else if(name.equals("gprints")) {
					gprintsNode=node;
				}
			}
			HashMap<String, DataXMLModel> dataModelMap=new HashMap<>();
			if(legendNode!=null){
				for (int i = 0; i < legendNode.getChildNodes().getLength(); i++) {
					Node node = legendNode.getChildNodes().item(i);
					if (node instanceof Element) {
						DataXMLModel dataModel = new DataXMLModel();
						dataModel.setName(node.getTextContent().trim().replace("\\g", ""));
						dataModel.setData(new String[resultModel.getRows()]);
						resultModel.getDatas().add(dataModel);
						dataModelMap.put(dataModel.getName(), dataModel);
					}
				}
			}
			if(gprintsNode!=null) {
				Node cNode = gprintsNode.getFirstChild();
				cNode = nextElement(cNode);
				while (cNode != null && !cNode.getNodeName().equals("gprint")) {
					DataXMLModel dataModel = dataModelMap.get(cNode.getTextContent().trim().replace("\\g", ""));
					cNode = cNode.getNextSibling();
					cNode = nextElement(cNode);
					while (cNode != null && cNode.getNodeName().equals("gprint")) {
						String text = cNode.getTextContent().trim().replace("\\l", "");
						String[] kv = text.split(":");
						if (dataModel != null) {
							if (kv[0].equals("Now")) {
								dataModel.setNow(kv[1].trim());
							} else if (kv[0].equals("Min")) {
								dataModel.setMin(kv[1].trim());
							} else if (kv[0].equals("Avg")) {
								dataModel.setAvg(kv[1].trim());
							} else if (kv[0].equals("Max")) {
								dataModel.setMax(kv[1].trim());
							}
						}
						cNode = cNode.getNextSibling();
						cNode = nextElement(cNode);
					}
//				resultModel.getDatas().add(dataModel);
//				dataModelMap.put(dataModel.getName(), dataModel);
				}
			}
			int rowIndex=0;
			for (int i = 0; i < dataNode.getChildNodes().getLength(); i++) {
				Node rowNode=dataNode.getChildNodes().item(i);
				if (rowNode instanceof Element) {
					int colIndex=0;
					for (int j = 0; j < rowNode.getChildNodes().getLength(); j++) {
						Node vNode=rowNode.getChildNodes().item(j);
						if (vNode instanceof Element && !vNode.getNodeName().equals( "t" )) {
							DataXMLModel dataModel=resultModel.getDatas().get(colIndex);
							dataModel.getData()[rowIndex]=vNode.getTextContent().trim();
							colIndex++;
						}
					}
					rowIndex++;
				}
			}
			return resultModel;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Node nextElement(Node node){
		while (node!=null&&!(node instanceof Element)) {
			node=node.getNextSibling();
		}
		return node;
	}
	
	public static void main(String[] args) throws BussException {
		String a = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
				"\n" +
				"<xport>\n" +
				"  <meta>\n" +
				"    <start>1474184170</start>\n" +
				"    <step>10</step>\n" +
				"    <end>1474185960</end>\n" +
				"    <rows>180</rows>\n" +
				"    <columns>2</columns>\n" +
				"    <legend>\n" +
				"      <entry>In </entry>\n" +
				"      <entry>Out</entry>\n" +
				"    </legend>\n" +
				"  </meta>\n" +
				"  <data>\n" +
				"    <row><t>1474184170</t><v>7.9962400000e+02</v><v>2.0057500000e+02</v></row>\n" +
				"    <row><t>1474184180</t><v>7.8832000000e+02</v><v>1.9113000000e+02</v></row>\n" +
				"    <row><t>1474184190</t><v>2.1771900000e+03</v><v>6.3173000000e+02</v></row>\n" +
				"    <row><t>1474184200</t><v>2.1771900000e+03</v><v>6.3173000000e+02</v></row>\n" +
				"    <row><t>1474185960</t><v>1.0162400000e+03</v><v>2.5379000000e+02</v></row>\n" +
				"  </data>\n" +
				"</xport>\n";
		String xml="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+


"<xport>\n" +
				"  <meta>\n" +
				"    <start>1465977360</start>\n" +
				"    <end>1465979160</end>\n" +
				"    <step>10</step>\n" +
				"    <rows>180</rows>\n" +
				"    <columns>3</columns>\n" +
				"    <legend>\n" +
				"      <entry>Total</entry>\n" +
				"      <entry>Remaining</entry>\n" +
				"      <entry>Used</entry>\n" +
				"    </legend>\n" +
				"  </meta>\n" +
				"  <data>\n" +
				"    <row><v>5.0000000000e+01</v><v>3.2000000000e+01</v><v>5.0000000000e+00</v></row>\n" +
				"    <row><v>5.0000000000e+01</v><v>3.2000000000e+01</v><v>5.0000000000e+00</v></row>\n" +
				"    <row><v>5.0000000000e+01</v><v>3.2000000000e+01</v><v>5.0000000000e+00</v></row>\n" +
				"    <row><v>5.0000000000e+01</v><v>3.2000000000e+01</v><v>5.0000000000e+00</v></row>\n" +
				"    <row><v>5.0000000000e+01</v><v>3.2000000000e+01</v><v>5.0000000000e+00</v></row>\n" +
				"  </data>\n" +
				"</xport>";
		
		FullXMLModel resultModel = parse(a);
		System.out.println(JSON.toJSONString(resultModel,true));

	}
}
