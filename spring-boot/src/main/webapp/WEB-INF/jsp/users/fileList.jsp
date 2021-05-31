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
    <link rel="stylesheet" href="/css/themes/default/style.css" />
    <!-- <link rel="stylesheet" href="/css/themes/proton/style.less" /> -->
    <script src="/js-lib/jquery-3.3.1.min.js"></script>
    <script src="/js-lib/jstree.min.js"></script>
    <script src="/js/fileList.js"></script>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script> 
    <title>파일 드래그 드랍 테스트 화면</title>
</head>
<body>
	<div id="container">
		<div id="sidebar">
			<nav>
		    	<div id="topMenuBar">
		    		<button type="button" id="createFolderBtn" class="btn btn-primary">폴더생성</button>
		    	</div>
		        <div id="jstree"></div>
		    </nav>
		</div>
	    <form name="uploadForm" id="uploadForm" enctype="multipart/form-data" method="POST">
	        <div class="dropZone" style="overflow-x:hidden">
	            <table id="fileTable">
	                <thead>
	                    <tr>
	                        <th style="width:55%">파일명</th>
	                        <th style="width:14%">파일크기</th>
	                        <th style="width:8%">파일형식</th>
	                        <th style="width:18%">수정된날짜</th>
	                        <th style="width:10%">삭제</th>
	                    </tr>
	                </thead>
	                <tbody class="fileList"></tbody>
	            </table>
	        </div>
	    </form>
	    <%-- <c:forEach var="vo" items="${filePath}">
	    	${vo}<br/>
	    </c:forEach> --%>
	</div>
    
</body>
</html>