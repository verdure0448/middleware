//Grid Columns 
	$(document).ready(function () {
		$('#MySplitter').width(1200).height(850).split({orientation:'vertical', limit:270, position:'40%'});
		$('#sWrap').split({orientation:'horizontal', limit:0});

		$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
		scopeSynchronizeIntervalId=setInterval("viewSynchronize(userPoolScope,userScope,userFilterScope)",200);
	});

var userPoolColumnDefs = [
	{headerName: "유저풀ID", field: "id", filter: 'text', width: 200},
	{headerName: "유저풀명", field: "usePoolNm", filter: 'text', width: 80},
	{headerName: "비고", field: "remark",filter: 'text', width: 150},
	{headerName: "변경일시", field: "altDate",filter: 'text', width: 80},
	{headerName: "등록일시", field: "regDate",filter: 'text', width: 80}
];

var userColumnDefs = [
	{headerName: "유저ID", field: "id", filter: 'text', width: 80},
	{headerName: "유저풀ID", field: "usePoolId", filter: 'text', width: 180},
	{headerName: "유저 패스워드", field: "usePass",filter: 'text', width: 80},
	/*{headerName: "유저 타입", field: "useType", width: 80},*/
	{headerName: "유저명", field: "useNm",filter: 'text', width: 80},
	{headerName: "회사명", field: "compNm",filter: 'text', width: 80},
	{headerName: "부서명", field: "depNm",filter: 'text', width: 80},
	{headerName: "직위", field: "jobTitle",filter: 'text', width: 80},
	{headerName: "비고", field: "remark",filter: 'text', width: 150},
	{headerName: "변경일시", field: "altDate",filter: 'text', width: 80},
	{headerName: "등록일시", field: "regDate",filter: 'text', width: 80}
];

var userFilterColumnDefs = [
	{headerName: "유저ID", field: "id", filter: 'text', width: 200},
	{headerName: "권한필터", field: "authFilt", filter: 'text', width: 80},
	{headerName: "비고", field: "remark",filter: 'text', width: 235},
	{headerName: "변경일시", field: "altDate",filter: 'text', width: 80},
	{headerName: "등록일시", field: "regDate",filter: 'text', width: 80}
];

//var searchUserPoolDataColumnDefs = [	
//	{headerName: "유저풀ID", field: "id",width:538}
//];

	/******************************* CONTROLLERS START ******************************/
	agGridModule.controller("userPoolCtrl", function($scope) {

		userPoolScope = $scope;

		$scope.gridOptions = {
			columnDefs: userPoolColumnDefs,
			rowSelection: 'single',
			onRowSelected: rowSelectedFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};
		
		/* Normal Functions */
		function rowSelectedFunc(event) { //UserPool List Click Event Function
			clearUserData();

			var upId = event.node.data.id;
			var upNm = event.node.data.usePoolNm;
			var upRmrk = event.node.data.remark;

			selectedUserPoolId = upId;
			selectedUserPoolName = upNm;
			selectedUserPoolRemark = upRmrk;

			setUserData(upId);
		}

		/* Init */
		 //Login and Get Data Progress

	});

	agGridModule.controller("userCtrl", function($scope) {

		userScope = $scope;

		$scope.gridOptions = {
			columnDefs: userColumnDefs,
			rowSelection: 'single',
			onRowSelected: rowSelectedFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};

		/* Normal Functions */
		function rowSelectedFunc(event) { //User List Click Event Function
			clearUserFilterData();
			var uId = event.node.data.id;
			var uPw = event.node.data.usePass;
			var uTp = event.node.data.useType;
			var uNm = event.node.data.useNm;
			var coNm = event.node.data.compNm;
			var dpNm = event.node.data.depNm;
			var jTtl = event.node.data.jobTitle;
			var uRmrk = event.node.data.remark;

			selectedUserId = uId;
			selectedUserPassword = uPw;
			selectedUserType = uTp;
			selectedUserName = uNm;
			selectedCompanyName = coNm;
			selectedDepartName = dpNm;
			selectedJobTitle = jTtl;
			selectedUserRemark = uRmrk;

			setUserFilterData(uId);
		}

	});

	agGridModule.controller("userFilterCtrl", function($scope) {

		userFilterScope = $scope;

		$scope.gridOptions = {
			columnDefs: userFilterColumnDefs,
			rowSelection: 'single',
			onRowSelected: rowSelectedFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};

		/* Normal Functions */
		function rowSelectedFunc(event) { //User Filter List Click Event Function
			var uFid = event.node.data.id;
			var aFilt = event.node.data.authFilt;
			var fRmrk = event.node.data.remark;

			selectedAuthorityFilter = aFilt;
			selectedUserPoolRemark = fRmrk;
			selectedUserFilterId = uFid; 
		}

	});

