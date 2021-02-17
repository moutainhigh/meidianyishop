package com.mp.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tets {
	public List<String> specialParam(List<String> goodsManage, List<String> userList) {
		// 门店管理 store_list
		if (isInner(goodsManage, "store_list")) {
			System.out.println("进来");
			List<String> list2 = Arrays.asList("store_list","add_store");
			 exchangeInner(goodsManage, list2, "store_list");
		}
		return goodsManage;

	}

	private Boolean isInner(List<?> list, String param) {
		for (Object object : list) {
			System.out.println(object.toString());
			if (object.toString().equals(param)) {
				System.out.println("有");
				return true;
			}
		}
		System.out.println("没有");
		return false;
	}

	private void exchangeInner(List<String> list, List<String> list2, String param) {
		int indexOf = list.indexOf(param);
		System.out.println(indexOf);
		list.remove(param);
		list.addAll(indexOf,list2);
	}

	public static void main(String[] args) {
		tets test = new tets();
		List<String> goodsManage1 = Arrays.asList("store_list","store_verify","add_store1","group_manage","store_service_config");
		List<String> goodsManage=new ArrayList<>(goodsManage1);
		List<String> userList = Arrays.asList("store_list","add_store");
		List<String> specialParam = test.specialParam(goodsManage, userList);
		for(Object s:specialParam) {
			System.out.println(s);
		}
	}
}
