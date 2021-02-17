package com.meidianyi.shop.shell;


import com.github.fonimus.ssh.shell.SshShellHelper;
import com.github.fonimus.ssh.shell.commands.SshShellComponent;
import com.google.common.collect.Lists;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.saas.SaasApplication;
import com.meidianyi.shop.thread.es.EsThreadConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
/**
 * @author luguangyao
 */
@Slf4j
@SshShellComponent
@ShellCommandGroup("ElasticSearch Commands")
public class EsCommand {

    @Autowired
    private EsThreadConfig esThreadConfig;

    @Autowired
    private SaasApplication saas;

    @Autowired
    private SshShellHelper helper;



    @ShellMethod("ElasticSearch goods Index . --all[false|true]default true --shop-id<shopId>")
    @ShellMethodAvailability("argsAvailability")
    public void es(@ShellOption(arity = 1, defaultValue = "true") boolean all,@ShellOption( defaultValue = "0")int shopId) {
        List<Integer> shopIdList = getShopIds(all,shopId);
        shopIdList.forEach(x->
            esThreadConfig.doIndexByShopId(x)
        );
    }
    @ShellMethod("ElasticSearch label Index . --all[false|true]default true --shopId<shopId>")
    public void esl(@ShellOption(arity = 1, defaultValue = "true") boolean all,@ShellOption( defaultValue = "0")int shopId) {
        List<Integer> shopIdList = getShopIds(all,shopId);
        shopIdList.forEach(x->
            esThreadConfig.doLabelIndexByShopId(x)
        );
    }
    @ShellMethod("ElasticSearch product Index . --all[false|true]default true --shopId<shopId>")
    public void esp(@ShellOption(arity = 1, defaultValue = "true") boolean all,@ShellOption( defaultValue = "0")int shopId) {
        List<Integer> shopIdList = getShopIds(all,shopId);
        shopIdList.forEach(x->
            esThreadConfig.doProductIndexByShopId(x)
        );
    }
    @ShellMethod("ElasticSearch label Index . --all[false|true]default true --shopId<shopId>")
    public void estest(@ShellOption(arity = 1, defaultValue = "true") boolean all,@ShellOption( defaultValue = "0")int shopId) {
        saas.esMappingUpdateService.updateEsGoodsMapping();
    }
    private List<Integer> getShopIds(boolean all,int shopId){
        List<Integer> shopIdList = Lists.newArrayList();
        if( all ) {
            shopIdList = saas.shop.getAll().stream().map(ShopRecord::getShopId).collect(Collectors.toList());
        } else if( 0 != shopId ){
            shopIdList = Collections.singletonList(shopId);
        }
        if( Objects.requireNonNull(shopIdList).isEmpty() ){
            helper.printError("Please shopId is not null");
        }
        return shopIdList;
    }

    private Availability argsAvailability(){
        if( helper.checkAuthorities(Collections.singletonList("admin")) ){
            return Availability.unavailable("admin command is only for an admin users !");
        }
        return Availability.available();
    }
}
