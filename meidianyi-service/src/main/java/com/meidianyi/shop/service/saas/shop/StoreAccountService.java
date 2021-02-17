package com.meidianyi.shop.service.saas.shop;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.dao.main.StoreAccountDao;
import com.meidianyi.shop.dao.shop.store.StoreDao;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.main.tables.records.StoreAccountRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.StoreAuthConstant;
import com.meidianyi.shop.service.pojo.shop.auth.StoreAuthInfoVo;
import com.meidianyi.shop.service.pojo.shop.auth.StoreLoginParam;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountEditParam;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountParam;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import com.meidianyi.shop.service.pojo.shop.store.authority.StoreAuthListPage;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;
import jodd.util.StringUtil;
import org.jooq.Condition;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.meidianyi.shop.db.main.Tables.SHOP;
import static com.meidianyi.shop.db.main.tables.StoreAccount.STORE_ACCOUNT;

/**
 *
 * @author zhaojianqiang
 * @time 下午4:58:30
 */
@Service
public class StoreAccountService extends MainBaseService {
	private static final Byte IS_DEL = 1;
	private static final Byte NO_DEL = 0;
	private static final String DOT = ",";
	@Autowired
	public StoreAccountDao storeAccountDao;
    @Autowired
    public StoreDao storeDao;

    /**
     * 获取用户列表
     * @param param
     * @param sysId
     * @param shopId
     * @return
     */
	public PageResult<StoreAccountVo> accountList(StoreAuthListPage param, Integer sysId, Integer shopId) {
		SelectConditionStep<StoreAccountRecord> where = db().selectFrom(STORE_ACCOUNT).where(STORE_ACCOUNT.DEL_FLAG
				.eq(NO_DEL).and(STORE_ACCOUNT.SYS_ID.eq(sysId).and(STORE_ACCOUNT.SHOP_ID.eq(shopId))));
		if (!StringUtils.isEmpty(param.getAccountName())) {
			where.and(STORE_ACCOUNT.ACCOUNT_NAME.like(likeValue(param.getAccountName())));
		}
		if (!StringUtils.isEmpty(param.getMobile())) {
			where.and(STORE_ACCOUNT.MOBILE.like(likeValue(param.getMobile())));
		}
		if (param.getAccountType() > 0) {
			where.and(STORE_ACCOUNT.ACCOUNT_TYPE.eq(param.getAccountType()));
		}
		if (param.getStatus() > -1) {
			where.and(STORE_ACCOUNT.STATUS.eq(param.getStatus()));
		}
		if (param.getStoreIds() != null) {
            Condition condition = DSL.noCondition();
            for (Integer storeId:param.getStoreIds()) {
                condition = condition.or(DslPlus.findInSet(storeId, STORE_ACCOUNT.STORE_LIST));
            }
            where.and(condition);
        }
		return this.getPageResult(where, param.getCurrentPage(), param.getPageRows(), StoreAccountVo.class);
	}

	/**
	 * 根据id获取
	 *
	 * @param accountId
	 * @return
	 */
	public StoreAccountVo getStoreInfoById(Integer accountId) {
		 StoreAccountVo fetchAnyInto = db().selectFrom(STORE_ACCOUNT).where(STORE_ACCOUNT.ACCOUNT_ID.eq(accountId))
				.fetchAnyInto(StoreAccountVo.class);
		 if(fetchAnyInto!=null) {
			 List<Integer> list = changeToArray(fetchAnyInto.getStoreList());
			 fetchAnyInto.setStoreLists(list);
			 fetchAnyInto.setStoreNum(list.size());
		 }
		 return fetchAnyInto;

	}

	public int updateStoreList(Integer accountId, String storeList) {
		return db().update(STORE_ACCOUNT).set(STORE_ACCOUNT.STORE_LIST, storeList)
				.where(STORE_ACCOUNT.ACCOUNT_ID.eq(accountId)).execute();
	}

