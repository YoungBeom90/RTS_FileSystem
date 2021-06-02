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
    <link rel="stylesheet" href="/css/bootstrap.min.css" />
    <link rel="stylesheet" href="/css/buttons.css" />
    <link rel="stylesheet" href="/css/themes/default/style.css" /> 
     
    <script src="/js-lib/jquery-3.3.1.min.js"></script>
    <script src="/js-lib/bootstrap.min.js"></script>
    <script src="/js-lib/jstree.min.js"></script> 
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
    <script src="/js/fileList.js"></script>
    <title>파일 드래그 드랍 테스트 화면</title>
</head>
<body>
	<div id="container">
		<div id="sidebar">
			<nav>
				<div id="logo">
					Logo
				</div>
		        <div id="jstree"></div>
		    </nav>
		</div>
		<div id="fileContainer">
			<div id="fileTool">
				<button type="button" id="uploadFileBtn" data-toggle="modal" data-target="#uploadModal" onclick="modalPopup()" class="btn btn-primary btn-sm">업로드</button>
		    	<button type="button" id="createFolderBtn" class="btn btn-primary btn-sm">폴더생성</button>
		    	<button type="button" id="deleteBtn" class="btn btn-danger btn-sm">삭제</button>
			</div>
			<form name="uploadForm" id="uploadForm" enctype="multipart/form-data" method="POST">
		        <div class="dropZone" style="overflow-x:hidden">
		            <table id="fileTable" class="table">
		                <thead>
		                    <tr>
		                    	<th style="width:5%; font-size: 15px">
		                    		<input type="checkbox" id="allCheck" onclick="checkAll()" style="zoom: 1.5; cursor: pointer;">
		                    	</th>
		                        <th style="width:50%;">파일명</th>
		                        <th style="width:14%;">파일크기</th>
		                        <th style="width:8%;">파일형식</th>
		                        <th style="width:18%;">수정된날짜</th>
		                        <th style="width:10%;">게시자</th>
		                    </tr>
		                </thead>
		                <tbody class="fileList"></tbody>
		            </table>
		        </div>
		        
		    </form>
		</div>
	
		<jsp:include page="/WEB-INF/jsp/common/modal.jsp" />
	</div>
</body>
</html>