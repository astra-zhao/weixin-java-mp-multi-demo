package com.github.binarywang.demo.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.binarywang.demo.spring.modal.Classes;
import com.github.binarywang.demo.spring.service.BaseWxService;
import com.github.binarywang.demo.spring.service.Gzh1WxService;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

@RestController
@RequestMapping("/learn")
public class WechatLearnStudyController extends AbstractWxPortalController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	  private Gzh1WxService wxService;
	  private static int pagesize = 10;

	@Override
	protected BaseWxService getWxService() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@RequestMapping(value ="",method=RequestMethod.GET)
	public ModelAndView  index(String pagenum,Classes classes)throws WxErrorException{
		ModelAndView mv=new ModelAndView();
		mv.setViewName("classes");
		mv.addObject("sidebar","classes");
		int num = 1;
		if(null!=pagenum){
			num = Integer.parseInt(pagenum);
		}
		List<Classes> list = listClasses((num-1)*pagesize, pagesize,classes);
		mv.addObject("classesList", list);
		mv.addObject("length", list.size());
		mv.addObject("pagenum", num);
		mv.addObject("classes", classes);
		logger.info("当前得到的OpenID="+openId);
		return mv;		
	}
	
	@RequestMapping(value="/addclassespage",method=RequestMethod.GET)
	public ModelAndView addClassesPage(){
		ModelAndView mv=new ModelAndView();
		mv.setViewName("addclasses");
		mv.addObject("sidebar","classes");
		return mv;
	}

	private List<Classes> listClasses(int i, int pagesize2, Classes classes) {
		List<Classes> cList=new ArrayList<Classes>();
		Classes cs=new Classes();
		cs.setId(1);
		cs.setName("张三");
		cs.setStudentcount(20);
		cs.setHeadteacher("老校长");
		cList.add(cs);
		cs.setId(2);
		cs.setName("李四");
		cs.setStudentcount(22);
		cs.setHeadteacher("老校长");
		cList.add(cs);
		return cList;
	}
	
	@RequestMapping(value="/addclasses",method=RequestMethod.POST)
	public ModelAndView addClasses(Classes classes){
		ModelAndView mv=new ModelAndView();
		Classes cls = findClassesById(classes.getId());
		if(null==cls){
			mv.setViewName("redirect:/learn/classes");
			classes.setStudentcount(0);
			addDistinctClasses(classes);
		}else{
			mv.setViewName("redirect:/learn/addclassespage");
			mv.addObject("name", classes.getName());
			mv.addObject("headteacher", classes.getHeadteacher());
			mv.addObject("notice","已存在编号为"+classes.getId()+"的班级");
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
