package com.block.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.upload.UploadFile;

public class AttachmentController extends BaseController{
	
	/**
	 * 功能描述:上传文件
	 * 
	 * @param request
	 * @param map
	 * @return
	 */
	public void upload(){
		HttpServletRequest request = getRequest();  
		
        Date d = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");  
        String yearmonth = sdf.format(d);  
        
        //存储路径  
        File serverpath=new File(request.getSession().getServletContext().getRealPath("/"));
		String fileUrl =  serverpath.getParent()+"/upload/" + yearmonth + "/";
        UploadFile file = getFile("file");  
        System.out.println(fileUrl);  
        String fileName = "";  
        if(file.getFile().length() > 200*1024*1024) {  
            System.err.println("文件长度超过限制，必须小于200M");  
        }else{  
            //上传文件  
        	File dir=new File(fileUrl);
        	if(!dir.exists()){
        		dir.mkdirs();
        	}
            String type = file.getFileName().substring(file.getFileName().lastIndexOf(".")); // 获取文件的后缀  
            fileName = System.currentTimeMillis() + type; // 对文件重命名取得的文件名+后缀  
            String dest = fileUrl + "/" + fileName;  
            file.getFile().renameTo(new File(dest));  
        	setAttr("filePath", "/upload/" + yearmonth + "/"+fileName);
			setAttr("fileName", fileName);	//服务器上的文件名
        }  
              
        renderJson();  
    }
	
}
