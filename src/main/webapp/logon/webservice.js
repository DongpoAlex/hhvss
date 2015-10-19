	var http_request;
	var name;
	var str,str1;
	function send_request(url,username) {//初始化、指定处理函数、发送请求的函数
		http_request = false;
		name = username;
		//开始初始化XMLHttpRequest对象
		if(window.XMLHttpRequest) { //Mozilla 浏览器
			http_request = new XMLHttpRequest();
			if (http_request.overrideMimeType) {//设置MiME类别
				http_request.overrideMimeType('text/xml');
			}
		}
		else if (window.ActiveXObject) { // IE浏览器
			try {
				http_request = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				try {
					http_request = new ActiveXObject("Microsoft.XMLHTTP");
				} catch (e) {}
			}
		}
		if (!http_request) { // 异常，创建对象实例失败
			window.alert("不能创建XMLHttpRequest对象实例.");
			return false;
		}
		http_request.onreadystatechange = processRequest;
		// 确定发送请求的方式和URL以及是否同步执行下段代码
		http_request.open("GET", url, true);
		http_request.send(null);
	}
	// 处理返回信息的函数
	   function processRequest() {
	       if (http_request.readyState == 4) { // 判断对象状态
	           if (http_request.status == 200) { // 信息已经成功返回，开始处理信息
	           		str = http_request.responseText;
	           		str1 = str.substring(4,6);
	           		//alert("str-----"+str);
	           		//alert("str1----"+str1);
		           	if (str1 == -5) {
						alert('状态错误');
						return;
					}else
					if (str1 == -2) {
						alert('账号资费不足');
						return;
					}else
					if (str1 == 03) {
						alert('商场给名单开通');
						return;
					}else
					if (str1 == 04) {
						alert('暂停功能');
						return;
					}else
					if (str1 == 10) {
						alert('用户名为空');
						return;
					}else
					if (str1 == 11) {
						alert('密码为空');
						return;
					}else
					if (str1 == 12) {
						alert('用户名或密码含有非法字符');
						return;
					}else
					if (str1 == 13) {
						alert('服务器错误');
						return;
					}else
					if (str1 == 14) {
						alert('用户名或密码不正确,请重新输入');
						return;
					}else
					if (str1 == 15) {
						alert('无权登陆');
						return;
					}else
					if (str1 == 16) {
						alert('用户名或密码不存在请重新输入 ');
						return;
					}else
					if (str1 == 17) {
						alert('用户名或密码错误！请重新输入；');
						return;
					}if (str == -1) {
						alert('webservices请求失败!');
						return;
					}else{
						document.myForm.action = "http://113.140.27.50:81/Web_login.aspx?"+str;
						document.myForm.submit();
					}
	           } else { //页面不正常
	               alert("您所请求的页面有异常。");
	           }
	       }
	   }