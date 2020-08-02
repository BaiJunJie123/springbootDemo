package com.demo.controller;

import com.demo.http.Response;
import com.demo.service.FileService;
import com.demo.util.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class FileController extends  BaseController {

	@Autowired
	private FileService fileService;

	@PostMapping("file")
	public Response<String> file(@RequestParam String image,@RequestParam String jobId,@RequestParam String jobType){
		if(!image.isEmpty()){
			// 1 当前任务减库存
			// 2 添加到当前用户的已做任务表
			// 4 上传文件到本地
			// 5 修改当日可做任务数量
			MultipartFile file = Base64Util.base64ToMultipart(image);
				fileService.addFiles(file,Integer.valueOf(jobId),Integer.valueOf(jobType));
				return  new Response().OK();
		}else{
			return  new Response().NO();
		}
	}
}
