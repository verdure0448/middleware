$(document).ready(function() {	
	scopeSynchronizeIntervalId=setInterval("viewSynchronize(devicePoolScope,deviceScope)",200);
});


//Grid Columns 
var devicePoolColumnDefs = [
	{headerName: "장치풀ID", field: "id", filter: 'text', width: 250},
	{headerName: "장치풀명", field: "devPoolNm", filter: 'text', width: 180},
	{headerName: "비고", field: "remark",filter: 'text', width: 150} ,
	{headerName: "변경일시", field: "altDate",filter: 'text', width: 100},
	{headerName: "등록일시", field: "regDate",filter: 'text', width: 100}
];

var deviceColumnDefs = [
	{headerName: "장치ID", field: "id", filter: 'text', width: 150},
	{headerName: "장치풀ID", field: "devPoolId", filter: 'text', width: 150},
	{headerName: "장치명", field: "devNm",filter: 'text', width: 100},
	/*{headerName: "사용여부", field: "isUse", width: 80},
	{headerName: "세션타임아웃", field: "sessTimeout", width: 80},*/
	{headerName: "아이피", field: "ip", filter: 'text', width: 80},
	{headerName: "포트", field: "port", filter: 'text', width: 80},
	{headerName: "위도", field: "lat",filter: 'text', width: 80},
	{headerName: "경도", field: "lng",filter: 'text', width: 80},
	{headerName: "비고", field: "remark",filter: 'text', width: 80},
	{headerName: "변경일시", field: "altDate",filter: 'text', width: 80},
	{headerName: "등록일시", field: "regDate",filter: 'text', width: 80}
];

//var searchDevicePoolDataColumnDefs = [
//	{headerName: "장치풀ID", field: "id",width:538}
//];

	/******************************* CONTROLLERS START ******************************/
	agGridModule.controller("devicePoolCtrl", function($scope) {

		devicePoolScope = $scope;

		$scope.gridOptions = {
			columnDefs: devicePoolColumnDefs,
			rowSelection: 'single',
			onRowSelected: rowSelectedFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true
		}
		
//		$scope.singleClick = function(event) {			
//			clearDeviceData();
//
//			var dpId = event.node.data.id;
//			var dpNm = event.node.data.devPoolNm;
//			var dpRmrk = event.node.data.remark;
//	
//			selectedDevicePoolId = dpId;
//			selectedDevicePoolName = dpNm;
//			selectedDevicePoolRemark = dpRmrk;
//	
//			setDeviceData(dpId);
//		}
		/* Normal Functions */
		function rowSelectedFunc(event) { //Session List Click Event Function
			clearDeviceData();

			var dpId = event.node.data.id;
			var dpNm = event.node.data.devPoolNm;
			var dpRmrk = event.node.data.remark;

			selectedDevicePoolId = dpId;
			selectedDevicePoolName = dpNm;
			selectedDevicePoolRemark = dpRmrk;

			setDeviceData(dpId);
		}
	});
	
