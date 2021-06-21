<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<div class="modal fade" id="uploadModal" tabindex="-1" role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="uploadModelTitle">파일 업로드</h4>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			</div>
			<div class="modal-body">
				<form id="modalForm" method="post" enctype="multipart/form-data" >
					<div class="form-group">
						<label for="recipient-name" class="control-label">현재 경로 :</label>
						<input type="text" class="form-control" id="modalParentPath">
					</div>
					<div class="form-group">
						<label for="message-text" class="control-label">파일 :</label>
						<input type="file" id="modalUpload" name="modalUpload" multiple required />
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