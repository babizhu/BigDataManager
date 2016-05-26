package com.bbz.bigdata.platform.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by   liu_k
 * Time         2015/8/10 17:19
 */

public enum ErrorCode{

    SUCCESS( 0 ),

    ////////////////////////////////////////////////系统错误/////////////////////////////////////////

    /**
     * 无效的http请求
     */
    INVALID_REQUEST( 100 ),

    /**
     * 处理句柄不存在
     */
    HANDLER_NOT_FOUND( 101 ),

    /**
     * 客户端发送的签名字段验证错误
     */
    SIGNATURE_ERROR( 102 ),

    /**
     * 客户端发送请求参数错误
     */
    PARAMETER_ERROR( 103 ),

    /**
     * 方法未实现
     */
    NOT_IMPLENMENT( 104 ),


    ////////////////////////////////////////////////用户错误/////////////////////////////////////////
    /**
     * 尚未登录
     */
    USER_NOT_LOGIN( 1000 ),
    /**
     * 用户名或昵称重复
     */
    USER_DUPLICATE( 1001 ),
    /**
     * 已经登录
     */
    USER_HAS_LOGIN( 1002 ),
    /**
     * 用户不存在
     */
    USER_NOT_FOUND( 1003 ),
    /**
     * 用户名或密码错误
     */
    USER_UNAME_PASS_INVALID( 1004 ),
    /**
     * 用户体力值不足
     */
    USER_STRENGTH_NOT_ENOUGH( 1020 ),


    HERO_NOT_FOUND( 2000 ),

    /**
     * 用户能源值不足
     */
    USER_ENERGY_NOT_ENOUGH( 3002 ),

    /**
     * 用户金币不足
     */
    USER_CASH_NOT_ENOUGH( 3003 ),

    /**
     * 用户钻石不足
     */
    USER_DIAMOND_NOT_ENOUGH( 3004 ),

    ///////////////////////////////////////////////测试用错误/////////////////////////////////////////

    /**
     * 摇钱树今日运行次数超上限
     */
    MONEY_TREE_RUN_UPPER_LIMIT( 4000 ),


    ////////////////////////////////////////////////飞机错误/////////////////////////////////////////
    /**
     * 玩家指定id的飞机没有找到
     */
    PLANE_NOT_FOUND( 5000 ),
    /**
     * 相同模板的飞机已经存在了
     */
    PLANE_TEMPLET_DUPLICATE( 5001 ),
    /**
     * 飞机模板没找到
     */
    PLANE_TEMPLET_NOT_FOUND( 5002 ),
    /**
     * 飞机已经升到最大等级
     */
    PLANE_REACH_MAX_LEVEL( 5003 ),


    ////////////////////////////////////////////////奖励错误/////////////////////////////////////////
    /**
     * 道具不足
     */
    AWARD_NOT_ENOUGH( 6500 ),
    /**
     * 道具扣除数字不合法（小于0）
     */
    AWARD_REDUCE_COUNT_ILLEGAL( 6501 ),
    /**
     * 要变化数量的道具没找到
     */
    AWARD_PROP_NOT_FOUND( 6502 ),

    ////////////////////////////////////////////////邮件错误/////////////////////////////////////////
    /**
     * 邮件未找到
     */
    MAIL_MAIL_NOT_FOUND( 7000 ),


    ////////////////////////////////////////////////装备错误/////////////////////////////////////////
    /**
     * 装备未找到
     */
    EQUIPMENT_NOT_FOUND( 8000 ),

    /**
     * 装备升级等级超过限制
     */
    EQUIPMENT_LEVEL_OVER_LIMIT( 8001 ),

    /**
     * 装备进阶等级未达到要求
     */
    EQUIPMENT_LEVEL_UNDER_LIMIT( 8002 ),

    /**
     * 装备进阶需要的装备材料不足
     */
    EQUIPMENT_UPGRADE_EQUIP_NOT_ENOUGH( 8003 ),

    /**
     * 装备进阶品质超过限制
     */
    EQUIPMENT_UPGRADE_OVER_LIMIT( 8004 ),

    /**
     * 装备已被删除
     */
    EQUIPMENT_HAS_BEEN_DELETED( 8005 ),

    /**
     * 装备模版未找到
     */
    EQUIPMENT_TEMPLET_NOT_FOUND( 8006 ),

    /**
     * 装备类型错误，无法计算属性
     */
    EQUIPMENT_TYPE_ERROR( 8007 ),

    /**
     * 装备合成需要的万能碎片超出限制
     */
    EQUIPMENT_PURPOSE_OVERLIMIT( 8008 ),

    /**
     * 要分解的装备不合法
     */
    EQUIPMENT_SPLIT_ILLEGAL( 8009 ),

    ////////////////////////////////////////////////僚机错误/////////////////////////////////////////
    /**
     * 僚机未找到
     */
    WING_NOT_FOUND( 8500 ),
    /**
     * 僚机模板未找到
     */
    WING_TEMPLET_NOT_FOUND( 8501 ),
    /**
     * 僚机进阶超过品质上限
     */
    WING_UPGRADE_OVER_LIMIT( 8502 ),
    /**
     * 僚机进阶未达等级上限
     */
    WING_UPGRADE_LEVEL_UNDER_LIMIT( 8503 ),
    /**
     * 被吞噬的僚机是要进阶的僚机
     */
    WING_SWALLOW_SELF( 8504 ),
    /**
     * 被吞噬的僚机是当前出战的僚机
     */
    WING_SWALLOW_IS_CURRENT( 8505 ),
    /**
     * 被吞噬的僚机数量不够或者多了
     */
    WING_SWALLOW_COUNT_INVALID( 8506 ),

    /**
     * 升级所使用的道具不是经验卡
     */
    WING_LEVELUP_STUFF_ERROR( 8507 ),

    /**
     * 僚机升级已达当前品质的上限
     */
    WING_LEVELUP_REACHED_LIMIT( 8508 ),
    /**
     * 用于升阶的被吞噬的僚机的品质小于需求
     */
    WING_SWALLOW_QUALITY_UNDER_LIMIT( 8509 ),

    /**
     * 此僚机不能被合成
     */
    WING_CAN_NOT_COMPOSE( 8510 ),
    /**
     * 合成僚机时，万能碎片的使用超过上限
     */
    WING_PURPOSE_OVERLIMIT( 8511 ),

    ////////////////////////////////////////////////道具错误/////////////////////////////////////////
    /**
     * 道具不足
     */
    STUFF_NOT_ENOUGH( 9000 );


    ////////////////////////////////////////////////枚举结束/////////////////////////////////////////


    private static final Map<Integer, ErrorCode> numToEnum = new HashMap<>();

    static{
        for( ErrorCode t : values() ) {

            ErrorCode s = numToEnum.put( t.number, t );
            if( s != null ) {
                throw new RuntimeException( t.number + "重复了" );
            }
        }
    }


    /**
     * 判断当前枚举是否成功
     *
     * @return true:成功 false:失败
     */
    public boolean isSuccess(){

        return number == 0;
    }

    private final int number;
    private final String msg;

    ErrorCode( int number ){
        this.number = number;
        this.msg = "aaa";
        
    }

    ErrorCode( int number, String msg ){
        this.number = number;
        this.msg = msg;
    }

    public static ErrorCode fromNum( int n ){
        return numToEnum.get( n );
    }

    public int toNum(){
        return number;
    }


    public static void main( String[] args ){
        System.out.println( ErrorCode.SUCCESS.toNum() );
    }

}

