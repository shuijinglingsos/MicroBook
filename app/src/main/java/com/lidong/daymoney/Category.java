package com.lidong.daymoney;

/**
 * @author Lidong
 * 分类表字段
 */
public class Category {

	/**
	 * ID
	 */
	public static final String ID="_id";

	/**
	 * 名称
	 */
	public static final String NAME="name";

	/**
	 * 1收入/0支出
	 */
	public static final String IO_TYPE="io_type";

	/**
	 * 状态
	 */
	public static final String STATUS="status";

	public long id;

	public String name;

	public int io_type;

	public int status;
}
