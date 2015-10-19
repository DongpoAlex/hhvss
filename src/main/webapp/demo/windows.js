/**
 * 功能函数
*/
//获得对象
function $(id){
	return document.getElementById(id);
}

///////////////////////////////////////
//自定义的窗口类/////////////////
///////////////////////////////////////
//构造函数
function Windows(id){
	/*标识*/
	this.id = id;
	/*属性定义*/
	this.connect = null;//显示的消息
	this.width = 400;//宽
	this.height = 300;//高
	this.xPos = 0;
	this.yPos = 0;
	this.title = null;
	/*内部定义*/
	this.em = null;//窗体	
	this.emTitle = null; //标题
	this.emContent = null;//显示的内容
	this.emOK = null;//确定按钮
	this.emNO = null;//取消按钮
	/* 可移动属性临时变量*/
	this.dragging = false;
	this.startedDragging = false;
	this.dragEffect = function(){};
	//生成Window Tag,保证单列
	if( !$(this.id) ){
		//如果没有添加Tag，则添加
		this.em = document.createElement("div");
		this.em.id = this.id;
		this.em.style.display ="none";
		document.body.appendChild(this.em);
		// 添加主体
		this.em.innerHTML = "<DIV class='WindowsShadow'></DIV><div class='WindowsTitle' id='"+this.id+"Title'></div><DIV class='WindowsGlass'></DIV><div class='WindowsBorder'><div class='WindowsContent' id='"+this.id+"Content'></div><div class='WindowsBotton' ><input type='button' class='WindowsBoxOK' id='"+this.id+"BoxOK' value='确定'><input type='button' class='WindowsBoxNO' id='"+this.id+"BoxNO' value='取消'></div></div>";
	}else{
		this.em	=$("messageBox");
	}
	//指向
	this.emTitle = $(this.id+"Title");
	this.emMSG = $(this.id+"Content");
	this.emOK = $(this.id+"BoxOK");
	this.emNO = $(this.id+"BoxNO");
}
//设置样式
Windows.prototype.setStyle = function(style){
	for(var p in style){
		this.em.style[p] = style[p];
	}
};
//隐藏
Windows.prototype.hide = function(){
	$(this.id).style.display = "none";
};
//显示
Windows.prototype.show = function(option){
	
	if(this.xPos == 0) 
		this.xPos = (document.body.clientWidth) / 2 - this.width/2;
	if(this.yPos == 0)
		this.yPos = (document.body.clientHeight) / 2 - this.height/2;
	if(this.em != null){
		var s = {
			top:this.xPos,
			left:this.yPos,
			display:"block",
			width:this.width,
			height:this.height
		}
		this.setStyle(s);
		
		if(this.connect == null){
			this.connect ="内容没有定义";
		}
		if(this.title === null){
			this.title ="baibai窗口 v1.1";
		}
		this.emContent.innerHTML = this.msg;
		this.emTitle.innerHTML = this.title;
	}
	
	if( option != undefine && option != null ){
		//添加事件
		this.emOK.onclick = function(){
			option.onClickOK();
		};
		this.emNO.onclick = function(){
			option.onClickNO();
		};
	}
	//设置可以移动，改变大小
	
};
//设置可以移动，想使框移动必须调用此方法，参数为实例化Windows对象
Windows.prototype.canBeMove = function(e){
		//添加文档对象属性
		document.body.onmouseup = function(){e.releaseCurrent();};
		document.body.onmousemove = function(){e.moveCurrent();};
		this.em.onmousedown = function(){e.setCurrent();};
		this.em.onmousemove = function(){e.calcResize();};
}
Windows.prototype.ok = function(){alert("ok")}
//设置当前的对话框状态
Windows.prototype.setCurrent = function(){
  // init drag
  var offsetX = event.x - parseInt(this.em.currentStyle.left);
  var offsetY = event.y - parseInt(this.em.currentStyle.top);
  if (offsetY < 20){
    this.dragging = true;
    this.dragEffect = function(){
      this.em.style.pixelLeft = event.x - offsetX;
      this.em.style.pixelTop = event.y - offsetY;
    }
  }else{
    var width = parseInt(this.em.currentStyle.width);
    var resizeX = offsetX > width - 10;
    var height = parseInt(this.em.currentStyle.height);
    var resizeY = offsetY > height - 10;
    if (resizeX || resizeY){
      this.dragging = true;
      var offsetX = event.x - width;
      var offsetY = event.y - height;

      this.dragEffect = function(){
        if (resizeX){
          this.em.style.pixelWidth = event.x - offsetX;}
        if (resizeY){
          this.em.style.pixelHeight = event.y - offsetY;}
      }
    }
  } 
}
//计算重设置大小光标
Windows.prototype.calcResize =function(){
  var offsetX = event.x - parseInt(this.em.currentStyle.left);
  var offsetY = event.y - parseInt(this.em.currentStyle.top);
  var width = parseInt(this.em.currentStyle.width);
  var resizeX = offsetX > width - 10;
  var height = parseInt(this.em.currentStyle.height);
  var resizeY = offsetY > height - 10;
  this.em.style.cursor = (resizeX||resizeY)?(resizeY?"S":"")+(resizeX?"E":"")+"-resize":"default";
}
//移动当前的
Windows.prototype.moveCurrent = function(){
  if (!this.dragging){
    return;
  }
  if (event.button == 0){
    this.releaseCurrent();
    return;
  }
  if (!this.startedDragging){
    this.startedDragging = true;
  }
  this.dragEffect();
}
	
//释放当前的
Windows.prototype.releaseCurrent = function(){
  this.dragging = false;
  this.startedDragging = false;
}
///////////////////////////////////////
//自定义的模态对话框类结束/////////////
///////////////////////////////////////