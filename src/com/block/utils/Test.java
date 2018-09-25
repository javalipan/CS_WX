package com.block.utils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Test {

	/**
	 * @param args
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
//		Map<String, Object> xh = XMLParser.getMapFromXML("<xml><ToUserName><![CDATA[gh_bde36f90bbbb]]></ToUserName><FromUserName><![CDATA[oVw8Qv6FvT4hR3mZSMiAZXFlEjjs]]></FromUserName><CreateTime>1477447340</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[帮助]]></Content><MsgId>6345588007271633617</MsgId></xml>");
//		String s = (String) xh.get("Content");
//		System.out.println(System.currentTimeMillis());
//		System.out.println(s);
//		
//		
//		XmlHelper xmlHelper = XmlHelper.of("<xml><ToUserName><![CDATA[gh_bde36f90bbbb]]></ToUserName><FromUserName><![CDATA[oVw8Qv6FvT4hR3mZSMiAZXFlEjjs]]></FromUserName><CreateTime>1477447340</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[帮助]]></Content><MsgId>6345588007271633617</MsgId></xml>");
//		InTextMsg im = (InTextMsg) InMsgParser.parse("<xml><ToUserName><![CDATA[gh_bde36f90bbbb]]></ToUserName><FromUserName><![CDATA[oVw8Qv6FvT4hR3mZSMiAZXFlEjjs]]></FromUserName><CreateTime>1477447340</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[帮助]]></Content><MsgId>6345588007271633617</MsgId></xml>");
//		System.out.println(im.getContent());
//		
//		OutTextMsg outMsg = new OutTextMsg(im);
//		outMsg.setContent("\t发送包装上【批次号】可追溯商品信息，发送【推荐】可获取推荐信息，发送【帮助】可获得帮助信息。公众号持续完善中...敬请期待!");
//		
//		System.out.println(outMsg.toXml());
//		System.out.println("鍙戦�佹秷鎭�:");
		
		
		System.out.println(System.getenv("java.io.tmpdir"));
	}

}
