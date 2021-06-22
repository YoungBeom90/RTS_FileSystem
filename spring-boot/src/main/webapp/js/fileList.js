let totalFileSize = 0; // 업로드 총사이즈
let uploadSize = 50 * 1024 * 1024; // 단일 업로드 사이즈
let maxUploadSize = 500; // 최대 업로드 사이즈
let globalData; // 선택된 노드 파일 리스트
let selectParentPath;// 선택한 노드의 부모 경로
let fileNameInput; // 선택된 체크박스의 파일 이름
let allFilePath; // 백엔드에서 출력된 노드의 리스트 부모 경로
let globalSelectFolder; // 선택된 노드;

//로딩시작
const loadingStart = () => {
	setTimeout(() => {
		$("body").css("zIndex", "1000");
		$("body").css("opacity", "0.5");
		$("body").css("background", "no-repeat url('/images/loading.gif')");
		$("body").css("background-position", "center center");
		$("body").css("width", "100%");
	}, 0);
}
// 로딩종료
const loadingEnd = () => {
	$("body").css("zIndex", "0");
	$("body").css("opacity", "1");
	$("body").removeAttr("style");
}

$(document).ready(function() {
	
	
	tree_Common.init(); // 트리 초기렌더링 시작
	tree_Common.loadedTree(); // 트리 렌더링 이후 이벤트
	tree_Common.selectNode(); // 선택 된 노드 파일 리스트 생성
	tree_Common.doubleClick(); // 트리 더블클릭 이벤트
	
	// 파일 드롭다운 기능 활성화
	fileDropDown(); 
	
	// 폴더 생성기능 활성화 
	let btn = document.getElementById("createFolderBtn");
	createFolder(btn); 
	
	
	// 삭제 버튼 클릭 이벤트
	$("#deleteBtn").on("click", function() {	
		let checkBox = $(".checkBox");
		let filePath = $("#filePath").val(); //삭제할 폴더 경로
		let checkList = new Array;
		let checkFlag = false;
		console.log(filePath);
		
		
		for(let i=0; i<checkBox.length; i++) {
			if(checkBox[i].checked) {
				checkList = checkBox[i];
				checkFlag = true;
			}
		}		
		
		if(checkFlag) {
			Swal.fire({
				title: '파일을 삭제하시겠습니까?',
				text: "삭제하시면 다시 복구시킬 수 없습니다.",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: '삭제',
				cancelButtonText: '취소'
			}).then((result) => {
				if (result.value) {
					deleteFile(filePath).then((res) => {
						reqCnt = res;
						Swal.fire({
							title: reqCnt + "개 파일을 삭제하였습니다.",
							icon: "success",
							confirmButtonColor: '#3085d6',
							confirmButtonText: "확인"				
						}).then(() => {
							$(".jstree-clicked").trigger("click");
							tree_Common.treeRefresh();
						});
					});
				}
			});
		} else {
			Swal.fire({
				title: "파일을 선택해주세요.",
				icon: "info",
				confirmButtonColor: '#3085d6',
				confirmButtonText: "확인"				
			});
		}
		
		
		
	});
	
	// 파일 삭제
		async function deleteFile(filePath) {
			let checkBox = $(".checkBox");
			let fileNameList = $(".fileName");
			let fileExtList = $(".fileExt");
			let fileName;
			let fileExt;
			let reqCnt = 0;
			let fileIdx = 0;
			console.log(filePath);
			for await(let target of checkBox) {
				
				if(target.checked) {
					fileName = fileNameList[fileIdx].innerText; //파일명 가져오기 
					fileExt = fileExtList[fileIdx].innerText;//확장자명 가져오기
					await axios.post("/axios/deleteFile", null, {params : {
						'parent': filePath,
						'fileName' : fileName.trim(),
						'fileExt' : fileExt.trim()
					}}).then((res) => {
						console.log(res);
			        	if(res.data === "삭제 완료") {
							reqCnt++;
			        	}
			    	}).catch(function(error) {
			        	console.log(error);
			    	});
				}
				fileIdx++;
			}
			return reqCnt;
		}
	
	//업로드 모달창 저장 클릭 이벤트
	$("#modalSubmit").on("click", function() {
		if($("#modalParentPath").val() === "") {
			
		}
		
		let files = document.getElementById("modalUpload").files;
		doUpload(files);
		
	});
	
	// 다운로드 버튼 클릭 이벤트
	$("#donwloadBtn").on("click", function() {	
		let checkBox = $(".checkBox");
		let filePath = $("#filePath").val(); //다운받을 폴더 경로
		let checkList = new Array;
		let checkFlag = false;
		console.log(filePath);
		
		for(let i=0; i<checkBox.length; i++) {
			if(checkBox[i].checked) {
				checkList = checkBox[i];
				checkFlag = true;
			}
		}	
		
		downloadFile(filePath);
		
	});//파일다운로드 이벤트 end
	
	
	
	$(document).dblclick((e) => {
		let selectFolder = e.target.innerText.trim();
		let clicked = document.querySelector(".jstree-clicked");
		
		if(clicked===null) {
			if(confirm("트리에서 해당 폴더를 찾지 못했습니다.\n 모든 트리를 확장시킵니다.")) {
				$("#jstree").jstree("open_all");
			}
			return;
		}
		
		let isStatus = clicked.parentElement.getAttribute('aria-expanded');
		if(isStatus === "false") {
			clicked.previousSibling.click();
		}
		
		let el = clicked.nextSibling.childNodes;
		for(let i in el) {
			if(el[i].id){
				let substrIdx = el[i].id.lastIndexOf("\\")
				let childList = el[i].id.substr(substrIdx);
				childList = childList.replaceAll("\\", "");
				if(selectFolder === childList) {
					el[i].childNodes[1].click();
					return;
				}
			}
		}
		
	})

	//파일 다운로드
	async function downloadFile(filePath) {
		let checkBox = $(".checkBox");
		let fileNameList = $(".fileName");
		let fileExtList = $(".fileExt");
		let fileName;
		let fileExt;
		let encodeUri;
		let reqCnt = 0;
		let fileIdx = 0;
		let fileInfo = [];
		for await (let target of checkBox) {
			
			if(target.checked) {
				fileName = fileNameList[fileIdx].innerText; //파일명 가져오기 
				fileExt = fileExtList[fileIdx].innerText;//확장자명 가져오기
				fileInfo.push(fileName.trim()+fileExt.trim());
			}//if end
			fileIdx++;
		}//for end
					
		await axios.get("/axios/downloadFile", {
				params : {
					'parent' : filePath,	
					'fileName' : encodeURI(fileInfo)
				},
			    paramsSerializer : function(params){
			      return jQuery.param(params)
			    }}).then((res) => {
					console.log(res);
					encodeUri = encodeURI(filePath);
					for(let i = 0; i<res.config.params.fileName.split(",").length;i++){
						console.log(res.config.params.fileName.split(",").length);

							if(res.config.params.fileName.split(",")[i].lastIndexOf(".")!=-1){
								if(res.config.params.fileName.split(",").length==1) {
									window.location =`/axios/downloadFile?fileName=${res.config.params.fileName.split(",")[i]}&parent=${encodeUri}`
								} else {
									window.location = `/axios/downloadFile?fileName=${res.config.params.fileName.split(",")[0].substring(0,res.config.params.fileName.split(",")[0].lastIndexOf("."))+".zip"}&parent=${encodeUri}`
								}//if~else end
								
							reqCnt++;
							}//if end
						console.log("다운")
					}//for 
				}).catch( function(error) {
					console.log(error);
			})//axios end
		
		return reqCnt;
	}//파일 다운로드 end
	
	$('#searchText').on("keyup", function(){
		const searchData = $("#searchText").val();
		$.ajax({
			url : "/ajax/searchFile",
			data : {"fileName" : searchData},
			dataType : "json",
			success : function(json){
				console.log(json);
				if(json.length!=0) {
					for(let i = 0; i<json.length;i++){
					//console.log(json[0].fpath);
					//addFileList(json[i].fname, json[i].fext, json[i].fdate, json[i].fsize);
					}
				}else{
					//alert("검색결과가 없습니다.");
				}//if~else end
			},
			error : function(xhr, error){
				console.log(error)
			}
			
		})
	})// searchFile end
	
	$("#searchBtn").on("click", function() {
		console.log("clicked : " + $("#searchText").val());
		let name = $("#searchText").val();
		console.log(name);
		console.log(selectParentPath);
/*
		let json = {
			fileName: name
		}
		console.log(json);
*/		
		$.ajax({
			url: '/ajax/searchFile',
			async: true,
			type: 'post',
			data: {"fileName" : name,
			"fpath" : selectParentPath},
			dataType: 'json',
//			contentType: "application/json; charset=UTF-8",  
			success: function(json) {
				console.log(json);
				if(json.length!=0) {
					$(".fileList").empty();
					for(let i = 0; i<json.length;i++){
					let fileName = json[i].fname;
					let fileSize = json[i].fsize;
					let ext = json[i].fext;
					let mdfDate = json[i].fdate;
					console.log(json[i].fpath);
					addFileList(fileName, fileSize, ext, mdfDate);
					}
					//downloadFile(json[i].fpath);
				}else{
					alert("검색결과가 없습니다.");
				}//if~else end
			},
			error: function(err) {
				console.log(err);
			}
		})
	})
	
	
});//$(document).ready 종료



