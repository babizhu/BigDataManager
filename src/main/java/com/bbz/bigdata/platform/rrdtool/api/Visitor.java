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


//    private String tempres="<xport>\n" +
//            "  <meta>\n" +
//            "    <start>1464766950</start>\n" +
//            "    <end>1464767150</end>\n" +
//            "    <step>10</step>\n" +
//            "    <rows>20</rows>\n" +
//            "    <columns>9</columns>\n" +
//            "    <legend>\n" +
//            "      <entry>Use\\g</entry>\n" +
//            "      <entry>Share\\g</entry>\n" +
//            "      <entry>Cache\\g</entry>\n" +
//            "      <entry>Buffer\\g</entry>\n" +
//            "      <entry>Free\\g</entry>\n" +
//            "      <entry>Swap\\g</entry>\n" +
//            "      <entry>Total\\g</entry>\n" +
//            "    </legend>\n" +
//            "    <gprints>\n" +
//            "        <area>  Use\\g</area>\n" +
//            "        <gprint> Now:6163226624.0</gprint>\n" +
//            "        <gprint> Min:6158430208.0</gprint>\n" +
//            "        <gprint>Avg:6175022878.7</gprint>\n" +
//            "        <gprint> Max:6193704960.0\\l</gprint>\n" +
//            "        <area>  Share\\g</area>\n" +
//            "        <gprint> Now:   0.0</gprint>\n" +
//            "        <gprint> Min:   0.0</gprint>\n" +
//            "        <gprint>Avg:   0.0</gprint>\n" +
//            "        <gprint> Max:   0.0\\l</gprint>\n" +
//            "        <area>  Cache\\g</area>\n" +
//            "        <gprint> Now:6066929664.0</gprint>\n" +
//            "        <gprint> Min:6063722496.0</gprint>\n" +
//            "        <gprint>Avg:6065455247.4</gprint>\n" +
//            "        <gprint> Max:6068740096.0\\l</gprint>\n" +
//            "        <area>  Buffer\\g</area>\n" +
//            "        <gprint> Now:335872.0</gprint>\n" +
//            "        <gprint> Min:335872.0</gprint>\n" +
//            "        <gprint>Avg:335872.0</gprint>\n" +
//            "        <gprint> Max:335872.0\\l</gprint>\n" +
//            "        <area>  Free\\g</area>\n" +
//            "        <gprint> Now:2990125056.0</gprint>\n" +
//            "        <gprint> Min:2961997824.0</gprint>\n" +
//            "        <gprint>Avg:2979803217.9</gprint>\n" +
//            "        <gprint> Max:2996531200.0\\l</gprint>\n" +
//            "        <area>  Swap\\g</area>\n" +
//            "        <gprint> Now:1327104.0</gprint>\n" +
//            "        <gprint> Min:1327104.0</gprint>\n" +
//            "        <gprint>Avg:1327104.0</gprint>\n" +
//            "        <gprint> Max:1327104.0\\l</gprint>\n" +
//            "        <line>Total\\g</line>\n" +
//            "        <gprint> Now:15220617216.0</gprint>\n" +
//            "        <gprint> Min:15220617216.0</gprint>\n" +
//            "        <gprint>Avg:15220617216.0</gprint>\n" +
//            "        <gprint> Max:15220617216.0\\l</gprint>\n" +
//            "    </gprints>\n" +
//            "  </meta>\n" +
//            "  <data>\n" +
//            "    <row><v>6.1918945280e+09</v><v>0.0000000000e+00</v><v>6.0637224960e+09</v><v>3.3587200000e+05</v><v>2.9646643200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1918945280e+09</v><v>0.0000000000e+00</v><v>6.0637224960e+09</v><v>3.3587200000e+05</v><v>2.9646643200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1918945280e+09</v><v>0.0000000000e+00</v><v>6.0637224960e+09</v><v>3.3587200000e+05</v><v>2.9646643200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1918945280e+09</v><v>0.0000000000e+00</v><v>6.0637224960e+09</v><v>3.3587200000e+05</v><v>2.9646643200e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1937049600e+09</v><v>0.0000000000e+00</v><v>6.0645785600e+09</v><v>3.3587200000e+05</v><v>2.9619978240e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1937049600e+09</v><v>0.0000000000e+00</v><v>6.0645785600e+09</v><v>3.3587200000e+05</v><v>2.9619978240e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1937049600e+09</v><v>0.0000000000e+00</v><v>6.0645785600e+09</v><v>3.3587200000e+05</v><v>2.9619978240e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1937049600e+09</v><v>0.0000000000e+00</v><v>6.0645785600e+09</v><v>3.3587200000e+05</v><v>2.9619978240e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1584302080e+09</v><v>0.0000000000e+00</v><v>6.0653199360e+09</v><v>3.3587200000e+05</v><v>2.9965312000e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1584302080e+09</v><v>0.0000000000e+00</v><v>6.0653199360e+09</v><v>3.3587200000e+05</v><v>2.9965312000e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1584302080e+09</v><v>0.0000000000e+00</v><v>6.0653199360e+09</v><v>3.3587200000e+05</v><v>2.9965312000e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1612060672e+09</v><v>0.0000000000e+00</v><v>6.0660350976e+09</v><v>3.3587200000e+05</v><v>2.9930401792e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1615144960e+09</v><v>0.0000000000e+00</v><v>6.0661145600e+09</v><v>3.3587200000e+05</v><v>2.9926522880e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1615144960e+09</v><v>0.0000000000e+00</v><v>6.0661145600e+09</v><v>3.3587200000e+05</v><v>2.9926522880e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1615144960e+09</v><v>0.0000000000e+00</v><v>6.0661145600e+09</v><v>3.3587200000e+05</v><v>2.9926522880e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1615144960e+09</v><v>0.0000000000e+00</v><v>6.0661145600e+09</v><v>3.3587200000e+05</v><v>2.9926522880e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1631954944e+09</v><v>0.0000000000e+00</v><v>6.0668481536e+09</v><v>3.3587200000e+05</v><v>2.9902376960e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1632421888e+09</v><v>0.0000000000e+00</v><v>6.0669296640e+09</v><v>3.3587200000e+05</v><v>2.9901094912e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>6.1632266240e+09</v><v>0.0000000000e+00</v><v>6.0669296640e+09</v><v>3.3587200000e+05</v><v>2.9901250560e+09</v><v>1.3271040000e+06</v><v>1.5220617216e+10</v></row>\n" +
//            "    <row><v>NaN</v><v>NaN</v><v>NaN</v><v>NaN</v><v>NaN</v><v>NaN</v><v>NaN</v></row>\n" +
//            "  </data>\n" +
//            "</xport>";
}
