package com.meidianyi.shop.service.shop.activity.factory;

import com.meidianyi.shop.service.shop.activity.processor.Processor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年10月30日
 */
@Service
@Slf4j
public abstract class AbstractProcessorFactory<P,T> {

    protected List<P> processors = new ArrayList<>();

    @Autowired(required = false)
    private List<Processor> sortProcessors;

    protected void sort(){
        sortProcessors.sort(Comparator.comparing(Processor::getPriority));
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    protected void init(){
        getProcessorImplClass();
        if (sortProcessors == null || sortProcessors.size() == 0) {
            LoggerFactory.getLogger(this.getClass()).error("{}处理器工厂初始化失败", this.getClass());
            processors = new ArrayList<>();
        } else {
            sort();
            sortProcessors.forEach(processorPriority -> {
                try {
                    Class<?> clazz =  getProcessorImplClass();
                    if (clazz.isAssignableFrom(processorPriority.getClass())) {
                        processors.add((P) processorPriority);
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                }
            });
            LoggerFactory.getLogger(this.getClass()).debug(processors.toString());
        }
    }

    private Class<?> getProcessorImplClass(){
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            throw new IllegalArgumentException();
        }
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }

    /**
     * 调用各个处理器处理函数进行数据处理
     * @param capsules 目标商品列表，数据不可为null
     * @param userId 用id
     * @throws Exception 异常
     */
    public abstract void doProcess(List<T> capsules,Integer userId) throws Exception;
}
