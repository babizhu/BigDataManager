package com.bbz.bigdata.platform.rrdtool.api;

import com.alibaba.fastjson.JSON;
import com.bbz.bigdata.platform.cmd.CmdExecutor;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.cmd.CmdBuilder;
import com.bbz.bigdata.platform.rrdtool.cmd.ICmd;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.FullJsonModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
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
     * @param hostName             主机名,空字符串则为集群
     * @param timePeriod           从查询起点到目前为止的时间段，单位：分钟
     * @param measurementDetails   测量详细量数组 [CPU.Free,Memory.Total]
     * @param showUnit             显示单位 (Unit.GB)
     * @param changeValueToPercent 是否转换为百分比单位,如果为true则showUnit无效
     * @throws ParseException
     * @throws BussException
     */
    public String visit( String hostName, int timePeriod, Measurement.Detail[] measurementDetails, Unit showUnit, boolean changeValueToPercent ) throws ParseException, BussException{
        Calendar date = Calendar.getInstance();
        Date now = new Date();
        date.setTime( now );
        date.add( Calendar.MINUTE, timePeriod * -1 );
        Date stime = date.getTime();
        String startTime = dateFormater.format( stime );
        String endTime = dateFormater.format( now );
        return visit( hostName, startTime, endTime, measurementDetails, showUnit, changeValueToPercent );
    }


    /**
     * @param hostName             主机名,空字符串则为集群
     * @param startTime            MM/dd/yyyy HH:mm
     * @param endTime              MM/dd/yyyy HH:mm
     * @param showUnit             显示单位
     * @param changeValueToPercent 是否统一为百分比单位
     * @throws ParseException
     * @throws BussException
     */
    public String visit( String hostName,
                         String startTime,
                         String endTime,
                         Measurement.Detail[] measurementDetails,
                         Unit showUnit,
                         boolean changeValueToPercent ) throws ParseException, BussException{
        if( measurementDetails == null || measurementDetails.length == 0 ) {
            throw new IllegalArgumentException( "measurementDetails can not be empty" );
        }
        if( hostName.isEmpty() ) {
            hostName = "__SummaryInfo__";
        }
        HashMap<Measurement, List<Measurement.Detail>> measurements = new HashMap<>();
        for( int i = 0; i < measurementDetails.length; i++ ) {
            List<Measurement.Detail> list = measurements.get( measurementDetails[i].getMeasurement() );
            if( list == null ) {
                list = new ArrayList<>();
                measurements.put( measurementDetails[i].getMeasurement(), list );
            }
            list.add( measurementDetails[i] );
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
        FullJsonModel fullJsonModel = null;
        Date sdate = new SimpleDateFormat( "MM/dd/yyyy HH:mm" ).parse( startTime );
        for( Map.Entry<Measurement, List<Measurement.Detail>> mEntry : measurements.entrySet() ) {
            Measurement measurement = mEntry.getKey();
            List<Measurement.Detail> detailList = mEntry.getValue();
            ICmd cmd = CmdBuilder.buildCmd( hostName, measurement, startTime, endTime );
            String result = CmdExecutor.execute( cmd.getCmd() );
            FullXMLModel crm = XMLResultParser.parse( result );
            FullJsonModel jsonModel = JsonResultConvertor.convert( crm, cmd, sdate, detailList, showUnit, changeValueToPercent );
            if( fullJsonModel == null ) {
                fullJsonModel = jsonModel;
            } else {
                fullJsonModel = JsonResultJoiner.join( fullJsonModel, jsonModel, true );
            }
        }
        return JSON.toJSONString( fullJsonModel );
    }


}
