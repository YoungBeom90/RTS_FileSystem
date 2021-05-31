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


$(document).ready(function() {
	let btn = document.getElementById("createFolderBtn");	
	
	init();
	fileDropDown();
	createFolder(btn);
	
	// 파일 트리 생성
	$('#jstree').on("select_node.jstree", function (e, data) { 
		let selectID = data.node.id;
		selectID = selectID.substring(3);
		selectList(selectID);
		
		if($('#filePath').length === 0) {
			let html = "<input type='hidden' id='filePath' value='"+selectID+"' />";
			$(".fileList").prepend(html);
		} else if($('#filePath').val() === selectID) {
			return;
		} else if($('#filePath').val() !== selectID) {
			$('#filePath').remove();
			let html = "<input type='hidden' id='filePath' value='"+selectID+"' />";
			$(".fileList").prepend(html);
		}
	});

});//$(document).ready 종료

function selectLine(node) {
	let nodeId = $(node).attr("id");
	if($("#"+nodeId).attr("style") === "background-color: #2257d4;") {
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
		
		axiosCreateFolder(addFolderNm, addFolderPrt);
	})
}

function renameFolderListener(parent, child) {
	$("#" + child).on("focusout", function() {
		
	});
}
//첫화면 파일트리 가져오기
function init() {
	axios.post("/axios/showFolderTree").then((res) => {
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
									let inst = $.jstree.reference(data.reference);
									console.log(inst);
									obj = inst.get_node(data.reference);
									console.log(obj.id);
									try {
										$("#jstree").jstree(true).edit(obj);
										addFolderListener(o,obj.id);
									} catch(ex) {
										alert(ex);
									}
									
									/*data.jstree("edit", obj);*/
								}
							}
						}
					}			
				},
		    });
			let firstDir = treeData[0].path;
			selectList(firstDir);
		}
	}).catch((err) => {
		console.log(err);
	});
}

// 선택된 폴더에 대한 자식요소 리스트 불러오기
function selectList(firstDir) {
	$.ajax({
		type : 'post',
		url : '/ajax/selectFileList',
		dataType : 'json',
		data : "isDir=" + firstDir,
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
					console.log(filePath);
					addFileList(idx, fileName, fileSize, ext, mdfDate, filePath);	
				}
			}
		}
	});
		
}
	
// 파일 드롭다운 
function fileDropDown() {
    var dropZone = $(".dropZone");

    dropZone.on('dragover',function(e){
        e.preventDefault();
        dropZone.css('background-color','#e3f2fC');
    });

    dropZone.on("dragleave", function(e) {
        e.preventDefault();
        dropZone.css({
            "border" : "1px solid black",
            "background-color" : "#fff"
        });
    });

    dropZone.on("drop", function(e) {
        e.preventDefault();
        dropZone.css({
            "border" : "1px solid #802791",
            "background-color" : "#dbccff"
        });
		var files = e.originalEvent.dataTransfer.files;

        if(files != null) {
            selectFile(files);
			console.log("gg");
			console.log(files);
			console.log(globalData)
			var form = new FormData();
			
			/** 
			fname, fext, fdate, size, parent 까지 넘어감
			
			append할것 : fid, pid
			*/
			for(var i=0; i<fileIndex; i++){
				form.append('file', files[i]);
				form.append('fid', globalData[i])
				form.append('pid', globalData[i])
			}
			console.log(selectParentPath);
			form.append('parent', allFilePath);
			
			for (var pair of form.entries()) { 
				console.log(pair[0]+ ', ' + pair[1]); 
				}

			$.ajax({
				url: "/ajax/uploadFile.json",
				type : "post",
				enctype : "multipart/form-data",
				processData :false,
				contentType :false,
				data:form,
				timeout:50000,
				dataType: "JSON",
				success : function(JSON){
					console.log(JSON);
					console.log("성공");
				},
				error : function(err){
					console.log(err);
					console.log("에러");
				}
			})

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
                alert("등록 불가 확장자 입니다.");
                break;
            } else if(fileSize > uploadSize) {
                alert("용량 초과!\n(업로드 가능용량: " + uploadSize + "MB");
                break;
            } else if(files[i].type === "") {
                alert("폴더를 업로드 할 수 없습니다.");
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
	html += "<tr id='fileTr_" + fileIndex + "' class='fileTr' onclick='selectLine(this)'>";
	html += "<td class='fileName'>" + fileName + "</td>";
	html += "<td class='fileSize'>" + fileSize + "MB</td>";
	html += "<td class='fileExt'>" + ext + "</td>"; 
	html += "<td class='udTime'>" + fileDate + "</td>";
	html += "<td class='deletechk'>" +
	            "<img name='xButton' src='/images/xButton.png' onclick='deleteBtn("+fileIndex+")'>" +
	        "</td>";
	html += "</tr>";
	
	$('.fileList').append(html);
}

// 파일 삭제
function deleteBtn(fileIndex){
    axios.post("/axios/deleteFile", null, 
	{
		params : {
			parent : selectParentPath,
			fileName : globalData[fileIndex].text
			}
		}).then(function(res) {
			confirm("삭제하시겠습니까?");
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
	
	/*location.reload(true);*/
};

// 폴더 생성
function createFolder(btn) {
    btn.addEventListener("click", function() {
		let newFolder = $("#fileNameInput").html();
		let fileIndex = 0;
		
		if(newFolder === "") {
			console.log("return");
			return;
		}
		
		let lastNode = $(".fileList > tr:last");
		if(lastNode.length == 0) {
			lastNode = $(".fileList");
		} else {
			let lastNodeId = lastNode.attr("id");
			fileIndex = Number(lastNodeId.split("_")[1]) + 1;
		}
		
		
		let addTr =  "<tr id='fileTr_" + fileIndex + "' class='fileTr' onclick='selectLine(this)'>";
		addTr += "<td class='fileName'>";
		addTr += "<input type='text' id='fileNameInput' class='folderInput' value='새 폴더' onsubmit='return false' />";
		addTr += "<input type='button' id='fileNmSubmit' value='저장' />";
		addTr += "</td>";
		addTr += "<td class='fileSize'></td>";
		addTr += "<td class='fileExt'></td>"; 
		addTr += "<td class='udTime'></td>";
		addTr += "<td class='deletechk'>" +
		            "<img name='xButton' src='/images/xButton.png' onclick='deleteBtn("+fileIndex+")'>" +
		        "</td>";
		addTr += "</tr>";
		
		lastNode.after(addTr);
		fileNameInput = $("#fileNameInput");
		$(".dropZone").scrollTop($(".dropZone")[0].scrollHeight);
		$("#fileNameInput").focus();
    });
	// 새폴더 이름 수정후 이벤트
	$(document).on("click", "#fileNmSubmit", function() {
		axiosCreateFolder($("#fileNameInput").val(), $("#filePath").val());
	});

}

// 폴더생성 aiox 호출
function axiosCreateFolder(fldNm, fldPrt) {
	let oriPath = $("#filePath").val();
	
	if(oriPath === fldPrt) {
		axios.post("/axios/createFolder", null, {params : {
			value : fldNm,
			path : fldPrt
		}}).then(function(res) {
	        if(res) {
				if(res.data === -1) {
					alert("권리자 권한 문제로 폴더를 생성하지 못했습니다.");
				} else {
					alert(res.data);
				}
	             
	            return;
	        }
	        alert(res.data);
			
	    });
	}
	selectList(fldPrt);
	$("#jstree").jstree('refresh');
	location.reload(true);
}
