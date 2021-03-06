package com.github.binarywang.demo.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.binarywang.demo.spring.service.BaseWxService;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author Binary Wang
 */
public abstract class AbstractWxPortalController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  //public static volatile String openId=null;

  @ResponseBody
  @GetMapping(produces = "text/plain;charset=utf-8")
  public String authGet(@RequestParam("signature") String signature,
      @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,
      @RequestParam("echostr") String echostr) {
    this.logger.info("\n接收到来自微信服务器的认证消息：[{},{},{},{}]", signature, timestamp, nonce, echostr);

    if (this.getWxService().checkSignature(timestamp, nonce, signature)) {
      return echostr;
    }

    return "非法请求";
  }

  @ResponseBody
  @PostMapping(produces = "application/xml; charset=UTF-8")
  public String post(@RequestBody String requestBody, @RequestParam("timestamp") String timestamp,
      @RequestParam("nonce") String nonce, @RequestParam("signature") String signature,
      @RequestParam(name = "encrypt_type", required = false) String encType,
      @RequestParam(name = "msg_signature", required = false) String msgSignature) {

    this.logger.info(
        "\n接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}],"
            + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
        signature, encType, msgSignature, timestamp, nonce, requestBody);

    if (!this.getWxService().checkSignature(timestamp, nonce, signature)) {
      throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
    }

    String out = null;
    if (encType == null) {
      // 明文传输的消息
      WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
      //openId = inMessage.getFromUser();
      //this.logger.info("当前用户的OpenID:"+openId);
      WxMpXmlOutMessage outMessage = this.getWxService().route(inMessage);
      if (outMessage == null) {
        return "";
      }
      out = outMessage.toXml();
    } else if ("aes".equals(encType)) {
      // aes加密的消息
      WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody,
          this.getWxService().getWxMpConfigStorage(), timestamp, nonce, msgSignature);
      //openId = inMessage.getFromUser();
      //this.logger.info("当前用户的OpenID:"+openId);
      this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
      WxMpXmlOutMessage outMessage = this.getWxService().route(inMessage);
      if (outMessage == null) {
        return "";
      }
      out = outMessage.toEncryptedXml(this.getWxService().getWxMpConfigStorage());
    }

    this.logger.debug("\n组装回复信息：{}", out);

    return out;
  }

  protected abstract BaseWxService getWxService();

}
