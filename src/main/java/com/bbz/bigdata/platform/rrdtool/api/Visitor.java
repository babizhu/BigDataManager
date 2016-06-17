package com.bbz.bigdata.platform.rrdtool.api;

import com.bbz.bigdata.platform.cmd.CmdExecutor;
import com.bbz.bigdata.platform.rrdtool.Constant;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.cmd.CmdBuilder;
import com.bbz.bigdata.platform.rrdtool.cmd.ICmd;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.MeasurementCreator;
import com.bbz.bigdata.platform.rrdtool.xmlmodel.FullXMLModel;
import com.bbz.bigdata.platform.rrdtool.resultparser.RRDResultConvertor;
import com.bbz.bigdata.platform.rrdtool.resultparser.RRDResultJoiner;
import com.bbz.bigdata.platform.rrdtool.resultparser.XMLResultParser;
import com.bbz.bigdata.util.Util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Visitor{

    private static SimpleDateFormat dateFormater = new SimpleDateFormat( "MM/dd/yyyy HH:mm" );

    /**
     * @param clusterName          集群名称
     * @param hostName             主机名,空字符串则为集群的summaryinfo
     * @param timePeriod           从查询起点到目前为止的时间段，单位：分钟
     * @param measurementDetails   测量数据数组 [CPU.Free,Memory.Total]
     * @param showUnit             显示单位 (Unit.GB) ，若为空则自动转换为合适的单位
     * @param changeValueToPercent 是否转换为百分比单位,如果为true则showUnit只对total有效
     * @param measurementCreators  新数据创建方法
     * @throws ParseException
     * @throws BussException
     */
    public RRDModel visit(String clusterName, String hostName, int timePeriod, Measurement.Detail[] measurementDetails, Unit showUnit
            , boolean changeValueToPercent, MeasurementCreator[] measurementCreators, Measurement.Detail... measurementDetailsForShow) throws ParseException, BussException{
        Calendar date = Calendar.getInstance();
        Date now = new Date();
        date.setTime( now );
        date.add( Calendar.SECOND, timePeriod * -1 );
        Date stime = date.getTime();
        String startTime = dateFormater.format( stime );
        String endTime = dateFormater.format( now );
        return visit(clusterName, hostName, startTime, endTime, measurementDetails, showUnit, changeValueToPercent, measurementCreators,measurementDetailsForShow);
    }


    /**
     * @param clusterName          集群名称
     * @param hostName             主机名,空字符串则为集群的summaryinfo
     * @param startTime            MM/dd/yyyy HH:mm
     * @param endTime              MM/dd/yyyy HH:mm
     * @param measurementDetails   测量数据数组 [CPU.Free,Memory.Total]
     * @param showUnit             显示单位 (Unit.GB) ，若为空则自动转换为合适的单位
     * @param changeValueToPercent 是否转换为百分比单位,如果为true则showUnit只对total有效
     * @param measurementCreators  新数据创建方法
     * @throws ParseException
     * @throws BussException
     */
    public RRDModel visit(String clusterName, String hostName,
                          String startTime,
                          String endTime,
                          Measurement.Detail[] measurementDetails,
                          Unit showUnit,
                          boolean changeValueToPercent , MeasurementCreator[] measurementCreators, Measurement.Detail... measurementDetailsForShow) throws ParseException, BussException{
        if( measurementDetails == null || measurementDetails.length == 0 ) {
            throw new IllegalArgumentException( "measurementDetails can not be empty" );
        }
        if( hostName.isEmpty() ) {
            hostName = null;
        }
        if(hostName!=null){
            hostName = hostName.toLowerCase();
        }
        String hostDir= Constant.rrdDataLocation+clusterName+(hostName==null?"": (File.separator+hostName));
        if(!Util.INSTANCE.isDebug()) {
            if (!new File(hostDir).exists()) {
                System.out.println(hostDir);
                return null;
            }
        }
        HashMap<Measurement, List<Measurement.Detail>> measurements = new HashMap<>();
        for (Measurement.Detail measurementDetail : measurementDetails) {
            List<Measurement.Detail> list = measurements.get(measurementDetail.getMeasurement());
            if (list == null) {
                list = new ArrayList<>();
                measurements.put(measurementDetail.getMeasurement(), list);
            }
            list.add(measurementDetail);
        }
//        if( !changeValueToPercent ) {
//            //不转化为百分比，则要求单位同类型
//            Measurement.Detail firstD = null;
//            for( Measurement.Detail detail : measurementDetails ) {
//                if( firstD == null ) {
//                    firstD = detail;
//                } else if( !firstD.unit().sameType(detail.unit()) ) {
//                    throw new BussException( BussException.UNITTYPE_NOT_MATCHED );
//                }
//            }
//        }

        RRDModel fullJsonModel = null;
        Date sdate = dateFormater.parse( startTime );
        try {
            for (Map.Entry<Measurement, List<Measurement.Detail>> mEntry : measurements.entrySet()) {
                Measurement measurement = mEntry.getKey();
                List<Measurement.Detail> detailList = mEntry.getValue();
                ICmd cmd = CmdBuilder.buildCmd(clusterName, hostName, measurement, startTime, endTime);
                String result;
                if (Util.INSTANCE.isDebug()) {
                    result = tempres;
                } else {
                    System.out.println("\r\n" + cmd.getCmd());
                    result = CmdExecutor.execute(cmd.getCmd());
//                    System.out.println(result);
                }
                FullXMLModel crm = XMLResultParser.parse(result);
                RRDModel jsonModel = RRDResultConvertor.convert(crm, cmd, sdate, detailList, showUnit, changeValueToPercent, measurementCreators, measurementDetailsForShow);
                if (fullJsonModel == null) {
                    fullJsonModel = jsonModel;
                } else {
                    fullJsonModel = RRDResultJoiner.join(fullJsonModel, jsonModel, true);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return fullJsonModel;
    }


    private String tempres="<xport>\n" +
            "  <meta>\n" +
            "    <start>1466152260</start>\n" +
            "    <end>1466152320</end>\n" +
            "    <step>10</step>\n" +
            "    <rows>6</rows>\n" +
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
            "    <row><v>5.0000000000e+01</v><v>3.2000000000e+01</v><v>5.0000000000e+00</v></row>\n" +
            "  </data>\n" +
            "</xport>";
}