//	agGridModule.controller("searchCtrl", function($scope) {
//
//		searchScope = $scope;
//
//		$scope.gridOptions = {
//			columnDefs: searchUserPoolDataColumnDefs,
//			rowSelection: 'single',
//			enableColResize: true,
//			onRowSelected: rowSelectedFunc,
//			suppressSizeToFit: true,
//			rowData: null,
//		};
//
//		/* Normal Functions */
//		function rowSelectedFunc(event) { //Session List Click Event Function
//			var id = event.node.data.id;
//			selectedSearchId = id;
//
//			document.getElementById("searchUserPoolId").value = selectedSearchId; 
//		}
//
//	});

	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/
	function initGetData() {
	  setUserPoolData();
    }

	/* Call backs */
	
	function cbUsePoolGetAllSucessFunc(evt){// 유저풀 목록 조회 성공
		console.log('유저풀 조회 성공');
		var rowDataByJson = parseUserPoolJsonData(evt);
		loadData(userPoolScope,rowDataByJson);
		$(".blankUserUserpool").text("");
	}

	function cbUsePoolGetAllFailFunc(evt){// 유저풀 목록 조회 실패
		console.log('유저풀 목록 조회 실패');
		commonErrorMessage(evt,"blankUserUserpool");
	}

//	function cbSrchUsePoolGetAllSucessFunc(evt){// 유저풀 목록 조회 성공(검색)
//		console.log('유저풀 조회 성공');
//		var rowDataByJson = parseUserPoolJsonData(evt);
//		console.log(rowDataByJson);
//		loadData(searchScope,rowDataByJson);
//	}
//
//	function cbSrchUsePoolGetAllFailFunc(evt){// 유저풀 목록 조회 실패(검색)
//		console.log('유저풀 조회 실패');
//		commonErrorMessage(evt);
//	}
	
	function cbUseSearchByUpidSucessFunc(evt){// 유저 목록 조회 성공
		console.log('유저 조회 성공');
		var rowDataByJson = parseUserJsonData(evt);
		loadData(userScope,rowDataByJson);
		$(".blankUserUser").text("");
	}

	function cbUseSearchByUpidFailFunc(evt){// 유저 목록 조회 실패
		console.log(evt);
		commonErrorMessage(evt,"blankUserUser");
	}

	function cbUseFilterSearchByUidSucessFunc(evt){// 유저필터 조회 성공
		console.log('유저필터 조회 성공');
		var rowDataByJson = parseUserFilterJsonData(evt);
		loadData(userFilterScope,rowDataByJson);
		$(".blankUserFilter").text("");
	}

	function cbUseFilterSearchByUidFailFunc(evt){// 유저필터 조회 실패
		console.log(evt);
		commonErrorMessage(evt,"blankUserFilter");
	}


	/******************************* API SCRIPTS END ******************************/

	/* Clear Datas */
	
	function clearUserPoolData(){ //Clear datas to grid(search device pool) 
		selectedUserPoolId = null;
		selectedUserPoolName = null;
		selectedUserPoolRemark = null;

		var emptyAttributeRow = [];	
		/*var emptyAttributeRow = [{ id: '' }];*/	
		userPoolScope.gridOptions.api.setRowData(emptyAttributeRow);
		clearUserData();
	}

	function clearUserData(){ //Clear datas to grid(search device pool) 
		selectedUserId = null;
		/*var emptyAttributeRow = [{ id: '' }];*/	
		var emptyAttributeRow = [];	
		userScope.gridOptions.api.setRowData(emptyAttributeRow);
		clearUserFilterData();
		$(".blankUserUser").text("");
	}

	function clearUserFilterData(){ //Clear datas to grid(search device pool) 
		selectedUserFilterId = null;
		selectedAuthorityFilter = null;
		selectedUserPoolRemark = null;

		var emptyAttributeRow = [];	
//		var emptyAttributeRow = [{ id: '' }];	
		userFilterScope.gridOptions.api.setRowData(emptyAttributeRow);
		$(".blankUserUser").text("");
		$(".blankUserFilter").text("");
	}
	
	var form_mode = null; //PUT SET 폼 모드

	$(document).ready(function() {
		/**
		 * User Pool form
		 */
		$("#addUserPool").click(function() {

			  form_mode = 'put';
			  $(".myModalLabel").text("유저풀 등록");
			  $(".popUpDefaultBtn").text("등록");

			  document.getElementById("userPoolId").readOnly = false;
			  document.getElementById("userPoolId").value = "";
			  document.getElementById("userPoolName").value = "";
			  document.getElementById("userPoolRemark").value = "";
			  
		});
		$("#editUserPool").click(function() {
			if(selectedUserPoolId == null){
			  /*bootbox.alert("유저풀을 선택 해 주세요.", function(){
			  });*/
			  commonAlert("알림","유저풀을 선택 해 주세요.","유저풀이 선택되지 않음.");
			  return false;
			}else{
			  form_mode = 'set';
			  $(".myModalLabel").text("유저풀 수정");
			  $(".popUpDefaultBtn").text("수정");
			  
			  selectedUserPoolId=html5SpecialCharDeCode(selectedUserPoolId);
			  selectedUserPoolName=html5SpecialCharDeCode(selectedUserPoolName);
			  selectedUserPoolRemark=html5SpecialCharDeCode(selectedUserPoolRemark);

			  document.getElementById("userPoolId").readOnly = true;
			  document.getElementById("userPoolId").value = selectedUserPoolId;
			  document.getElementById("userPoolName").value = selectedUserPoolName;
			  document.getElementById("userPoolRemark").value = selectedUserPoolRemark;

			}
		});
		$("#delUserPool").click(function() {
			if(selectedUserPoolId == null){
			  /*bootbox.alert("유저풀을 선택 해 주세요.", function(){
			  });*/
			  commonAlert("알림","유저풀을 선택 해 주세요.","유저풀이 선택되지 않음.");
			  return false;
			}else{
				commonConfirm("알림","삭제 하시겠습니까?",delUserPoolDatas,selectedUserPoolId);
			}
		});
		/**
		 * User form
		 */
		$("#addUser").click(function() {
			if(selectedUserPoolId == null){
			  /*bootbox.alert("유저풀을 선택 해 주세요.", function(){
			  });*/
				commonAlert("알림","유저풀을 선택 해 주세요.","유저풀이 선택되지 않음.");

			  return false;
			}else{
			  form_mode = 'put';
			  $(".myModalLabel").text("유저 등록");
			  $(".popUpDefaultBtn").text("등록");

			  document.getElementById("userId").readOnly = false;
			  document.getElementById("searchUserPoolId").value = selectedUserPoolId;
			  document.getElementById("userId").value = "";
			  document.getElementById("userPass").value = "";
//			  document.getElementById("userType").value = "";
			  document.getElementById("userName").value = "";
			  document.getElementById("compName").value = "";
			  document.getElementById("departName").value = "";
			  document.getElementById("jobTitle").value = "";
			  document.getElementById("userRemark").value = "";
			}
		});
		$("#editUser").click(function() {
			if(selectedUserId == null){
			  /*bootbox.alert("유저를을 선택 해 주세요.", function(){
			  });*/
				commonAlert("알림","유저를 선택 해 주세요.","유저가 선택되지 않음.");

			  return false;
			}else{
			  form_mode = 'set';			  
			  $(".myModalLabel").text("유저 수정");
			  $(".popUpDefaultBtn").text("수정");

			  selectedUserPoolId=html5SpecialCharDeCode(selectedUserPoolId);
			  selectedUserId=html5SpecialCharDeCode(selectedUserId);
			  selectedUserPassword=html5SpecialCharDeCode(selectedUserPassword);
			  selectedUserName=html5SpecialCharDeCode(selectedUserName);
			  selectedCompanyName=html5SpecialCharDeCode(selectedCompanyName);
			  selectedDepartName=html5SpecialCharDeCode(selectedDepartName);
			  selectedJobTitle=html5SpecialCharDeCode(selectedJobTitle);
			  selectedUserRemark=html5SpecialCharDeCode(selectedUserRemark);

			  document.getElementById("userId").readOnly = true;
			  document.getElementById("searchUserPoolId").value = selectedUserPoolId;
			  document.getElementById("userId").value = selectedUserId;
			  document.getElementById("userPass").value = selectedUserPassword;
//			  document.getElementById("userType").value = selectedUserType;
			  document.getElementById("userName").value = selectedUserName;
			  document.getElementById("compName").value = selectedCompanyName;
			  document.getElementById("departName").value = selectedDepartName;
			  document.getElementById("jobTitle").value = selectedJobTitle;
			  document.getElementById("userRemark").value = selectedUserRemark;

			}
		});
		$("#delUser").click(function() {
			if(selectedUserId == null){
			  /*bootbox.alert("유저를 선택 해 주세요.", function(){
			  });*/
			  commonAlert("알림","유저풀을 선택 해 주세요.","유저풀이 선택되지 않음.");

			  return false;
			}else{
				commonConfirm("알림","삭제 하시겠습니까?",delUserDatas,selectedUserId);
			}
		});
		/**
		 * User Filter form
		 */
		$("#addUserFilter").click(function() {
			if(selectedUserId == null){
			  /*bootbox.alert("유저풀을 선택 해 주세요.", function(){
			  });*/
				commonAlert("알림","유저를 선택 해 주세요.","유저가 선택되지 않음.");

			  return false;
			}else{
			  form_mode = 'put';
			  $(".myModalLabel").text("필터 등록");
			  $(".popUpDefaultBtn").text("등록");

			  document.getElementById("authorFilter").readOnly = false;
			  document.getElementById("userFilterUserId").value = selectedUserId;
			  document.getElementById("authorFilter").value = "";
			  document.getElementById("userFilterRemark").value = "";
			}
		});
		$("#editUserFilter").click(function() {
			if(selectedAuthorityFilter == null){
			  /*bootbox.alert("필터를 선택 해 주세요.", function(){
			  });*/
				commonAlert("알림","필터를 선택 해 주세요.","필터가 선택되지 않음.");

				
			  return false;
			}else{
			  form_mode = 'set';			  
			  $(".myModalLabel").text("필터 수정");
			  $(".popUpDefaultBtn").text("수정");

			  selectedUserId=html5SpecialCharDeCode(selectedUserId);
			  selectedAuthorityFilter=html5SpecialCharDeCode(selectedAuthorityFilter);
			  selectedUserPoolRemark=html5SpecialCharDeCode(selectedUserPoolRemark);

			  document.getElementById("authorFilter").readOnly = true;
			  document.getElementById("userFilterUserId").value = selectedUserId;
			  document.getElementById("authorFilter").value = selectedAuthorityFilter;
			  document.getElementById("userFilterRemark").value = selectedUserPoolRemark;

			}
		});
		$("#delUserFilter").click(function() {
			if(selectedAuthorityFilter == null){
			  /*bootbox.alert("필터를 선택 해 주세요.", function(){
			  });*/
				commonAlert("알림","필터를 선택 해 주세요.","필터가 선택되지 않음.");

			  return false;
			}else{
			
			 // delUserFilterDatas(selectedUserId,selectedAuthorityFilter);
			  commonConfirm("알림","삭제 하시겠습니까?",delUserFilterDatas,selectedUserId,selectedAuthorityFilter);

			}
		});

	});

	function userPoolCancleEvent(){
		
		$("#searchUserPoolId").val("");		
	}
	
	function refreshUserPoolList(){//장치풀목록 새로고침
		clearUserPoolData();
		setUserPoolData();
	}

	function refreshUserList(){//장치목록 새로고침
		if(selectedUserPoolId == null){
		  /*bootbox.alert("장치풀을 선택 해 주세요.", function(){
		  });*/
			commonAlert("알림","장치풀을 선택 해 주세요.","장치풀이 선택되지 않음.");

		  return false;
		}else{
		  clearUserData();
		  setUserData(selectedUserPoolId);
		}
	}

	function refreshUserFilterList(){//장치목록 새로고침
		if(selectedUserId == null){
		  /*bootbox.alert("장치풀을 선택 해 주세요.", function(){
		  });*/
		  commonAlert("알림","장치풀을 선택 해 주세요.","장치풀이 선택되지 않음.");

		  return false;
		}else{
		  clearUserFilterData();
		  setUserFilterData(selectedUserId);
		}
	}
	
	function showToolPanel(scope){
		if(!scope.gridOptions.api.isToolPanelShowing()){
			scope.gridOptions.api.showToolPanel(true);
			//scope.gridOptions.api.sizeColumnsToFit();
		}else{
			scope.gridOptions.api.showToolPanel(false);
			//scope.gridOptions.api.sizeColumnsToFit();
		}
	}

