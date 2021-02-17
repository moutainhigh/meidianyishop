package com.meidianyi.shop.service.foundation.language;

/**
 * @author lixinguo
 */
public class LanguageManager {

	/**
	 * 当前语言，线程单例
	 */
	private static  ThreadLocal<String> currentLanguage = ThreadLocal.withInitial(() -> "zh_CN");
	
	/**
	 * 切换当前线程语言
	 * @param language
	 */
	public static void switchLanguage(String language) {
		currentLanguage.set(language);
	}
	
	/**
	 * 得到当前线程语言
	 * @return
	 */
	public static String getCurrentLanguage() {
		return currentLanguage.get();
	}
}