//	agGridModule.directive('sglclick', ['$parse', function($parse) {
//	    return {
//	        restrict: 'A',
//	        link: function(scope, element, attr) {
//	          var fn = $parse(attr['sglclick']);
//	          var delay = 200, clicks = 0, timer = null;
//	          element.on('click', function (event) {
//	            clicks++;  //count clicks
//	            if(clicks === 1) {
//	              timer = setTimeout(function() {
//	                scope.$apply(function () {
//	                    fn(scope, { $event: event });
//	                }); 
//	                clicks = 0;             //after action performed, reset counter
//	              }, delay);
//	              } else {
//	                clearTimeout(timer);    //prevent single-click action
//	                clicks = 0;             //after action performed, reset counter
//	              }
//	          });
//	        }
//	    };
//	}]);

	agGridModule.controller("deviceCtrl", function($scope) {

		deviceScope = $scope;

		$scope.gridOptions = {
			columnDefs: deviceColumnDefs,
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
		function rowSelectedFunc(event) { //Instance List Click Event Function
			var dId = event.node.data.id;
			var dNm = event.node.data.devNm;
			var dIsUse = event.node.data.isUse;
			var dIp = event.node.data.ip;
			var dPort = event.node.data.port;
			var dLat = event.node.data.lat;
			var dLng = event.node.data.lng;
			var dRmrk = event.node.data.remark;
			var dSessionTimeout=event.node.data.sessTimeout;
			
			selectedDeviceId = dId;
			selectedDeviceName = dNm;
//			selectedDeviceIsUse = dIsUse;
			selectedDeviceIp = dIp;
			selectedDevicePort = dPort;
			selectedDeviceLat = dLat;
			selectedDeviceLng = dLng;
			selectedDeviceRemark = dRmrk;
			selectedDeviceSessionTimeout=dSessionTimeout;
		}

	});
	
//	agGridModule.controller("searchCtrl", function($scope) {
//
//		searchScope = $scope;
//
//		$scope.gridOptions = {
//			columnDefs: searchDevicePoolDataColumnDefs,
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
//			document.getElementById("searchDevicePoolId").value = selectedSearchId; 
//		}
//
//	});

	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/
	function initGetData() {
	  setDevicePoolData();
    }

	/* Call backs */
	
	function cbDevPoolGetAllSucessFunc(evt){// 장치풀 목록 조회 성공
		console.log('장치풀 조회 성공');
		var rowDataByJson = parseDevicePoolJsonData(evt);
		loadData(devicePoolScope,rowDataByJson);
		$(".blankDevDevicepool").text("");
	}

	function cbDevPoolGetAllFailFunc(evt){// 장치풀 목록 조회 실패
		console.log('장치풀 조회 실패');
		commonErrorMessage(evt,"blankDevDevicepool");
	}
	
	function cbDevSearchByDpidSucessFunc(evt){// 장치 목록 조회 성공
		console.log('장치 조회 성공');
		var rowDataByJson = parseDeviceJsonData(evt);
		loadData(deviceScope,rowDataByJson);
		$(".blankDevDevice").text("");
	}

	function cbDevSearchByDpidFailFunc(evt){// 장치 목록 조회 실패
		console.log('장치 조회 실패');
		commonErrorMessage(evt,"blankDevDevice");
	}

//	function cbSrchDevPoolGetAllSucessFunc(evt){// 장치풀 목록 조회 성공(검색)
//		console.log('장치풀 조회 성공');
//		var rowDataByJson = parseDevicePoolJsonData(evt);
//		loadData(searchScope,rowDataByJson);
//	}
//
//	function cbSrchDevPoolGetAllFailFunc(evt){// 장치풀 목록 조회 실패(검색)
//		console.log('장치풀 조회 실패');
//		commonErrorMessage(evt);
//	}

//	function cbSrchDevSearchByDpidSucessFunc(evt){// 장치 목록 조회 성공(검색)
//		console.log('장치 조회 성공');
//		var rowDataByJson = parseDeviceJsonData(evt); //장치데이터
//		loadData(searchDevScope,rowDataByJson);
//	}
//
//	function cbSrchDevSearchByDpidFailFunc(evt){// 장치 목록 조회 실패(검색)
//		console.log('장치 조회 실패');
//		commonErrorMessage(evt);
//	}

	/******************************* API SCRIPTS END ******************************/

	/* Clear Datas */
	
	function clearDevicePoolData(){ //Clear datas to grid(search device pool) 
		selectedDevicePoolId = null;
		selectedDevicePoolName = null;
		selectedDevicePoolRemark = null;

/*		var emptyAttributeRow = [{ id: '' }];*/	
		var emptyAttributeRow = [];
		devicePoolScope.gridOptions.api.setRowData(emptyAttributeRow);
		clearDeviceData();
	}

	function clearDeviceData(){ //Clear datas to grid(search device pool) 

		selectedDeviceId = null;
		selectedDeviceName = null;
//		selectedDeviceIsUse = null;
		selectedDeviceIp = null;
		selectedDevicePort = null;
		selectedDeviceLat = null;
		selectedDeviceLng = null;
		selectedDeviceRemark = null;

/*		var emptyAttributeRow = [{ id: '' }];	*/
		var emptyAttributeRow = [];	
		deviceScope.gridOptions.api.setRowData(emptyAttributeRow);
		$(".blankDevDevice").text("");
	}

//	/* Init */
//	initAPI(); //Login and Get Data Progress
	
	
	/* 윤영진 추가 */
	$(document).ready(function () {
	
		$('#MySplitter').width(1200).height(850).split({orientation:'vertical', limit:270, position:'50%'});	
		$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
	
	});

	
	var form_mode = null; //PUT SET 폼 모드

	$(document).ready(function() {
		
		//전체 화면에 있는 + 버튼을 눌렀을때  나오는 팝업메뉴의 화면 중 콤보박스 선택 스크립트
		$(".dropdown-menu li a").click(function(){
			
			 var selText = $(this).text();	 
			 $(this).parents('.btn-group').find('.dropdown-toggle').html(selText+'<span class="caret"></span>');
		});
		/**
		 * DevicePool form
		 */
		$("#addDevicePool").click(function() {

			  form_mode = 'put';
			  
			  $(".myModalLabel").text("장치풀 등록");
			  $(".popUpDefaultBtn").text("등록");

			  document.getElementById("devicePoolId").readOnly = false;
			  document.getElementById("devicePoolId").value = "";
			  document.getElementById("devicePoolName").value = "";
			  document.getElementById("devicePoolRemark").value = "";

		});
		$("#editDevicePool").click(function() {
			if(selectedDevicePoolId == null){
			  /* alert("장치풀을 선택 해 주세요"); */
			  /*bootbox.alert("장치풀을 선택 해 주세요.", function(){
			  });*/
			  commonAlert("알림","장치풀을 선택해 주세요.","장치풀선택하지않음.");
			  return false;
			}else{
			  form_mode = 'set';
			  $(".myModalLabel").text("장치풀 수정");
			  $(".popUpDefaultBtn").text("수정");

			  selectedDevicePoolId=html5SpecialCharDeCode(selectedDevicePoolId);
			  selectedDevicePoolName=html5SpecialCharDeCode(selectedDevicePoolName);
			  selectedDevicePoolRemark=html5SpecialCharDeCode(selectedDevicePoolRemark);

			  document.getElementById("devicePoolId").readOnly = true;
			  document.getElementById("devicePoolId").value = selectedDevicePoolId;
			  document.getElementById("devicePoolName").value = selectedDevicePoolName;
			  document.getElementById("devicePoolRemark").value = selectedDevicePoolRemark;

			}
		});
		$("#delDevicePool").click(function() {
			if(selectedDevicePoolId == null){
			  /* alert("장치풀을 선택 해 주세요"); */
			  /*bootbox.alert("장치풀을 선택 해 주세요.", function(){
			  });*/
			  commonAlert("알림","장치풀을 선택해 주세요.","장치풀선택하지않음.");
			  return false;
			}else{
				commonConfirm("알림","삭제 하시겠습니까?",delDevicePoolDatas,selectedDevicePoolId);
			}
		});
		
		/**
		 * Device form
		 */
		$("#addDevice").click(function() {
			if(selectedDevicePoolId == null){
				  /* alert("장치를 선택 해 주세요"); */
				  /*bootbox.alert("장치를 선택 해 주세요.", function(){
				  });*/
				  commonAlert("알림","장치풀를 선택해 주세요.","장치풀 선택하지않음.");
				  return false;
			}else{
			  form_mode = 'put';
			  $(".myModalLabel").text("장치 등록");
			  $(".popUpDefaultBtn").text("등록");

			  document.getElementById("deviceId").readOnly = false;
			  document.getElementById("searchDevicePoolId").value = selectedDevicePoolId;
			  document.getElementById("deviceId").value = "";
			  document.getElementById("deviceName").value = "";
			  $("#deviceIsuse").parents('.btn-group').find('.dropdown-toggle').html("Is use"+'<span class="caret"></span>');
			  document.getElementById("deviceIp").value = "";
			  document.getElementById("devicePort").value = "";
			  document.getElementById("deviceLatitude").value = "";
			  document.getElementById("deviceLongitude").value = "";
			  document.getElementById("deviceRemark").value = "";
//			  document.getElementById("deviceSessionTimeout").value = "";			  
			}
		});
		$("#editDevice").click(function() {
			if(selectedDeviceId == null){
			  /* alert("장치를 선택 해 주세요"); */
			  /*bootbox.alert("장치를 선택 해 주세요.", function(){
			  });*/
			  commonAlert("알림","장치를 선택해 주세요.","장치 선택하지않음.");
			  return false;
			}else{
			  form_mode = 'set';		

			  $(".myModalLabel").text("장치 수정");
			  $(".popUpDefaultBtn").text("수정");		

			  selectedDevicePoolId=html5SpecialCharDeCode(selectedDevicePoolId);
			  selectedDeviceId=html5SpecialCharDeCode(selectedDeviceId);
			  selectedDeviceName=html5SpecialCharDeCode(selectedDeviceName);
			  selectedDeviceIp=html5SpecialCharDeCode(selectedDeviceIp);
			  selectedDevicePort=html5SpecialCharDeCode(selectedDevicePort);
			  selectedDeviceLat=html5SpecialCharDeCode(selectedDeviceLat);
			  selectedDeviceLng=html5SpecialCharDeCode(selectedDeviceLng);
			  selectedDeviceRemark=html5SpecialCharDeCode(selectedDeviceRemark);
				
			  
			  document.getElementById("searchDevicePoolId").value = selectedDevicePoolId;
			  document.getElementById("deviceId").value = selectedDeviceId;
			  document.getElementById("deviceId").readOnly = true;
			  document.getElementById("deviceName").value = selectedDeviceName;
			  /*document.getElementById("deviceIsuse").value = selectedDeviceIsUse;*/
			  /*$("#deviceIsuse").parents('.btn-group').find('.dropdown-toggle').html(selectedDeviceIsUse+'<span class="caret"></span>');*/
			  document.getElementById("deviceIp").value = selectedDeviceIp;
			  document.getElementById("devicePort").value = selectedDevicePort;
			  document.getElementById("deviceLatitude").value = selectedDeviceLat;
			  document.getElementById("deviceLongitude").value = selectedDeviceLng;
			  document.getElementById("deviceRemark").value = selectedDeviceRemark;
//			  document.getElementById("deviceSessionTimeout").value = selectedDeviceSessionTimeout;

			}
		});
		$("#delDevice").click(function() {
			if(selectedDeviceId == null){
			  /* alert("장치를 선택 해 주세요"); */
			  /*bootbox.alert("장치를 선택 해 주세요.", function(){
			  });*/
			  commonAlert("알림","장치를 선택해 주세요.","장치를 선택하지않음.");
			  return false;
			}else{
				commonConfirm("알림","삭제 하시겠습니까?",delDeviceDatas,selectedDeviceId);
			}
		});

	});
	
	function refreshDevicePoolList(){//장치풀목록 새로고침
		clearDevicePoolData();
		setDevicePoolData();
	}

	function refreshDeviceList(){//장치목록 새로고침
		if(selectedDevicePoolId == null){
		  /* alert("장치풀을 선택 해 주세요"); */
		  /*bootbox.alert("장치풀을 선택 해 주세요.", function(){
		  });*/
		  commonAlert("알림","장치풀을 선택해 주세요.","장치풀선택하지않음.");
		  return false;
		}else{
		  clearDeviceData();
		  setDeviceData(selectedDevicePoolId);
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
	
	function devicePoolCancleEvent(){
		
		$("#searchDevicePoolId").val("");
	}
	
//	function showSearchDeviceList(){
//		setSearchDevicePoolData();
//		
////		setTimeScope(searchScope,searchDevicePoolDataColumnDefs);
//	}

	function updateDevicePool(mode){

		var nowDate = Date.now();
		nowDate = formatTime(nowDate);

		var dpId = document.getElementById("devicePoolId").value;
		var name = document.getElementById("devicePoolName").value;
		var remark = document.getElementById("devicePoolRemark").value;

		if (!dpId){
			/* alert('장치풀 ID를 입력하세요'); */
			/*bootbox.alert("장치풀 ID를 입력 해주세요.", function(){
			});*/
			 commonAlert("알림","장치풀ID를 입력 해 주세요.","장치풀 ID를 입력하지않음.");
			return false;
		}
		if (!name){
			/* alert('장치풀 이름을 입력하세요'); */
			/*bootbox.alert("장치풀 이름을 입력 해주세요.", function(){
			});*/
			 commonAlert("알림","장치풀명을 입력 해 주세요.","장치풀명을 입력 하지않음.");
			return false;
		}
		if(uniqueIDValidateCheck(dpId,"장치풀ID")){
			return false;
		}
		

		dpId=html5SpecialCharCode(dpId);
		name=html5SpecialCharCode(name);
		remark=html5SpecialCharCode(remark);
		
		var devicePoolObject = new Object();
		devicePoolObject['device.pool.id'] = dpId;
		devicePoolObject['device.pool.name'] = name;
		devicePoolObject['remark'] = remark;

		if (mode=='put'){
			devicePoolObject['registration.date'] = nowDate;
		}else if (mode=='set'){
			devicePoolObject['alter.date'] = nowDate;
		}

		var devicePoolJson = JSON.stringify(devicePoolObject,null,'\t');

		if (mode=='put'){
			putDevicePoolDatas(dpId,devicePoolJson,'json');
		}else if (mode=='set'){
			setDevicePoolDatas(dpId,devicePoolJson,'json');
		}

		
	}

	function updateDevice(mode){

		var nowDate = Date.now();
		nowDate = formatTime(nowDate);
		
		var dpId = document.getElementById("searchDevicePoolId").value;
		var dId = document.getElementById("deviceId").value;
		var name = document.getElementById("deviceName").value;
		var isUse = $("#deviceIsuse").text();	
		var ip = document.getElementById("deviceIp").value;
		var port = document.getElementById("devicePort").value;
		var lat = document.getElementById("deviceLatitude").value;
		var lng = document.getElementById("deviceLongitude").value;
		var remark = document.getElementById("deviceRemark").value;
//		var sessionTimeout = document.getElementById("deviceSessionTimeout").value;
		
		
		/*if(sessionTimeout==""){
			sessionTimeout="0";
		}*/
		
//		if(isUse=="none" || isUse=="Is use"){
//			isUse="";
//		}
		
		dpId=html5SpecialCharCode(dpId);
		dId=html5SpecialCharCode(dId);
		name=html5SpecialCharCode(name);
		isUse=html5SpecialCharCode(isUse);
		ip=html5SpecialCharCode(ip);
		port=html5SpecialCharCode(port);
		lat=html5SpecialCharCode(lat);
		lng=html5SpecialCharCode(lng);
		remark=html5SpecialCharCode(remark);
	
		
		if (!dId){
			/* alert('장치 ID를 입력하세요'); */
			/*bootbox.alert("장치 ID를 입력 해주세요.", function(){
			});*/
			commonAlert("알림","장치ID를 입력해 주세요.","장치ID 입력하지않음.");
			return false;
		}
		if (!dpId){
			/* alert('장치 이름을 입력하세요');	 */
			/*bootbox.alert("장치 이름을 입력 해주세요.", function(){
			});*/
			commonAlert("알림","장치풀ID를 입력 해 주세요.","장치풀ID를 입력 하지않음.");
			return false;
		}		
		
		if(uniqueIDValidateCheck(dId,"장치ID는 ")){
			return false;
		}
		
		if(ipValidateCheck(ip)){
			return false;
		}

		if(portValidateCheck(port)){
			return false;
		}
		if(longitudeValidateCheck(lng)){
			return false;
		}
		if(latitudeValidateCheck(lat)){
			return false;
		}


				
		var deviceObject = new Object();
		deviceObject['device.pool.id'] =dpId;
		deviceObject['device.id'] = dId;
		deviceObject['device.name'] = name;
		deviceObject['is.use'] = isUse;
		deviceObject['ip'] = ip;
		deviceObject['port'] = port;
		deviceObject['latitude'] = lat;
		deviceObject['longitude'] = lng;
		deviceObject['remark'] = remark;
//		deviceObject['session.timeout'] = sessionTimeout;

		
		if (mode=='put'){
			deviceObject['registration.date'] = nowDate;
		}else if (mode=='set'){
			deviceObject['alter.date'] = nowDate;
		}
		var deviceJson = JSON.stringify(deviceObject,null,'\t');
		if (mode=='put'){
			putDeviceDatas(dId,deviceJson,'json');
		}else if (mode=='set'){
			setDeviceDatas(dId,deviceJson,'json');
		}		

			
	}