//	function showSearchUserPoolList(){
//		setSearchUserPoolData();
////		searchScope.gridOptions.api.sizeColumnsToFit();
////		setTimeScope(searchScope,searchUserPoolDataColumnDefs);
//	}

	function updateUserPool(mode){

		var nowDate = Date.now(); 
		nowDate = formatTime(nowDate);

		var upId = document.getElementById("userPoolId").value;
		var name = document.getElementById("userPoolName").value;
		var remark = document.getElementById("userPoolRemark").value;
		

		if (!upId){
			/*bootbox.alert("유저풀 ID를 입력 해 주세요.", function(){
			});*/
			commonAlert("알림","유저풀 ID를 입력 해 주세요.","유저풀 ID가 입력되지 않음.");

			return false;
		}
		if (!name){
			/* alert('장치풀 이름을 입력하세요'); */
			/*bootbox.alert("장치풀 이름을 입력 해주세요.", function(){
			});*/
			 commonAlert("알림","유저풀명을 입력 해 주세요.","유저풀명을 입력 하지않음.");
			return false;
		}
		
		if(uniqueIDValidateCheck(upId,"유저풀ID는 ")){
			return false;
		}
		
		upId=html5SpecialCharCode(upId);
		name=html5SpecialCharCode(name);
		remark=html5SpecialCharCode(remark);
		
		var userPoolObject = new Object();
		userPoolObject['user.pool.id'] = upId;
		userPoolObject['user.pool.name'] = name;
		userPoolObject['remark'] = remark;
		
		/*
		if (mode=='put'){
			userPoolObject['registration.date'] = nowDate;
		}else if (mode=='set'){
			userPoolObject['alter.date'] = nowDate;
		}
		*/

		var userPoolJson = JSON.stringify(userPoolObject,null,'\t');

		if (mode=='put'){
			putUserPoolDatas(upId,userPoolJson,'json');
		}else if (mode=='set'){
			setUserPoolDatas(upId,userPoolJson,'json');
		}
	}
	
	function updateUser(mode){

		var nowDate = Date.now();
		nowDate = formatTime(nowDate);

		var upId = document.getElementById("searchUserPoolId").value;
		var uId = document.getElementById("userId").value;
		var uPw = document.getElementById("userPass").value;
//		var uTp = document.getElementById("userType").value;
		var uNm = document.getElementById("userName").value;
		var coNm = document.getElementById("compName").value;
		var dpNm = document.getElementById("departName").value;
		var jTtl = document.getElementById("jobTitle").value;
		var uRmrk = document.getElementById("userRemark").value;

		if (!uId){
			/*bootbox.alert("유저 ID를 입력 해 주세요.", function(){
			});*/
			commonAlert("알림","유저 ID를 입력 해 주세요.","유저 ID가 입력되지 않음.");

			return false;
		}
		if (!uPw){
			/*bootbox.alert("유저 비밀번호를 입력 해 주세요.", function(){
			});*/
			commonAlert("알림","유저패스워드를 입력 해 주세요.","유저패스워드가 입력되지 않음.");

			return false;
		}	
		
		if (!upId){
			/*bootbox.alert("유저풀 ID를 입력 해 주세요.", function(){
			});*/
			commonAlert("알림","유저풀ID를 입력 해 주세요.","유저풀 ID가 입력되지 않음.");

			return false;
		}
		
		if(uniqueIDValidateCheck(uId,"유저ID는 ")){
			return false;
		}
		uId=html5SpecialCharCode(uId);
		upId=html5SpecialCharCode(upId);
		uPw	=html5SpecialCharCode(uPw);
		uNm=html5SpecialCharCode(uNm);
		coNm=html5SpecialCharCode(coNm);
		dpNm=html5SpecialCharCode(dpNm);
		jTtl=html5SpecialCharCode(jTtl);
		uRmrk=html5SpecialCharCode(uRmrk);


		var userObject = new Object();
		userObject['user.id'] = uId;
		userObject['user.pool.id'] = upId;
		userObject['user.password'] = uPw;
//		userObject['user.type'] = uTp;
		userObject['user.name'] = uNm;
		userObject['company.name'] = coNm;
		userObject['department.name'] = dpNm;
		userObject['job.title'] = jTtl;
		userObject['remark'] = uRmrk;
		
		/*
		if (mode=='put'){
			userObject['registration.date'] = nowDate;
		}else if (mode=='set'){
			userObject['alter.date'] = nowDate;
		}
		*/

		var userJson = JSON.stringify(userObject,null,'\t');

		if (mode=='put'){
			putUserDatas(uId,userJson,'json');
		}else if (mode=='set'){
			setUserDatas(uId,userJson,'json');
		}
	}

	function updateUserFilter(mode){

		var nowDate = Date.now();
		nowDate = formatTime(nowDate);

		var filter = document.getElementById("authorFilter").value;
		var remark = document.getElementById("userFilterRemark").value;

		if (!filter){
			/*bootbox.alert("권한필터를 입력 해 주세요.", function(){
			});*/
			commonAlert("알림","권한필터를 입력 해 주세요.","권한필터가 입력되지 않음.");

			return false;
		}			
		
		if(filterValidateCheck(filter)){
			return false;
		}
		
		selectedUserId=html5SpecialCharCode(selectedUserId);
		filter=html5SpecialCharCode(filter);
		remark	=html5SpecialCharCode(remark);

		var userFilterObject = new Object();
		userFilterObject['user.id'] = selectedUserId;
		userFilterObject['authority.filter'] = filter;
		userFilterObject['remark'] = remark;
		
		/*
		if (mode=='put'){
			userFilterObject['registration.date'] = nowDate;
		}else if (mode=='set'){
			userFilterObject['alter.date'] = nowDate;
		}
		*/

		var userFilterJson = JSON.stringify(userFilterObject,null,'\t');

		if (mode=='put'){
			putUserFilterDatas(selectedUserId,filter,userFilterJson,'json');
		}else if (mode=='set'){
			setUserFilterDatas(selectedUserId,filter,userFilterJson,'json');
		}
	}