function checkAll() {
	let trigger = $("#allCheck");
	let chkbox = $(".checkBox");
	if(trigger[0].checked) {
		for(let i=0; i<chkbox.length; i++) {
			chkbox[i].checked = true;
			chkbox.eq(i).parent().parent().attr("style", "background-color: #2257d4;");
		}
	} else if(trigger[0].checked === false){
		for(let i=0; i<chkbox.length; i++) {
			chkbox[i].checked = false;
			chkbox.eq(i).parent().parent().removeAttr("style");
		}
	}
}

// 폴더 추가시 실행
async function addFolderListener(parent, child) {
	$("#"+ child).on("focusout", function() {		
		let addFolderNm = $("#"+ child).children().children().eq(1).val();
		let addFolderPrt = parent.id.substring(3);
		/*renameFolderListener(child);*/
		axiosCreateFolder(addFolderNm, addFolderPrt);
	});
}

// 폴더 이름 수정.
function renameFolderListener(obj) {
	let target = obj.node.id;
	let preNm = obj.old;
	let afterNm = obj.text;
	target = target.substr(3);
	console.log("나는 obj");
	console.log(obj);
	if(obj) {
		axios.post("/axios/renameFolder", null, {params: {
			path : target,
			value : preNm,
			rename : afterNm
		}}).then((res) => {
			if(res) {				
				Swal.fire({
					title: "이름 수정 완료",
					icon: "success",
					confirmButtonColor: '#3085d6',
					confirmButtonText: "확인"
				});
			}
		});
	}
}

