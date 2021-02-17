package com.meidianyi.shop.service.pojo.shop.order.write.operate;


/**
 * ************************************************
 * **以下枚举不可调换顺序，param与处理类依赖顺序关联 **
 * ************************************************
 * 订单状态操作标识service枚举类;此类中的clz只是为了方便查找业务的对于service；
 * 具体关联是IOrderBase实现类与其getServiceCode里的OrderServiceCode对应
 *
 * @author 王帅
 */
public enum OrderServiceCode {
    //0:admin后台发货ShipService
    ADMIN_SHIP(),
    //1:退款 退货 ReturnMoneyApple
    RETURN(),
    //2:取消
    CANCEL(),
    //3:关闭
    CLOSE(),
    //4:核销（强制核销）
    VERIFY(),
    //5:完成
    FINISH(),
    //6:收货
    RECEIVE(),
    //7:延长收货
    EXTEND_RECEIVE(),
    //8:提醒发货
    REMIND(),
    //9:删除订单
    DELETE(),
    //10:下单
    CREATE(),
    //11:支付
    PAY(),
    //12:好友代付
    INSTEAD_PAY(),
    //13:订单得处方续方
    PRESCRIPTION(),
    //14:订单待开方
    MAKE_PRESCRIPTION(),
    //15:药师拣药完成
    PICK_MEDICINE();

    OrderServiceCode() {
    }
}
