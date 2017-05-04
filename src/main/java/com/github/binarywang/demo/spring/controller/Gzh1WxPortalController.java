package com.github.binarywang.demo.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.binarywang.demo.spring.service.BaseWxService;
import com.github.binarywang.demo.spring.service.Gzh1WxService;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * 第一个公众号的微信交互接口
 * 
 * @author Binary Wang
 *
 */
@RestController
@RequestMapping("/api/gzh1/portal")
public class Gzh1WxPortalController extends AbstractWxPortalController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Gzh1WxService wxService;

	@Override
	protected BaseWxService getWxService() {
		return this.wxService;
	}

}
