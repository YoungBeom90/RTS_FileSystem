
let tree_Common = {
	init : async function init() { 
		//첫화면 파일트리 가져오기
		loadingStart();
		await axios.post("/axios/showFolderTree").then((res) => {
			if(res) {
				let treeData = res.data.folderList
				console.log(treeData);
				for(let i in treeData){
					treeData[i].id = treeData[i].id.replaceAll("\\", "\\\\");
					treeData[i].parent = treeData[i].parent.replaceAll("\\", "\\\\");
				}
				
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
	},
	selectNode : function() {
		// 선택 된 노드 파일 리스트 생성
		$('#jstree').on("select_node.jstree", function (e, data) { 
			let selectID = data.node.id;
			selectID = selectID.substring(3);
			globalSelectFolder = selectID;
			selectID = selectID;
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
	},
	doubleClick : function() {
		$('#jstree').bind("dblclick.jstree", function(e, data) {
			console.log(e.target);
			let getPath = e.target.id.replace("_anchor", "");
			getPath = getPath.substr(3);
			console.log(getPath);
		});
	},
	loadedTree : function() {
		$("#jstree").on("loaded.jstree", function() {
			$(".jstree-clicked").trigger("click");
		});
	}
}






