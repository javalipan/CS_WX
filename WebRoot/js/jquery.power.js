
//jQuery easing 动画引擎拓展
//如果你使用过 easing 那个请删掉或者注释
jQuery.easing['jswing'] = jQuery.easing['swing'];

jQuery.extend(jQuery.easing, {
    swing: function (x, t, b, c, d) {
        //alert(jQuery.easing.default);
        return jQuery.easing['jswing'](x, t, b, c, d)
    },
    easeOutCubic: function (x, t, b, c, d) {
        return c*((t=t/d-1)*t*t + 1) + b;
    },
    easeOutExpo: function (x, t, b, c, d) {
        return (t==d) ? b+c : c * (-Math.pow(2, -10 * t/d) + 1) + b;
    },
    easeOutBack: function (x, t, b, c, d, s) {
        if (s == undefined) s = 1.70158;
        return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
    },
    easeOutBounce: function (x, t, b, c, d) {
        if ((t/=d) < (1/2.75)) {
            return c*(7.5625*t*t) + b;
        } else if (t < (2/2.75)) {
            return c*(7.5625*(t-=(1.5/2.75))*t + .75) + b;
        } else if (t < (2.5/2.75)) {
            return c*(7.5625*(t-=(2.25/2.75))*t + .9375) + b;
        } else {
            return c*(7.5625*(t-=(2.625/2.75))*t + .984375) + b;
        }
    },
    easeOutElastic: function (x, t, b, c, d) {
        var s=1.70158;var p=0;var a=c;
        if (t==0) return b;  if ((t/=d)==1) return b+c;  if (!p) p=d*.3;
        if (a < Math.abs(c)) { a=c; var s=p/4; }
        else var s = p/(2*Math.PI) * Math.asin (c/a);
        return a*Math.pow(2,-10*t) * Math.sin( (t*d-s)*(2*Math.PI)/p ) + c + b;
    }
});

