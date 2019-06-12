var AuthObj = sessionStorage.getItem("authObj");

function initAPI(){

	if(AuthObj == null){
//		alert("로그인 후 접근 가능합니다.");
//		commonAlert("로그인 후 접근 가능합니다.");
		var indexPage = function(){
			window.location.href="./index.html";
		}
		
		customConfirm("알림", "로그인 후 접근 가능합니다.",indexPage);				
		$(".bootbox-close-button").click(indexPage);
		clearInterval(scopeSynchronizeIntervalId);
		//return false;
	}else{
	// check is login
		otpAuth.restore(AuthObj, cbRestoreAuthSucessFunc, cbRestoreAuthCloseFunc, cbRestoreAuthFailFunc);
	}
}