package com.meidianyi.shop.service.shop.order.invoice;

import static com.meidianyi.shop.db.shop.tables.Invoice.INVOICE;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.db.shop.tables.Invoice;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.invoice.InvoiceVo;

/**
 * 发票
 * @author 王帅
 *
 */
@Service
public class InvoiceService extends ShopBaseService{
	public final Invoice TABLE = INVOICE;
	
	public InvoiceVo get(Integer id) {
		return db().selectFrom(TABLE).where(TABLE.ID.eq(id)).orderBy(TABLE.ID.desc()).limit(1).fetchOneInto(InvoiceVo.class);
	}

}
