package com.bbz.bigdata.exam.genaral;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liulaoye on 16-8-19.
 * 盘子
 */
public class Plate<T>{
    private T item;

    public Plate( T item ){
        this.item = item;
    }



    public T getItem(){
        return item;
    }

    public void setItem( T item ){
        this.item = item;
    }

    public static void main( String[] args ){
        //Plate<Fruit> p= new Plate<Apple>(new Apple());
        Plate<Apple> applePlate = new Plate<>( new Apple() );
        Plate<? extends Fruit> applePlate1 = new Plate<Fruit>( new Apple() );
//        applePlate1.setItem( new Object() );
//        applePlate1.setItem( new Fruit() );

        List<? super Fruit> list=new ArrayList<>();
        list.add( new Apple() );
        list.add( new Fruit() );


    }


}
