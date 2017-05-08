/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.github.binarywang.demo.spring.tools;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Cache工具类
 * 
 * @author ThinkGem
 * @version 2013-5-29
 */
public class EhCacheUtils {

	private static CacheManager cacheManager = ((CacheManager) SpringContextHolder.getBean("cacheManager"));

	private static final String OPENID_CACHE = "openIdCache";
	private String key = "key1";

	public static Object get(Object key) {
		Cache cache = cacheManager.getCache(OPENID_CACHE);
		if (cache != null) {
			Element element = cache.get(key);
			if (element != null) {
				return element.getObjectValue();
			}
		}
		return null;
	}

	public static void put(Object key, Object value) {
		Cache cache = cacheManager.getCache(OPENID_CACHE);
		if (cache != null) {
			try {
				cache.acquireWriteLockOnKey(key);
				cache.put(new Element(key, value));
			} finally {
				cache.releaseWriteLockOnKey(key);
			}
		}
	}

	public static boolean remove(String cacheName, Object key) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			return cache.remove(key);
		}
		return false;
	}

	public static void main(String[] args) {
		String key = "key";
		String value = "hello";
		String value2 = "hello2";
		EhCacheUtils.put(key, value);
		EhCacheUtils.put(key, value2);
		System.out.println(EhCacheUtils.get(key));
	}

	public static CacheManager getCacheManager() {
		return cacheManager;
	}

}