// 선택된 폴더에 대한 자식요소 리스트 불러오기
async function selectList(firstDir) {
	loadingStart();
	console.log(firstDir);
	await $.ajax({
		type : 'post',
		url : '/ajax/selectFileList',
		dataType : 'json',
		data : "isDir=" + encodeURIComponent(firstDir),
		success : function(res) {
			if(res) {
				let data = res.filePath;
				console.log(data);
				globalData = data;
				selectParentPath=firstDir;
				$(".fileList > tr").remove();
				for(idx in data) {
					let fileName = data[idx].fname;
					let ext = data[idx].fext;
					if(ext===undefined) ext = "폴더";
					let fileSize = data[idx].fsize / 1024 / 1024;
					fileSize = fileSize.toFixed(3);
					let date= new Date(data[idx].fdate);
					let year = date.getFullYear();
					let month = date.getMonth()+1>=10?date.getMonth():"0"+date.getMonth();
					let day = date.getDate()>=10?date.getDate():"0"+date.getDate();
					let hour = date.getHours()>=10?date.getHours():"0"+date.getHours();
					let minutes = date.getMinutes()>=10?date.getMinutes():"0"+date.getMinutes();
					let seconds = date.getSeconds()>=10?date.getSeconds():"0"+date.getSeconds();
					let mdfDate = year+"-"+month+"-"+day+" "+hour+":"+minutes+":"+seconds;
					let filePath = data[idx].fpath;
					
					let lastIdx = filePath.lastIndexOf("\\");
					filePath = filePath.substr(0, lastIdx);
					let fullPath = filePath;
					addFileList(fileName, fileSize, ext, mdfDate, filePath, fullPath);	
					
				}
				if($(".fileList").children().length === 0) {
					let html = "<tr>";
						html += "<td colspan='6' style='text-align: center;'>이 폴더는 비어있습니다.</td>";
						html += "</tr>";
						
					$(".fileList").append(html);
				}//if end
			}
		}
	});
}


	
// 파일 드롭다운 
function fileDropDown() {
    let dropZone = $(".dropZone");
	// 드랍하여 드랍존으로 옮겼을 경우
    dropZone.on('dragover',function(e){
        e.preventDefault();
        dropZone.css({
			'background-color':'#dbccff',
			'opacity':'0.8'
    	});
	});
	// 드랍후 마우스를 드랍존에서 벗어날 경우
    dropZone.on("dragleave", function(e) {
        e.preventDefault();
        dropZone.css({
            "border" : "none",
            "background-color" : "rgb(227,242,253)",
			'opacity':'1'
        });
    });
	// 드랍 후 
    dropZone.on("drop", function(e) {
        e.preventDefault();
        dropZone.css({
            "background-color" : "rgb(227,242,253)",
			'opacity':'1'
        });
		let files = e.originalEvent.dataTransfer.files;
		doUpload(files);
/*		
        if(files != null) {
            
			let form = new FormData();
			
			for(let i=0; i<files.length; i++){
				form.append('file', files[i]);
				form.append('fdate', files[i].lastModified);
			}
			form.append('parent', selectParentPath);
			
			let dupCheck = false;
			
			for(let i=0; i<files.length; i++) {
				
				for(let j=0; j<globalData.length; j++) {
					if(files[i].name === globalData[j].text) {
						dupCheck = true;
						break;	
					} 
					
				}
				
			}
			
			if(dupCheck) {
				Swal.fire({
					title: "이미 저장되어 있는 파일 이름이 존재합니다.",
					text: "덮어 씌우겠습니까?",
					icon: 'warning',
					showCancelButton: true,
					confirmButtonColor: '#3085d6',
					cancelButtonColor: '#d33',
					confirmButtonText: '덮어쓰기',
					cancelButtonText: '취소'
				}).then((res) => {
					if(res.value) {
						selectFile(files)
						
						$.ajax({
							url: "/ajax/uploadFile.json",
							type : "post",
							enctype : "multipart/form-data",
							processData :false,
							contentType :false,
							data:form,
							timeout:50000,
							success : function(data){
								$(".jstree-clicked").trigger("click");
							},
							error : function(err){
								console.log(err);
								$(".jstree-clicked").trigger("click");
							}
						});
					} 
				});
			} else {
				selectFile(files)
					
				$.ajax({
					url: "/ajax/uploadFile.json",
					type : "post",
					enctype : "multipart/form-data",
					processData :false,
					contentType :false,
					data:form,
					timeout:50000,
					success : function(data){
						console.log(data);
						console.log("성공");
						$(".jstree-clicked").trigger("click");
					},
					error : function(err){
						console.log(err);
						console.log("에러");
						$(".jstree-clicked").trigger("click");
					}
				});
			}
        } else {
            alert("Error");
        }

*/
    });
}

