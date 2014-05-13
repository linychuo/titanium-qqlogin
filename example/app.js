var qqlogin = require('com.liyongchao.qqlogin');
qqlogin.setAppId("xxxxxx");

var button = Ti.UI.createButton({
	title : "qq login"
});
button.addEventListener('click', function(e) {
	qqlogin.login();
});

qqlogin.addEventListener('qqlogin-onComplete', function(e) {
	alert(e.openid);
});

var win = Ti.UI.createWindow({
	backgroundColor : 'white'
});
win.add(button);
win.open();