<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>
<body>

    <form action="${pageContext.request.contextPath}/upload.do" enctype="multipart/form-data" method="post">
        上传用户：<input type="text" name="username"><br/>
        <p><input type="file" name="file1"></p><br/>
        <p><input type="submit"> | <input type="reset"></p><br/>
    </form>
</body>
</html>
