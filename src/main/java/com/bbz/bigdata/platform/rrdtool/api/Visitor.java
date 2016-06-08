package com.bbz.bigdata.platform.rrdtool.api;

import com.bbz.bigdata.platform.cmd.CmdExecutor;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.cmd.CmdBuilder;
import com.bbz.bigdata.platform.rrdtool.cmd.ICmd;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.MeasurementCreator;
import com.bbz.bigdata.platform.rrdtool.resultmodel.FullXMLModel;
import com.bbz.bigdata.platform.rrdtool.resultparser.JsonResultConvertor;
import com.bbz.bigdata.platform.rrdtool.resultparser.JsonResultJoiner;
import com.bbz.bigdata.platform.rrdtool.resultparser.XMLResultParser;
import com.bbz.bigdata.util.Util;

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
    public RRDJsonModel visit(String clusterName, String hostName, int timePeriod, Measurement.Detail[] measurementDetails, Unit showUnit
            , boolean changeValueToPercent, MeasurementCreator[] measurementCreators,Measurement.Detail... measurementDetailsForShow) throws ParseException, BussException{
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
    public RRDJsonModel visit(String clusterName, String hostName,
                              String startTime,
                              String endTime,
                              Measurement.Detail[] measurementDetails,
                              Unit showUnit,
                              boolean changeValueToPercent , MeasurementCreator[] measurementCreators,Measurement.Detail... measurementDetailsForShow) throws ParseException, BussException{
        if( measurementDetails == null || measurementDetails.length == 0 ) {
            throw new IllegalArgumentException( "measurementDetails can not be empty" );
        }
        if( hostName.isEmpty() ) {
            hostName = "__SummaryInfo__";
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

        RRDJsonModel fullJsonModel = null;
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
                    System.out.println("\r" + cmd.getCmd());
                    result = CmdExecutor.execute(cmd.getCmd());
                    System.out.println(result);
                }
                FullXMLModel crm = XMLResultParser.parse(result);
                RRDJsonModel jsonModel = JsonResultConvertor.convert(crm, cmd, sdate, detailList, showUnit, changeValueToPercent, measurementCreators, measurementDetailsForShow);
                if (fullJsonModel == null) {
                    fullJsonModel = jsonModel;
                } else {
                    fullJsonModel = JsonResultJoiner.join(fullJsonModel, jsonModel, true);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return fullJsonModel;
    }


    private String tempres="<xport>\n" +
            "  <meta>\n" +
            "    <start>1465262460</start>\n" +
            "    <end>1465262640</end>\n" +
            "    <step>10</step>\n" +
            "    <rows>18</rows>\n" +
            "    <columns>7</columns>\n" +
            "    <legend>\n" +
            "      <entry>User\\g</entry>\n" +
            "      <entry>Nice\\g</entry>\n" +
            "      <entry>System\\g</entry>\n" +
            "      <entry>Wait\\g</entry>\n" +
            "      <entry>Steal\\g</entry>\n" +
            "      <entry>Idle\\g</entry>\n" +
            "      <entry>Speed\\g</entry>\n" +
            "    </legend>\n" +
            "    <gprints>\n" +
            "        <area>  User\\g</area>\n" +
            "        <gprint> Now:  0.2%</gprint>\n" +
            "        <gprint> Min:  0.2%</gprint>\n" +
            "        <gprint> Avg:  0.3%</gprint>\n" +
            "        <gprint> Max:  0.8%\\l</gprint>\n" +
            "        <area>  Nice\\g</area>\n" +
            "        <gprint> Now:  0.0%</gprint>\n" +
            "        <gprint> Min:  0.0%</gprint>\n" +
            "        <gprint> Avg:  0.0%</gprint>\n" +
            "        <gprint> Max:  0.0%\\l</gprint>\n" +
            "        <area>  System\\g</area>\n" +
            "        <gprint> Now:  0.2%</gprint>\n" +
            "        <gprint> Min:  0.2%</gprint>\n" +
            "        <gprint> Avg:  0.3%</gprint>\n" +
            "        <gprint> Max:  1.2%\\l</gprint>\n" +
            "        <area>  Wait\\g</area>\n" +
            "        <gprint> Now:  0.0%</gprint>\n" +
            "        <gprint> Min:  0.0%</gprint>\n" +
            "        <gprint> Avg:  0.0%</gprint>\n" +
            "        <gprint> Max:  0.0%\\l</gprint>\n" +
            "        <area>  Steal\\g</area>\n" +
            "        <gprint> Now:  0.0%</gprint>\n" +
            "        <gprint> Min:  0.0%</gprint>\n" +
            "        <gprint> Avg:  0.0%</gprint>\n" +
            "        <gprint> Max:  0.0%\\l</gprint>\n" +
            "        <area>  Idle\\g</area>\n" +
            "        <gprint> Now: 99.6%</gprint>\n" +
            "        <gprint> Min: 98.1%</gprint>\n" +
            "        <gprint> Avg: 99.4%</gprint>\n" +
            "        <gprint> Max: 99.6%\\l</gprint>\n" +
            "        <area>  Speed\\g</area>\n" +
            "        <gprint> Now:2660.0%</gprint>\n" +
            "        <gprint> Min:2660.0%</gprint>\n" +
            "        <gprint> Avg:2660.0%</gprint>\n" +
            "        <gprint> Max:2660.0%\\l</gprint>\n" +
            "    </gprints>\n" +
            "  </meta>\n" +
            "  <data>\n" +
            "    <row><v>8.0000000000e-01</v><v>0.0000000000e+00</v><v>1.2000000000e+00</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.8100000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>8.0000000000e-01</v><v>0.0000000000e+00</v><v>1.2000000000e+00</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.8100000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>6.8000000000e-01</v><v>0.0000000000e+00</v><v>1.0000000000e+00</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.8400000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "    <row><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>2.0000000000e-01</v><v>0.0000000000e+00</v><v>0.0000000000e+00</v><v>9.9600000000e+01</v><v>2.6600000000e+03</v></row>\n" +
            "  </data>\n" +
            "</xport>";
}
