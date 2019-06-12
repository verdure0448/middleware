/**
 * Copyright 2015,2016 <hd-bsnc.com>. All rights reserved.
 */

var bsnc = bsnc || {};
bsnc.otp = bsnc.otp || {};
bsnc.otp.util = bsnc.otp.util || {};
bsnc.otp.api = bsnc.otp.api || {};
//bsnc.otp.api.ins = bsnc.otp.api.ins || {};
//bsnc.otp.api.dev = bsnc.otp.api.dev || {};
/**
 * OTP stringBuilder
 */
bsnc.otp.util.stringBuilder = function(value) {
	var strings = new Array('');
	if (value && value.length > 0) {
		strings[0] = value;
	}
	return {
		append : function(value) {
			if (value)
				strings.push(value);
		},
		clear : function() {
			strings.length = 1;
			strings[0] = '';
		},
		removeLastChar : function() {
			if (strings.length > 0) {
				var editString = strings[strings.length - 1];
				if (editString.length > 1) {
					editString = editString.substring(0, editString.length - 1);
					strings[strings.length - 1] = editString;
				} else if (editString == 1) {
					strings.length = strings.length - 1;
				}
			}
		},
		toString : function() {
			return strings.join("");
		}
	};
}

/**
 * 암호화
 */
bsnc.otp.util.Kryptos = function() {
	return {
		encrypt : function(theText) {
			output = new String;
			Temp = new Array();
			Temp2 = new Array();
			TextSize = theText.length;
			for (i = 0; i < TextSize; i++) {
				rnd = Math.round(Math.random() * 122) + 68;
				Temp[i] = theText.charCodeAt(i) + rnd;
				Temp2[i] = rnd;
			}
			for (i = 0; i < TextSize; i++) {
				output += String.fromCharCode(Temp[i], Temp2[i]);
			}
			return output;
		},
		unEncrypt : function(theText) {
			output = new String;
			Temp = new Array();
			Temp2 = new Array();
			TextSize = theText.length;
			for (i = 0; i < TextSize; i++) {
				Temp[i] = theText.charCodeAt(i);
				Temp2[i] = theText.charCodeAt(i + 1);
			}
			for (i = 0; i < TextSize; i = i + 2) {
				output += String.fromCharCode(Temp[i] - Temp2[i]);
			}
			return output;
		}
	};
}

bsnc.otp.util.event = function() {
	var eventObj = {
		result : '', // ack or nack
		code : '',
		data : ''
	};

	return eventObj;
}

/**
 * OTP 구조체 클래스
 */
bsnc.otp.util.struct = function() {
	var _structObj = {
		protocol : 'otp',
		SID : '',
		seq : '',
		TID : '',
		DID : '',
		port : '',
		path : '',
		fileName : '',
		params : [],
		trans : '',
		content : '',
		body : ''
	};

	return {
		getProtocol : function() {
			return _structObj.protocol;
		},
		setSID : function(sender) {
			_structObj.SID = sender;
		},
		getSID : function() {
			return _structObj.SID;
		},
		setSeq : function(seq) {
			_structObj.seq = seq;
		},
		getSeq : function() {
			return _structObj.seq;
		},
		setTID : function(tid) {
			_structObj.TID = tid;
		},
		getTID : function() {
			return _structObj.TID;
		},
		setPort : function(port) {
			_structObj.port = port;
		},
		getPort : function() {
			return _structObj.port;
		},
		setPath : function(path) {
			_structObj.path = path;
		},
		getPath : function() {
			return _structObj.path;
		},
		setFileName : function(fileName) {
			_structObj.fileName = fileName;
		},
		getFileName : function() {
			return _structObj.fileName;
		},
		setParam : function(key, value) {
			_structObj.params.push({
				'key' : key,
				'value' : urlEncode(value)
			});
		},
		setParams : function(params) {
			_structObj.params = params;
		},
		getParam : function(searchKey) {
			for (cnt = 0; cnt < _structObj.params.length; cnt++) {
				if (_structObj.params[cnt].key == searchKey) {
					return urlDecode(_structObj.params[cnt].value);
				}
			}
			return null;
		},
		getParams : function() {
			return _structObj.params;
		},
		getParamsUrl : function() {
			if (!_structObj.params)
				return '';
			if (_structObj.params.length < 1)
				return '';
			var strBuf = new bsnc.otp.util.stringBuilder();
			for (n = 0; n < _structObj.params.length; n++) {
				strBuf.append(_structObj.params[n].key + '='
						+ _structObj.params[n].value + '&');
			}
			strBuf.removeLastChar();

			return strBuf.toString();
		},
		setTrans : function(trans) {
			_structObj.trans = trans;
		},
		getTrans : function() {
			return _structObj.trans;
		},
		setContent : function(content) {
			_structObj.content = content;
		},
		getContent : function() {
			return _structObj.content;
		},
		setBody : function(body) {
			_structObj.body = body;
		},
		getBody : function() {
			return _structObj.body;
		}

	};
}
/**
 * OTP 파서
 */
