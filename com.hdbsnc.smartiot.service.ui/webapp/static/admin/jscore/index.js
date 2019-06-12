/**
 *  index.html에서 필요한 js함수
 */

$(document).ready(function () {
	
	var agent = navigator.userAgent.toLowerCase();

	if (!(agent.indexOf("chrome")!=-1 || agent.indexOf("safari")!=-1)){// || agent.indexOf("firefox")!=-1)){
//		var check= commonAlert("에러","지원하지 않는 브라우져 입니다.</br>사파리, 크롬을 이용해주세요.","지원하지 않는 브라우져");
		customConfirm("에러","지원하지 않는 브라우져 입니다.</br>사파리, 크롬을 이용해주세요.",function(){
			$("html").hide();
		});
	}
});

/* 0윤영진 */
function login(){

	var login_ip = document.domain + ':8899';
	var login_sid = document.getElementById('user_sid').value;
	var login_uid = document.getElementById('user_id').value;
	var login_upass = document.getElementById('user_pass').value;
	
	var now = new Date();	
	var nowAll = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() + " " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds() + " ";
	
	var authArg = {
	//ip : '192.168.0.127:8899', // 내부 테스트1 용
	//ip : '192.168.0.93:8899', // 내부 테스트2 용
	//ip : '222.119.221.212:8899', // 부산인터넷방송국 테스트1 
	ip : login_ip, // 부산인터넷방송국 테스트1 ( 수정 2015-11-26 )
	
	uid : login_uid,
	upass : login_upass,
	cbSucessFunc : cbLoginSucessFunc,
	cbFailFunc : cbLoginFailFunc,
	cbCloseFunc : cbCloseFunc,
	
	//sid : "com.hdbsnc.smartiot.device.websocketapi.1" // 내부테스트1
	//sid : "com.hdbsnc.smartiot.device.websocketapi.2" // 내부 테스트2
	//sid : "com.hdbsnc.smartiot.device.websocketapi.3" // 부산인터넷방송국 테스트1 
	sid : login_sid // 부산인터넷방송국 테스트1 
	}

	sessionStorage.setItem("uid",login_uid);
	sessionStorage.setItem("loginTime",nowAll);
	sessionStorage.setItem("did",login_sid);

	otpAuth.login(authArg);
}

function enterLogin(){
	
	var keyCode=window.event.keyCode;
	
	if(keyCode==13){
		login();
	}
}