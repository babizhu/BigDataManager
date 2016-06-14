package com.bbz.bigdata.platform.rrdtool.resultparser;

import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;

import java.util.ArrayList;

public class RRDResultJoiner {

	/**
	 * json结果合并方法
	 * @param primaryModel 主数据，数据合并到该数据对象中，不能为空
	 * @param joiner 要合并的数据
	 * @param checkYUnit 是否检查纵坐标单位是否相同
	 * @throws BussException 
	 */
	public static RRDModel join(RRDModel primaryModel, RRDModel joiner, boolean checkYUnit) throws BussException{
		if (joiner==null) {
			return primaryModel;
		}
		if (checkYUnit) {
			if (primaryModel.getYunit()==joiner.getYunit()
					||primaryModel.getYunit()!=null&&primaryModel.getYunit().equals(joiner.getYunit())) {
				if (primaryModel.getList()==null) {
					primaryModel.setList(new ArrayList<>());
				}
				if (joiner.getList()==null) {
					joiner.setList(new ArrayList<>());
				}
				primaryModel.getList().addAll(joiner.getList());
				return primaryModel;
			}else{
				throw new BussException(BussException.UNIT_NOT_MATCHED);
			}
		}else{
			if (primaryModel.getList()==null) {
				primaryModel.setList(new ArrayList<>());
			}
			if (joiner.getList()==null) {
				joiner.setList(new ArrayList<>());
			}
			primaryModel.getList().addAll(joiner.getList());
			return primaryModel;
		}
	}
}
