	
	$(document).ready(function(){
		$('#MySplitter').width(1200).height(850).split({orientation:'vertical', limit:270, position:'70%'});
		$('#sWrap').split({orientation:'horizontal', limit:0});
		$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
		scopeSynchronizeIntervalId=setInterval("viewSynchronize(adapterScope,attributeScope,functionScope)",200);				 
	});
		

/**
	 * 아답터 목록 그리드
	 */
	var adapterColumnDefs = [
		//{headerName: 'CK', width: 30, checkboxSelection: true, suppressSorting: true, suppressMenu: true},
		{headerName: "아답터ID", field: "adapterId", filter: 'text',width: 200},
		{headerName: "아답터명", field: "adapterName", filter: 'text',width: 150},
		{headerName: "아답터종류", field: "adapterKind",width: 80},
		{headerName: "아답터구분", field: "adapterType",width: 130},
		{headerName: "디폴트장치ID", field: "defaultDeviceId",filter: 'text',width: 130},
		{headerName: "세션타임아웃", field: "sessionTimeout",filter: 'text',width: 80}, 
		/*{headerName: "장치 초기상태", field: "initDeviceStatus",width: 80},*/
		{headerName: "아이피", field: "ip",filter: 'text',width: 80},
		{headerName: "포트", field: "port",filter: 'text',width: 80},
		{headerName: "위도", field: "lat", filter: 'text'},
	 	{headerName: "경도", field: "lon", filter: 'text'},
	 	{headerName: "셀프ID", field: "selfId", filter: 'text'},
	 	{headerName: "셀프암호", field: "selfPw", filter: 'text'},
	 	{headerName: "비고", field: "remark", filter: 'text'}
	];

	var attributeColumnDefs = [
		{headerName: "아답터ID", field: "id", filter: 'text', width: 150},
	    {headerName: "아답터 속성", field: "att", filter: 'text', width: 200}
	];
	
	
	var functionColumnDefs = [
	    {headerName: "아답터ID", field: "id", filter: 'text', width: 150},
	    {headerName: "아답터 기능", field: "adaptFunction", filter: 'text', width: 200}
	];

	
	/******************************* CONTROLLERS START ******************************/

	agGridModule.controller("adapterCtrl", function($scope) {
		
		adapterScope = $scope;

		$scope.gridOptions = {
			columnDefs: adapterColumnDefs,
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
		function rowSelectedFunc(event) { //Adapter List Click Event Function
			var aid = event.node.data.adapterId;
			selectedAdapterId = aid;

			clearFunctioneData();
			clearAttributeData();
						
			setAdapterFunctionData(aid);
		}
		
	});

	agGridModule.controller("attributeCtrl", function($scope) {

		attributeScope = $scope;

		$scope.gridOptions = {
			columnDefs: attributeColumnDefs,
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
		function rowSelectedFunc(event) { //Attribute List Click Event Function
			var sid = event.node.data.id;
			selectedSessionId = sid;
		}

	});
	
	agGridModule.controller("functionCtrl", function($scope) {

		functionScope = $scope;

		$scope.gridOptions = {
			columnDefs: functionColumnDefs,
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
		function rowSelectedFunc(event) { //Attribute List Click Event Function
			var sid = event.node.data.id;
			selectedSessionId = sid;
		}

	});
	
	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/

	function initGetData() {
		setAdapterData();
    }
	
	function cbAdtGetAllSucessFunc(evt){// 아답터 목록 조회 성공
		console.log('아답터 조회 성공');
		var rowDataByJson = parseAdapterJsonData(evt);
		loadData(adapterScope,rowDataByJson);
	}

	function cbAdtGetAllFailFunc(evt){// 아답터 목록 조회 실패
		console.log('아답터 조회 실패');
		clearAdapterData();
		commonErrorMessage(evt);
	}

	function cbAdtAttGetSucessFunc(evt){// 아답터 속성 목록 조회 성공
		console.log('아답터 속성 목록 성공');
		var rowDataByJson = parseAdapterAttributeJsonData(evt);
		loadData(attributeScope,rowDataByJson);
		$(".blankAdtAtt").text("");
	}

	function cbAdtAttGetFailFunc(evt){// 아답터 속성 목록 조회 실패
		console.log('아답터 속성 조회 실패');
		commonErrorMessage(evt,"blankAdtAtt");
	}
	
	function cbAdtFunctionGetSucessFunc(evt){// 아답터 기능 목록 조회 성공
		console.log('아답터 기능 목록 조회 성공');
		setAdapterAttData(selectedAdapterId);
		var rowDataByJson = parseAdapterFunctionJsonData(evt);
		
		console.log(rowDataByJson);
		loadData(functionScope,rowDataByJson);
		$(".blankAdtFunc").text("");
	}

	function cbAdtFunctionGetFailFunc(evt){// 아답터 기능 목록 조회 실패
		setAdapterAttData(selectedAdapterId);
		console.log('아답터 기능 조회 실패');
		commonErrorMessage(evt,"blankAdtFunc");	
	}

	/**
	 * 아답터 인스톨 성공시의 이벤트 함수
	 * @param evt 이벤트
	 */
	// TODO 구현중
	function cbAdtInstallSucessFunc(evt) {		
		$("#progressUpload").hide();
		
		customConfirm("알림","아답터 설치 또는 업데이트에 성공했습니다.",function(){
			$("#popUpAddAdapter").modal("hide");
		});
		
		clearAdapterData();
		setAdapterData();
	}
	
	/**
	 * 아답터 인스톨 실패시의 이벤트 함수
	 * @param evt 이벤트
	 */
	// TODO 구현중
	function cbAdtInstallFailFunc(evt) {
		console.log('아답터 인스톨 실패');
		$("#progressUpload").hide();
		commonErrorMessage(evt, null);
	}
	/**
	 * 아답터 언인스톨 성공시의 이벤트 함수
	 * @param evt
	 */
	// TODO 구현중
	function cbAdtUninstallSucessFunc(evt) {
		console.log('아답터 언인스톨 성공');
		clearAdapterData();
		setAdapterData();
	}
	
	/**
	 * 아답터 언인스톨 실패시의 이벤트 함수
	 * @param evt
	 */
	// TODO 구현중
	function cbAdtUninstallFailFunc(evt) {
		console.log('아답터 언인스톨 실패');
		commonErrorMessage(evt, null);
	}
	/******************************* API SCRIPTS END ******************************/
	
	/* Clear Datas */
	function clearAdapterData(){ //Clear datas to grid(adapter)
		selectedAdapterId = null;
		/*var emptyRow = [{ id: '', name: '', kind: '', type: '', deviceid: '', ip: '', status: '' }];*/
		var emptyRow = [];
		adapterScope.gridOptions.api.setRowData(emptyRow);
		clearAttributeData();
	}
	
	function clearFunctioneData(){ //Clear datas to grid(Function) 
		var emptyFunctionRow = [];
	/*	var emptyFunctionRow = [{ adaptFunction: '', id: '' }];*/
		functionScope.gridOptions.api.setRowData(emptyFunctionRow);
	}
	function clearAttributeData(){ //Clear datas to grid(attribute) 
		/*var emptyAttributeRow = [{ att: '', id: '' }];	*/
		var emptyAttributeRow = [];	
		attributeScope.gridOptions.api.setRowData(emptyAttributeRow);
	}

//	/* Init */
//	initAPI(); //Login and Get Data Progress
	
	
	
	/* 윤영진 추가 */
//	$(document).ready(function () {
//		$('#MySplitter').jqxSplitter({
//			width:'1200px',
//			height:'850px',
//			panels: [{size: '70%'}] 
//		});
//		
//		 $('#sWrap').jqxSplitter({ orientation: 'horizontal', width: "100%", height: "100%", 
//			panels : [{
//				size : 400,
//				collapsible : true
//			}]
//		});
//		 
//		$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
//	});
	
	$(document).ready(function(){
		$("#addAdapter").click(function() {
			
//			document.getElementById("txt").value = "";
//			document.getElementById("adapterFile").value = "";
			$(":file").filestyle('placeholder', 'File Name');
			$(":file").filestyle('clear');
//			document.getElementById("adapterFile").value = "";			
			document.getElementById("fileSize").value = "";		
		
		});
	});
		
	function refreshAdapterList(){//아답터목록 새로고침
		  clearAdapterData();
		  setAdapterData();
	}

	function refreshAttributeList(){//속성목록 새로고침
		if(selectedAdapterId == null){
		  commonAlert("알림", "아답터를 선택 해 주세요.", "아답터를 선택 해 주세요.");
		  return false;
		}else{
		  clearAttributeData();
		  setAdapterAttData(selectedAdapterId);
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
	
	/**
	 * 아답터 등록 onClick이벤트 함수
	 */
	function adapterInstall(){

		if($("#adapterFile").val()==""){
			commonAlert("알림", "파일을 추가해주세요", "파일 입력 실패");
		}else{
			var file = $("#adapterFile").prop("files")[0];		
			
			$("#progressUpload").show();			
			var reader = new FileReader();
		
			// 파일 읽기 완료 이벤트 함수 등록
			reader.onload = function(){
				otpAdt.install(file.name, file.size.toString(), btoa(reader.result), cbAdtInstallSucessFunc, cbAdtInstallFailFunc);
			}
			
			// 파일 읽기 실패 이벤트 함수 등록
			reader.onerror = function(evt){
				commonAlert("에러","아답터 파일 읽기에 실패 했습니다. code=" + evt.target.error.code, "파일 읽기 실패 Error Code=" + evt.target.error.code);
			}
			
			// 파일읽기 개시
			reader.readAsBinaryString(file, "UTF-8");		
		}
	}
	
	/**
	 * 아답터 삭제 onClick이벤트 함수
	 */
	function adapterUninstall(){
		
		if(selectedAdapterId==null){
			commonAlert("알림","아답터를 선택해주세요");
		}else{
			var deleteAdapter=function(){otpAdt.uninstall(selectedAdapterId, cbAdtUninstallSucessFunc, cbAdtUninstallFailFunc)};
			customConfirm("알림","아답터를 정말로 삭제하시겠습니까?",deleteAdapter,function(){})
		}
	}
	
	function fileUploadChangeListener(){		

		var file = $("#adapterFile").prop("files")[0];
		var fileSize=$("#fileSize");

		
		console.log("바뀜");
		if(file==undefined){
			fileSize.val("");
		}else{
			fileSize.val(file.size+" bytes");
		}
	}
	
	