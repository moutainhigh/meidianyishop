package com.meidianyi.shop.service.foundation.es.annotation;

import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName;

/**
 * EsFiled序列化
 * @author 卢光耀
 * @date 2019/11/6 9:51 上午
 *
*/
public class EsFiledSerializer extends JacksonAnnotationIntrospector implements Versioned {
    @Override
    public PropertyName findNameForSerialization(Annotated a){
        boolean useDefault = false;
        EsFiled jg = _findAnnotation(a, EsFiled.class);
        if (jg != null) {
            String s = jg.name();
            return PropertyName.construct(s);
        }
        return null;
    }
    @Override
    public PropertyName findNameForDeserialization(Annotated a)
    {
        // @JsonSetter has precedence over @JsonProperty, being more specific

        boolean useDefault = false;
        EsFiled js = _findAnnotation(a, EsFiled.class);
        if (js != null) {
            String s = js.name();
            return PropertyName.construct(s);
        }

        return null;
    }
    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return _isIgnorable(m);
    }

}
