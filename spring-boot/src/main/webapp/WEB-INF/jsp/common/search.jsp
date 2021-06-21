<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="modal fade" id="searchModal" tabindex="-1" role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content" style="width: 1000px;">
			<div class="modal-header">
				<h4 class="model-title" id="searchModalTitle">파일 검색</h4>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button> 
			</div>
			<div class="modal-body">
				<form id="modalForm" method="post" enctype="multipart/form-data">
					<div class="form-group">
						<select class="form-control" style="width: 100px; margin-bottom: 10px; display: inline;">
							<option class="form-control" value="file">파일</option>
							<!-- <option class="form-control" value="folder">폴더</option> -->
						</select>
						<input type="text" class="form-control" id="searchFileName" placeholder="검색" style="width: 77%; display: inline;">
						<button type="button" id="seatchSubmit" class="btn btn-success btn-sm" style="display: inline; margin:10px 0px 16px 10px;">확인</button>
					</div>
					<div class="form-group" style="min-height: 300px;">
						<table class="table" style="overflow-y: auto;">
							<thead>
			                    <tr>
			                    	<th style="width:5%; font-size: 15px">
			                    		<input type="checkbox" id="allCheck" onclick="checkAll()" style="cursor: pointer;">
			                    	</th>
			                        <th style="width:500px;">파일명</th>
			                        <th style="width:200px;">파일형식</th>
			                        <th style="width:200px;">수정된날짜</th>
			                        <th style="width:200px;">파일크기</th>
			                        <th style="width:200px;">게시자</th>
			                    </tr>
			                </thead>
						</table>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-danger btn-sm" data-dismiss="modal">취소</button>
			</div>
		</div>
	</div>
</div>