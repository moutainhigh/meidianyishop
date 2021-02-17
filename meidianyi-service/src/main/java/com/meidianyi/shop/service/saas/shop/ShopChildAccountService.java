package com.meidianyi.shop.service.saas.shop;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.ShopChildAccount;
import com.meidianyi.shop.db.main.tables.ShopChildRole;
import com.meidianyi.shop.db.main.tables.ShopRole;
import com.meidianyi.shop.db.main.tables.records.ShopChildAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopChildRoleRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.ShopChildAccountListQueryParam;
import com.meidianyi.shop.service.pojo.saas.shop.ShopChildAccountPojo;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopSubAccountAddParam;
import com.meidianyi.shop.service.pojo.shop.config.group.*;
import org.jooq.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.main.tables.Shop.SHOP;
import static com.meidianyi.shop.db.main.tables.ShopChildAccount.SHOP_CHILD_ACCOUNT;
import static com.meidianyi.shop.db.main.tables.ShopChildRole.SHOP_CHILD_ROLE;
import static com.meidianyi.shop.db.main.tables.ShopRole.SHOP_ROLE;

/**
 * 
 * @author 新国
 *
 */
@Service

public class ShopChildAccountService extends MainBaseService {

	public ShopChildAccountRecord verify(Integer sysId, String username, String password) {
		return db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId))
				.and(SHOP_CHILD_ACCOUNT.ACCOUNT_NAME.eq(username).or(SHOP_CHILD_ACCOUNT.MOBILE.eq(username)))
				.and(SHOP_CHILD_ACCOUNT.ACCOUNT_PWD.eq(Util.md5(password))).fetchAny();
	}

	public ShopChildAccountRecord getUserFromAccountName(Integer sysId, String username) {
		return db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId))
				.and(SHOP_CHILD_ACCOUNT.ACCOUNT_NAME.eq(username)).fetchAny();
	}

	public ShopChildAccountRecord getUserFromMobile(Integer sysId, String mobile) {
		return db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId))
				.and(SHOP_CHILD_ACCOUNT.MOBILE.eq(mobile)).fetchAny();
	}

	public PageResult<ShopChildAccountPojo> getPageList(ShopChildAccountListQueryParam param) {
		SelectWhereStep<Record> select = db().select().from(SHOP_CHILD_ACCOUNT);
		select.where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(param.sysId));
		select.orderBy(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.desc());
		return this.getPageResult(select, param.page, ShopChildAccountPojo.class);
	}

	public int removeAccount(Integer sysId, Integer accountId) {
		return db().delete(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId))
				.and(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.eq(accountId)).execute();
	}

	public ShopChildAccountRecord updateAccount(ShopChildAccountPojo childAccount) {
		ShopChildAccountRecord record = db().newRecord(SHOP_CHILD_ACCOUNT, childAccount);
		db().executeUpdate(record);
		return record;
	}

	public Result<Record6<Integer, Integer, Integer, Integer, String, String>> getShopRole(Integer sysId,
			Integer accountId) {
		return db()
				.select(SHOP_CHILD_ROLE.SYS_ID, SHOP_CHILD_ROLE.SHOP_ID, SHOP_CHILD_ROLE.ACCOUNT_ID,
						SHOP_CHILD_ROLE.ROLE_ID, SHOP_ROLE.ROLE_NAME, SHOP.SHOP_NAME)
				.from(SHOP_CHILD_ROLE).join(SHOP_ROLE).on(SHOP_CHILD_ROLE.ROLE_ID.eq(SHOP_ROLE.ROLE_ID)).join(SHOP)
				.on(SHOP_CHILD_ROLE.SHOP_ID.eq(SHOP.SHOP_ID)).where(SHOP_CHILD_ROLE.SYS_ID.eq(sysId))
				.and(SHOP_CHILD_ROLE.ACCOUNT_ID.eq(accountId)).fetch();
	}

	public PageResult<ShopChildAccountPojo> getNotAccountPageList(ShopChildAccountListQueryParam param) {
		SelectLimitStep<?> select = db().select(SHOP_CHILD_ACCOUNT.asterisk()).from(SHOP_CHILD_ACCOUNT)
				.join(SHOP_CHILD_ROLE).on(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.notEqual(SHOP_CHILD_ROLE.ROLE_ID))
				.where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(param.sysId)).orderBy(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.desc());
		return this.getPageResult(select, param.page, ShopChildAccountPojo.class);
	}

	public Result<ShopChildAccountRecord> getAccount(Integer sysId) {
		return db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId)).fetch();
	}

	public ShopChildAccountRecord getSubAccountInfo(Integer sysId, Integer subAccountId) {
		return db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId))
				.and(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.eq(subAccountId)).fetchAny();
	}

	public ShopChildAccountRecord getSubAccountInfo(Integer subAccountId) {
		return db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.eq(subAccountId)).fetchAny();
	}

	public Result<ShopChildAccountRecord> getSubAccountUser(String nameOrMobile) {
		return db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.ACCOUNT_NAME.eq(nameOrMobile))
				.or(SHOP_CHILD_ACCOUNT.MOBILE.eq(nameOrMobile)).fetch();
	}

	public ShopChildAccountRecord checkByIdAndNameOnChild(Integer sysId, String accountname, Integer accountId) {
		return db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId))
				.and(SHOP_CHILD_ACCOUNT.ACCOUNT_NAME.eq(accountname)).and(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.eq(accountId))
				.fetchAny();
	}

	public ShopChildRoleRecord checkByRecode(Integer roleId, AdminTokenAuthInfo info) {
		return db().selectFrom(SHOP_CHILD_ROLE)
				.where(SHOP_CHILD_ROLE.SYS_ID.eq(info.getSysId())
						.and(SHOP_CHILD_ROLE.ROLE_ID.eq(roleId).and(SHOP_CHILD_ROLE.SHOP_ID.eq(info.getLoginShopId()))))
				.fetchAny();
	}

	public List<ShopChildAccountVo> getInfoBySysId(Integer sysId) {
		SelectConditionStep<Record3<Integer, String, String>> sRecord = db()
				.select(SHOP_CHILD_ACCOUNT.ACCOUNT_ID, SHOP_CHILD_ACCOUNT.ACCOUNT_NAME, SHOP_CHILD_ACCOUNT.MOBILE)
				.from(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId));
		sRecord.orderBy(SHOP_CHILD_ACCOUNT.CREATE_TIME).fetch();
		List<ShopChildAccountVo> childAccountList = new ArrayList<>();
		for (Record3<Integer, String, String> record : sRecord) {
			ShopChildAccountVo vo = new ShopChildAccountVo();
			vo.setAccountId(record.get(SHOP_CHILD_ACCOUNT.ACCOUNT_ID));
			vo.setAccountName(record.get(SHOP_CHILD_ACCOUNT.ACCOUNT_NAME));
			vo.setMobile(record.get(SHOP_CHILD_ACCOUNT.MOBILE));
			childAccountList.add(vo);
		}
		return childAccountList;
	}

	public int insertshopChildRole(ShopRoleAddParam sAddParam, AdminTokenAuthInfo info) {
		ShopChildRoleRecord scrr = new ShopChildRoleRecord();
		scrr.setSysId(info.getSysId());
		scrr.setAccountId(sAddParam.getAccountId());
		scrr.setShopId(info.getLoginShopId());
		scrr.setRoleId(sAddParam.getRoleId());
		return db().executeInsert(scrr);
	}

	public List<ShopRoleAddListVo> queryRoleAndAccount(Integer sysId) {
		ShopChildAccount a = SHOP_CHILD_ACCOUNT.as("a");
		ShopChildRole b = SHOP_CHILD_ROLE.as("b");
		ShopRole c = SHOP_ROLE.as("c");

		Result<Record6<Integer, String, String, Integer, String, Integer>> result = db()
				.select(a.ACCOUNT_ID, a.ACCOUNT_NAME, a.MOBILE, c.ROLE_ID, c.ROLE_NAME, b.REC_ID).from(a).join(b)
				.on(b.SYS_ID.eq(a.SYS_ID).and(b.ACCOUNT_ID.eq(a.ACCOUNT_ID))).join(c)
				.on(c.ROLE_ID.eq(b.ROLE_ID).and(b.SYS_ID.eq(c.SYS_ID))).and(c.SYS_ID.eq(sysId))
				.orderBy(a.CREATE_TIME.desc()).fetch();
		List<ShopRoleAddListVo> list = new ArrayList<>();
		for (Record6<Integer, String, String, Integer, String, Integer> record : result) {
			ShopRoleAddListVo vo = new ShopRoleAddListVo();
			vo.setAccountId(record.get(a.ACCOUNT_ID));
			vo.setAccountName(record.get(a.ACCOUNT_NAME));
			vo.setMobile(record.get(a.MOBILE));
			vo.setRoleId(record.get(c.ROLE_ID));
			vo.setRoleName(record.get(c.ROLE_NAME));
			vo.setRecId(record.get(b.REC_ID));
			list.add(vo);
		}
		return list;
	}
	
	public PageResult<ShopRoleAddListVo> getAccountRolePageList(Integer shopId,ShopRoleAddListParam sAddListParam) {
		ShopChildRole a = SHOP_CHILD_ROLE.as("a");
		ShopChildAccount b = SHOP_CHILD_ACCOUNT.as("b");
		ShopRole c = SHOP_ROLE.as("c");
		SelectConditionStep<Record8<Integer, String, String, Integer, String, Integer, String, Byte>> select = db()
				.select(a.ACCOUNT_ID, b.ACCOUNT_NAME, b.MOBILE, c.ROLE_ID, c.ROLE_NAME, a.REC_ID,
						b.OFFICIAL_OPEN_ID, b.IS_BIND)
				.from(a).join(c).on(a.ROLE_ID.eq(c.ROLE_ID)).join(b).on(a.ACCOUNT_ID.eq(b.ACCOUNT_ID))
				.where(a.SHOP_ID.eq(shopId));
		select.orderBy(a.ACCOUNT_ID.desc());
		return this.getPageResult(select, sAddListParam.getCurrentPage(),sAddListParam.getPageRows(), ShopRoleAddListVo.class);
	}

	public ShopChildRoleRecord checkByRecodeAndAccId(Integer roleId, Integer accountId, AdminTokenAuthInfo info) {
		return db().selectFrom(SHOP_CHILD_ROLE)
				.where(SHOP_CHILD_ROLE.SYS_ID.eq(info.getSysId()).and(SHOP_CHILD_ROLE.ACCOUNT_ID.eq(accountId))
						.and(SHOP_CHILD_ROLE.SHOP_ID.eq(info.getLoginShopId())))
				.fetchAny();
	}

	public int updateShopChildRole(ShopRoleUpdateParam sParam, AdminTokenAuthInfo info) {
		ShopChildRoleRecord scrr = new ShopChildRoleRecord();
		scrr.setAccountId(sParam.getAccountId());
		scrr.setRoleId(sParam.getRoleId());
		scrr.setRecId(sParam.getRecId());
		return db().executeUpdate(scrr);
	}

	public int deleteShopChildRole(ShopRoleUpdateParam sUpdatePara) {
		return db().deleteFrom(SHOP_CHILD_ROLE)
				.where(SHOP_CHILD_ROLE.REC_ID.eq(sUpdatePara.getRecId()).and(SHOP_CHILD_ROLE.ACCOUNT_ID
						.eq(sUpdatePara.getAccountId()).and(SHOP_CHILD_ROLE.ROLE_ID.eq(sUpdatePara.getRoleId()))))
				.execute();
	}
	
	public int upateBind(Integer subAccountId, String officalOpenId, byte bind) {
		return db().update(SHOP_CHILD_ACCOUNT).set(SHOP_CHILD_ACCOUNT.OFFICIAL_OPEN_ID, officalOpenId)
				.set(SHOP_CHILD_ACCOUNT.IS_BIND, bind).where(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.eq(subAccountId)).execute();

	}
	
	public int updateRowBind(Integer subAccountId,byte bind) {
		return db().update(SHOP_CHILD_ACCOUNT).set(SHOP_CHILD_ACCOUNT.IS_BIND, bind)
				.where(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.eq(subAccountId)).execute();
	}
	
	/**
	 * 主账号管理子账号按页查询列表
	 * @param sysId
	 * @param currentPage
	 * @param pageRows
	 * @return
	 */
	public PageResult<ShopChildUserListVo> getAccountUserList(Integer sysId,Integer currentPage,Integer pageRows) {
		SelectSeekStep1<ShopChildAccountRecord, Integer> select = db().selectFrom(SHOP_CHILD_ACCOUNT)
				.where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId)).orderBy(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.desc());
		PageResult<ShopChildUserListVo> pageResult = this.getPageResult(select, currentPage, pageRows,
				ShopChildUserListVo.class);
		for (ShopChildUserListVo vo : pageResult.dataList) {
			Result<Record4<Integer, Integer, String, Integer>> fetch = db()
					.select(SHOP_CHILD_ROLE.REC_ID, SHOP_ROLE.ROLE_ID, SHOP_ROLE.ROLE_NAME, SHOP_CHILD_ROLE.SHOP_ID)
					.from(SHOP_CHILD_ROLE, SHOP_ROLE)
					.where(SHOP_CHILD_ROLE.SYS_ID.eq(sysId).and(SHOP_CHILD_ROLE.ACCOUNT_ID.eq(vo.getAccountId()))
							.and(SHOP_CHILD_ROLE.ROLE_ID.eq(SHOP_ROLE.ROLE_ID)))
					.orderBy(SHOP_CHILD_ROLE.REC_ID.desc()).fetch();
			if(fetch.size()>0) {
				List<ShopChildUserShopInfoVo> list=new ArrayList<ShopChildUserShopInfoVo>();
				for(Record4<Integer, Integer, String, Integer> record:fetch) {
					ShopChildUserShopInfoVo inner = record.into(ShopChildUserShopInfoVo.class);
					String shopName = db().select(SHOP.SHOP_NAME).from(SHOP).where(SHOP.SHOP_ID.eq(record.get(SHOP_CHILD_ROLE.SHOP_ID))).fetchAny().into(String.class);
					inner.setShopName(shopName);
					list.add(inner);
				}
				vo.setShopInfo(list);
			}
			
		}
		return pageResult;
	}
	
	/**
	 * 添加子账户
	 * @param sysId
	 * @param param
	 * @return
	 */
	public JsonResultCode addSubAccount(Integer sysId,ShopSubAccountAddParam param) {
		ShopChildAccountRecord recordfetchAny = db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId).and(SHOP_CHILD_ACCOUNT.ACCOUNT_NAME.eq(param.getAccountName()))).fetchAny();
		if(recordfetchAny!=null) {
			//用户名重复
			return JsonResultCode.CODE_ACCOUNT_SAME;
		}
		ShopChildAccountRecord rAccountRecord = db().selectFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId).and(SHOP_CHILD_ACCOUNT.MOBILE.eq(param.getMobile()))).fetchAny();
		if(rAccountRecord!=null) {
			//手机号重复
			return JsonResultCode.CODE_MOBILE_SAME;
		}
		param.setAccountPwd(Util.md5(param.getAccountPwd()));
		ShopChildAccountRecord record=db().newRecord(SHOP_CHILD_ACCOUNT,param);
		record.setSysId(sysId);
		int insert = record.insert();
		if(insert>0) {
			//成功
			return null;
		}
		return JsonResultCode.CODE_FAIL;
	}
	
	/**
	 * 删除子账户
	 * @param sysId
	 * @param accountId
	 * @return
	 */
	public int delSubAccount(Integer sysId,Integer accountId){
		return db().deleteFrom(SHOP_CHILD_ACCOUNT).where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId).and(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.eq(accountId))).execute();
	}
	
	/**
	 * 更新
	 * @param sysId
	 * @param accountId
	 * @param accountName
	 * @param passwd
	 * @return
	 */
	public int editSubAccount(Integer sysId, Integer accountId, String accountName, String passwd,String mobile) {
		int execute = 0;
		if (passwd != null) {
			execute = db().update(SHOP_CHILD_ACCOUNT).set(SHOP_CHILD_ACCOUNT.MOBILE, mobile)
					.set(SHOP_CHILD_ACCOUNT.ACCOUNT_PWD, Util.md5(passwd))
					.where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId).and(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.eq(accountId)
							.and(SHOP_CHILD_ACCOUNT.ACCOUNT_NAME.eq(accountName))))
					.execute();
		} else {
			execute = db().update(SHOP_CHILD_ACCOUNT).set(SHOP_CHILD_ACCOUNT.MOBILE, mobile)
					.where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId).and(SHOP_CHILD_ACCOUNT.ACCOUNT_ID.eq(accountId)
							.and(SHOP_CHILD_ACCOUNT.ACCOUNT_NAME.eq(accountName))))
					.execute();
		}

		return execute;

	}
	
	/**
	 * 获得绑定第三方公众号的账号
	 * @param sysId
	 * @return
	 */
	public List<ShopChildAccountPojo> getAccountByBindThird(Integer sysId) {
		return db().selectFrom(SHOP_CHILD_ACCOUNT)
				.where(SHOP_CHILD_ACCOUNT.SYS_ID.eq(sysId).and(SHOP_CHILD_ACCOUNT.IS_BIND.eq((byte) 1)))
				.fetchInto(ShopChildAccountPojo.class);
	}
}
