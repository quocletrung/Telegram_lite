package com.telegram_lite.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/uploadFile")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB: Kích thước ngưỡng để lưu file tạm thời trên đĩa
        maxFileSize = 1024 * 1024 * 50, // 50MB: Kích thước file tối đa cho phép
        maxRequestSize = 1024 * 1024 * 100 // 100MB: Kích thước request tối đa cho phép (bao gồm nhiều file và form data)
)
public class FileUploadServlet extends HttpServlet {

    // Tên thư mục lưu trữ file upload (bên trong webapp)
    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Lấy đường dẫn thực tế đến thư mục webapp trên server
        String applicationPath = request.getServletContext().getRealPath("");
        // Tạo đường dẫn đầy đủ đến thư mục uploads
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

        // Tạo thư mục uploads nếu nó chưa tồn tại
        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileUrl = null;
        String errorMessage = null;

        try {
            // Lấy file từ request (giả sử tên của input field là "fileToUpload")
            Part filePart = request.getPart("fileToUpload");

            if (filePart != null && filePart.getSize() > 0) {
                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // Lấy tên file gốc
                String fileExtension = "";
                int i = originalFileName.lastIndexOf('.');
                if (i > 0) {
                    fileExtension = originalFileName.substring(i); // Lấy phần đuôi file, ví dụ: .jpg
                }
                // Tạo tên file duy nhất để tránh ghi đè
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                // Đường dẫn đầy đủ để lưu file trên server
                String filePath = uploadFilePath + File.separator + uniqueFileName;
                filePart.write(filePath); // Lưu file

                // Tạo URL để client có thể truy cập file
                // request.getContextPath() sẽ là /Telegram_lite
                fileUrl = request.getContextPath() + "/" + UPLOAD_DIR + "/" + uniqueFileName;

                System.out.println("File uploaded successfully: " + fileUrl);
            } else {
                errorMessage = "No file selected or file is empty.";
            }

        } catch (Exception e) {
            errorMessage = "File upload failed: " + e.getMessage();
            e.printStackTrace(); // Log lỗi ra console của server
        }

        // Trả về kết quả cho client dưới dạng JSON
        if (fileUrl != null) {
            response.getWriter().write("{\"success\": true, \"fileUrl\": \"" + fileUrl + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Hoặc một mã lỗi phù hợp
            response.getWriter().write("{\"success\": false, \"message\": \"" + (errorMessage != null ? errorMessage : "Unknown error") + "\"}");
        }
    }
}