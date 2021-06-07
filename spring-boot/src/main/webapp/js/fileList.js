let fileIndex = 0;
let totalFileSize = 0;
let fileList = new Array();
let fileSizeList = new Array();
let uploadSize = 50 * 1024 * 1024;
let maxUploadSize = 500;
let globalData;
let selectParentPath;
let fileNameInput;
let allFilePath;
let globalSelectFolder;

const loadingStart = () => {
	setTimeout(() => {
		$("#container").css("opacity", "0.3");
		$("body").css("background", "no-repeat url('/images/loading.gif')");
		$("body").css("background-position", "center center");
		$("body").css("width", "100%");
	}, 0);
}

const loadingEnd = () => {
	$("#container").css("opacity", "1");
}


$(document).ready(function() {
	let btn = document.getElementById("createFolderBtn");
	
	init().then((res) => {
		if(res === "1") {
			setTimeout(function() {
				$(".jstree-clicked").trigger("click");
			},1000);
		}
	});
	
	fileDropDown();
	createFolder(btn);
	
	// 파일 트리 생성
	$('#jstree').on("select_node.jstree", function (e, data) { 
		let selectID = data.node.id;
		selectID = selectID.substring(3);
		globalSelectFolder = selectID;
		selectID = selectID.replaceAll("\\", "\\\\");
		console.log(selectID);
		selectList(selectID).then(() => {
			loadingEnd();
		});
		if($('#filePath').length === 0) {
			let html = "<input type='hidden' id='filePath' value='"+selectID+"' />";
			$("#sidebar").append(html);
		} else if($('#filePath').val() === selectID) {
			return;
		} else if($('#filePath').val() !== selectID) {
			$('#filePath').attr("value", selectID);
		}
	});
	
	$('#jstree').bind("dblclick.jstree", function(e, data) {
		console.log(e.target);
		let getPath = e.target.id.replace("_anchor", "");
		getPath = getPath.substr(3);
		console.log(getPath);
	});
	
	// 삭제 버튼 클릭 이벤트
	$("#deleteBtn").on("click", function() {	
		let checked = $(".checkBox");
		let filePath = $("#filePath").val();
		let reqCnt = 0;
		let checkList = [];
		let checkFlag = false;
		
		for(let i=0; i<checked.length; i++) {
			if(checked[i].checked) {
				checkFlag = true;
				break;
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
					deleteFile().then((res) => {
						console.log(res);
						reqCnt = res;
						Swal.fire({
							title: reqCnt + "개 파일을 삭제하였습니다.",
							icon: "success",
							confirmButtonColor: '#3085d6',
							confirmButtonText: "확인"				
						}).then(() => {
							location.reload();
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
		
		// 파일 삭제
		async function deleteFile() {
			
			for await(let target of checked) {
				
				if(target.checked === true) {
					let fileName = target.offsetParent.nextSibling.innerHTML;
				
					await axios.post("/axios/deleteFile", null, {params : {
						parent : filePath,
						fileName : fileName
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
			}
			
			return reqCnt;
		}
		
	});
	
	//모달창 저장 클릭 이벤트
	$("#modalSubmit").on("click", function() {
		if($("#modalParentPath").val() === "") {
			
		}
	});
	
	
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

function checkLine(node) {
	let nodeId = $(node).attr("id");
	let checkBox = $(node).children().eq(0).children();
	console.log(checkBox[0].checked);
	if(checkBox[0].checked === false) {
		$("#" + nodeId).removeAttr("style");
	} else {
		$("#" + nodeId).attr("style", "background-color: #2257d4;");
	}
}

// 폴더 추가시 실행
function addFolderListener(parent, child) {
	$("#"+ child).on("focusout", function() {		
		let addFolderNm = $("#"+ child).children().children().eq(1).val();
		let addFolderPrt = parent.id.substring(3);
		/*renameFolderListener(child);*/
		axiosCreateFolder(addFolderNm, addFolderPrt);
		console.log("폴더 생성.");
	});
}

// 폴더 이름 수정.
function renameFolderListener(obj) {
	let target = obj.node.id;
	let preNm = obj.old;
	let afterNm = obj.text;
	target = target.substr(3);
	console.log(target);
	console.log(preNm);
	console.log(afterNm);
	
	if(obj) {
		axios.post("/axios/renameFolder", null, {params: {
			path : target,
			value : preNm,
			rename : afterNm
		}}).then((res) => {
			if(res) {
				console.log(res);
				
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



//첫화면 파일트리 가져오기
async function init() {
	loadingStart();
	await axios.post("/axios/showFolderTree").then((res) => {
		if(res) {
			let treeData = res.data.folderList
			globalFolderData = treeData;
			
			$('#jstree').jstree({
       			plugins: ["contextmenu"],
		        core: {
					check_callback :  true,
					data : treeData,
					themes: {
                        name: "default",
                        dots: false,
                        icons: false,
                        responsive:true
					}
        		},
				contextmenu : {		
					items : function(o, cb) {
						return {
							create : {
								seperator_before : false,
								seperator_after : true,
								label : "폴더생성",
								action : function(data) {
									let inst = $.jstree.reference(data.reference);
									obj = inst.get_node(data.reference);
									inst.create_node(obj, data, "last", function (new_node) {
										try {
											new_node.text = "새 폴더";
											inst.edit(new_node);
											addFolderListener(o,new_node.id);
										} catch(ex) {
											alert(ex);
										}
									});
								},
								
							},
							rename : {
								seperator_before : false,
								seperator_after : true,
								label : "이름수정",
								action : function(data) {
									let temp;
									let inst = $.jstree.reference(data.reference);
									obj = inst.get_node(data.reference);
									
									try {
										inst.edit(obj)
										console.log(obj);
										
									} catch(ex) {
										alert(ex);
									}
									
									/*data.jstree("edit", obj);*/
								}
							}
						}
					}			
				},
		    }).bind("rename_node.jstree", function (e, data) {    
		    	renameFolderListener(data);
			});
			let firstDir = treeData[0].path;
			selectList(firstDir).then(() => {
				loadingEnd();
			});
			
		}
	}).catch((err) => {
		console.log(err);
	});
	
	return "1";
}

// 선택된 폴더에 대한 자식요소 리스트 불러오기
async function selectList(firstDir) {
	console.log("dir = " + firstDir);
	loadingStart();
	
	await $.ajax({
		type : 'post',
		url : '/ajax/selectFileList',
		dataType : 'json',
		data : "isDir=" + encodeURIComponent(firstDir),
		success : function(res) {
			if(res) {
				let data = res.filePath;
				globalData = data;
				allFilePath=firstDir;
				$(".fileList > tr").remove();
				for(idx in data) {
					let fileName = data[idx].text;
					let ext = data[idx].ext;
					let fileSize = data[idx].size / 1024 / 1024;
					fileSize = fileSize.toFixed(3);
					let mdfDate = data[idx].date;
					let filePath = data[idx].url;
					let lastIdx = filePath.lastIndexOf("\\");
					filePath = filePath.substr(0, lastIdx);
					selectParentPath = filePath;
					
					addFileList(idx, fileName, fileSize, ext, mdfDate, filePath);	
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

    dropZone.on('dragover',function(e){
        e.preventDefault();
        dropZone.css({
			'background-color':'#dbccff',
			'opacity':'0.8'
    	});
	});
	
    dropZone.on("dragleave", function(e) {
        e.preventDefault();
        dropZone.css({
            "border" : "none",
            "background-color" : "rgb(227,242,253)",
			'opacity':'1'
        });
    });

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
			for(let i=0; i<fileIndex; i++){
				form.append('file', files[i]);
				console.log(files[i]);
				if(files[i].lastModified === undefined){
					continue;
				}
				form.append('fdate', files[i].lastModified);
			}
			form.append('parent', allFilePath);
			
			for (let pair of form.entries()) { 
				console.log(pair[0]+ ', ' + pair[1]); 
			}
			
			let dupCheck = false;
			
			for(let i=0; i<files.length; i++) {
				for(let j=0; j<files.length; j++) {
					console.log(globalData[j]);
					if(files[i].text = globalData[j].text) {
						dupCheck = true;
						break;	
					} 
				}
			}
			
			if(dupCheck) {
				Swal.fire({
					title: "이미 저장되어 있는 파일 이름이 존재합니다. ",
					text: "덮어 씌우겠습니까?",
					icon: 'warning',
					showCancelButton: true,
					confirmButtonColor: '#3085d6',
					cancelButtonColor: '#d33',
					confirmButtonText: '덮어쓰기',
					cancelButtonText: '취소'
				}).then((res) => {
					if(res.value) {
						
						selectFile(files);
						
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
							},
							error : function(err){
								console.log(err);
								console.log("에러");
							}
						});
					} 
				});
			}
        } else {
            alert("Error");
        }
		
		/*location.reload();*/
    });
}

// 드롭다운 파일 가공 후 리스트 추가
function selectFile(files) {
		
    if(files) {
		
        for(let i = 0; i < files.length; i++) {
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
                fileList[fileIndex] = files[i];
                fileSize = fileSize.toFixed(3);
                fileSizeList[fileIndex] = fileSize;
                addFileList(fileIndex, fileName, fileSize, ext);

                fileIndex++;
            }
			
        }
    }
}


// 파일리스트 조회, 추가 
function addFileList(fileIndex, fileName, fileSize, ext, mdfDate) {
	
	/*console.log(fileIndex + "/" + fileName + "/" + fileSize + "/" + ext);*/
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
	html += "<tr id='fileTr_" + fileIndex + "' name='"+fileIndex+"' class='fileTr' onclick='checkLine(this)'>";
	html += "<td>";
	html += "<input type='checkbox' class='checkBox'/>"
	html += "</td>";
	html += "<td class='fileName'>" + fileName + "</td>";
	html += "<td class='fileSize'>" + fileSize + "MB</td>";
	html += "<td class='fileExt'>" + ext + "</td>"; 
	html += "<td class='udTime'>" + fileDate + "</td>";
	html += "<td class='fileAuth'>admin</td>";
	html += "</tr>";
	
	$('.fileList').append(html);
}

// 파일 삭제
/*function deleteBtn(fileIndex){
    axios.post("/axios/deleteFile", null, 
	{
		params : {
			parent : selectParentPath,
			fileName : globalData[fileIndex].text
			}
		}).then(function(res) {
			console.log(res);
        if(res) {
            alert(res.data);
        }
    }).catch(function(error) {
        console.log(error);
    });
    totalFileSize -1;
    delete fileList[fileIndex];
    delete fileSizeList[fileIndex];
    $("#fileTr_" + fileIndex).remove();
	console.log(globalData[fileIndex].parent+"/"+globalData[fileIndex].text);
};*/

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
	
	if(oriPath === fldPrt) {
		console.log(fldPrt);
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
							location.reload();
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