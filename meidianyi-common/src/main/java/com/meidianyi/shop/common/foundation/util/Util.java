package com.meidianyi.shop.common.foundation.util;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static java.util.stream.Collectors.toList;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.tools.Convert;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meidianyi.shop.common.foundation.data.BaseConstant;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 新国
 *
 */
@Slf4j
public class Util {

	final protected static String UNDEER_LINE = "_";
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson LINE_GSON = new GsonBuilder().create();

    public static String toJson(Object o) {
        if (Objects.isNull(o)) {
            return StringUtils.EMPTY;
        }
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(o);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    /**
     * To json enable null string.
     *
     * @param o the o
     * @return the string
     */
    public static String toPrettyJson(Object o) {
        if (Objects.isNull(o)) {
            return StringUtils.EMPTY;
        }
        if (o instanceof UpdatableRecordImpl) {
            return LINE_GSON.toJson(o);
        }
        return PRETTY_GSON.toJson(o);
    }

    /**
     * String list 2 int list list.
     *
     * @param source the source
     * @return the list
     */
    public static List<Integer> stringList2IntList(List<String> source) {
        List<Integer> target = new ArrayList<>();
        CollectionUtils.collect(source, Integer::valueOf, target);
        return target;
    }

	/**
	 *  对象转json忽略null字段
	 * @param o
	 * @return
	 */
	public static String toJsonNotNull(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			return mapper.writeValueAsString(o);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static JsonNode toJsonNode(String str){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * jackson解析自定义注解的json
     * @param o
     * @param ai 自定义注解解析器
     * @return
     */
    public static String toJson(Object o, AnnotationIntrospector ai) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(ai);
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static <T> T parseJson(String json, Class<T> valueType,AnnotationIntrospector ai) {
        if(StringUtils.isBlank(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(ai);
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(json, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	public static <T> T parseJson(String json, Class<T> valueType) {
		if(StringUtils.isBlank(json)) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		// 如果json中有新增的字段并且是实体类类中不存在的，不报错
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(json, valueType);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static <T> T parseJson(String json, TypeReference<T> valueTypeRef) {
		ObjectMapper mapper = new ObjectMapper();
		// 如果json中有新增的字段并且是实体类类中不存在的，不报错
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(json, valueTypeRef);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String,Object> convertPojoToMap(Object obj){
		ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
	}

    public static <T> T json2Object(String json, TypeReference<T> reference, boolean failOnUnknownProperties) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
        	T t =  mapper.readValue(json, reference);
        	return t;
        } catch (IOException e) {
			log.error("数据 ->{}<- 反序列化失败", e);
			e.printStackTrace();
        }
        return null;
    }

    public static <T> T json2Object(String json, Class<T> clazz, boolean failOnUnknownProperties) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            T t = mapper.readValue(json, clazz);
            return t;
        } catch (IOException e) {
            log.error("数据 ->{}<- 反序列化失败", json);
            e.printStackTrace();
        }
        return null;
    }

	public static <T> T parseResourceJson(String path, Class<T> valueType) {
		return parseJson(Util.loadResource(path), valueType);
	}

	@SuppressWarnings("rawtypes")
	public static <T> T parseResourceJson(String path, TypeReference<T> valueTypeRef) {
		return parseJson(Util.loadResource(path), valueTypeRef);
	}

	public static String[] mergeArray(String[] array1, String[] array2) {
		Map<String, Integer> map = new HashMap<String, Integer>(0);
		for (String str : array1) {
			map.put(str, 1);
		}
		for (String str : array2) {
			if (!map.containsKey(str)) {
				map.put(str, 1);
			}
		}
		return (String[]) map.keySet().toArray(new String[map.size()]);
	}

	public static <T> void mergeList(List<T> list1, List<T> list2) {
		for (T t : list2) {
			if (!list1.contains(t)) {
				list1.add(t);
			}
		}
	}

    /**
     * 返回差集，同时不改变list1和list2的内容
     * @param list1
     * @param list2
     * @param <T>
     * @return
     */
	public static <T> List<T> diffList(List<T> list1, List<T> list2){
        return list1.stream().filter(item -> !list2.contains(item)).collect(toList());
    }

	public static <T> T readValue(String content,Class<?> clz1,Class<?> clz2){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(content,mapper.getTypeFactory().constructParametricType(clz1,clz2));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	public static String md5(String string) {
		return DigestUtils.md5DigestAsHex(string.getBytes());
	}

	public static String getCleintIp(HttpServletRequest request) {
		String ipAddress = null;
		String unkown = "unknown";
		String localhost = "127.0.0.1";
		String comma = ",";
		Integer maxIpLength = 15;
		try {
			ipAddress = request.getHeader("x-forwarded-for");
			if (ipAddress == null || ipAddress.length() == 0 || unkown.equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || unkown.equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || unkown.equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
				if (localhost.equals(ipAddress)) {
					// 根据网卡取本机配置的IP
					InetAddress inet = null;
					try {
						inet = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					ipAddress = inet.getHostAddress();
				}
			}
			// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
			if (ipAddress != null && ipAddress.length() > maxIpLength) {
				if (ipAddress.indexOf(comma) > 0) {
					ipAddress = ipAddress.substring(0, ipAddress.indexOf(comma));
				}
			}
		} catch (Exception e) {
			ipAddress = "";
		}
		return ipAddress;
	}



	/**
	 * 获取对象的属性值
	 *
	 * @param o
	 * @param name
	 * @return
	 */
	public static Object getObjectProperty(Object o, String name) {
		Class<?> cls = o.getClass();
		while (cls != null && !cls.getName().equals(Object.class.getName())) {
			try {
				Field field = cls.getDeclaredField(name);
				field.setAccessible(true);
				return field.get(o);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				cls = cls.getSuperclass();
			}
		}
		return null;
	}


	public static String getProperty(String path, String key) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			return properties.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String loadResource(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final <T> T convert(Object from, Class<? extends T> toClass, T defaultValue) {
		try {
			if(from == null) {
				return defaultValue;
			}
			T t = Convert.convert(from, toClass);
			return t == null ? defaultValue : t;
		} catch (DataTypeException e) {
			return defaultValue;
		}
	}

	public static final Integer getInteger(Object from) {
		return convert(from, Integer.class, 0);
	}

	public static Integer randomInteger(Integer min, Integer max) {
		Random rnd = new Random();
		return min + rnd.nextInt(max - min);
	}

	public static List<Part> getFilePart(HttpServletRequest request, String name) throws IOException, ServletException {
		List<Part> result = new ArrayList<Part>();
		String names = name + "[]";
		Collection<Part> parts;
		parts = request.getParts();
		for (Part part : parts) {
			if (part.getName().equals(name) || part.getName().equals(names)) {
				result.add(part);
			}
		}
		return result;
	}

	/**
	 * 转换语言
	 *
	 * @param language
	 * @param message
	 * @param defaultMessage 缺省内容
	 * @param languageType
	 * @return
	 */
	public static String translateMessage(String language, String message, String defaultMessage, String languageType,Object ...args) {
		language = StringUtils.isBlank(language) ? "zh_CN" : language;
		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasename("static/i18n/" + languageType);
		source.setDefaultEncoding("UTF-8");
		MessageSourceAccessor accessor = new MessageSourceAccessor(source);
		String[] languages = language.split(UNDEER_LINE);
		Locale locale = new Locale(languages[0], languages[1]);
		String result =  accessor.getMessage(message, defaultMessage, locale);
		return MessageFormat.format(result, args);
	}
    /**
     * 转换语言(针对多个模版拼接而成的字符串)
     *
     * @param language
     * @param messages
     * @param defaultMessage 缺省内容
     * @param languageType
     * @return
     */
    public static String translateMessage(String language, List<String> messages, String defaultMessage, String languageType,Object ...args) {
        List<Integer> address = Lists.newArrayList();
        language = StringUtils.isBlank(language) ? "zh_CN" : language;
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("static/i18n/" + languageType);
        source.setDefaultEncoding("UTF-8");
        MessageSourceAccessor accessor = new MessageSourceAccessor(source);
        String[] languages = language.split(UNDEER_LINE);
        Locale locale = new Locale(languages[0], languages[1]);
        StringBuilder result = new StringBuilder();
        for( String msg: messages ){
            result.append(accessor.getMessage(msg, msg, locale));
        }
        char[] charArray = result.toString().toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            int a = (int)charArray[i];
            //ASCII 匹配 '{'符号
            if( a == 123 ){
                address.add(i+1);
            }
        }
        for (int i = 0; i < address.size(); i++) {
            charArray[address.get(i)] = Character.forDigit(i, 10);
        }
        return MessageFormat.format(String.valueOf(charArray), args);
    }

	/**
	 * 转换语言
	 *
	 * @param language
	 * @param message
	 * @return
	 */
	public static String translateMessage(String language, String message, String languageType,Object ...args) {
		return translateMessage(language, message, message, languageType,args);
	}
    /**
     * 转换语言(针对多个模版拼接而成的字符串)
     *
     * @param language
     * @param messages
     * @return
     */
    public static String translateMessage(String language, List<String> messages, String languageType,Object ...args) {
        return translateMessage(language, messages, null, languageType,args);
    }

	/***
	 * 下划线命名转为驼峰命名
	 *
	 * @param para
	 *
	 */
	public static String underlineToHump(String para) {
		StringBuilder result = new StringBuilder();
		String[] a = para.split(UNDEER_LINE);
		for (String s : a) {
			if (!para.contains(UNDEER_LINE)) {
				result.append(s);
				continue;
			}
			if (result.length() == 0) {
				result.append(s.toLowerCase());
			} else {
				result.append(s.substring(0, 1).toUpperCase());
				result.append(s.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}

	/***
	 * 驼峰命名转为下划线命名
	 *
	 * @param para
	 *
	 */
	public static String humpToUnderline(String para) {
		StringBuilder sb = new StringBuilder(para);
		int temp = 0;
		if (!para.contains(UNDEER_LINE)) {
			for (int i = 0; i < para.length(); i++) {
				if (Character.isUpperCase(para.charAt(i))) {
					sb.insert(i + temp, UNDEER_LINE);
					temp += 1;
				}
			}
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * 产生uuid
	 *
	 * @return
	 */
	public static String randomId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 将字符串数组中的每一个 元素 转换为 Integer类型，转换失败直接跳过，不会抛异常
	 *
	 * @param from
	 * @return
	 */
	public static List<Integer> valueOf(String[] from) {
		if (from == null || from.length == 0) {
			return new ArrayList<Integer>(0);
		}
		ArrayList<Integer> list = new ArrayList<Integer>(from.length);
		for (int i = 0; i < from.length; i++) {
			try {
				list.add(Integer.valueOf(from[i]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 将字符串元素 转换为 Integer类型list
	 *
	 * @param value
	 * @return
	 */
	public static List<Integer> splitValueToList(String value){
	    if(StringUtils.isBlank(value)){
	        return Collections.emptyList();
        }
		return valueOf(value.split(","));
	}

	public static List<Integer> splitValueToList(String value,String regex){
		return valueOf(value.split(regex));
	}

	/**
	 * 获取某一天的开始时间
	 */
	public static Timestamp getStartToday(Date date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			date = formatter.parse(formatter.format(date));
			return new Timestamp(date.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
//			return new Timestamp(0);
		}
	}

	/**
	 * 获取给定日期之前/之后多少天的日期
	 *
	 * @param date 给定日期
	 * @param days 指定多少天之前/之后
	 * @return 返回的是后推或者前移后的日期的开始时间
	 */
	public static Timestamp getEarlyTimeStamp(Date date, int days) {
		return new Timestamp(getEarlyDate(date, days).getTime());
	}

	public static java.sql.Date getEarlySqlDate(Date date, int days) {
		return new java.sql.Date(getEarlyDate(date, days).getTime());
	}

	public static Date getEarlyDate(Date date, int days) {
		date = getStartToday(date);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		/** 把日期往后推或者往前移；正数往后推,负数往前移 */
		calendar.add(Calendar.DATE, days);
		/** 这个时间就是变动后的结果 */
		date = calendar.getTime();
		return date;
	}

	/**
	 * 将map 以String方式返回
	 *
	 * @param map
	 * @return
	 */
	public static String convertMapToString(Map<?, ?> map) {
		return map.keySet().stream().map(key -> key + " = " + map.get(key)).collect(Collectors.joining(", ", "{", "}"));
	}

	public static <T extends Collection<?>> boolean isEmpty(T t) {
		return t == null || t.isEmpty();
	}

    /**
     * List 转 String
     */
    public static <T> String listToString(List<T> stringValue){
    	if (null == stringValue) {
            return null;
        }
        return stringValue.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * String 转 List
     */
    public static List<Integer> stringToList(String idString) {
        if (StringUtils.isBlank(idString)) {
            return Collections.emptyList();
        }
        return Arrays.stream(idString.split(",")).map(Integer::valueOf).collect(Collectors.toList());
    }
    /**
     * String 转 List
     */
    public static List<String> stringToStringList(String idString) {
        if (StringUtils.isBlank(idString)) {
            return Collections.emptyList();
        }
        return Arrays.stream(idString.split(",")).map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * 当前时间戳
     */
    public static Timestamp currentTimeStamp() {
        return new Timestamp(new java.util.Date().getTime());
    }

    /**
     * 获取驼峰到小写下划线风格的 Gson 实例
     */
    public static Gson underLineStyleGson() {
        return new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
    }

    /**
     * 获取标准格式的日期字符串
     */
    public static String getStandardDate(Timestamp timestamp) {
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(date);
    }

    /**
     * 获取金额字符串
     */
    public static String getCurrencyAmount(BigDecimal amount) {
        DecimalFormat format = new DecimalFormat("###.00");
        return format.format(amount);
    }

    /**
     * 数值转字符串
     */
    public static String numberToString(Number fullNumber) {
        if (null != fullNumber) {
            return String.valueOf(fullNumber);
        }
        return null;
    }

    /**
     * 获取自定义格式化后的当前时间
     * @param formate
     * @return
     */
    public static String getdate(String formate) {
    	LocalDateTime localDate = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(formate);
		String date = dtf.format(localDate);
		return date;
    }

    /**
     * 读取文件
     * @param path
     * @return
     */
	public static InputStream loadFile(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			return  resource.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    /**
     * emoji表情替换
     *
     * @param source  原字符串
     * @param slipStr emoji表情替换成的字符串
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source, String slipStr) {
        if (StringUtils.isNotBlank(source)) {
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        } else {
            return source;
        }
    }

    /**
     * 活动状态
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
	public static Byte getActStatus(Byte status,Timestamp startTime,Timestamp endTime){
        return getActStatus(status,startTime,endTime,BaseConstant.ACTIVITY_NOT_FOREVER);
	}

    /**
     *  活动状态
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isForever 是否是永久
     * @return
     */
    public static Byte getActStatus(Byte status,Timestamp startTime,Timestamp endTime,Byte isForever){
        if (BaseConstant.ACTIVITY_NOT_FOREVER.equals(isForever)){
        	if(Objects.isNull(startTime)||Objects.isNull(endTime)){
        		return BaseConstant.NAVBAR_TYPE_NOT_STARTED;
			}
            Timestamp now  = DateUtils.getLocalDateTime();
            if (Objects.equals(status, BaseConstant.ACTIVITY_STATUS_NORMAL)){
                if (now.compareTo(startTime)<0){
                    return BaseConstant.NAVBAR_TYPE_NOT_STARTED;
                }else if (now.compareTo(endTime)>0){
                    return BaseConstant.NAVBAR_TYPE_FINISHED;
                }else {
                    return BaseConstant.NAVBAR_TYPE_ONGOING;
                }
            }else {
                return BaseConstant.NAVBAR_TYPE_DISABLED;
            }
        }else{
            if (Objects.equals(status, BaseConstant.ACTIVITY_STATUS_NORMAL)){
                return BaseConstant.NAVBAR_TYPE_ONGOING;
            }else {
                return BaseConstant.NAVBAR_TYPE_DISABLED;
            }
        }
    }

    /** 地球半径 */
    private static final double EARTH_RADIUS = 6378.137;

    /**
     * Gets distance.
     *
     * @param lng1 the lng 1
     * @param lat1 the lat 1
     * @param lng2 the lng 2
     * @param lat2 the lat 2
     * @return the distance
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double rad = Math.PI / 180.0;
        double radLat1 = lat1 * rad;
        double radLat2 = lat2 * rad;
        double a = radLat1 - radLat2;
        double b = (lng1 - lng2) * rad;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
            Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s;
    }
	public static Map<String, Object> transBeanToMap(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new TreeMap<>(Comparator.naturalOrder());
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!"class".equals(key)) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);
					map.put(key, value);
				}
			}
		} catch (Exception e) {
			log.error("transBeanToMap error {}", e.getMessage());
		}
		return map;
	}
    /**
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {   //同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                boolean isLeapYear = i % 4 == 0 && i % 100 != 0 || i % 400 == 0;
                if (isLeapYear) {    //闰年
                    timeDistance += 366;
                } else {   //不是闰年

                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {//不同年
            return day2 - day1;
        }
    }
}
