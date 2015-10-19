function extendNumber() {
	Number.prototype.toFiscalString = function () {
		return normalize_number(this);
	};
}
function parseFiscalNumber(s) {
	var re = /,/g;
	var str = s.replace(re, "");
	return Number(str);
}
function normalize_number(f) {
	if (isNaN(f)) {
		return "NaN";
	}
	if (f > 900000000000) {
		return "VALUE_TOO_HIGH";
	}
	if (f < -900000000000) {
		return "VALUE_TOO_LOW";
	}
	if (f < 0) {
		return "-" + normalize_number(0 - f);
	}
	f = Number(f.toFixed(2));
	var t = parseInt(f);
	var s = f - t;
	return _normalize_integer(t) + "." + _normalize_cents(s);
}
function _normalize_cents(c) {
	c = c.toPrecision(2);
	c *= 100;
//	c = Number( c.toFixed(0) );
	if (c == 0) {
		return "00";
	}
	if (c > 0 && c < 10) {
		return "0" + c.toFixed(0);
	}
	if (c >= 10) {
		return c.toFixed(0);
	}
}
function _normalize_integer(n) {
	n = Number(n.toFixed(0));
	if (n < 1000) {
		return n.toFixed(0);
	}
	var j = n % 1000;
	var i = (n - j) / 1000;
	var s = "000";
	if (j == 0) {
		s = "000";
	}
	if (j > 0 && j < 10) {
		s = "00" + j.toFixed(0);
	}
	if (j >= 10 && j < 100) {
		s = "0" + j.toFixed(0);
	}
	if (j >= 100) {
		s = j.toFixed(0);
	}
	return _normalize_integer(i) + "," + s;
}

extendNumber();