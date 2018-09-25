function ismobile(value){
	return /^(((13[0-9]{1})|(14[0-9]{1})|(15[0-9]{1})|(16[0-9]{1})|(17[0-9]{1})|(18[0-9]{1})|(19[0-9]{1}))+\d{8})$/.test(value);
}

function isTelorMobile(value){
	return /^(((13[0-9]{1})|(14[0-9]{1})|(15[0-9]{1})|(16[0-9]{1})|(17[0-9]{1})|(18[0-9]{1})|(19[0-9]{1}))+\d{8})$/.test(value)||/^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{1,3}))?$/.test(value);
}

function addNum(t){
	var num=parseInt($(t).prev().text());
	if(!num||num==0){
		num=1;
	}
	num++;
	$(t).prev().text(num);
}

function subNum(t){
	var num=parseInt($(t).next().text());
	if(!num||num==0){
		num=1;
	}
	num--;
	if(num==0){
		$.iBox.alert('至少订购一件');
		num=1;
	}
	$(t).next().text(num);
}

function defaultImg(t){
	$(t).attr("src",ctx+"/images/noimg.jpg");
}

//加法
Number.prototype.add = function(arg){   
    var r1,r2,m;   
    try{r1=this.toString().split(".")[1].length;}catch(e){r1=0;}   
    try{r2=arg.toString().split(".")[1].length;}catch(e){r2=0;}   
    m=Math.pow(10,Math.max(r1,r2));
    return (this*m+arg*m)/m;
};
 
//减法   
Number.prototype.sub = function (arg){   
    return this.add(-arg);   
};
 
//乘法   
Number.prototype.mul = function (arg)   
{   
    var m=0,s1=this.toString(),s2=arg.toString();   
    try{m+=s1.split(".")[1].length;}catch(e){}   
    try{m+=s2.split(".")[1].length;}catch(e){}   
    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
};
 
//除法   
Number.prototype.div = function (arg){   
    var t1=0,t2=0,r1,r2;   
    try{t1=this.toString().split(".")[1].length;}catch(e){}   
    try{t2=arg.toString().split(".")[1].length;}catch(e){}   
    with(Math){   
        r1=Number(this.toString().replace(".",""));
        r2=Number(arg.toString().replace(".",""));
        return (r1/r2)*pow(10,t2-t1);   
    }   
};

function fixImg(imgs){
	for(var i=0;i<imgs.length;i++){
		var img=imgs.eq(i);
		var img_w = $(img).width();
		var img_h = $(img).height();
		$(img).parent().css("width",$(img).parent().width());
		$(img).parent().css("height",$(img).parent().width());
		if( img_w < img_h ){
			$(img).css("height",($(img).parent().width()));
			$(img).css("margin","0 auto");
	    }
		else if ( img_w > img_h )
		  {
			$(img).css("width",($(img).parent().width()));
			var he=img_h*$(img).parent().width()/img_w;
			$(img).css("margin-top",((($(img).parent().height())-he)/2+"px"));
		  }
		else 
		  {
			$(img).css("margin","0");
			$(img).css("width",($(img).parent().width()));
			$(img).css("height",($(img).parent().width()));
		  }
	}
}
