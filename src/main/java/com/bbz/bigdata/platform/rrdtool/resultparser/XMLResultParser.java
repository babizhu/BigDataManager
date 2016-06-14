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
			Node cNode = gprintsNode.getFirstChild();
			cNode=nextElement(cNode);
			while (cNode!=null&&!cNode.getNodeName().equals("gprint")) {
				DataXMLModel dataModel=dataModelMap.get(cNode.getTextContent().trim().replace("\\g", ""));
				cNode = cNode.getNextSibling();
				cNode = nextElement(cNode);
				while (cNode!=null&&cNode.getNodeName().equals("gprint")) {
					String text=cNode.getTextContent().trim().replace("\\l", "");
					String[] kv=text.split(":");
					if(dataModel!=null) {
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
			int rowIndex=0;
			for (int i = 0; i < dataNode.getChildNodes().getLength(); i++) {
				Node rowNode=dataNode.getChildNodes().item(i);
				if (rowNode instanceof Element) {
					int colIndex=0;
					for (int j = 0; j < rowNode.getChildNodes().getLength(); j++) {
						Node vNode=rowNode.getChildNodes().item(j);
						if (vNode instanceof Element) {
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
		
		String xml="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+


"<xport>"+
  "<meta>"+
    "<start>1463361240</start>"+
    "<end>1463361420</end>"+
    "<step>15</step>"+
    "<rows>12</rows>"+
    "<columns>7</columns>"+
    "<legend>"+
      "<entry>Share\\g</entry>"+
      "<entry>Cache\\g</entry>"+
      "<entry>Buffer\\g</entry>"+
      "<entry>Free\\g</entry>"+
      "<entry>Swap\\g</entry>"+
      "<entry>Total\\g</entry>"+
    "</legend>"+
    "<gprints>"+
        "<area>  Use\\g</area>"+
        "<gprint> Now:1467211776.0</gprint>"+
        "<gprint> Min:1466593280.0</gprint>"+
        "<gprint>Avg:1467081733.3</gprint>"+
        "<gprint> Max:1467211776.0\\l</gprint>"+
        "<area>  Share\\g</area>"+
        "<gprint> Now:   0.0</gprint>"+
        "<gprint> Min:   0.0</gprint>"+
        "<gprint>Avg:   0.0</gprint>"+
        "<gprint> Max:   0.0\\l</gprint>"+
        "<area>  Cache\\g</area>"+
        "<gprint> Now:576946176.0</gprint>"+
        "<gprint> Min:576942080.0</gprint>"+
        "<gprint>Avg:576945314.8</gprint>"+
        "<gprint> Max:576946176.0\\l</gprint>"+
        "<area>  Buffer\\g</area>"+
        "<gprint> Now:1409024.0</gprint>"+
        "<gprint> Min:1409024.0</gprint>"+
        "<gprint>Avg:1409024.0</gprint>"+
        "<gprint> Max:1409024.0\\l</gprint>"+
        "<area>  Free\\g</area>"+
        "<gprint> Now:5529026560.0</gprint>"+
        "<gprint> Min:5529026560.0</gprint>"+
        "<gprint>Avg:5529157464.0</gprint>"+
        "<gprint> Max:5529649152.0\\l</gprint>"+
        "<area>  Swap\\g</area>"+
        "<gprint> Now:   0.0</gprint>"+
        "<gprint> Min:   0.0</gprint>"+
        "<gprint>Avg:   0.0</gprint>"+
        "<gprint> Max:   0.0\\l</gprint>"+
        "<line>Total\\g</line>"+
        "<gprint> Now:7574593536.0</gprint>"+
        "<gprint> Min:7574593536.0</gprint>"+
        "<gprint>Avg:7574593536.0</gprint>"+
        "<gprint> Max:7574593536.0\\l</gprint>"+
    "</gprints>"+
  "</meta>"+
  "<data>"+
    "<row><v>0.0000000000e+00</v><v>5.7694208000e+08</v><v>1.4090240000e+06</v><v>5.5296491520e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694208000e+08</v><v>1.4090240000e+06</v><v>5.5296491520e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694317227e+08</v><v>1.4090240000e+06</v><v>5.5294831275e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694617600e+08</v><v>1.4090240000e+06</v><v>5.5290265600e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694617600e+08</v><v>1.4090240000e+06</v><v>5.5290265600e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694617600e+08</v><v>1.4090240000e+06</v><v>5.5290265600e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694617600e+08</v><v>1.4090240000e+06</v><v>5.5290265600e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694617600e+08</v><v>1.4090240000e+06</v><v>5.5290265600e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694617600e+08</v><v>1.4090240000e+06</v><v>5.5290265600e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694617600e+08</v><v>1.4090240000e+06</v><v>5.5290265600e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694617600e+08</v><v>1.4090240000e+06</v><v>5.5290265600e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
    "<row><v>0.0000000000e+00</v><v>5.7694617600e+08</v><v>1.4090240000e+06</v><v>5.5290265600e+09</v><v>0.0000000000e+00</v><v>7.5745935360e+09</v></row>"+
  "</data>"+
"</xport>";
		
		FullXMLModel resultModel = parse(xml);
		System.out.println(JSON.toJSONString(resultModel,true));

	}
}
