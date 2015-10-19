var XmlReader = function() {
	this.GetHttpRequest = function() {
		if (window.XMLHttpRequest)
			// Gecko
			return new XMLHttpRequest();
		else if (window.ActiveXObject)
			// IE
			return new ActiveXObject("MsXml2.XmlHttp");

	};
	this.LoadUrl = function(urlToCall, asyncFunctionPointer) {
		var oXmlReader = this;
		var bAsync = (typeof (asyncFunctionPointer) == "function");
		var oXmlHttp = this.GetHttpRequest();
		oXmlHttp.open("GET", urlToCall, bAsync);
		if (bAsync) {
			oXmlHttp.onreadystatechange = function() {
				if (oXmlHttp.readyState == 4) {
					oXmlReader.DOMDocument = oXmlHttp.responseXML;
					if (oXmlHttp.status == 200)
						asyncFunctionPointer(oXmlReader);
					else
						alert("XML request error: " + oXmlHttp.statusText
								+ " (" + oXmlHttp.status + ")");
				}
			};
		}
		oXmlHttp.send(null);

		if (!bAsync) {
			if (oXmlHttp.status == 200)
				this.DOMDocument = oXmlHttp.responseXML;
			else {
				alert("XML request error: " + oXmlHttp.statusText + " ("
						+ oXmlHttp.status + ")");
			}
		}
	};
	this.SelectNodes = function(xpath) {
		if (document.all)
			// IE
			return this.DOMDocument.selectNodes(xpath);
		else {
			// Gecko
			var aNodeArray = new Array();

			var xPathResult = this.DOMDocument
					.evaluate(
							xpath,
							this.DOMDocument,
							this.DOMDocument
									.createNSResolver(this.DOMDocument.documentElement),
							XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
			if (xPathResult) {
				var oNode = xPathResult.iterateNext();
				while (oNode) {
					aNodeArray[aNodeArray.length] = oNode;
					oNode = xPathResult.iterateNext();
				}
			}
			return aNodeArray;
		}
	};
	this.SelectSingleNode = function(xpath) {
		if (document.all)
			// IE
			return this.DOMDocument.selectSingleNode(xpath);
		else {
			// Gecko
			var xPathResult = this.DOMDocument
					.evaluate(
							xpath,
							this.DOMDocument,
							this.DOMDocument
									.createNSResolver(this.DOMDocument.documentElement),
							9, null);

			if (xPathResult && xPathResult.singleNodeValue)
				return xPathResult.singleNodeValue;
			else
				return null;
		}
	};

}