bsnc.otp.util.parser = function() {
	return {
		makeOtpSendData : function(otpstruct) {
			var buf = new bsnc.otp.util.stringBuilder(otpstruct.getProtocol()
					+ '://');

			var sid = otpstruct.getSID();
			if (!otpstruct.getSeq()) {
				sid += otpstruct.getSeq();
			}

			var tid = otpstruct.getTID();

			if (!otpstruct.getPort()) {
				tid += otpstruct.getPort();
			}
			buf.append(sid + '@' + tid + '/' + otpstruct.getPath());

			var urlParams = otpstruct.getParamsUrl();
			if (urlParams) {
				buf.append('?' + urlParams);
			}

			if (otpstruct.getTrans()) {
				buf.append('#trans:' + otpstruct.getTrans());
				if (otpstruct.getContent()) {
					buf.append('&content:' + otpstruct.getContent());
				}
			} else {
				if (otpstruct.getContent()) {
					buf.append('#content:' + otpstruct.getContent());
				}
			}

			buf.append('\r\n');
			if (otpstruct.getBody()) {
				buf.append(otpstruct.getBody() + '\r\n');
			}

			return buf.toString();
		},
		// r1[0] 입력
		// "otp://1234567890:seq@this:port/login/temp/ack?comsvr=localhost%3A8082&authsvr=127.0.0.1%3A8083#transmission:end&content:json\r\naaaaaaaaaaaaa"
		// r1[1] "otp://" 이후 부터 "\r\n"까지
		// "1234567890:seq@this:port/login/temp/ack?comsvr=localhost%3A8082&authsvr=127.0.0.1%3A8083#transmission:end&content:json"
		// r1[2] body-contents "aaaaaaaaaaaaa"
		// r2[0] 입력
		// "1234567890:seq@this:port/login/temp/ack?comsvr=localhost%3A8082&authsvr=127.0.0.1%3A8083#transmission:end&content:json"
		// r2[1] sid ~ fullpath "1234567890@this/login/ack"
		// r2[2] params "comsvr=localhost%3A8082&authsvr=127.0.0.1%3A8083"
		// r2[3] anchor "transmission:end&content:json"
		// r3[0] 입력 "1234567890@this/login/ack"
		// r3[1] 전체 fullpath "/login/ack"
		// r4[0] 입력 "/login/ack"
		// r4[1] path "/login" [path]
		// r4[2] filename "ack" [filename]
		// r5[0] 입력 "1234567890:seq@this:port"
		// r5[1] SID부 "1234567890:seq"
		// r5[2] TID부 "this:port"
		// r6[0] 입력부 "1234567890:seq"
		// r6[1] SID "1234567890:seq"
		// r6[2] SEQ "seq"
		// r7[0] 입력부 "this:port"
		// r7[1] TID "this"
		// r7[2] SEQ "port"
		makeOtpRecvData : function(recvData) {
			var otpStruct = new bsnc.otp.util.struct();
			var r1 = recvData.match(/(?:^otp)(?:\:\/\/)(.*)(?:\r\n)(.*)/mi);
			var r2 = [];
			if (r1 == null) {
				console.log("[DEBUG]" + recvData);
			}
			if (r1[1].match(/\?/) && r1[1].match(/\#/)) {
				r2 = r1[1].match(/(.*)(?:[\?\#])(.*)(?:[\#])(.*)/i);
			} else if (r1[1].match(/\#/)) {
				r2 = r1[1].match(/(.*)(?:[\#])(.*)/i);
				r2[3] = r2[2];
				r2[2] = '';
			} else if (r1[1].match(/\?/)) {
				r2 = r1[1].match(/(.*)(?:[\?])(.*)/i);
				r2[3] = '';
			} else {
				r2[0] = r1[1];
				r2[1] = r1[1];
				r2[2] = '';
				r2[3] = '';
			}

			// path추출
			var r3 = r2[1].match(/(\/+.*)/i);
			// file추출
			var r4 = r3[1].match(/(.*\/)(.*)/i);
			r4[1] = r4[1].replace(/^\/|\/$/g, '');// 양끝 '/' 삭제
			// ID파트 추출
			var idPart = r2[1].replace(r3[1], '');
			var r5 = idPart.match(/(.*)(?:\@)(.*)/i);
			var r6 = [];
			if (r5[1].match(':')) {
				r6 = r5[1].match(/(.*)(?:\:)(.*)/i);
			} else {
				r6[1] = r5[1];
			}
			var r7 = [];
			if (r5[2].match(':')) {
				r7 = r5[2].match(/(.*)(?:\:)(.*)/i);
			} else {
				r7[1] = r5[2];
			}

			// param추출
			var params = new Array();
			if (r2[2]) {
				var arrParam = r2[2].split('&');
				for (cnt = 0; cnt < arrParam.length; cnt++) {
					var spliteParam = arrParam[cnt].split('=');
					params.push({
						'key' : urlDecode(spliteParam[0]),
						'value' : urlDecode(spliteParam[1])
					});
				}
			}

			// anchor추출
			var trans;
			var content;
			if (r2[3]) {
				trans = r2[3]
						.match(/(?:transmission\:|trans\:|t\:)(res|response|req|request|evt|event)/i);
				content = r2[3]
						.match(/(?:content\:|cont\:|c\:)(url|xml|json|text|csv)/i);
			}

			if (!trans) {
				trans = [ '', '' ]
			}
			;
			if (!content) {
				content = [ '', '' ]
			}
			;

			// SID설정
			otpStruct.setSID(r6[1] ? r6[1] : '');
			// seq설정
			otpStruct.setSeq(r6[2] ? r6[2] : '');
			// TID설정
			otpStruct.setTID(r7[1] ? r7[1] : '');
			// port설정
			otpStruct.setPort(r7[2] ? r7[2] : '');
			// path설정
			otpStruct.setPath(r4[1] ? r4[1] : '');
			// filename설정
			otpStruct.setFileName(r4[2] ? r4[2] : '');
			// param설정
			otpStruct.setParams(params);
			// anchor설정
			otpStruct.setTrans(trans[1] ? trans[1] : '');
			otpStruct.setContent(content[1] ? content[1] : '');
			// body설정
			otpStruct.setBody(r1[2] ? r1[2] : '');

			return otpStruct;
		}
	};
}

// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 사용자 API
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

Date.prototype.yyyymmdd = function() {
	var yyyy = this.getFullYear();
	var mm = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this
			.getMonth() + 1);
	var dd = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
	return "".concat(yyyy).concat(mm).concat(dd);
};

Date.prototype.yyyymmddhhmm = function() {
	var yyyy = this.getFullYear();
	var mm = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this
			.getMonth() + 1);
	var dd = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
	var hh = this.getHours() < 10 ? "0" + this.getHours() : this.getHours();
	var min = this.getMinutes() < 10 ? "0" + this.getMinutes() : this
			.getMinutes();
	return "".concat(yyyy).concat(mm).concat(dd).concat(hh).concat(min);
};

Date.prototype.yyyymmddhhmmss = function() {
	var yyyy = this.getFullYear();
	var mm = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this
			.getMonth() + 1);
	var dd = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
	var hh = this.getHours() < 10 ? "0" + this.getHours() : this.getHours();
	var min = this.getMinutes() < 10 ? "0" + this.getMinutes() : this
			.getMinutes();
	var ss = this.getSeconds() < 10 ? "0" + this.getSeconds() : this
			.getSeconds();
	return "".concat(yyyy).concat("/").concat(mm).concat("/").concat(dd)
			.concat(" ").concat(hh).concat(":").concat(min).concat(":").concat(
					ss);
};

function urlEncode(str) {
	return encodeURIComponent(str).replace(/'/g,"%27").replace(/"/g,"%22");	
}
function urlDecode(str) {
	return decodeURIComponent(str.replace(/\+/g,  " "));
}

//function Utf8ArrayToStr(array) {
//    var out, i, len, c;
//    var char2, char3;
//
//    out = "";
//    len = array.length;
//    i = 0;
//    while(i < len) {
//	c = array[i++];
//	switch(c >> 4)
//	{ 
//	  case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
//	    // 0xxxxxxx
//	    out += String.fromCharCode(c);
//	    break;
//	  case 12: case 13:
//	    // 110x xxxx   10xx xxxx
//	    char2 = array[i++];
//	    out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
//	    break;
//	  case 14:
//	    // 1110 xxxx  10xx xxxx  10xx xxxx
//        char2 = array[i++];
//	    char3 = array[i++];
//	    out += String.fromCharCode(((c & 0x0F) << 12) |
//					   ((char2 & 0x3F) << 6) |
//					   ((char3 & 0x3F) << 0));
//	    break;
//	}
//    }
//
//    return out;
//}
//
//function fromUTF8Array(data) { // array of bytes
//    var str = '',
//        i;
//
//    for (i = 0; i < data.length; i++) {
//        var value = data[i];
//
//        if (value < 0x80) {
//            str += String.fromCharCode(value);
//        } else if (value > 0xBF && value < 0xE0) {
//            str += String.fromCharCode((value & 0x1F) << 6 | data[i + 1] & 0x3F);
//            i += 1;
//        } else if (value > 0xDF && value < 0xF0) {
//            str += String.fromCharCode((value & 0x0F) << 12 | (data[i + 1] & 0x3F) << 6 | data[i + 2] & 0x3F);
//            i += 2;
//        } else {
//            // surrogate pair
//            var charCode = ((value & 0x07) << 18 | (data[i + 1] & 0x3F) << 12 | (data[i + 2] & 0x3F) << 6 | data[i + 3] & 0x3F) - 0x010000;
//
//            str += String.fromCharCode(charCode >> 10 | 0xD800, charCode & 0x03FF | 0xDC00); 
//            i += 3;
//        }
//    }
//
//    return str;
//}
/**
 * OTP 웹소켓
 */
bsnc.otp.websocket = function() {

	var _connectUrl = '';
	var _websocketObj = null;
	var _cbOpenEvent = null;
	var _cbSucessEvent = null;
	var _cbFailEvent = null;
	var _cbCloseEvent = null;

	var otpStorageEvt = new CustomEvent("otp-storage");
	/**
	 * 이벤트 함수
	 */
	function defaultOnOpenEvent() {
		return (function(evt) {
			console.log("[bsnc.otp.ws] defaultOnOpenEvent url[" + _connectUrl
					+ "]");
			if (_cbOpenEvent)
				_cbOpenEvent(evt);
		});
	}
	function defaultOnMessageEvent() {
		return (function(evt) {
			console.log("[bsnc.otp.ws] defaultOnMessageEvent url["
					+ _connectUrl + "]");

			// document.getElementById('textareaCode1').value += evt.data;
			sessionStorage.setItem("otp.websocket.receive", evt.data);

			otpStorageEvt.key = "otp.websocket.receive";
			if(evt.data instanceof Blob){
				// file read
//				var bytearray = new Uint8Array(evt.data.size);
//				for (var i=0;i<bytearray.length;++i) {
//					bytearray[i] = evt.data[i];
//				}
//				otpStorageEvt.data = fromUTF8Array(bytearray);
//				alert("서버 수신DATA가 지원하지 않는 포멧(Blob)입니다.");
				commonAlert("에러", "서버 수신DATA가 지원하지 않는 포멧(Blob)입니다.", "서버 수신DATA가 지원하지 않는 포멧(Blob)입니다.");
				return;
			} else {
				otpStorageEvt.data = evt.data;
			}
			otpStorageEvt.time = (new Date()).yyyymmddhhmmss();
			window.dispatchEvent(otpStorageEvt);

			if (_cbSucessEvent)
				_cbSucessEvent(evt);
		});
	}

	function defaultOnCloseEvent() {
		return (function(evt) {
			console.log("[bsnc.otp.ws] defaultOnCloseEvent url[" + _connectUrl
					+ "]");
			// evt.code == 1001 은 정상적인 서버 타임아웃
			if (_cbCloseEvent)
				_cbCloseEvent(evt);
		});
	}

	function defaultOnErrorEvent() {
		return (function(evt) {
			console.log("[bsnc.otp.ws] defaultOnErrorEvent code[" + evt.code
					+ "]");
			if (_cbFailEvent)
				_cbFailEvent(evt);
		});
	}

	return {
		doConnect : function(url, cbOpenSucessEvent, cbOpenFailEvent,
				cbCloseEvent) {
			_connectUrl = url;
			_cbOpenEvent = cbOpenSucessEvent;
			_cbCloseEvent = cbCloseEvent;
			_cbFailEvent = cbOpenFailEvent;

			// 웹소켓 생성
			_websocketObj = new WebSocket(_connectUrl);
			// 이벤트 등록
			_websocketObj.onopen = defaultOnOpenEvent();
			_websocketObj.onmessage = defaultOnMessageEvent();
			_websocketObj.onclose = defaultOnCloseEvent();
			_websocketObj.onerror = defaultOnErrorEvent();

			return;
		},
		doSend : function(sendData, cbSucessEvent, cbFailEvent) {
			_cbSucessEvent = cbSucessEvent;
			_cbFailEvent = cbFailEvent;

			// document.getElementById('textareaCode').value += sendData;
			sessionStorage.setItem("otp.websocket.send", new Date() + ' '
					+ sendData);
			otpStorageEvt.key = "otp.websocket.send";
			otpStorageEvt.data = sendData;
			otpStorageEvt.time = (new Date()).yyyymmddhhmmss();
			window.dispatchEvent(otpStorageEvt);
			
			_websocketObj.send(sendData);
		},
		doSendByte : function(sendData, cbSucessEvent, cbFailEvent) {
			_cbSucessEvent = cbSucessEvent;
			_cbFailEvent = cbFailEvent;

			//_websocketObj.binaryType = "arraybuffer";
			//_websocketObj.binaryType = "blob";
			var buf = new ArrayBuffer(sendData.length);
			var bufView = new Uint8Array(buf);
			for (var i=0; i < sendData.length;i++) {
				bufView[i] = sendData.charCodeAt(i);
			}
			_websocketObj.send(buf);
		},
		doKill : function() {
			_connectUrl = '';
			_cbOpenEvent = null;
			_cbSucessEvent = null;
			_cbFailEvent = null;
			_cbCloseEvent = null;
			_websocketObj.close();
			_websocketObj = null;
		},
		doClose : function() {
			_websocketObj.close();
		},
		getStatus : function() {
			return _websocketObj.readyState;
		},
		getBufferAmount : function() {
			return _websocketObj.bufferedAmount;
		},
		getWebsocketObj : function() {
			return _websocketObj;
		}
	};
}

// /////////////////////////////////////////////////////////////////////////////////////
// 이하는 공개용 API
// /////////////////////////////////////////////////////////////////////////////////////

/**
 * OTP메세지
 */
var OTP_MSG = function() {

	var msg = {
		E0001 : '네트워크 장애가 발생했습니다.',

	}
	return {

		getMsg : function(s) {
			return msg[s];
		}
	}
}();
/**
 * OTP글로벌 변수
 */
var OTP_GLOBALS = function() {
	var globals = {
		protocol : "ws://",
		urlPattern1 : "/smartiot/auth",
		urlPattern2 : "/"
	}
	return {
		getValue : function(s) {
			return globals[s];
		}
	}
}();

/**
 * OTP 인증 API
 */

bsnc.otp.api.auth = function() {

	//로그인시의 장치ID(DID)
	var _login_did = '';
	
	// 인증Data
	var _authData = null;
	
	// 전역 이벤트 리스트
	var cbGlobalEventList = [];
	// 세션종료 이벤트 함수
	var otpSessionDisconnectEvt = new CustomEvent("otp-sessionDisconnectEvt");
	cbGlobalEventList["session/disconnect"] = (function(evt) {
		otpSessionDisconnectEvt.content = '[' + evt.content + ']';
		
		var jsonContent = JSON.parse(evt.content);
		
		var sessionDisconnectStr = sessionStorage.getItem("otp.session.disconnect:" + _authData.ldid);
		var sessionDisconnectJson = null;
		if(sessionDisconnectStr != null && sessionDisconnectStr != undefined ){
			sessionDisconnectJson = JSON.parse(sessionDisconnectStr);
			sessionDisconnectJson.push(jsonContent);
		} else {
			
			sessionDisconnectJson = JSON.parse('[' + evt.content + ']');
		}
		
		//sessionDisconnectInfo.unshift(evt.content);
		
		sessionStorage.setItem("otp.session.disconnect:" + _authData.ldid, JSON.stringify(sessionDisconnectJson));
		window.dispatchEvent(otpSessionDisconnectEvt);
	});
	
	/*
	 * 인증서버 오픈 성공
	 */
	cbAuthSvrOpenSucess = function(evt) {
		// 장치ID, 유저ID,유저패스로 otp프로토콜 변환

		var otpStruct = bsnc.otp.util.struct();
		otpStruct.setPath("auth/open");
		otpStruct.setParam("uid", _authData.uid);
		otpStruct.setParam("upass", _authData.upass);
		otpStruct.setSID(_authData.sid);
		otpStruct.setTID(_authData.tid);

		var otpParser = new bsnc.otp.util.parser();
		var sendData = otpParser.makeOtpSendData(otpStruct);
		// 로그인 전송
		_authData.otpWs.doSend(sendData, cbAuthSvrSucess, cbAuthSvrFail);
	}

	/*
	 * 인증서버 close
	 */
	cbAuthSvrClose = function(evt) {
		// 처리 없음(무시)
	}

	/*
	 * 인증서버 오픈 실패(네트워크 & 서버 장애)
	 */
	cbAuthSvrOpenFail = function(evt) {
		var rtn = {
			result : 'F',
			code : 'E0001',
			type : 'error',
			msg : OTP_MSG.getMsg('E0001') + " [code] : " + evt.code
					+ " [url] : " + _authData.websocketUrl
		}
		_authData.cbFailFuncPool["login"](rtn);
	}

	/*
	 * 공통서버 오픈 성공시
	 */
	cbComSvrOpenSucess = function(evt) {
		var rtn = {
			result : 'S',
			code : '',
			type : '',
			msg : ''
		}
		_authData.cbSucessFuncPool["login"](rtn);
	}

	/*
	 * 공통서버 Colse시
	 */
	cbComSvrClose = function(evt) {
		var rtn = {
			result : 'S',
			code : evt.code,
			type : '',
			msg : ''
		}
		_authData.cbCloseFunc(rtn);
	}

	/*
	 * 공통서버 오픈 실패시
	 */
	cbComSvrOpenFail = function(evt) {
		var rtn = {
			result : 'F',
			code : 'E0001',
			type : 'error',
			msg : OTP_MSG.getMsg('E0001') + " [code] : " + evt.code
					+ " [url] : " + _authData.websocketUrl
		}
		_authData.cbFailFuncPool["login"](rtn);
	}

	/*
	 * 로그인 성공시 -> 인증정보 수집 & 공통서버 접속을 행한다.
	 */
	cbAuthSvrSucess = function(evt) {
		var otpParser = new bsnc.otp.util.parser();

		// 로그인 응답전문에서 공통서버, 인증서버, 세션 정보 획득

		var otpStruct = otpParser.makeOtpRecvData(evt.data);

		if (otpStruct.getFileName() == 'ack') {
			_authData.comServer = otpStruct.getParam('comsvr');
			_authData.url = otpStruct.getParam('url');
			// _authData.authServer = otpStruct.getParam('authsvr');
			_authData.sid = otpStruct.getSID();

			// 인증서버 웹소켓 kill
			_authData.otpWs.doKill();

			// 공통서버 웹소켓 open
			//_authData.websocketUrl = OTP_GLOBALS.getValue("protocol")
			//		+ _authData.comServer + OTP_GLOBALS.getValue("urlPattern2");
			_authData.websocketUrl = _authData.url;
			_authData.otpWs = new bsnc.otp.websocket();
			_authData.otpWs.doConnect(_authData.websocketUrl,
					cbComSvrOpenSucess, cbComSvrOpenFail, cbComSvrClose);

			return;
		} else { // nack 응답
			// 로그인 실패
			var rtn = {
				result : 'F',
				code : otpStruct.getParam('code'),
				type : otpStruct.getParam('type'),
				msg : otpStruct.getParam('msg')
			}
			_authData.cbFailFuncPool["login"](rtn);
			return;
		}
	}

	/*
	 * 로그인 처리 에러(네트워크 또는 서버 장애)
	 */
	cbAuthSvrFail = function(evt) {
		var rtn = {
			result : 'F',
			code : 'E0001',
			type : 'error',
			msg : OTP_MSG.getMsg('E0001') + " [url] : " + _websocketUrl
		}
		_authData.cbFailFunc(rtn);
		return;
	}

	/*
	 * 공통서버 전송처리 성공
	 */
	cbComSvrSucess = function(evt) {
		var otpParser = new bsnc.otp.util.parser();
		var otpStruct = otpParser.makeOtpRecvData(evt.data);

		if (otpStruct.getFileName() == 'ack') {
			var rtn = {
				result : 'S',
				msgCode : '',
				msgType : '',
				msg : '',
				params : otpStruct.getParams(),
				contentType : otpStruct.getContent(),
				content : otpStruct.getBody()
			}
			console.log("response path : " + otpStruct.getPath());
			
			if(otpStruct.getTrans() == 'evt' | otpStruct.getTrans() == 'event'){
				if(otpStruct.getPath() == 'session/disconnect'){
					// session 종료 이벤트라면
					cbGlobalEventList[otpStruct.getPath()](rtn);
				} else {
					// event 응답이라면
					_authData.cbEventFuncPool[otpStruct.getPath() + "/event"](rtn);
				}
			} else {
				_authData.cbSucessFuncPool[otpStruct.getPath()](rtn);
			}
			return;
		} else if(otpStruct.getFileName() == 'nack') {// 'nack' 응답
			var rtn = {
				result : 'F',
				msgCode : otpStruct.getParam('code'),
				msgType : otpStruct.getParam('type'),
				msg : otpStruct.getParam('msg')
			}
			_authData.cbFailFuncPool[otpStruct.getPath()](rtn);
			return;
		} else {
			if(otpStruct.getTrans() == 'evt' | otpStruct.getTrans() == 'event'){
				var rtn = {
					result : 'S',
					msgCode : '',
					msgType : '',
					msg : '',
					params : otpStruct.getParams(),
					contentType : otpStruct.getContent(),
					content : otpStruct.getBody()
				}
				if(otpStruct.getPath() == 'session'){
					// session 종료 이벤트라면
					cbGlobalEventList[otpStruct.getPath() + '/' + otpStruct.getFileName()](rtn);
				}
			}
		}
	}

	/*
	 * 공통서버 전송처리 실패(네트워크 & 서버 장애)
	 */
	cbComSvrFail = function(evt) {
//		var rtn = {
//			result : 'F',
//			code : 'E0001',
//			type : 'error',
//			msg : OTP_MSG.getMsg('E0001') + " [code] : " + evt.code
//					+ " [url] : " + _websocketUrl
//		}
//		_authData.cbFailFuncPool[](rtn);
		alert("웹소켓 통신에 치명적인 장애가 발생했습니다.");
		//commonAlert("에러", "네트워크  장애가 발생했습니다.", "웹소켓 통신에 치명적인 장애가 발생했습니다.");
	
	}
	return {
		/**
		 * 로그인 ip, uid, password, cbSucessFunc, cbFailFunc, cbCloseFunc, sid,
		 * tid 입력
		 * 
		 * 주위 : 입력 cbCloseFunc은 계속 감시하면서 close이벤트 발생시 로그아웃과 동일한 UI 처리 필요!
		 */
		login : function(authData) {

			_authData = {
				websocketUrl : OTP_GLOBALS.getValue("protocol") + authData.ip
						+ OTP_GLOBALS.getValue("urlPattern1"),
				authServer : authData.ip,
				uid : authData.uid,
				upass : btoa(authData.upass),
				cbSucessFuncPool : {"login" : authData.cbSucessFunc},
				cbFailFuncPool : {"login" : authData.cbFailFunc},
				cbCloseFunc : authData.cbCloseFunc,
				tid : authData.tid ? authData.tid : 'this',
				sid : authData.sid ? authData.sid : 'default',
				ldid : authData.sid ? authData.sid : 'default',
				otpWs : null
			}

			// 웹소켓 생성
			_authData.otpWs = new bsnc.otp.websocket();
			_authData.otpWs.doConnect(_authData.websocketUrl,
					cbAuthSvrOpenSucess, cbAuthSvrOpenFail, cbAuthSvrClose);

			return;
		},
		clone : function(authArg) {

		},
		logout : function() {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("logout");
			otpStruct.setSID(_authData.sid);
			otpStruct.setTID(_authData.tid);

			var otpParser = new bsnc.otp.util.parser();
			var sendData = otpParser.makeOtpSendData(otpStruct);
			
			// 전송
			_authData.otpWs.doSend(sendData);
		},
		isLogin : function() {
			// TODO 미구현
		},

		send : function(otpStruct, cbSucessFunc, cbFailFunc) {

			var otpParser = new bsnc.otp.util.parser();
			var sendData = otpParser.makeOtpSendData(otpStruct);
			
			// TODO 검토 : 웹소켓이 ready 상태인가를 체크해서 재귀호출 처리가 필요한가?
			console.log("request path : " + otpStruct.getPath());
			_authData.cbSucessFuncPool[otpStruct.getPath()] = cbSucessFunc;
			_authData.cbFailFuncPool[otpStruct.getPath()] = cbFailFunc;
			
			_authData.otpWs.doSend(sendData, cbComSvrSucess, cbComSvrFail);
		},
		sendByte : function(otpStruct, cbSucessFunc, cbFailFunc) {

			var otpParser = new bsnc.otp.util.parser();
			var sendData = otpParser.makeOtpSendData(otpStruct);
			
			// TODO 검토 : 웹소켓이 ready 상태인가를 체크해서 재귀호출 처리가 필요한가?
			console.log("request path : " + otpStruct.getPath());
			_authData.cbSucessFuncPool[otpStruct.getPath()] = cbSucessFunc;
			_authData.cbFailFuncPool[otpStruct.getPath()] = cbFailFunc;
			
			_authData.otpWs.doSendByte(sendData, cbComSvrSucess, cbComSvrFail);
		},
		eventRequest : function(otpStruct, cbSucessFunc, cbFailFunc, cbEventFunc) {

			var otpParser = new bsnc.otp.util.parser();
			var sendData = otpParser.makeOtpSendData(otpStruct);
			
			// TODO 검토 : 웹소켓이 ready 상태인가를 체크해서 재귀호출 처리가 필요한가?
			console.log("request path : " + otpStruct.getPath());
			_authData.cbSucessFuncPool[otpStruct.getPath()] = cbSucessFunc;
			_authData.cbFailFuncPool[otpStruct.getPath()] = cbFailFunc;
			// 이벤트 함수가 있을 경우 이벤트 콜백 함수풀에 저장
			if(cbEventFunc) _authData.cbEventFuncPool[otpStruct.getPath() + "/event"] = cbEventFunc;
			
			_authData.otpWs.doSend(sendData, cbComSvrSucess, cbComSvrFail);
		},
		getLoginDID : function() {
			return _authData.ldid;
		},
		getUID : function() {
			return _authData.uid;
		},
		getAuthInfo : function() {
			return _authData;
		},
		toString : function(authArg) {
			return '{"uid":"' + _authData.uid + '",' + '"sid":"'
					+ _authData.sid + '",' + '"tid":"' + _authData.tid + '",'
					+ '"authServer":"' + _authData.authServer + '",'
					+ '"comServer":"' + _authData.comServer + '",'
					+ '"url":"' + _authData.url + '",'
					+ '"ldid":"' + _authData.ldid + '",'
					+ '"dId":"' + _authData.sid+ '"}';
		},
		restore : function(strObj, cbSucessFunc, cbFailFunc, cbCloseFunc) {

			var obj = JSON.parse(strObj);

			_authData = {
				//websocketUrl : OTP_GLOBALS.getValue("protocol") + obj.comServer
				//		+ OTP_GLOBALS.getValue("urlPattern"),
				websocketUrl : obj.url,
				authServer : obj.authServer,
				uid : obj.uid,
				cbSucessFuncPool : {"login" : cbSucessFunc},
				cbFailFuncPool : {"login" : cbFailFunc},
				cbEventFuncPool : {},
				cbCloseFunc : cbCloseFunc,
				sid : obj.sid,
				tid : obj.tid,
				ldid : obj.ldid
			}

			// 웹소켓 생성
			_authData.otpWs = new bsnc.otp.websocket();
			_authData.otpWs.doConnect(_authData.websocketUrl,
					cbComSvrOpenSucess, cbComSvrOpenFail, cbComSvrClose);

			return;
		}
	};
}

/**
 * OTP 인스턴스 API
 */
bsnc.otp.api.ins = function(authObj) {
	var _authObj = authObj;

	return {
		start : function(iid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/start");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 전송
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		stop : function(iid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/stop");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 전송
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		suspend : function(iid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/suspend");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 전송
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		get : function(iid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/get");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 전송
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		set : function(iid, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/set");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);

			// 전송
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		put : function(iid, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/put");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);

			// 전송
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		del : function(iid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/del");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 전송
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		searchByAid : function(aid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/search/by-aid");
			otpStruct.setParam("adapter.id", aid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		
		all : function(iid,did,ip,cbSucessFunc,cbFailFunc){
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/all");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setParam("device.id", did);
			otpStruct.setParam("ip", ip);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}

/**
 * OTP 인스턴스속성 API
 */
bsnc.otp.api.ins.att = function(authObj) {
	var _authObj = authObj;
	return {
		get : function(iid, attKey, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/att/get");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setParam("attribution.key", attKey);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		set : function(iid, attKey, content, contentType, cbSucessFunc,
				cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/att/set");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setParam("attribution.key", attKey);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		put : function(iid, attKey, content, contentType, cbSucessFunc,
				cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/att/put");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setParam("attribution.key", attKey);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		del : function(iid, attKey, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/att/del");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setParam("attribution.key", attKey);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		searchByIid : function(iid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/att/search/by-iid");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}

/**
 * OTP 인스턴스기능 API
 */
bsnc.otp.api.ins.func = function(authObj) {
	var _authObj = authObj;
	return {
		get : function(iid, funcKey, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/func/get");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setParam("function.key", funcKey);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		set : function(iid, funcKey, content, contentType, cbSucessFunc,
				cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/func/set");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setParam("function.key", funcKey);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		put : function(iid, funcKey, content, contentType, cbSucessFunc,
				cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/func/put");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setParam("function.key", funcKey);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		del : function(iid, funcKey, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/func/del");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setParam("function.key", funcKey);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		searchByIid : function(iid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("ins/func/search/by-iid");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}

/**
 * OTP 세션 API
 */
bsnc.otp.api.ins.session = function(authObj) {
	var _authObj = authObj;
	return {

		/**
		 * 인스턴스ID로 세션리스트를 조회
		 */
		searchByIid : function(iid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("session/search/by-iid");
			otpStruct.setParam("instance.id", iid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},

		get : function(did, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("session/get");
			otpStruct.setParam("device.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},

		/**
		 * 제어(속성)목록을 조회
		 */
		attGetAll : function(did, cbSucessFunc, cbFailFunc) {
			// session/att/get/all
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("session/att/get/all");
			otpStruct.setParam("device.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);

		},

		/**
		 * 제어(기능)목록을 조회
		 */
		funcGetAll : function(did, cbSucessFunc, cbFailFunc) {
			// session/att/get/all
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("session/func/get/all");
			otpStruct.setParam("device.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);

		},
		/**
		 * 세션을 종료
		 */
		disconnect : function(did, cbSucessFunc, cbFailFunc) {
			// session/disconnect
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("session/disconnect");
			otpStruct.setParam("device.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}

	};
}

/**
 * OTP 아답터 API
 * 
 * @param authObj
 * @returns {___anonymous22524_22532}
 */
bsnc.otp.api.adt = function(authObj) {
	var _authObj = authObj;
	return {
		get : function(aid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("adt/get");
			otpStruct.setParam("adapter.id", aid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		getAll : function(cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("adt/get/all");
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		attGet : function(aid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("adt/att/get");
			otpStruct.setParam("adapter.id", aid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		funcGet : function(aid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("adt/func/get");
			otpStruct.setParam("adapter.id", aid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		install : function(fileName, fileSize, file, cbSucessFunc, cbFailFunc) {
			
			var totalSeq = parseInt(file.length/50000) + 1;
			var startIndex = 0;
			var endIndex = 0;
			for(var i = 0; i < totalSeq; i++){
				var otpStruct = bsnc.otp.util.struct();
				otpStruct.setPath("adt/install");
				otpStruct.setParam("adapter.file.name", fileName);
				otpStruct.setParam("adapter.file.size", fileSize);
				otpStruct.setParam("total.sequence", (totalSeq).toString());
				otpStruct.setParam("current.sequence", (i+1).toString());
				otpStruct.setContent("json");
				
				startIndex = i*50000;
				if((i*50000 + 50000) <= file.length){
					endIndex = i*50000 + 50000;
				} else {
					endIndex = file.length;
				}
				
				otpStruct.setBody(file.substring(startIndex, endIndex));
				otpStruct.setSID(_authObj.getAuthInfo().sid);
				otpStruct.setTID(_authObj.getAuthInfo().tid);

				// 요청
				_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
			}
			
//			var otpStruct = bsnc.otp.util.struct();
//			otpStruct.setPath("adt/install");
//			otpStruct.setParam("adapter.file.name", fileName);
//			otpStruct.setParam("adapter.file.size", fileSize);
//			otpStruct.setContent("file");
//			otpStruct.setBody(file);
//			otpStruct.setSID(_authObj.getAuthInfo().sid);
//			otpStruct.setTID(_authObj.getAuthInfo().tid);
//
//			// 요청
//			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		uninstall : function(aid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("adt/uninstall");
			otpStruct.setParam("adapter.id", aid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}

/**
 * OTP 장치풀 API
 */
bsnc.otp.api.devpool = function(authObj) {
	var _authObj = authObj;
	return {
		get : function(dpid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("devpool/get");
			otpStruct.setParam("device.pool.id", dpid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		getAll : function(cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("devpool/get/all");
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		set : function(dpid, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("devpool/set");
			otpStruct.setParam("device.pool.id", dpid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);
			
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		put : function(dpid, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("devpool/put");
			otpStruct.setParam("device.pool.id", dpid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);
			
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		del : function(dpid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("devpool/del");
			otpStruct.setParam("device.pool.id", dpid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}

/**
 * OTP 장치 API
 */
bsnc.otp.api.dev = function(authObj) {
	var _authObj = authObj;
	return {
		get : function(did, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("dev/get");
			otpStruct.setParam("device.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		set : function(did, content, contType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("dev/set");
			otpStruct.setParam("device.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contType);
			otpStruct.setBody(content);
			
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		put : function(did, content, contType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("dev/put");
			otpStruct.setParam("device.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contType);
			otpStruct.setBody(content);
			
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		searchByDpid : function(dpid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("dev/search/by-dpid");
			otpStruct.setParam("device.pool.id", dpid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		del : function(did, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("dev/del");
			otpStruct.setParam("device.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}

/**
 * OTP 장치제어 API
 */
bsnc.otp.api.dev.control = function(authObj) {
	var _authObj = authObj;
	return {
		update : function(did, attKey, attValue, cbSucessFunc, cbFailFunc) {			
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath(attKey);
			otpStruct.setParam("update", attValue);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(did);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		read : function(did, attKey, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath(attKey);
			otpStruct.setParam("read", "");
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(did);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		create : function() {
			// 미정
		},
		del : function() {
			// 미정
		},
		funcNonParam : function(did, funcKey, jsonContents,cbSucessFunc, cbFailFunc){
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath(funcKey);
			
			if(jsonContents!=""){
				otpStruct.setContent('json');
				otpStruct.setBody(jsonContents);
			}
			
			otpStruct.setSID(_authObj.getAuthInfo().sid);			
			otpStruct.setTID(did);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);	
		},
		funcOneParam : function(did, funcKey,  param1Key, param1, jsonContents, cbSucessFunc, cbFailFunc){		
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath(funcKey);
			otpStruct.setParam(param1Key, param1);

			if(jsonContents!=""){
				otpStruct.setContent('json');
				otpStruct.setBody(jsonContents);
			}
			
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(did);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},	
		funcTwoParams : function(did, funcKey,  param1Key, param1, param2Key, param2, jsonContents, cbSucessFunc, cbFailFunc){		
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath(funcKey);
			otpStruct.setParam(param1Key, param1);
			otpStruct.setParam(param2Key, param2);

			if(jsonContents!=""){
				otpStruct.setContent('json');
				otpStruct.setBody(jsonContents);
			}
			
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(did);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);			
		},	
		funcThreeParams : function(did, funcKey,  param1Key, param1, param2Key, param2,param3Key, param3, jsonContents, cbSucessFunc, cbFailFunc){		
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath(funcKey);
			otpStruct.setParam(param1Key, param1);
			otpStruct.setParam(param2Key, param2);
			otpStruct.setParam(param3Key, param3);

			if(jsonContents!=""){
				otpStruct.setContent('json');
				otpStruct.setBody(jsonContents);
			}
			
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(did);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);			
		},	
		funcFourParams : function(did, funcKey,  param1Key, param1, param2Key, param2,param3Key, param3, param4Key, param4, jsonContents, cbSucessFunc, cbFailFunc){		
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath(funcKey);
			otpStruct.setParam(param1Key, param1);
			otpStruct.setParam(param2Key, param2);
			otpStruct.setParam(param3Key, param3);
			otpStruct.setParam(param4Key, param4);

			if(jsonContents!=""){
				otpStruct.setContent('json');
				otpStruct.setBody(jsonContents);
			}
			
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(did);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);			
		},	
		funcFiveParams : function(did, funcKey,  param1Key, param1, param2Key, param2,param3Key, param3, param4Key, param4, param5Key, param5, jsonContents, cbSucessFunc, cbFailFunc){		
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath(funcKey);
			otpStruct.setParam(param1Key, param1);
			otpStruct.setParam(param2Key, param2);
			otpStruct.setParam(param3Key, param3);
			otpStruct.setParam(param4Key, param4);
			otpStruct.setParam(param5Key, param5);

			if(jsonContents!=""){
				otpStruct.setContent('json');
				otpStruct.setBody(jsonContents);
			}
			
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(did);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);			
		},
	};
}

/**
 * OTP 유저풀 API
 * 
 * @param authObj
 */
bsnc.otp.api.userpool = function(authObj) {
	var _authObj = authObj;
	return {
		get : function(upid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("userpool/get");
			otpStruct.setParam("user.pool.id", upid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		set : function(upid, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("userpool/set");
			otpStruct.setParam("user.pool.id", upid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		put : function(upid, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("userpool/put");
			otpStruct.setParam("user.pool.id", upid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		getAll : function(cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("userpool/get/all");
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		del : function(upid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("userpool/del");
			otpStruct.setParam("user.pool.id", upid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}
/**
 * OTP 유저 API
 * 
 * @param authObj
 */
bsnc.otp.api.user = function(authObj) {
	var _authObj = authObj;
	return {
		get : function(uid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/get");
			otpStruct.setParam("user.id", uid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		set : function(uid, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/set");
			otpStruct.setParam("user.id", uid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		put : function(uid, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/put");
			otpStruct.setParam("user.id", uid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		searchByUpid : function(upid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/search/by-upid");
			otpStruct.setParam("user.pool.id", upid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		del : function(uid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/del");
			otpStruct.setParam("user.id", uid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}

/**
 * OTP 유저필터 API
 * 
 */
bsnc.otp.api.user.filter = function(authObj) {
	var _authObj = authObj;
	return {
		get : function(uid, authFilter, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/filter/get");
			otpStruct.setParam("user.id", uid);
			otpStruct.setParam("authority.filter", authFilter);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		set : function(uid, authFilter, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/filter/set");
			otpStruct.setParam("user.id", uid);
			otpStruct.setParam("authority.filter", authFilter);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);
			
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		put : function(uid, authFilter, content, contentType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/filter/put");
			otpStruct.setParam("user.id", uid);
			otpStruct.setParam("authority.filter", authFilter);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			otpStruct.setContent(contentType);
			otpStruct.setBody(content);
			
			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		searchByUid : function(uid, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/filter/search/by-uid");
			otpStruct.setParam("user.id", uid);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		},
		del : function(uid, authFilter, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("user/filter/del");
			otpStruct.setParam("user.id", uid);
			otpStruct.setParam("authority.filter", authFilter);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}

/**
 * OTP 도메인 API
 * 
 * @param authObj
 * @returns {___anonymous43586_43667}
 */
bsnc.otp.api.domain = function(authObj) {
	var _authObj = authObj;
	return {
		searchByType : function(domainType, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("domain/search/by-type");
			otpStruct.setParam("domain.type", domainType);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);

			// 요청
			_authObj.send(otpStruct, cbSucessFunc, cbFailFunc);
		}
	};
}


bsnc.otp.api.event = function(authObj) {
	var _authObj = authObj;
	
	return {
		// 장치별 제어명령 메세지
		devMsgStart : function(did, cbSucessFunc, cbFailFunc, cbEventFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("event/dmsg/start");
			otpStruct.setParam("device.id", did);
			otpStruct.setParam("event.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			// 요청
			_authObj.eventRequest(otpStruct, cbSucessFunc, cbFailFunc, cbEventFunc);
		},
		devMsgStop : function(did, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("event/dmsg/stop");
			otpStruct.setParam("device.id", did);
			otpStruct.setParam("event.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			// 요청
			_authObj.eventRequest(otpStruct, cbSucessFunc, cbFailFunc, null);
		},
		// 장치별 제어 명령 트래픽
		devTrafficStart : function(did, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("event/dtraffic/start");
			otpStruct.setParam("device.id", did);
			otpStruct.setParam("event.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			// 요청
			_authObj.eventRequest(otpStruct, cbSucessFunc, cbFailFunc, cbEventFunc);
		},
		devTrafficStop : function(did, cbSucessFunc, cbFailFunc) {
			var otpStruct = bsnc.otp.util.struct();
			otpStruct.setPath("event/dtraffic/stop");
			otpStruct.setParam("device.id", did);
			otpStruct.setParam("event.id", did);
			otpStruct.setSID(_authObj.getAuthInfo().sid);
			otpStruct.setTID(_authObj.getAuthInfo().tid);
			// 요청
			_authObj.eventRequest(otpStruct, cbSucessFunc, cbFailFunc, null);
		}
	};
}