package com.github.binarywang.demo.spring.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.binarywang.demo.spring.modal.Classes;
import com.github.binarywang.demo.spring.service.BaseWxService;
import com.github.binarywang.demo.spring.service.Gzh1WxService;
import com.github.binarywang.demo.spring.tools.EhCacheUtils;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.RequestExecutor;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;

@RestController
@RequestMapping("/learn")
public class WechatLearnStudyController extends AbstractWxPortalController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Gzh1WxService wxService;
	private static int pagesize = 10;

	@Override
	protected BaseWxService getWxService() {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = "")
	public ModelAndView Oauth(HttpServletRequest request) throws WxErrorException {
		ModelAndView mv = new ModelAndView();
		String url = wxService.oauth2buildAuthorizationUrl("http://astra.ngrok.cc/learn/list",
				WxConsts.OAUTH2_SCOPE_BASE, "addclasses");
		mv.setViewName("redirect:"+url);
		logger.info("回調的URL是：" + url);
		//wxService
		return mv;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView index(String pagenum, Classes classes, HttpServletRequest request, HttpServletResponse response,
			@RequestParam String code,@RequestParam String state) throws WxErrorException {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("classes");
		mv.addObject("sidebar", "classes");
		int num = 1;
		if (null != pagenum) {
			num = Integer.parseInt(pagenum);
		}
		List<Classes> list = listClasses((num - 1) * pagesize, pagesize, classes);
		mv.addObject("classesList", list);
		mv.addObject("length", list.size());
		mv.addObject("pagenum", num);
		mv.addObject("classes", classes);
		/**
		 * 旧的，获取OpenId的方式，这种方式适合使用SCOPE范围是snsapi_userinfo
		 */
		//
		// WxMpOAuth2AccessToken wxMpOAuth2AccessToken =
		// wxService.oauth2getAccessToken(code);
		// WxMpUser wxMpUser =
		// wxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
		// logger.info("当前得到的微信Code="+wxMpUser.getOpenId());
		/**
		 * 新的，获取OpenId的方式，这种方式适合使用SCOPE范围是snsapi_base,缓存OPENID，让其只运行一次
		 * 测试的时候一定要测试OPENID有无释放，可以缩减时间进行测试，此OpenID跟Session的缓存时间要一致。Tomcat默认是
		 */
		logger.info("当前得到的微信Code=" + code);
		logger.info("当前得到的微信State="+state);
		logger.info("当前微信返回的路径参数="+request.getQueryString());
		logger.info("当前sessionID="+request.getSession().getId());
		if (code != null && state.equals("addclasses")) {
			//String cacheCode=EhCacheUtils.get(code).toString();
			//logger.info("当前得到的CacheCode=" + cacheCode);
			//if (cacheCode!=null){
			
			WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxService.oauth2getAccessToken(code);
			logger.info("当前得到的微信OpenID=" + wxMpOAuth2AccessToken.getOpenId());
			logger.info("当前得到的微信UnionID=" + wxMpOAuth2AccessToken.getUnionId());
			logger.info("当前的Access_Token="+wxMpOAuth2AccessToken.getAccessToken());
			//Object cacheOpenId=EhCacheUtils.get(wxMpOAuth2AccessToken.getOpenId());
			Object cacheOpenId=EhCacheUtils.get(wxMpOAuth2AccessToken.getOpenId());
			if (cacheOpenId!=null){
				System.out.println("直接返回openID即可");
			}
			else{
				System.out.println("做需要单独执行一次的事情，比如保存订单等等");
				EhCacheUtils.put(wxMpOAuth2AccessToken.getOpenId(),wxMpOAuth2AccessToken);
			}			
		}
		return mv;
	}
	

	@RequestMapping(value = "/addclassespage", method = RequestMethod.GET)
	public ModelAndView addClassesPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("addclasses");
		mv.addObject("sidebar", "classes");
		// logger.info("当前得到的OpenID=" + request.getParameter("openid"));
		return mv;
	}

	private List<Classes> listClasses(int i, int pagesize2, Classes classes) {
		List<Classes> cList = new ArrayList<Classes>();
		Classes cs1 = new Classes();
		cs1.setId(1);
		cs1.setName("张三");
		cs1.setStudentcount(20);
		cs1.setHeadteacher("老校长");
		cList.add(cs1);
		Classes cs2 = new Classes();
		cs2.setId(2);
		cs2.setName("李四");
		cs2.setStudentcount(22);
		cs2.setHeadteacher("老校长");
		cList.add(cs2);
		return cList;
	}

	@RequestMapping(value = "/addclasses", method = RequestMethod.POST)
	public ModelAndView addClasses(Classes classes, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		Classes cls = findClassesById(classes.getId());
		logger.info("当前得到的OpenID=" + request.getParameter("openid"));
		if (null == cls) {
			mv.setViewName("redirect:/learn/classes");
			classes.setStudentcount(0);
			addDistinctClasses(classes);
		} else {
			mv.setViewName("redirect:/learn/addclassespage");
			mv.addObject("name", classes.getName());
			mv.addObject("headteacher", classes.getHeadteacher());
			mv.addObject("notice", "已存在编号为" + classes.getId() + "的班级");
		}
		return mv;
	}

	private void addDistinctClasses(Classes classes) {
		classes.setId(3);
		classes.setName("王五");
		classes.setStudentcount(22);
		classes.setHeadteacher("老校长");
	}

	private Classes findClassesById(int id) {
		return null;
	}
}
