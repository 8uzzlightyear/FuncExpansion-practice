package com.cao.servlet;

import com.sun.xml.internal.bind.v2.schemagen.XmlSchemaGenerator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

public class FileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (!ServletFileUpload.isMultipartContent(req)) {
            return;
        }
        String uploadPath = this.getServletContext().getRealPath("/WEB-INF/upload");
        File uploadFile = new File(uploadPath);
        if (!uploadFile.exists()) {
            uploadFile.mkdir();
        }

        //缓存
        String tmpPath = this.getServletContext().getRealPath("/WEB-INF/tmp");
        File tmpFile = new File(tmpPath);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        System.out.println(tmpFile.getAbsolutePath());
        try {
        //创建DiskFileItemFactory
        DiskFileItemFactory factory = getDiskFileItemFactory(tmpFile);

        //创建ServletFileUpload
        ServletFileUpload upload = getServletFileUpload(factory);

        //处理请求
            String msg = parseUploadParseRequest(req,upload,uploadPath);
            req.setAttribute("msg", msg);
            req.getRequestDispatcher("info.jsp").forward(req, resp);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

    public DiskFileItemFactory getDiskFileItemFactory(File file) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024*1024);
        factory.setRepository(file);
        return factory;
    }

    public ServletFileUpload getServletFileUpload(DiskFileItemFactory factory) {
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setProgressListener(new ProgressListener() {
            public void update(long l, long l1, int i) {
                System.out.println("总大小：" + l1 + "已上传：" + l);
            }
        });
        upload.setHeaderEncoding("UTF-8");
        upload.setFileSizeMax(1024*1024*10);
        upload.setSizeMax(1024*1024*10);
        return upload;
    }

    public String parseUploadParseRequest( HttpServletRequest req, ServletFileUpload upload, String uploadPath) throws IOException, FileUploadException {
        List<FileItem> fileItems = null;
        String msg = "";
        fileItems = upload.parseRequest(req);
        //遍历表单内容
        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) {
                String name = fileItem.getFieldName();
                String value = fileItem.getString("UTF-8");
                System.out.println(name + ":" + value);
            }else {
                String uploadFileName = fileItem.getName();
                System.out.println("上传的文件名：" + uploadFileName);

                if (uploadFileName.trim().equals("") || uploadFileName == null) {
                    continue;
                }

                String fileName = uploadFileName.substring(uploadFileName.lastIndexOf("/") + 1);
                String fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
                System.out.println("文件信息[件名: " + fileName + " ---文件类型" + fileExtName + "]");

                String uuidPath = UUID.randomUUID().toString();
                String realPath = uploadPath + "/" + uuidPath;
                File realPathFile = new File(realPath);
                if (!realPathFile.exists()) {
                    realPathFile.mkdir();
                }
                InputStream inputStream = fileItem.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(realPath + "/" + fileName);
                byte[] buffer = new byte[1024 * 1024];
                int len= 0;
                while((len = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len);
                }
                fileOutputStream.close();
                inputStream.close();
                fileItem.delete();
                msg = "文件上传成功";
            }
        }
        return msg;
    }
}