(function ($) {

    $.power = function (element, options) {

        // 插件的默认选项
        var defaults = {
            auto: 7000, //自动轮播时间 单位ms
            loop: true, //是否循环
            time: 500, //动画时间
            tolerance: 0.25, //容差
            father: null, //父级
            curr: 0, //当前显示元素位
            touch: true, //使用Touch事件
            easing: "easeOutCubic", //easing 支持
            resize: true, //是否开启自适应布局
            next: null, //显示下一位元素按钮
            prev: null, //显示上一位元素按钮
            nextFun: null, //显示下一位元素时的回调
            prevFun: null, //显示上一位元素的回调
            initFun: null //初始化完成后的回调
        };
        var plugin = this;
        plugin.settings = {};

        var $element = $(element), // jQuery版本的DOM元素的引用
            element = element;    // 实际的DOM元素的引用

        var __isTouch__ = false,
            __isShow__ = false,
            __isMove__ = false,
            __pageX__,
            __elem1__,
            __elem2__,
            __direction__,
            __differ__,
            __c__,
            __autoPlayTimer__;

        // 构造函数”方法创建对象时调用
        plugin.init = function () {
            var settings = plugin.settings = $.extend({}, defaults, options);
            if(!settings._$father) settings._$father = $element.parent();
            settings._oldWidth = $element.width();
            settings._oldHeight = $element.height();
            settings._width = settings._oldWidth;
            settings._height = settings._oldHeight;
            settings._$ul = $element.find("ul:eq(0)").css({ width: "100%", height: "100%", position: "relative", overflow: "hidden" });
            settings._$items = settings._$ul.children("li");

            settings._total = settings._$items.length;

            var $window = $(window), __resizeTimer__;
            if (settings.resize) {
                $window.resize(function () {
                    clearTimeout(__resizeTimer__);
                    __resizeTimer__ = setTimeout(function () {
                        resizeFun($window);
                    }, 300);
                });
                resizeFun($window);
            }
            // 初始化代码

            settings._sum = settings._$items.length;
            settings._$items.each(function (i) {
                $(this).css({position: "absolute", width: "100%", height: "100%", top: (i == settings.curr ? 0 : settings._height)});
            });

            if (settings.touch) {
                element.addEventListener("touchstart", function (e) {
                    if (__isShow__) return;
                    clearInterval(__autoPlayTimer__);
                    __isTouch__ = true;
                    __differ__ = 0;
                    var touch = event.targetTouches[0];
                    __pageX__ = touch.pageX;

                }, false);

                element.addEventListener("touchmove", function (e) {
                    e.preventDefault();
                    if (__isShow__ || !__isTouch__) return;
                    __isMove__ = true;
                    var touch = event.targetTouches[0];
                    var pageX = touch.pageX;
                    __differ__ = pageX - __pageX__;
                    if (__differ__ > 0) {
                        //prev
                        __c__ = settings.curr - 1;
                        if (__c__ < 0) {
                            __c__ = settings.loop ? settings._total - 1 : "none";
                        }
                        __direction__ = -1;
                    } else {
                        //next
                        __c__ = settings.curr + 1;
                        if (__c__ >= settings._total) {
                            __c__ = settings.loop ? 0 : "none";
                        }
                        __direction__ = 1;
                    }

                    __elem1__ = settings._$items.eq(settings.curr);
                    if (__c__ == "none") {
                        __elem2__ = null;
                        __c__ = settings.curr;
                        if (Math.abs(__differ__) > settings._width * settings.tolerance)  __differ__ = settings._width * (settings.tolerance - 0.01) * __direction__ * -1;
                    } else {
                        __elem2__ = settings._$items.eq(__c__);
                        __elem2__.css({top: 0, left: __direction__ * settings._width + __differ__});
                    }
                    __elem1__.css("left", __differ__);

                }, false);

                element.addEventListener("touchend", function () {
                    if (__isShow__ || !__isMove__) return;
                    __isShow__ = true;
                    __isTouch__ = false;
                    __isMove__ = false;
                    var t = Math.abs(__differ__ / settings._width);
                    if (t <= settings.tolerance) {
                        __elem1__.animate({left: 0}, settings.time, settings.easing, function () {
                            __isShow__ = false;
                            autoPlay();
                        });
                        if (__elem2__) __elem2__.animate({left: settings._width * __direction__}, settings.time, settings.easing);
                        return false;
                    }
                    __direction__ > 0 ? (settings.nextFun) && (settings.nextFun.call(element, __c__)) : (settings.prevFun) && (settings.prevFun.call(element, __c__));
                    __elem1__.animate({left: settings._width * __direction__ * -1}, settings.time, settings.easing, function () {
                        settings.curr = __c__;
                        __isShow__ = false;
                        autoPlay();
                    });
                    if (__elem2__) __elem2__.animate({left: 0}, settings.time, settings.easing);

                }, false);
            }

            $(settings.prev).click(function () {

                if (!settings.loop && !(settings.curr > 0)) return false;
                plugin.move(settings.curr - 1, -1);

            });

            $(settings.next).click(function () {

                if (!settings.loop && !(settings.curr < settings._total - 1)) return false;
                plugin.move(settings.curr + 1, 1);

            });

            $element.css({display: "block", visibility: "visible"});
            autoPlay();
            if (settings.initFun) settings.initFun.call(element, settings.curr, settings._total);
        };

        // 公共方法

        plugin.move = function (c, dir) {
            if (__isShow__) return false;
            __isShow__ = true;
            var settings = plugin.settings, direction = 1;

            if (c >= settings._total) c = 0;
            if (c < 0) c = settings._total - 1;
            if (c == plugin.settings.curr) {
                __isShow__ = false;
                return false;
            }

            clearInterval(__autoPlayTimer__);
            if (settings.curr > c) direction = -1;
            direction = dir || direction;

            direction > 0 ? (settings.nextFun) && (settings.nextFun.call(element, c)) : (settings.prevFun) && (settings.prevFun.call(element, c));
            __elem1__ = settings._$items.eq(settings.curr);
            __elem2__ = settings._$items.eq(c);
            __elem2__.css({top: 0, left: direction * settings._width});
            __elem1__.animate({left: settings._width * direction * -1}, settings.time, settings.easing, function () {
                settings.curr = c;
                __isShow__ = false;
                autoPlay();
            });

            __elem2__.animate({left: 0}, settings.time, settings.easing);
        };

        // 私有方法

        var resizeFun = function () {
            var settings = plugin.settings;
            var ww = settings._$father.width();
            var a = settings._oldWidth / ww;
            settings._width = settings._oldWidth / a;
            settings._height = settings._oldHeight / a;
            $element.css({width: settings._width, height: settings._height});
            settings._$items.each(function (i) {
                $(this).css({position: "absolute", width: "100%", height: "100%", top: (i == settings.curr ? 0 : settings._height)});
            });
        };


        var autoPlay = function () {
            if (!plugin.settings.auto) return false;
            clearInterval(__autoPlayTimer__);
            __autoPlayTimer__ = setInterval(function () {
                plugin.move(plugin.settings.curr + 1, true);
            }, plugin.settings.auto);
        };

        // 启动插件!
        plugin.init();

    };

    $.fn.power = function (options) {
        return this.each(function () {
            if (undefined == $(this).data('power')) {
                var plugin = new $.power(this, options);
                $(this).data('power', plugin);
            }
        });
    }

})(jQuery);