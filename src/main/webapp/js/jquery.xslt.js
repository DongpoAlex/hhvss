(function($) {
    $.fn.xslt = function() {
        return this;
    };
    var str = /^\s*</;
    if (document.recalc) { // IE 5+
        $.fn.xslt = function(xml, xslt, callback) {//, callback
            var target = $(this);
            var xm = new ActiveXObject("Microsoft.XMLDOM");
            xm.async = false;
            xm.loadXML(xml);
            $.get(xslt, function(data) {
             target.html(xm.transformNode(data));             
             if(typeof(callback) === 'function' ) callback.apply(this); 
             });
            return this;
        };
    }
    else if ($.browser.msie && $.browser.version == "9.0") { //IE9
        $.fn.xslt = function(xml, xslt, callback) {  
            if (window.ActiveXObject) {  
                var target = $(this);  
                
                var xmlDoc = new ActiveXObject("Msxml2.DOMDocument.6.0"); 
	           //裝載數據
	            xmlDoc.async = false; 
	            xmlDoc.loadXML(xml);    
		       //裝載樣式
		        var xslDoc = new ActiveXObject("Msxml2.DOMDocument.6.0"); 
		        xslDoc.async = false; 
		        xslDoc.resolveExternals = false; 
		        xslDoc.load(xslt);  
				target.html(xmlDoc.transformNode(xslDoc));                       
                if(typeof(callback) === 'function' ) callback.apply(this); 
            }  
            return this;  
        };  
    } 
    else if (window.DOMParser != undefined && window.XMLHttpRequest != undefined && window.XSLTProcessor != undefined) { // Mozilla 0.9.4+, Opera 9+
        var processor = new XSLTProcessor();
        var support = false;
        if ($.isFunction(processor.transformDocument)) {
            support = window.XMLSerializer != undefined;
        }
        else {
            support = true;
        }
        if (support) {
            $.fn.xslt = function(xml, xslt, callback) {
                var target = $(this);
                var transformed = false;
                var xm = {
                    readyState: 4
                };
                var xs = {
                    readyState: 4
                };

                var change = function() {
                    if (xm.readyState == 4 && xs.readyState == 4 && !transformed) {

                        var processor = new XSLTProcessor();
                        if ($.isFunction(processor.transformDocument)) {
                            // obsolete Mozilla interface
                            resultDoc = document.implementation.createDocument("", "", null);
                            processor.transformDocument(xm.responseXML, xs.responseXML, resultDoc, null);
                            target.html(new XMLSerializer().serializeToString(resultDoc));                               
                        }
                        else {
                            processor.importStylesheet(xs.responseXML);
                            resultDoc = processor.transformToFragment(xm.responseXML, document);
                            target.empty().append(resultDoc);
                        }
                        transformed = true;
                        if(typeof(callback) === 'function' ) callback.apply(this); 
                    }
                };
                if (str.test(xml)) {
                    xm.responseXML = new DOMParser().parseFromString(xml, "text/xml");
                }
                else {
                    xm = $.ajax({ dataType: "xml", url: xml, success: function(html){
                    	change();
                    }
                    });
                    //xm.onreadystatechange = change;
                }
                if (str.test(xslt)) {
                    xs.responseXML = new DOMParser().parseFromString(xslt, "text/xml");
                    change();
                }
                else {
                    xs = $.ajax({ dataType: "xml", url: xslt, success: function(html){
                    	change();
                    }
                    });
                    //xs.onreadystatechange = change;
                }
                return this;
            };
        }
    }
})(jQuery);
