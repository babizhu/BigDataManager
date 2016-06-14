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
            "    <start>1465809300</start>\n" +
            "    <end>1465809840</end>\n" +
            "    <step>10</step>\n" +
            "    <rows>54</rows>\n" +
            "    <columns>2</columns>\n" +
            "    <legend>\n" +
            "      <entry>In </entry>\n" +
            "      <entry>Out</entry>\n" +
            "    </legend>\n" +
            "    <gprints>\n" +
            "        <line>In </line>\n" +
            "        <gprint>Now:422846.9</gprint>\n" +
            "        <gprint>Min:418302.0</gprint>\n" +
            "        <gprint>Avg:420524.6</gprint>\n" +
            "        <gprint>Max:423333.8\\l</gprint>\n" +
            "        <line>Out</line>\n" +
            "        <gprint>Now:9590.3</gprint>\n" +
            "        <gprint>Min:9348.0</gprint>\n" +
            "        <gprint>Avg:9458.5</gprint>\n" +
            "        <gprint>Max:9608.3\\l</gprint>\n" +
            "    </gprints>\n" +
            "  </meta>\n" +
            "  <data>\n" +
            "    <row><v>4.1894488600e+05</v><v>9.4277600000e+03</v></row>\n" +
            "    <row><v>4.1991891000e+05</v><v>9.4709200000e+03</v></row>\n" +
            "    <row><v>4.1992149000e+05</v><v>9.4661240000e+03</v></row>\n" +
            "    <row><v>4.1993181000e+05</v><v>9.4469400000e+03</v></row>\n" +
            "    <row><v>4.2008064500e+05</v><v>9.4519560000e+03</v></row>\n" +
            "    <row><v>4.2142016000e+05</v><v>9.4971000000e+03</v></row>\n" +
            "    <row><v>4.2133272800e+05</v><v>9.4925890000e+03</v></row>\n" +
            "    <row><v>4.2054584000e+05</v><v>9.4519900000e+03</v></row>\n" +
            "    <row><v>4.2045074800e+05</v><v>9.4557320000e+03</v></row>\n" +
            "    <row><v>4.2007038000e+05</v><v>9.4707000000e+03</v></row>\n" +
            "    <row><v>4.2010486400e+05</v><v>9.4694760000e+03</v></row>\n" +
            "    <row><v>4.2041522000e+05</v><v>9.4584600000e+03</v></row>\n" +
            "    <row><v>4.2040350700e+05</v><v>9.4552100000e+03</v></row>\n" +
            "    <row><v>4.2029809000e+05</v><v>9.4259600000e+03</v></row>\n" +
            "    <row><v>4.2030644400e+05</v><v>9.4259130000e+03</v></row>\n" +
            "    <row><v>4.2038163000e+05</v><v>9.4254900000e+03</v></row>\n" +
            "    <row><v>4.2047996100e+05</v><v>9.4347820000e+03</v></row>\n" +
            "    <row><v>4.2136494000e+05</v><v>9.5184100000e+03</v></row>\n" +
            "    <row><v>4.2110699000e+05</v><v>9.5010240000e+03</v></row>\n" +
            "    <row><v>4.2007519000e+05</v><v>9.4314800000e+03</v></row>\n" +
            "    <row><v>4.2013352800e+05</v><v>9.4219820000e+03</v></row>\n" +
            "    <row><v>4.2036688000e+05</v><v>9.3839900000e+03</v></row>\n" +
            "    <row><v>4.2017120400e+05</v><v>9.3767960000e+03</v></row>\n" +
            "    <row><v>4.1938850000e+05</v><v>9.3480200000e+03</v></row>\n" +
            "    <row><v>4.1932123800e+05</v><v>9.3507180000e+03</v></row>\n" +
            "    <row><v>4.1871588000e+05</v><v>9.3750000000e+03</v></row>\n" +
            "    <row><v>4.1899720800e+05</v><v>9.3815950000e+03</v></row>\n" +
            "    <row><v>4.2152916000e+05</v><v>9.4409500000e+03</v></row>\n" +
            "    <row><v>4.2104034000e+05</v><v>9.4361540000e+03</v></row>\n" +
            "    <row><v>4.1908506000e+05</v><v>9.4169700000e+03</v></row>\n" +
            "    <row><v>4.1919675100e+05</v><v>9.4184210000e+03</v></row>\n" +
            "    <row><v>4.2020197000e+05</v><v>9.4314800000e+03</v></row>\n" +
            "    <row><v>4.2008587000e+05</v><v>9.4350780000e+03</v></row>\n" +
            "    <row><v>4.1962147000e+05</v><v>9.4494700000e+03</v></row>\n" +
            "    <row><v>4.2019021300e+05</v><v>9.4665550000e+03</v></row>\n" +
            "    <row><v>4.2151728000e+05</v><v>9.5064200000e+03</v></row>\n" +
            "    <row><v>4.2087423000e+05</v><v>9.4933680000e+03</v></row>\n" +
            "    <row><v>4.1830203000e+05</v><v>9.4411600000e+03</v></row>\n" +
            "    <row><v>4.1927221200e+05</v><v>9.4614020000e+03</v></row>\n" +
            "    <row><v>4.2315294000e+05</v><v>9.5423700000e+03</v></row>\n" +
            "    <row><v>4.2274496700e+05</v><v>9.5354670000e+03</v></row>\n" +
            "    <row><v>4.2179303000e+05</v><v>9.5193600000e+03</v></row>\n" +
            "    <row><v>4.2169399400e+05</v><v>9.4877430000e+03</v></row>\n" +
            "    <row><v>4.2146291000e+05</v><v>9.4139700000e+03</v></row>\n" +
            "    <row><v>4.2151472800e+05</v><v>9.4337540000e+03</v></row>\n" +
            "    <row><v>4.2172200000e+05</v><v>9.5128900000e+03</v></row>\n" +
            "    <row><v>4.2133156800e+05</v><v>9.5038980000e+03</v></row>\n" +
            "    <row><v>4.1976984000e+05</v><v>9.4679300000e+03</v></row>\n" +
            "    <row><v>4.2001778400e+05</v><v>9.4775220000e+03</v></row>\n" +
            "    <row><v>4.2100956000e+05</v><v>9.5158900000e+03</v></row>\n" +
            "    <row><v>4.2063778500e+05</v><v>9.4909930000e+03</v></row>\n" +
            "    <row><v>4.1977031000e+05</v><v>9.4329000000e+03</v></row>\n" +
            "    <row><v>4.2048299800e+05</v><v>9.4679860000e+03</v></row>\n" +
            "    <row><v>4.2333375000e+05</v><v>9.6083300000e+03</v></row>\n" +
            "  </data>\n" +
            "</xport>";
}
