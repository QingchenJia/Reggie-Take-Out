package reggietakeout.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Orders implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    //订单号
    private String number;

    //订单状态:1：待付款，2：待派送，3：已派送，4：已完成，5：已取消
    private Integer status;

    //下单用户id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    //地址id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long addressBookId;

    //下单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    //结账时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkoutTime;

    //支付方式:1：微信，2：支付宝
    private Integer payMethod;

    //实收金额
    private BigDecimal amount;

    //备注
    private String remark;

    //用户名
    private String userName;

    //手机号
    private String phone;

    //地址
    private String address;

    //收货人
    private String consignee;
}
