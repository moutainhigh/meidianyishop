package com.meidianyi.shop.service.saas.categroy;

import com.meidianyi.shop.service.pojo.saas.category.SysCatevo;
import com.meidianyi.shop.support.SpringUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 平台分类（缓存）
 * @author 卢光耀
 * @date 2019/10/12 5:30 下午
 *
*/
@Component
public class SysCatServiceHelper  implements ApplicationListener<ContextRefreshedEvent> {

    private static Map<Integer,SysCatevo> allMap = new HashMap<>();

    private SysCateService sysCateService ;

    public static SysCatevo getSysCateVoByCatId(Integer catId){
        return allMap.get(catId);
    }

    public static List<SysCatevo> getAllSysCateVoByCat(){
        return new ArrayList<>(allMap.values());
    }
    public static List<SysCatevo> getSysCateVoByCatIds(List<Integer> catId){

        return catId.stream().map(x->allMap.get(x)).collect(Collectors.toList());
    }
    public static List<SysCatevo> getSysCateVosByCatId(Integer catId){

        List<SysCatevo> result = new ArrayList<>(3);
        SysCatServiceHelper.getSysCateVo(catId,result);
        result.sort(Comparator.comparing(SysCatevo::getLevel));
        return result;
    }

    private static void getSysCateVo(Integer catId,List<SysCatevo> result){
        SysCatevo sysCatevo = SysCatServiceHelper.getSysCateVoByCatId(catId);
        if( sysCatevo == null ){
            return ;
        }
        result.add(sysCatevo);
        if(sysCatevo.getParentId() == 0 ){
            return ;
        }
        SysCatServiceHelper.getSysCateVo(sysCatevo.getParentId(),result);
    }


    private void initData(){
        allMap = sysCateService.getSysCate().stream().collect(Collectors.toMap(SysCatevo::getCatId, x->x));
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //root application context 没有parent
        if(event.getApplicationContext().getParent() == null){
            sysCateService = SpringUtil.getBean(SysCateService.class);
            initData();
        }
        String context = "Root WebApplicationContext";
        if(context.equals(event.getApplicationContext().getDisplayName())){

        }
    }
}
