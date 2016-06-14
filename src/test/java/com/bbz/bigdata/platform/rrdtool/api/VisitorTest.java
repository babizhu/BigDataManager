package com.bbz.bigdata.platform.rrdtool.api;

import com.alibaba.fastjson.JSON;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.Metrics;
import org.junit.Test;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Created by liu_k on 2016/5/20.
 * test
 */
public class VisitorTest{

    @Test
    public void testVisit() throws Exception{
        Visitor visitor=new Visitor();
        Object res=visitor.visit("bigdata", "","05/18/2016 14:11", "05/18/2016 14:21", new Measurement.Detail[]{Metrics.Disk.Free,Metrics.Disk.Total}, Unit.TB, false,null,null);
        System.out.println(JSON.toJSONString(res));
    }

    @Test
    public void testTemp(){
        LinkedHashMap<String,String> map=new LinkedHashMap<>();
        map.put("b","");
        map.put("a","");
        map.put("c","");
        map.put("d","");
        map.entrySet().forEach(e->{
            System.out.println(e.getKey());
        });
    }

    @Test
    public void testFile(){
        File file = new File("C:\\do");
        //判断文件夹是否存在,如果不存在则创建文件夹
        System.out.println(file.exists());
        System.out.println(File.separator);
        System.out.println(File.separatorChar);
        System.out.println(File.pathSeparator);
        System.out.println(File.pathSeparatorChar);
    }
}