package com.meidianyi.shop.shell;

import com.github.fonimus.ssh.shell.SshShellHelper;
import com.github.fonimus.ssh.shell.commands.SshShellComponent;
import com.google.common.collect.Lists;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.saas.SaasApplication;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
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
@ShellCommandGroup("WxTask Commands")
public class TaskCommand {

    @Autowired
    private SaasApplication saas;

    @Autowired
    private SshShellHelper helper;

    @ShellMethod("WX Task. --all[false|true]default true --shop-id<shopId>")
    @ShellMethodAvailability("argsAvailability")
    public void wxTaskD(@ShellOption(arity = 1, defaultValue = "true") boolean all, @ShellOption( defaultValue = "0")int shopId) {
        List<Integer> shopIdList = getShopIds(all,shopId);
        log.info("开始微信每天数据定时任务");
        for (Integer id : shopIdList) {
            log.info("微信数据定时任务-店铺{}",id);
            try {
                saas.getShopApp(id).
                    shopTaskService.wechatTaskService.beginDailyTask();
            }catch (Exception e){
                log.info("【错误信息】：{}",e.getMessage());
                log.info("微信数据定时任务失败-店铺{}",id);
            }
        }
    }
    @ShellMethod("WX Task. --all[false|true]default true --shop-id<shopId>")
    @ShellMethodAvailability("argsAvailability")
    public void wxTaskW(@ShellOption(arity = 1, defaultValue = "true") boolean all, @ShellOption( defaultValue = "0")int shopId) {
        List<Integer> shopIdList = getShopIds(all,shopId);
        log.info("开始微信每周数据定时任务");
        for (Integer id : shopIdList) {
            log.info("微信数据定时任务-店铺{}",id);
            try {
                saas.getShopApp(id).
                    shopTaskService.wechatTaskService.beginWeeklyTask();
            }catch (Exception e){
                log.info("【错误信息】：{}",e.getMessage());
                log.info("微信数据定时任务失败-店铺{}",id);
            }
        }
    }
    @ShellMethod("WX Task. --all[false|true]default true --shop-id<shopId>")
    @ShellMethodAvailability("argsAvailability")
    public void wxTaskDM(@ShellOption(arity = 1, defaultValue = "true") boolean all, @ShellOption( defaultValue = "0")int shopId) {
        List<Integer> shopIdList = getShopIds(all,shopId);
        log.info("开始微信每月数据定时任务");
        for (Integer id : shopIdList) {
            log.info("微信数据定时任务-店铺{}",id);
            try {
                saas.getShopApp(id).
                    shopTaskService.wechatTaskService.beginMonthlyTask();
            }catch (Exception e){
                log.info("【错误信息】：{}",e.getMessage());
                log.info("微信数据定时任务失败-店铺{}",id);
            }
        }
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
}
