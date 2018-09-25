(function($) {
	$.iBox = {
		act: 'tips',
		timeout: 1500,
		tips: '正在加载...'
	};
	$.iBox.tips = function(content, timeout) {
		$.iBox.tip(content, 'tips', timeout);
	};
	$.iBox.alert = function(content, timeout) {
		$.iBox.tip(content, 'alert', timeout);
	};
	$.iBox.loading = function(content) {
		$.iBox.tip(content, 'loading');
	};
	$.iBox.error = function(content, timeout) {
		$.iBox.tip(content, 'error', timeout);
	};
	$.iBox.success = function(content, timeout) {
		$.iBox.tip(content, 'success', timeout);
	};
	$.iBox.tip = function(tips, act, timeout) {
		$.iBox.remove();
		clearTimeout(ibox_timer);

		var cfg = $.iBox;

		var txt = !tips ? cfg.tips : tips;
		var action = !act ? cfg.act : act;
		var timeout = !timeout ? cfg.timeout : timeout;
		if (action == 'tips') {
			var boxWidth = 200;
			var boxMl = -120;
		} else {
			var boxWidth = 130;
			var boxMl = -80;
		}
		var a = $('<div class="ibox-box"></div>');
		var b = $('<div class="ibox-mask"></div>');

		$('body').append(a);
		$('body').append(b);


		var ibox_timer = null;

		switch (action) {
			case 'loading':
				var icon = '<div><img src="'+ctx+'/img/ibox/loading.gif" width="48" height="48"/></div>'
				a.append(icon);
				a.append(txt).show();
				b.show();
				break;
			case 'error':
				var icon = '<div><img src="'+ctx+'/img/ibox/error.png" width="48" height="48"/></div>'
				a.append(icon);
				a.append('<p>' + txt + '</p>').show();
				b.show();
				ibox_timer = setTimeout(function() {
					$.iBox.close()
				}, timeout);
				break;
			case 'success':
				var icon = '<div><img src="'+ctx+'/img/ibox/success.png" width="48" height="48"/></div>'
				a.append(icon);
				a.append('<p>' + txt + '</p>').show();
				b.show();
				ibox_timer = setTimeout(function() {
					$.iBox.close()
				}, timeout);
				break;
			case 'alert':
				var icon = '<div><img src="'+ctx+'/img/ibox/alert.png" width="48" height="48"/></div>'
				a.append(icon);
				a.append('<p>' + txt + '</p>').show();
				b.show();
				ibox_timer = setTimeout(function() {
					$.iBox.close()
				}, timeout);
				break;
			case 'tips':
				a.append('<p>' + txt + '</p>').show();
				b.show();
				ibox_timer = setTimeout(function() {
					$.iBox.close();
				}, timeout);
				break;
		}
	};
	$.iBox.close = function() {
		var a = $('.ibox-box');
		var b = $('.ibox-mask');
		a.fadeOut(300, function() {
			a.remove();
		});
		b.remove();
	};
	$.iBox.remove = function() {
		var a = $('.ibox-box');
		var b = $('.ibox-mask');
		a.remove();
		b.remove();
	};
	window.iBox = $.iBox;
})(jQuery);
function preLoadImage(src) {
	var img = new Image();
	img.src = src;
	img.onload = function() {
	};
}

$(function(){
	var  style= '<style>.ibox-box{background-color:#000; border-radius:5px; color:#fff; display:block; position:fixed; width:150px; height:130px; margin-left:-80px; left:50%; top:25%; display:block; opacity:0.7; text-align:center;font-size:18px;z-index:88888;font-size:12px;padding:2px 10px;}  .ibox-box > div{ padding:20px 0 15px;}  .ibox-mask{position:fixed; top:0; bottom:0; left:0; right:0; display:block;z-index:99999}</style>';
	$('body').append(style);
	
	preLoadImage(ctx+'/img/ibox/loading.gif');
	preLoadImage(ctx+'/img/ibox/error.png');
	preLoadImage(ctx+'/img/ibox/success.png');
	preLoadImage(ctx+'/img/ibox/alert.png');
});