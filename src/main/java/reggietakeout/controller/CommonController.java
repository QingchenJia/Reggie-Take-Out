package reggietakeout.controller;


import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reggietakeout.common.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.img-path}")
    private String basePath;

    /**
     * 文件上传接口
     * 该接口接收一个文件参数，将文件保存在服务器的指定路径，并返回文件的保存名称
     *
     * @param file 用户上传的文件，通过@RequestParam注解指定参数名为"file"
     * @return 返回一个R对象，包含上传文件的名称
     * @throws IOException 文件保存过程中可能抛出的异常
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        // 获取上传文件的原始名称
        String originalFilename = file.getOriginalFilename();

        // 提取文件后缀，用于保存时重新命名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用UUID生成唯一前缀，确保文件名不重复，并保留原文件后缀
        String fileName = UUID.randomUUID() + suffix;

        // 创建文件保存目录，如果目录不存在则创建
        File dir = new File(basePath);
        if (!dir.exists())
            dir.mkdirs();

        // 将上传的文件保存到指定路径
        file.transferTo(new File(basePath + fileName));

        // 返回上传文件的名称，表示上传成功
        return R.success(fileName);
    }


    /**
     * 处理文件下载请求的方法
     *
     * @param name     文件名，用于指定需要下载的文件
     * @param response 响应对象，用于向客户端输出文件流
     * @throws IOException 如果文件读取或输出流操作发生错误时抛出
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        // 创建文件输入流，用于读取指定文件
        FileInputStream fileInputStream = new FileInputStream(basePath + name);

        // 获取响应的输出流，用于向客户端发送文件数据
        ServletOutputStream outputStream = response.getOutputStream();

        // 设置响应的内容类型为JPEG图像，根据实际情况，这里可以动态设置
        response.setContentType("image/jpeg");

        // 初始化变量len用于记录每次读取的字节数，创建字节数组buff作为读写缓冲区
        int len = 0;
        byte[] buff = new byte[1024];

        // 循环读取文件数据，直到文件结束
        while ((len = fileInputStream.read(buff)) != -1) {
            // 将读取的数据写入输出流，并立即刷新输出流以确保数据及时发送给客户端
            outputStream.write(buff, 0, len);
            outputStream.flush();
        }

        // 关闭输出流和文件输入流，释放系统资源
        outputStream.close();
        fileInputStream.close();
    }
}