// 드롭다운 파일 가공 후 리스트 추가
function selectFile(files) {
		
    if(files) {
		
        for(let i = 0; i < files.length; i++) {
			console.log(files[i]);
            let fileName = files[i].name;
            let fileNameArr = fileName.split("\.");
            let ext = fileNameArr[fileNameArr.length - 1];
            let fileSize = files[i].size / 1024 / 1024; // MB
			console.log(fileNameArr);
			console.log("ext:" +ext);
			
            if($.inArray(ext, ['exe', 'bat', 'lnk']) >= 0) {
                Swal.fire({
					title: "등록불가 확장자 입니다.",
					icon: "warning",
					confirmButtonColor: '#3085d6',
					confirmButtonText: "확인"
				})
                return -1;
            } else if(fileSize > uploadSize) {
				Swal.fire({
					title: "용량 초과!\n(업로드 가능용량: " + uploadSize + "MB",
					icon: "warning",
					confirmButtonColor: '#3085d6',
					confirmButtonText: "확인"
				});
                return -1;
            } else if(fileNameArr.length <= 1) {
				Swal.fire({
					title: "폴더를 업로드 할 수 없습니다.",
					icon: "warning",
					confirmButtonColor: '#3085d6',
					confirmButtonText: "확인"
				});
				return -1;
            } 
			console.log(i);
			totalFileSize += fileSize;
            fileSize = fileSize.toFixed(3);
			// client화면에 드롭된 파일리스트 추가
            addFileList(fileName, fileSize, ext);

			return 1;
        }
    }
}

