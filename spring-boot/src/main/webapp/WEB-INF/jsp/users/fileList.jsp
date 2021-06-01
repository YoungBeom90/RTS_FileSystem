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
    <script src="/js-lib/bootstrap.min.js"></script>
    <script src="/js-lib/jstree.min.js"></script>
    <script src="/js/fileList.js"></script>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script> 
    <title>파일 드래그 드랍 테스트 화면</title>
</head>
<body>
	<div id="container">
		<div id="sidebar">
			<nav>
				<div id="logo">
					Logo
				</div>
		    	<div id="topMenuBar">
		    		<button type="button" id="uploadFileBtn" data-toggle="modal" data-target="#uploadModal" onclick="modalPopup()" class="btn btn-primary btn-sm">업로드</button>
		    		<button type="button" id="createFolderBtn" class="btn btn-primary btn-sm">폴더생성</button>
		    	</div>
		        <div id="jstree"></div>
		    </nav>
		</div>
		<div id="fileContainer">
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
		</div>
	    
		<div class="modal fade" id="uploadModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="exampleModalLabel">파일 업로드</h4>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
      </div>
      <div class="modal-body">
        <form>
          <div class="form-group">
            <label for="recipient-name" class="control-label">현재 경로 :</label>
            <input type="text" class="form-control" id="recipient-name">
          </div>
          <div class="form-group">
            <label for="message-text" class="control-label">파일 :</label>
            <input type="file" id="modalUpload" name="modalUpload" multiple required >
            <p style="font-size: 9px; margin-top: 5px; color: red;">* 폴더는 업로드 할 수 없습니다.</p>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger btn-sm" data-dismiss="modal">취소</button>
        <button id="modalSubmit" type="button" class="btn btn-success btn-sm">파일저장</button>
      </div>
    </div>
  </div>
</div>
	    <%-- <c:forEach var="vo" items="${filePath}">
	    	${vo}<br/>
	    </c:forEach> --%>
	</div>
    
</body>
</html>