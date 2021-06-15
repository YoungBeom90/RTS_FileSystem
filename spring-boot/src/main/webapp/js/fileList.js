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
		console.log("나야나");
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
							console.log(res.data);
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
	
	//모달창 저장 클릭 이벤트
	$("#modalSubmit").on("click", function() {
		if($("#modalParentPath").val() === "") {
			
		}
	});
		// 다운로드 버튼 클릭 이벤트
	$("#donwloadBtn").on("click", function() {	
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
		
		downloadFile(filePath).then(() => {
			reqCnt = res;
			Swal.fire({
				title: reqCnt + "개 파일을 다운로드하였습니다.",
				icon: "success",
				confirmButtonColor: '#3085d6',
				confirmButtonText: "확인"				
			}).then(() => {
				$(".jstree-clicked").trigger("click");
			});
		});	
		
	});//파일다운로드 이벤트 end
	

	//파일 다운로드
	 function downloadFile(filePath) {
		let checkBox = $(".checkBox");
		let fileNameList = $(".fileName");
		let fileExtList = $(".fileExt");
		let fileName;
		let fileExt;
		let encodeUri;
		let reqCnt = 0;
		let fileIdx = 0;
		
		for (let target of checkBox) {
			
			if(target.checked) {
				fileName = fileNameList[fileIdx].innerText; //파일명 가져오기 
				fileExt = fileExtList[fileIdx].innerText;//확장자명 가져오기
				console.log(fileName);
				console.log(fileExt);
				encodeUri = encodeURI(filePath);
				
				window.location =`/axios/downloadFile?fileName=${fileName.trim()}&fileExt=${fileExt.trim()}&parent=${encodeUri}`
				reqCnt++;
			}
			fileIdx++;
		}
		return reqCnt;
	}//파일 다운로드 end
	
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
function addFolderListener(parent, child) {
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
					/* 로컬 경로내에서 스캔
					let fileName = data[idx].text;
					let ext = data[idx].ext;
					let fileSize = data[idx].size / 1024 / 1024;
					fileSize = fileSize.toFixed(3);
					let mdfDate = data[idx].date;
					let filePath = data[idx].url;
					*/
					
					/** DB 내에서 스캔 */
					let fileName = data[idx].fname;
					let ext = data[idx].fext;
					if(ext===undefined)
						ext = "폴더";
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
					/**여기까지 주석처리하면 됨 */
					
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
				}
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

        if(files != null) {
            
			let form = new FormData();
			
			/** 
			fname, fext, fdate, size, parent 까지 넘어감
			
			append할것 : fid, pid
			*/
			for(let i=0; i<files.length; i++){
				form.append('file', files[i]);
				form.append('fdate', files[i].lastModified);
			}
			form.append('parent', selectParentPath);
			
			for (let pair of form.entries()) { 
				console.log(pair[0]+ ', ' + pair[1]); 
			}
			
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
			
			
            if($.inArray(ext, ['exe', 'bat']) >= 0) {
                Swal.fire({
					title: "등록불가 확장자 입니다.",
					icon: "warning",
					confirmButtonColor: '#3085d6',
					confirmButtonText: "확인"
				});
                break;
            } else if(fileSize > uploadSize) {
				Swal.fire({
					title: "용량 초과!\n(업로드 가능용량: " + uploadSize + "MB",
					icon: "warning",
					confirmButtonColor: '#3085d6',
					confirmButtonText: "확인"
				});
                break;
            } else if(files[i].type === "") {
				Swal.fire({
					title: "폴더를 업로드 할 수 없습니다.",
					icon: "warning",
					confirmButtonColor: '#3085d6',
					confirmButtonText: "확인"
				});
				break;
            } else {
                totalFileSize += fileSize;
                fileSize = fileSize.toFixed(3);
                addFileList(fileName, fileSize, ext);
            }
			
        }
    }
}

// 파일리스트 조회, 추가 
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
	html += "<td class='fileName'>";
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
	html += "&nbsp;&nbsp;&nbsp;" + fileName + "</td>";
	html += "<td class='fileSize'>" + fileSize + "MB</td>";
	html += "<td class='fileExt'>" + ext + "</td>"; 
	html += "<td class='udTime'>" + fileDate + "</td>";
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
							$(".jstree-clicked").trigger("click");
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
	
}