	public int updateStoreList(Integer accountId, Integer[] storeLists) {
		String string = changeToString(storeLists);
		return db().update(STORE_ACCOUNT).set(STORE_ACCOUNT.STORE_LIST, string)
				.where(STORE_ACCOUNT.ACCOUNT_ID.eq(accountId)).execute();
	}

	/**
	 * 删除
	 *
	 * @param accountId
	 * @return
	 */
	public int delStoreAccount(Integer accountId) {
		return db().update(STORE_ACCOUNT).set(STORE_ACCOUNT.DEL_FLAG, IS_DEL)
				.where(STORE_ACCOUNT.ACCOUNT_ID.eq(accountId)).execute();
	}

	/**
	 * 更新状态
	 *
	 * @param accountId
	 * @param status
	 * @return
	 */
	public int updateStatus(Integer accountId, Byte status) {
		return db().update(STORE_ACCOUNT).set(STORE_ACCOUNT.STATUS, status)
				.where(STORE_ACCOUNT.ACCOUNT_ID.eq(accountId)).execute();
	}

	protected List<Integer> changeToArray(String stores) {
		List<Integer> list = new ArrayList<Integer>();
		if (stores != null) {
			if (stores.contains(DOT)) {
				String[] split = stores.split(DOT);
				for (String string : split) {
					list.add(Integer.valueOf(string));
				}
			} else {
				list.add(Integer.valueOf(stores));
			}
		}
		return list;
	}

	protected String changeToString(Integer[] storeList) {
		Set<Integer> set = new LinkedHashSet<Integer>();
		for (Integer integer : storeList) {
			set.add(integer);
		}
		StringBuilder builder=new StringBuilder();
		for (Object object : set) {
			builder.append(object);
			builder.append(",");
		}
		if(builder.length()>0) {
			return builder.deleteCharAt(builder.length()-1).toString();
		}
		return null;
	}

    /**
     * 新建
     *
     * @param param
     * @return
     */
    public int create(StoreAccountParam param, Integer shopId) {
        ShopRecord shop = db().selectFrom(SHOP).where(SHOP.SHOP_ID.eq(shopId)).fetchAny();
        StoreAccountRecord record = db().newRecord(STORE_ACCOUNT, param);
        record.setStoreList(changeToString(param.getStoreList()));
        record.setAccountPasswd(Util.md5(param.getAccountPasswd()));
        record.setSysId(shop.getSysId());
        record.setShopId(shopId);
        record.setWxNickName("");
        int insert = record.insert();
        return insert;
    }

