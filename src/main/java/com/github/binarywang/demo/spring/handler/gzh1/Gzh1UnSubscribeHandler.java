package com.github.binarywang.demo.spring.handler.gzh1;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.binarywang.demo.spring.config.WxConfig;
import com.github.binarywang.demo.spring.config.WxGzh1Config;
import com.github.binarywang.demo.spring.handler.UnsubscribeHandler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

@Component
public class Gzh1UnSubscribeHandler extends UnsubscribeHandler {
  @Autowired
  private  WxGzh1Config wxConfig;

  @Override
  protected WxConfig getWxConfig() {
    return this.wxConfig;
  }
  
  @Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
			WxSessionManager sessionManager) {
		System.out.println("有用户取消了");
		return super.handle(wxMessage, context, wxMpService, sessionManager);
	}

}
