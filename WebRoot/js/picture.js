//轮播图
var _power_main = $(".jquery-power-main"), _tagListA;
var power = _power_main.power({
    next: "#next",
    prev: "#prev",
    loop: true,
    touch: true,
    easing:"",
    nextFun: function (curr) {
        _tagListA.removeClass("active");
        _tagListA.removeClass("active").eq(curr).addClass("active");
    },
    prevFun: function (curr) {
        _tagListA.removeClass("active");
        _tagListA.removeClass("active").eq(curr).addClass("active");
    },
    initFun: function (curr, total) {
        var _tag_list = $(".tag-list");
        for (var i = 0; i < total; i++) {
            _tag_list.append('<a href="javascript: power.move('+i+')" '+(i == curr ? 'class="active"' : '')+'></a>');
        }
        _tagListA = _tag_list.find("a");
    }
}).data("power");