// client화면 파일리스트 조회, 추가 
function addFileList(fileName, fileSize, ext, mdfDate, filePath, fullPath) {

	let udTime = new Date();
	let year = udTime.getFullYear();
	let month = udTime.getMonth() + 1;
	let day = udTime.getDate();
	let hour  = udTime.getHours();
	let minute = udTime.getMinutes();
	let seconds = udTime.getSeconds();
	let fileDate = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds;
	
	if(mdfDate) {
		fileDate = mdfDate;
	}
	 
	if(hour < 10 || minute < 10 || seconds < 10) {
	    seconds = "0" + seconds;
	}
	
	let html = "";
	html += "<tr id='fileTr_" + fileName + "' class='fileTr'>";
	html += "<td>";
	html += "<input type='checkbox' class='checkBox'/>";
	html += "</td>";
	
	if(ext === '폴더') {
		html += "<td class='fileName' style='cursor:pointer;'>";
	} else {
		html += "<td class='fileName'>";
	}
	if(ext) {
		switch(ext) {
			case "폴더" :
				html += "<i class='far fa-folder''></i>";
				break;
			case "xls" || "xlsx" : 
				html += "<i class='far fa-file-excel'></i>";
				break;
			case "jpg" || "gif" || "png" || "bmp" :
				html += "<i class='far fa-file-image'></i>";
				break;
			case "zip" || "7z" || "war" || "jar" || "tar" || "iso" :
				html += "<i class='far fa-file-archive'></i>";
				break;
			case "mp3" || "mp4" || "avi" || "wma" :
				html += "<i class='far fa-file-video'></i>";
				break;
			default : 
				html += "<i class='far fa-file'></i>";
				break;
		}
	}
	html += "<pre style='display: inline;'>&nbsp;&nbsp;&nbsp;" + fileName + "</pre></td>";
	html += "<td class='fileExt'>" + ext + "</td>"; 
	html += "<td class='udTime'>" + fileDate + "</td>";
	if(ext!='폴더'){
		html += "<td class='fileSize'>" + fileSize + "MB</td>";
	}else{
		html += "<td class='fileSize'></td>";
	}
	html += "<td class='filePath''>";
	html += fullPath + "</td>";
	html += "<td class='fileAuth'>admin</td>";
	html += "</tr>";
	
	$('.fileList').append(html);
	
	
}

// 폴더 생성
function createFolder(btn) {
    btn.addEventListener("click", function() {
		let newFolder = $("#fileNameInput").html();
		let prtPath = $("#filePath").val();
	
		if(prtPath === undefined) {
			Swal.fire({
				title: "상위 폴더를 선택하세요.",
				icon: "info",
				confirmButtonColor: '#3085d6',
				confirmButtonText: "확인"
			})
			return;
		}
		
		if(newFolder === "") {
			return;
		}
		
		let lastNode = $(".fileList > tr:last");
		if(lastNode.length == 0) {
			lastNode = $(".fileList");
		} 
		
		if(lastNode[0].innerText.includes("비어있습니다.")) {
			lastNode.hide();
		}
		
		let addTr =  "<tr class='fileTr'>";
		addTr += "<td class='checkBox'>";
		addTr += "</td>";
		addTr += "<td class='fileName'>";
		addTr += "<input type='text' id='fileNameInput' class='folderInput form-control form-control-sm' value='새 폴더' onsubmit='return false' />";
		addTr += "<input type='button' id='fileNmSubmit' class='btn btn-success btn-sm' value='저장' />";
		addTr += "<input type='button' class='btn btn-danger btn-sm' value='취소' onclick='createCancel();'/>";
		addTr += "</td>";
		addTr += "<td class='fileSize'></td>";
		addTr += "<td class='fileExt'></td>"; 
		addTr += "<td class='udTime'></td>";
		addTr += "<td class='fileAuth'>admin</td>";
		addTr += "</tr>";
		
		lastNode.after(addTr);
		fileNameInput = $("#fileNameInput");
		$(".dropZone").scrollTop($(".dropZone")[0].scrollHeight);
		$("#fileNameInput").focus();
    });

	// 새폴더 이름수정 후 이벤트
	$(document).on("click", "#fileNmSubmit", function() {
		axiosCreateFolder($("#fileNameInput").val(), $("#filePath").val());
	});
	
}

