package com.lidong.daymoney;

import java.util.Date;

/**
 * @author Lidong 交易明细表字段
 */
public class Transaction {

	/**
	 * ID
	 */
	public static final String ID = "_id";

	/**
	 * 日期
	 */
	public static final String DATE = "date";

	/**
	 * 交易类型 0支出 1收入 2转账 3修改余额
	 */
	public static final String T_TYPE="t_type";

	/**
	 * 分类ID
	 */
	public static final String CATEGORY_ID = "category_id";

	/**
	 * 账户ID  （转账时为转出账户）（修改余额时为修改账户ID）
	 */
	public static final String ACCOUNT_ID_1 = "account_id_1";

	/**
	 * 转账时为转入账户
	 */
	public static final String ACCOUNT_ID_2 = "account_id_2";

	/**
	 * 价格
	 */
	public static final String MONEY = "money";

	/**
	 * 备注
	 */
	public static final String REMARK = "remark";

	/**
	 * 状态
	 */
	public static final String STATUS = "status";

	public long id;

	public Date date;

	public int t_type;

	public long category_id;

	public long account_id_1;

	public long account_id_2;

	public double money;

	public String remark;

	public int status;

	public String ex_categroy_name;
}