	/**
	 * 编辑
	 *
	 * @param param
	 * @return
	 */
	public int edit(StoreAccountEditParam param) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put(STORE_ACCOUNT.MOBILE, param.getMobile());
		map.put(STORE_ACCOUNT.ACCOUNT_NAME, param.getAccountName());
		map.put(STORE_ACCOUNT.ACCOUNT_TYPE, param.getAccountType());
		map.put(STORE_ACCOUNT.STORE_LIST, changeToString(param.getStoreList()));
		if (!StringUtils.isEmpty(param.getAccountPasswd())) {
			map.put(STORE_ACCOUNT.ACCOUNT_PASSWD, Util.md5(param.getAccountPasswd()));
		}
		int execute = db().update(STORE_ACCOUNT).set(map).where(STORE_ACCOUNT.ACCOUNT_ID.eq(param.getAccountId()))
				.execute();
		return execute;
	}

	public StoreAccountRecord findInfo(String accountName, String mobile, Integer accountId) {
		SelectConditionStep<StoreAccountRecord> where = db().selectFrom(STORE_ACCOUNT)
				.where(STORE_ACCOUNT.ACCOUNT_NAME.eq(accountName).or(STORE_ACCOUNT.MOBILE.eq(mobile)).and(STORE_ACCOUNT.DEL_FLAG.eq(NO_DEL)));
		if (accountId != null) {
			where.and(STORE_ACCOUNT.ACCOUNT_ID.ne(accountId));
		}
		StoreAccountRecord any = where.fetchAny();
		return any;
	}

	public StoreAuthInfoVo getStoreAccountFlag(StoreLoginParam param){
        StoreAuthInfoVo storeAuthInfoVo = new StoreAuthInfoVo();
        StoreAccountVo storeAccountInfo = storeAccountDao.getStoreAccountInfo(param);
        if (storeAccountInfo == null) {
            storeAuthInfoVo.setMsg(StoreAuthConstant.ACCOUNT_NOT_EXIST);
        } else if (StoreAuthConstant.IS_DELETE.equals(storeAccountInfo.getDelFlag())){
            storeAuthInfoVo.setMsg(StoreAuthConstant.ACCOUNT_IS_DELETE);
        } else if (StoreAuthConstant.IS_FORBIDDEN.equals(storeAccountInfo.getStatus())){
            storeAuthInfoVo.setMsg(StoreAuthConstant.ACCOUNT_IS_FORBIDDEN);
        } else if (StringUtil.isBlank(storeAccountInfo.getStoreList())){
            storeAuthInfoVo.setMsg(StoreAuthConstant.STORE_IS_EMPTY);
        } else {
            Boolean isOk = true;
            List<Integer> list = changeToArray(storeAccountInfo.getStoreList());
            storeAccountInfo.setStoreLists(list);
            if (StoreAuthConstant.STORE_CLERK.equals(param.getStoreAccountType())) {
                StoreBasicVo storeInfo = saas().getShopApp(storeAccountInfo.getShopId()).store.getStoreByNo(param.getStoreNo());
                if (storeInfo == null || !list.contains(storeInfo.getStoreId())) {
                    storeAuthInfoVo.setMsg(StoreAuthConstant.STORE_NOT_EXIST);
                    isOk = false;
                } else {
                    List<Integer> storeListNew = new ArrayList<>();
                    storeListNew.add(storeInfo.getStoreId());
                    storeAccountInfo.setStoreLists(storeListNew);
                }
            }
            storeAuthInfoVo.setIsOk(isOk);
        }
        storeAuthInfoVo.setStoreAccountInfo(storeAccountInfo);
        return storeAuthInfoVo;
    }

    public StoreAuthInfoVo verifyStoreLogin(StoreLoginParam param){
        StoreAuthInfoVo storeAuthInfoVo = getStoreAccountFlag(param);
        if (!StoreAuthConstant.STORE_AUTH_OK.equals(storeAuthInfoVo.getIsOk())) {
            return storeAuthInfoVo;
        }
        if (!Util.md5(param.getPassword()).equals(storeAuthInfoVo.getStoreAccountInfo().getAccountPasswd())) {
            storeAuthInfoVo.setIsOk(false);
            storeAuthInfoVo.setMsg(StoreAuthConstant.ACCOUNT_PW_ERROR);
        }
        return storeAuthInfoVo;
    }

    /**
     * 获取门店登录账号信息
     * @param storeAccountId
     * @return
     */
    public StoreAccountVo getOneInfo(Integer storeAccountId){
        StoreAccountVo storeAccountVo=storeAccountDao.getOneInfo(storeAccountId);
        if(storeAccountVo!=null){
            storeAccountVo.setStoreLists(changeToArray(storeAccountVo.getStoreList()));
        }
        return storeAccountVo;
    }

    public int upateBind(Integer storeAccountId, String officalOpenId, byte bind) {
        return storeAccountDao.upateBind(storeAccountId,officalOpenId,bind);
    }

    public int updateRowBind(Integer storeAccountId,byte bind) {
        return storeAccountDao.updateRowBind(storeAccountId,bind);
    }

    /**
     * 获得绑定第三方公众号的账号
     * @param shopId
     * @param storeId
     * @return
     */
    public List<StoreAccountVo> getStoreAccountByBindThird(Integer shopId, Integer storeId) {
        return storeAccountDao.getStoreAccountByBindThird(shopId,storeId);
    }
}