// 폴더생성 취소
function createCancel() {
	$(".fileList tr:last-child").remove();
}

// 폴더생성 aiox 호출
function axiosCreateFolder(fldNm, fldPrt) {
	let oriPath = $("#filePath").val();
		console.log("oriPath : "+oriPath);
		console.log("fldPath : "+fldPrt);
	
	if(oriPath === fldPrt) {
		axios.post("/axios/createFolder", null, {params : {
			value : fldNm,
			path : fldPrt
		}}).then(function(res) {
	        if(res) {
				if(res.data === -1) {
					Swal.fire({
						title: "폴더경로를 읽어오지 못했습니다. 폴더 클릭 후 재시도 해주세요.",
						icon: "error",
						confirmButtonColor: '#3085d6',
						confirmButtonText: "확인"
					}).then((res) => {
						return;
					});
				} else if(res.data === 1){
					Swal.fire({
						title: "폴더 생성 완료.",
						icon: "success",
						confirmButtonColor: '#3085d6',
						confirmButtonText: "확인"
					}).then((res) => {
						if(res.value) {
							tree_Common.treeRefresh();
						}
					});
				} else {
					Swal.fire({
						title: "동일한 폴더 이름이 존재합니다.",
						icon: "warning",
						confirmButtonColor: '#3085d6',
						confirmButtonText: "확인"
					});
				}
	        }
	    });
	}
	
}

// 폴더 이름수정
function renameFolder(obj) {
	$("#")
}

// 업로드 모달창
function modalPopup() {
	console.log(globalSelectFolder);
	$('#uploadModal').modal();
	console.log($("#modalParentPath"));
	$("#modalParentPath").val(globalSelectFolder);

	let agent = navigator.userAgent.toLowerCase();
	if ( (navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1) ){
	    // ie 일때 input[type=file] init.
	    $("#modalUpload").replaceWith( $("#modalUpload").clone(true) );
	} else {
	    //other browser 일때 input[type=file] init.
	    $("#modalUpload").val("");
	}
}

function searchModalPopup() {
	console.log(`searchModal On`);
	$("#searchModal").modal();
	
	let agent = navigator.userAgent.toLowerCase();
	if ( (navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1) ){
	    // ie 일때 input[type=file] init.
	    $("#searchModal").replaceWith( $("#searchModal").clone(true) );
	} else {
	    //other browser 일때 input[type=file] init.
	    $("#searchModal").val("");
	}
	
}

function doUpload(files){
	if(files != null) {
            
		let form = new FormData();
		
		for(let i=0; i<files.length; i++){
			form.append('file', files[i]);
			form.append('fdate', files[i].lastModified);
		}
		form.append('parent', selectParentPath);
		
		let dupCheck = false;
		
		for(let i=0; i<files.length; i++) {
			
			for(let j=0; j<globalData.length; j++) {
				if(files[i].name === globalData[j].text) {
					dupCheck = true;
					break;	
				} 
				
			}
			
		}
		
		if(dupCheck) {
			Swal.fire({
				title: "이미 저장되어 있는 파일 이름이 존재합니다.",
				text: "덮어 씌우겠습니까?",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: '덮어쓰기',
				cancelButtonText: '취소'
			}).then((res) => {
				if(res.value) {
					let result = selectFile(files)
					if(result === -1) {
						return;
					} else if(result === 1) {
						$.ajax({
							url: "/ajax/uploadFile.json",
							type : "post",
							enctype : "multipart/form-data",
							processData :false,
							contentType :false,
							data:form,
							timeout:50000,
							success : function(data){
								$(".jstree-clicked").trigger("click");
							},
							error : function(err){
								console.log(err);
								$(".jstree-clicked").trigger("click");
							}
						});
					}
				} 
			});
		} else {
			let result = selectFile(files)
			if(result === -1) {
				return;
			} else if(result === 1) {
				$.ajax({
					url: "/ajax/uploadFile.json",
					type : "post",
					enctype : "multipart/form-data",
					processData :false,
					contentType :false,
					data:form,
					timeout:50000,
					success : function(data){
						$(".jstree-clicked").trigger("click");
					},
					error : function(err){
						console.log(err);
						$(".jstree-clicked").trigger("click");
					}
				});
			}
		}
    } else {
        alert("Error");
    }
	$("#uploadModal").modal('hide');
}