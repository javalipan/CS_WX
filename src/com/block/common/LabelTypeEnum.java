package com.block.common;

public enum LabelTypeEnum {
	
	LABELTYPE_LEVEL("level","会员级别"),
	LABELTYPE_MONEY("money","购物金额"),
	LABELTYPE_REPEAT("repeat","回头客"),
	LABELTYPE_RECENT("recent","近期购物情境"),
	LABELTYPE_TIME("time","入店资历"),
	LABELTYPE_SIZE("size","身形"),
	LABELTYPE_STYLE("style","风格"),
	LABELTYPE_BRAND("brand","品牌")
	;

	private String code;
    private String name;
 
    private LabelTypeEnum(String code,String name) {
        this.code = code;
        this.name = name;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
