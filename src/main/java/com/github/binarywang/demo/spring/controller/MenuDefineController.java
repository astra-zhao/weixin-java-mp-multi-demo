/**
 * 菜单定义
 */
package com.github.binarywang.demo.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.binarywang.demo.spring.service.BaseWxService;
import com.github.binarywang.demo.spring.service.Gzh1WxService;

import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

@RestController
@RequestMapping("/api/gzh1/createmenu")
public class MenuDefineController extends Gzh1WxPortalController{

	@Autowired
	  private Gzh1WxService wxService;
	
	@Override
	protected BaseWxService getWxService() {
		// TODO Auto-generated method stub
		return wxService;
	}
	
	@RequestMapping(value="")
	public String index(WxMpXmlMessage wxMessage)throws WxErrorException{
		//创建菜单
        //创建一级菜单
        WxMenuButton button1=new WxMenuButton();
        button1.setType("click"); //点击事件按钮
        button1.setName("点击菜单");
        button1.setKey("key1"); //根据标志获取点击菜单
         
        //创建一个复合菜单
        WxMenuButton button2=new WxMenuButton();
        button2.setName("多级菜单");
         
        WxMenuButton button2_1=new WxMenuButton();
        button2_1.setType("click"); //点击事件按钮
        button2_1.setName("子菜单一");
        button2_1.setKey("key2"); //根据标志获取点击菜单
         
        WxMenuButton button2_2=new WxMenuButton();
        button2_2.setType("click"); //点击事件按钮
        button2_2.setName("子菜单二");
        button2_2.setKey("key3"); //根据标志获取点击菜单
         
         
        WxMenuButton button3=new WxMenuButton();
        button3.setName("url菜单");
        button3.setType("view");
        button3.setUrl("http://astra.ngrok.cc/learn");  //必须添加http
         
         
        List<WxMenuButton> subButtons=new ArrayList<WxMenuButton>();
        subButtons.add(button2_1);
        subButtons.add(button2_2);
        button2.setSubButtons(subButtons);
         
        List<WxMenuButton> buttons=new ArrayList<WxMenuButton>();
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
         
        WxMenu menu=new WxMenu();
        menu.setButtons(buttons);
        this.wxService.getMenuService().menuCreate(menu);
		return menu.toJson();
	}

}
