package com.meidianyi.shop.service.saas.shop;

import static com.meidianyi.shop.db.main.tables.ShopRole.SHOP_ROLE;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRoleRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.MenuInnerParam;
import com.meidianyi.shop.service.pojo.shop.auth.MenuParam;
import com.meidianyi.shop.service.pojo.shop.auth.MenuReturnParam;
import com.meidianyi.shop.service.pojo.shop.config.group.ShopRoleAddListParam;
import com.meidianyi.shop.service.pojo.shop.config.group.ShopRoleGroupUpdateParam;
import com.meidianyi.shop.service.pojo.shop.config.group.ShopRoleParam;
import com.meidianyi.shop.service.pojo.shop.config.group.ShopRoleVo;

/**
 * 
 * @author 新国
 *
 */
@Service

public class ShopRoleService extends MainBaseService {
	protected final  String menuJsonPath = "admin.privilegeList.json";

	public ShopRoleRecord getRoleById(Integer roleId) {
		return db().selectFrom(SHOP_ROLE).where(SHOP_ROLE.ROLE_ID.eq(roleId)).fetchAny();
	}

	protected String[] getPrivilegeList(Integer roleId) {
		ShopRoleRecord role = getRoleById(roleId);
		if (role == null) {
			return null;
		}
		return Util.parseJson(role.getPrivilegeList(), String[].class);
	}

	public int insertRole(ShopRoleParam param, AdminTokenAuthInfo info) {
		ShopRoleRecord role = new ShopRoleRecord();
		role.setPrivilegeList(Util.toJson(param.getPrivilegeList()));
		role.setPrivilegePass(Util.toJson(param.getPrivilegePass()));
		role.setRoleName(param.getRoleName());
		if (!StringUtils.isEmpty(param.getRolePass())) {
			role.setRolePass(Util.md5(param.getRolePass()));
		}
		role.setSysId(info.getSysId());
		role.setShopId(info.getLoginShopId());
		return db().executeInsert(role);
	}

	public List<ShopRoleVo> getInfo(Integer sysId,Integer shopId) {
		SelectConditionStep<Record3<Integer, String, Timestamp>> records = db()
				.select(SHOP_ROLE.ROLE_ID, SHOP_ROLE.ROLE_NAME, SHOP_ROLE.CREATE_TIME).from(SHOP_ROLE)
				.where(SHOP_ROLE.SYS_ID.eq(sysId).and(SHOP_ROLE.SHOP_ID.eq(shopId)));
		records.orderBy(SHOP_ROLE.CREATE_TIME.desc()).fetch();
		List<ShopRoleVo> list = new ArrayList<>();
		for (Record3<Integer, String, Timestamp> record : records) {
			ShopRoleVo vo = new ShopRoleVo();
			vo.setRoleId(record.get(SHOP_ROLE.ROLE_ID));
			vo.setRoleName(record.get(SHOP_ROLE.ROLE_NAME));
			vo.setCreateTime(record.get(SHOP_ROLE.CREATE_TIME));
			list.add(vo);
		}
		return list;
	}
	
	public PageResult<ShopRoleVo> getInfo(Integer sysId,Integer shopId,ShopRoleAddListParam param) {
		SelectConditionStep<Record3<Integer, String, Timestamp>> records = db()
				.select(SHOP_ROLE.ROLE_ID, SHOP_ROLE.ROLE_NAME, SHOP_ROLE.CREATE_TIME).from(SHOP_ROLE)
				.where(SHOP_ROLE.SYS_ID.eq(sysId).and(SHOP_ROLE.SHOP_ID.eq(shopId)));
		records.orderBy(SHOP_ROLE.CREATE_TIME.desc()).fetch();
		List<ShopRoleVo> list = new ArrayList<>();
		for (Record3<Integer, String, Timestamp> record : records) {
			ShopRoleVo vo = new ShopRoleVo();
			vo.setRoleId(record.get(SHOP_ROLE.ROLE_ID));
			vo.setRoleName(record.get(SHOP_ROLE.ROLE_NAME));
			vo.setCreateTime(record.get(SHOP_ROLE.CREATE_TIME));
			list.add(vo);
		}
		return this.getPageResult(records, param.getCurrentPage(),param.getPageRows(), ShopRoleVo.class);
		//return list;
	}

	public ShopRoleRecord getRoleByIdAndSysId(Integer roleId, Integer sysId) {
		return db().selectFrom(SHOP_ROLE).where(SHOP_ROLE.ROLE_ID.eq(roleId).and(SHOP_ROLE.SYS_ID.eq(sysId)))
				.fetchAny();
	}

	public int deleteById(Integer roleId, Integer sysId, Integer shopId) {
		return db().deleteFrom(SHOP_ROLE)
				.where(SHOP_ROLE.ROLE_ID.eq(roleId).and(SHOP_ROLE.SYS_ID.eq(sysId).and(SHOP_ROLE.SHOP_ID.eq(shopId))))
				.execute();
	}

