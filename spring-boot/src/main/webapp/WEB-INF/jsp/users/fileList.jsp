<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
  	<link rel="stylesheet" href="/css/fileList.css" />
  	<link rel="stylesheet" href="/css/bootstrap.css" />
    <link rel="stylesheet" href="/css/bootstrap.min.css" />
    <link rel="stylesheet" href="/css/buttons.css" />
    <link rel="stylesheet" href="/css/themes/default/style.css" />
    
    <script src="/js-lib/jquery-3.3.1.min.js"></script>
    <script src="/js-lib/bootstrap.min.js"></script>
    <script src="/js-lib/jstree.min.js"></script> 
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@9"></script>
    <script src="https://kit.fontawesome.com/d983714281.js" crossorigin="anonymous"></script>
    <script src="/js/jsTree.js"></script>
    <script src="/js/fileList.js"></script>
    <title>파일 드래그 드랍 테스트 화면</title>
</head>
<body>
	<div id="container" aria-hidden="true;">
		<div id="sidebar">
			<div id="logo">
				Logo
			</div>
	        <div id="jstree"></div>
		</div>
		<div id="fileContainer">
			<form name="uploadForm" id="uploadForm" enctype="multipart/form-data" method="POST">
				<div id="fileTool">
					<button type="button" id="goParent" class="btn btn-info btn-sm">상위폴더</button>
					<button type="button" id="uploadFileBtn" data-toggle="modal" data-target="#uploadModal" onclick="modalPopup()" class="btn btn-primary btn-sm">업로드</button>
					<!-- <button type="button" id="searchBtn" data-toggle="modal" data-target="#searchModal" onclick="searchModalPopup()" class="btn btn-primary btn-sm">검색</button> -->
			    	<button type="button" id="createFolderBtn" class="btn btn-primary btn-sm">폴더생성</button>
			    	<button type="button" id="donwloadBtn" class="btn btn-primary btn-sm">다운로드</button>
			    	<button type="button" id="deleteBtn" class="btn btn-danger btn-sm">삭제</button>
				</div>
				<div id="search">
					<select id="searchSelect" class="form-control">
						<option value="file">파일</option>
						<option value="folder">폴더</option>
					</select>
					<input type="search" id="searchText" class="form-control input-sm" placeholder="검색"/>
					<button type="button" id="searchBtn" class="btn btn-warning btn-sm">확인</button>
				</div>
				
		        <div class="dropZone" style="overflow-x:hidden">
		            <table id="fileTable" class="table table-hover table-sm">
		                <thead>
		                    <tr>
		                    	<th style="width:5%; font-size: 15px; text-align: center;">
		                    		<input type="checkbox" id="allCheck" onclick="checkAll()" style="cursor: pointer;">
		                    	</th>
		                        <th style="width:400px;">파일명</th>
		                        <th style="width:100px;">파일형식</th>
		                        <th style="width:160px;">수정된날짜</th>
		                        <th style="width:100px;">파일크기</th>
		                        <th style="width:300px;">파일경로</th>
		                        <th style="width:100px;">게시자</th>
		                    </tr>
		                </thead>
		                <tbody class="fileList"></tbody>
		            </table>
		        </div>
		    </form>
		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/common/modal.jsp" />
	<jsp:include page="/WEB-INF/jsp/common/search.jsp" />
</body>
</html>