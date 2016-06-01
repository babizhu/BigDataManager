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
     * @param showUnit             显示单位 (Unit.GB)
     * @param changeValueToPercent 是否转换为百分比单位,如果为true则showUnit无效
     * @param measurementCreators 新数据创建方法
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
     * @param showUnit             显示单位
     * @param changeValueToPercent 是否统一为百分比单位
     * @param measurementCreators 新数据创建方法
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
        if( !changeValueToPercent ) {
            Unit.Type type = null;
            for( Measurement measurement : measurements.keySet() ) {
                if( type == null ) {
                    type = measurement.getResultUnit().getType();
                } else if( measurement.getResultUnit().getType() != type ) {
                    throw new BussException( BussException.UNITTYPE_NOT_MATCHED );
                }
            }
        }
        RRDJsonModel fullJsonModel = null;
        Date sdate = dateFormater.parse( startTime );
        for( Map.Entry<Measurement, List<Measurement.Detail>> mEntry : measurements.entrySet() ) {
            Measurement measurement = mEntry.getKey();
            List<Measurement.Detail> detailList = mEntry.getValue();
            ICmd cmd = CmdBuilder.buildCmd(clusterName, hostName, measurement, startTime, endTime );
            System.out.println("\r"+cmd.getCmd());
            String result = CmdExecutor.execute( cmd.getCmd() );
            System.out.println(result);
//            String result = tempres;
            FullXMLModel crm = XMLResultParser.parse( result );
            RRDJsonModel jsonModel = JsonResultConvertor.convert( crm, cmd, sdate, detailList, showUnit, changeValueToPercent, measurementCreators,measurementDetailsForShow);
            if( fullJsonModel == null ) {
                fullJsonModel = jsonModel;
            } else {
                fullJsonModel = JsonResultJoiner.join( fullJsonModel, jsonModel, true );
            }
        }
        return fullJsonModel;
    }


    private String tempres="<xport>\n" +
            "  <meta>\n" +
            "    <start>1464770760</start>\n" +
            "    <end>1464771360</end>\n" +
            "    <step>10</step>\n" +
            "    <rows>60</rows>\n" +
            "    <columns>7</columns>\n" +
            "    <legend>\n" +
            "      <entry>Use\\g</entry>\n" +
            "      <entry>Share\\g</entry>\n" +
            "      <entry>Cache\\g</entry>\n" +
            "      <entry>Buffer\\g</entry>\n" +
            "      <entry>Free\\g</entry>\n" +
            "      <entry>Swap\\g</entry>\n" +
            "      <entry>Total\\g</entry>\n" +
            "    </legend>\n" +
            "    <gprints>\n" +
            "        <area>  Use\\g</area>\n" +
            "        <gprint>    Now:6130827264.0</gprint>\n" +
            "        <gprint> Min:6116667392.0</gprint>\n" +
            "        <gprint>Avg:6127294900.5</gprint>\n" +
            "        <gprint> Max:6160711680.0\\l</gprint>\n" +
            "        <area>  Share\\g</area>\n" +
            "        <gprint>  Now:   0.0</gprint>\n" +
            "        <gprint> Min:   0.0</gprint>\n" +
            "        <gprint>Avg:   0.0</gprint>\n" +
            "        <gprint> Max:   0.0\\l</gprint>\n" +
            "        <area>  Cache\\g</area>\n" +
            "        <gprint>  Now:6234832896.0</gprint>\n" +
            "        <gprint> Min:6165618688.0</gprint>\n" +
            "        <gprint>Avg:6181880412.3</gprint>\n" +
            "        <gprint> Max:6234832896.0\\l</gprint>\n" +
            "        <area>  Buffer\\g</area>\n" +
            "        <gprint> Now:167936.0</gprint>\n" +
            "        <gprint> Min:167936.0</gprint>\n" +
            "        <gprint>Avg:167936.0</gprint>\n" +
            "        <gprint> Max:167936.0\\l</gprint>\n" +
            "        <area>  Free\\g</area>\n" +
            "        <gprint>  Now:2854789120.0</gprint>\n" +
            "        <gprint> Min:2854789120.0</gprint>\n" +
            "        <gprint>Avg:2911273967.2</gprint>\n" +
            "        <gprint> Max:2938163200.0\\l</gprint>\n" +
            "        <area>  Swap\\g</area>\n" +
            "        <gprint>   Now:1327104.0</gprint>\n" +
            "        <gprint> Min:1327104.0</gprint>\n" +
            "        <gprint>Avg:1327104.0</gprint>\n" +
            "        <gprint> Max:1327104.0\\l</gprint>\n" +
            "        <line>Total\\g</line>\n" +
            "        <gprint>  Now:15220617216.0</gprint>\n" +
            "        <gprint> Min:15220617216.0</gprint>\n" +
            "        <gprint>Avg:15220617216.0</gprint>\n" +
            "        <gprint> Max:15220617216.0\\l</gprint>\n" +
            "    </gprints>\n" +
            "  </meta>\n" +
            "  <data>\n" +
            "    <row><v>6.1606690816e+09</v><v>0.0000000000e+00</v><v>6.1667287040e+09</v><v>1.6793600000e+05</v><v>2.8930514944e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1607116800e+09</v><v>0.0000000000e+00</v><v>6.1667287040e+09</v><v>1.6793600000e+05</v><v>2.8930088960e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1607116800e+09</v><v>0.0000000000e+00</v><v>6.1667287040e+09</v><v>1.6793600000e+05</v><v>2.8930088960e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1166673920e+09</v><v>0.0000000000e+00</v><v>6.1656186880e+09</v><v>1.6793600000e+05</v><v>2.9381632000e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1166673920e+09</v><v>0.0000000000e+00</v><v>6.1656186880e+09</v><v>1.6793600000e+05</v><v>2.9381632000e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1166673920e+09</v><v>0.0000000000e+00</v><v>6.1656186880e+09</v><v>1.6793600000e+05</v><v>2.9381632000e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1195870208e+09</v><v>0.0000000000e+00</v><v>6.1659725824e+09</v><v>1.6793600000e+05</v><v>2.9348896768e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1199114240e+09</v><v>0.0000000000e+00</v><v>6.1660119040e+09</v><v>1.6793600000e+05</v><v>2.9345259520e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1199114240e+09</v><v>0.0000000000e+00</v><v>6.1660119040e+09</v><v>1.6793600000e+05</v><v>2.9345259520e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1199114240e+09</v><v>0.0000000000e+00</v><v>6.1660119040e+09</v><v>1.6793600000e+05</v><v>2.9345259520e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1218578432e+09</v><v>0.0000000000e+00</v><v>6.1663621120e+09</v><v>1.6793600000e+05</v><v>2.9322293248e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1220741120e+09</v><v>0.0000000000e+00</v><v>6.1664010240e+09</v><v>1.6793600000e+05</v><v>2.9319741440e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1220741120e+09</v><v>0.0000000000e+00</v><v>6.1664010240e+09</v><v>1.6793600000e+05</v><v>2.9319741440e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1220741120e+09</v><v>0.0000000000e+00</v><v>6.1664010240e+09</v><v>1.6793600000e+05</v><v>2.9319741440e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1220741120e+09</v><v>0.0000000000e+00</v><v>6.1664010240e+09</v><v>1.6793600000e+05</v><v>2.9319741440e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1233496064e+09</v><v>0.0000000000e+00</v><v>6.1667622912e+09</v><v>1.6793600000e+05</v><v>2.9303373824e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1234913280e+09</v><v>0.0000000000e+00</v><v>6.1668024320e+09</v><v>1.6793600000e+05</v><v>2.9301555200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1234913280e+09</v><v>0.0000000000e+00</v><v>6.1668024320e+09</v><v>1.6793600000e+05</v><v>2.9301555200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1245202432e+09</v><v>0.0000000000e+00</v><v>6.1671235584e+09</v><v>1.6793600000e+05</v><v>2.9288054784e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247774720e+09</v><v>0.0000000000e+00</v><v>6.1672038400e+09</v><v>1.6793600000e+05</v><v>2.9284679680e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247774720e+09</v><v>0.0000000000e+00</v><v>6.1672038400e+09</v><v>1.6793600000e+05</v><v>2.9284679680e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247774720e+09</v><v>0.0000000000e+00</v><v>6.1672038400e+09</v><v>1.6793600000e+05</v><v>2.9284679680e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247774720e+09</v><v>0.0000000000e+00</v><v>6.1672038400e+09</v><v>1.6793600000e+05</v><v>2.9284679680e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1237231616e+09</v><v>0.0000000000e+00</v><v>6.1683539968e+09</v><v>1.6793600000e+05</v><v>2.9283721216e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1236060160e+09</v><v>0.0000000000e+00</v><v>6.1684817920e+09</v><v>1.6793600000e+05</v><v>2.9283614720e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1236060160e+09</v><v>0.0000000000e+00</v><v>6.1684817920e+09</v><v>1.6793600000e+05</v><v>2.9283614720e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1244940288e+09</v><v>0.0000000000e+00</v><v>6.1688029184e+09</v><v>1.6793600000e+05</v><v>2.9271523328e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247160320e+09</v><v>0.0000000000e+00</v><v>6.1688832000e+09</v><v>1.6793600000e+05</v><v>2.9268500480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247160320e+09</v><v>0.0000000000e+00</v><v>6.1688832000e+09</v><v>1.6793600000e+05</v><v>2.9268500480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247160320e+09</v><v>0.0000000000e+00</v><v>6.1688832000e+09</v><v>1.6793600000e+05</v><v>2.9268500480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247160320e+09</v><v>0.0000000000e+00</v><v>6.1688832000e+09</v><v>1.6793600000e+05</v><v>2.9268500480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247160320e+09</v><v>0.0000000000e+00</v><v>6.1688832000e+09</v><v>1.6793600000e+05</v><v>2.9268500480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247160320e+09</v><v>0.0000000000e+00</v><v>6.1688832000e+09</v><v>1.6793600000e+05</v><v>2.9268500480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1247160320e+09</v><v>0.0000000000e+00</v><v>6.1688832000e+09</v><v>1.6793600000e+05</v><v>2.9268500480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1259988992e+09</v><v>0.0000000000e+00</v><v>6.1696020480e+09</v><v>1.6793600000e+05</v><v>2.9248483328e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1261414400e+09</v><v>0.0000000000e+00</v><v>6.1696819200e+09</v><v>1.6793600000e+05</v><v>2.9246259200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1266034688e+09</v><v>0.0000000000e+00</v><v>6.1696851968e+09</v><v>1.6793600000e+05</v><v>2.9241606144e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1267189760e+09</v><v>0.0000000000e+00</v><v>6.1696860160e+09</v><v>1.6793600000e+05</v><v>2.9240442880e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1284556800e+09</v><v>0.0000000000e+00</v><v>6.1728907264e+09</v><v>1.6793600000e+05</v><v>2.9191028736e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1288898560e+09</v><v>0.0000000000e+00</v><v>6.1736919040e+09</v><v>1.6793600000e+05</v><v>2.9178675200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1288898560e+09</v><v>0.0000000000e+00</v><v>6.1736919040e+09</v><v>1.6793600000e+05</v><v>2.9178675200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1288898560e+09</v><v>0.0000000000e+00</v><v>6.1736919040e+09</v><v>1.6793600000e+05</v><v>2.9178675200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1294895104e+09</v><v>0.0000000000e+00</v><v>6.1908066304e+09</v><v>1.6793600000e+05</v><v>2.9001531392e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1296394240e+09</v><v>0.0000000000e+00</v><v>6.1950853120e+09</v><v>1.6793600000e+05</v><v>2.8957245440e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1296394240e+09</v><v>0.0000000000e+00</v><v>6.1950853120e+09</v><v>1.6793600000e+05</v><v>2.8957245440e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1296394240e+09</v><v>0.0000000000e+00</v><v>6.1950853120e+09</v><v>1.6793600000e+05</v><v>2.8957245440e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1287612416e+09</v><v>0.0000000000e+00</v><v>6.1975068672e+09</v><v>1.6793600000e+05</v><v>2.8941811712e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1285416960e+09</v><v>0.0000000000e+00</v><v>6.1981122560e+09</v><v>1.6793600000e+05</v><v>2.8937953280e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1285416960e+09</v><v>0.0000000000e+00</v><v>6.1981122560e+09</v><v>1.6793600000e+05</v><v>2.8937953280e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1285416960e+09</v><v>0.0000000000e+00</v><v>6.1981122560e+09</v><v>1.6793600000e+05</v><v>2.8937953280e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1288955904e+09</v><v>0.0000000000e+00</v><v>6.2085062656e+09</v><v>1.6793600000e+05</v><v>2.8830474240e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1289840640e+09</v><v>0.0000000000e+00</v><v>6.2111047680e+09</v><v>1.6793600000e+05</v><v>2.8803604480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1289840640e+09</v><v>0.0000000000e+00</v><v>6.2111047680e+09</v><v>1.6793600000e+05</v><v>2.8803604480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1289840640e+09</v><v>0.0000000000e+00</v><v>6.2111047680e+09</v><v>1.6793600000e+05</v><v>2.8803604480e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1297311744e+09</v><v>0.0000000000e+00</v><v>6.2276165632e+09</v><v>1.6793600000e+05</v><v>2.8631015424e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1299179520e+09</v><v>0.0000000000e+00</v><v>6.2317445120e+09</v><v>1.6793600000e+05</v><v>2.8587868160e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1299179520e+09</v><v>0.0000000000e+00</v><v>6.2317445120e+09</v><v>1.6793600000e+05</v><v>2.8587868160e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1299179520e+09</v><v>0.0000000000e+00</v><v>6.2317445120e+09</v><v>1.6793600000e+05</v><v>2.8587868160e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1306454016e+09</v><v>0.0000000000e+00</v><v>6.2342152192e+09</v><v>1.6793600000e+05</v><v>2.8555886592e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "    <row><v>6.1308272640e+09</v><v>0.0000000000e+00</v><v>6.2348328960e+09</v><v>1.6793600000e+05</v><v>2.8547891200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
            "  </data>\n" +
            "</xport>";
}