	public int updateRole(ShopRoleGroupUpdateParam param, AdminTokenAuthInfo info) {
		ShopRoleRecord role = new ShopRoleRecord();
		role.setPrivilegeList(Util.toJson(param.getPrivilegeList()));
		role.setPrivilegePass(Util.toJson(param.getPrivilegePass()));
		role.setRoleName(param.getRoleName());
		if (!StringUtils.isEmpty(param.getRolePass())) {
			role.setRolePass(Util.md5(param.getRolePass()));
		}
		role.setSysId(info.getSysId());
		role.setShopId(info.getLoginShopId());
		role.setRoleId(param.getRoleId());
		return db().executeUpdate(role);

	}

	public MenuReturnParam getPrivilegeListPublic(Integer roleId) {
		SelectConditionStep<Record2<String, String>> selectLists = db()
				.select(SHOP_ROLE.PRIVILEGE_LIST, SHOP_ROLE.PRIVILEGE_PASS).from(SHOP_ROLE)
				.where(SHOP_ROLE.ROLE_ID.eq(roleId));
		String prlJson = null;
		String prpJson = null;
		for (Record2<String, String> selectList : selectLists) {
			prlJson = selectList.get(SHOP_ROLE.PRIVILEGE_LIST);
			prpJson = selectList.get(SHOP_ROLE.PRIVILEGE_PASS);
		}
		MenuReturnParam param = new MenuReturnParam();
		param.setPrivilegeList(Util.parseJson(prlJson, List.class));
		param.setPrivilegePass(Util.parseJson(prpJson, List.class));
		return param;
	}

	public MenuParam outParam(MenuParam menuParam, List<?> userList) {
		Class<?> clazz = menuParam.getClass();
		Field[] fields = clazz.getDeclaredFields();
		MenuParam outParam = new MenuParam();
		Class<?> clazz2 = outParam.getClass();
		for (Field field : fields) {
			List<String> haveList = new ArrayList<>();
			for (Object inner : userList) {
				field.setAccessible(true);
				try {
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
					Method rm = pd.getReadMethod();
					List<?> dylist = (List<?>) rm.invoke(menuParam);
					for (Object dyObj : dylist) {
						if (inner.equals(dyObj)) {
							haveList.add(inner.toString());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				PropertyDescriptor pd2 = new PropertyDescriptor(field.getName(), clazz2);
				Method wm = pd2.getWriteMethod();
				wm.invoke(outParam, haveList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return outParam;
	}

	protected String[] getPrivilegePass(Integer roleId) {
		ShopRoleRecord role = getRoleById(roleId);
		if (role == null) {
			return null;
		}
		return Util.parseJson(role.getPrivilegePass(), String[].class);
	}

	/**
	 * 密码校验
	 * 
	 * @param passwd
	 * @param roleId
	 * @return
	 */
	public Boolean veryPasswd(String passwd, Integer roleId) {
		ShopRoleRecord record = db().selectFrom(SHOP_ROLE)
				.where(SHOP_ROLE.ROLE_ID.eq(roleId).and(SHOP_ROLE.ROLE_PASS.eq(Util.md5(passwd)))).fetchAny();
		if (record != null) {
			return true;
		}
		return false;
	}

	public Boolean checkPrivilegeList(Integer roleId, String enName) {
		SelectConditionStep<Record1<String>> selectLists = db().select(SHOP_ROLE.PRIVILEGE_LIST).from(SHOP_ROLE)
				.where(SHOP_ROLE.ROLE_ID.eq(roleId));
		if(StringUtils.isEmpty(enName)) {
			return false;
		}
		String prlJson = null;
		for (Record1<String> selectList : selectLists) {
			prlJson = selectList.get(SHOP_ROLE.PRIVILEGE_LIST);
		}

		List<String> list = Util.parseJson(prlJson, new TypeReference<List<String>>() {});
		
		//加入特殊的
		String json = Util.loadResource(menuJsonPath);
		MenuParam menuParam = Util.parseJson(json, MenuParam.class);
		List<MenuInnerParam> plus = menuParam.getPlus();
		for(MenuInnerParam param:plus) {
			List<String> preName = param.getPreName();
			list.addAll(preName);
		}
		
		for (Object enInnerName : list) {
			if (enName.equals(enInnerName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 特殊的几个权限的注入
	 * 门店管理、订单管理、商品管理
	 * @param menuParam
	 * @param userList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MenuParam specialParam(MenuParam menuParam, List<MenuInnerParam> plus) {
		// 门店管理 store_list
		
		Class<?> clazz = menuParam.getClass();
		Field[] fields = clazz.getDeclaredFields();
		MenuParam outParam = new MenuParam();
		Class<?> clazz2 = outParam.getClass();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
				Method rm = pd.getReadMethod();
				List<String> dylist = (List<String>) rm.invoke(menuParam);
				PropertyDescriptor pd2 = new PropertyDescriptor(field.getName(), clazz2);
				Method wm = pd2.getWriteMethod();
				for (MenuInnerParam innParam:plus) {
					if (isInner(dylist, innParam.getEnName())) {
						exchangeInner(dylist, innParam.getPreName(),innParam.getEnName());
						wm.invoke(outParam, dylist);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return menuParam;

	}
	private Boolean isInner(List<?> list,String param) {
		for(Object object:list) {
			if(object.equals(param)) {
				return true;
			}
		}
		return false;
	}
	
	private void exchangeInner(List<String> list,List<String> list2,String param) {
		int indexOf = list.indexOf(param);
		list.remove(param);
		list.addAll(indexOf, list2);
	}
}
