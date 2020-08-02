package com.demo.service

import com.demo.model.File
import com.demo.repository.FileRepository
import com.demo.util.Auth
import com.demo.util.RedisUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

/***
 * 文件上传service
 * @author baijj
 * @date 2020-06-22
 */
@Service
open class FileService {

    @Autowired
    lateinit var fileRepository: FileRepository

    @Autowired
    lateinit var jobService: JobService

    @Autowired
    lateinit var finishJobService: FinishJobService

    @Autowired
    lateinit var redisUtil: RedisUtils

    @Value("\${files.path}")
    var path = ""


    @Value("\${files.prefix}")
    var pathPreifx = ""


    /***
     * 查出已经审核通过或审核驳回的文件id
     */
    fun findFileIdforstatus():List<Int>?{

       return finishJobService.findFileIdBystatus()
    }

    /***
     * 通过多个文件Id 获取文件集合
     * @param ids String
     * @return List<File>
     */
    fun getFiles(ids: String?): List<File>? {
        if (ids != null) {
            var idList = ids.replace("\\s", "").split(",")
            if (idList.isNotEmpty()) {
                var longList: MutableList<Long> = mutableListOf()
                idList.forEach {
                    if (!it.equals("")) {
                        longList.add(it.toLong())
                    }
                }
                return fileRepository.findAllByIdIn(longList)
            }
        }
        return emptyList()
    }

    /***
     * 上传文件
     * @param uploadFile MultipartFile
     * @return File
     */
    @Transactional(propagation = Propagation.REQUIRED)
    open fun addFiles(uploadFile: MultipartFile, jobId: Int, jobType: Int): File {

        // 1 当前任务减库存
        // 2 添加到当前用户的已做任务表
        // 4 上传文件到本地
        // 5 修改当日可做任务数量
        val jobNum: String = redisUtil.getforString("jobNum_" + Auth.userName).toString()
        val newNUm = jobNum.toInt() - 1
        val time: Long = redisUtil.getKeyTime("jobNum_" + Auth.userName)
        redisUtil.puts("jobNum_" + Auth.userName, newNUm, time)
        var file = File()
        file.jobId = jobId
        file.userId = Auth.id.toLong()
        uploadFile(uploadFile, file)
        var saveFiles = fileRepository.save(file)
        jobService.jianJobokNumberByjobId(jobId)
        finishJobService.addFinishJob(jobId, jobType, saveFiles.id.toInt())
        return file
    }

    /***
     * 封装数据
     * @param uploadFile MultipartFile
     * @param model File
     */
    private fun uploadFile(uploadFile: MultipartFile, model: File) {
        if (uploadFile.isEmpty) {
            return
        }
        // 文件存储路径
        var fileSavePath = listOf(
                path,
                SimpleDateFormat("yyyy-MM-dd").format(Date())
        ).joinToString(java.io.File.separator)
        // 获取文件的后缀名
        var suffix = uploadFile.originalFilename!!.substring(uploadFile.originalFilename!!.lastIndexOf("."))
        var newFileName = UUID.randomUUID().toString() + suffix

        //相对路径
        var contextSavePath = listOf(
                pathPreifx,
                SimpleDateFormat("yyyy-MM-dd").format(Date())
        ).joinToString(java.io.File.separator)
        contextSavePath += (java.io.File.separator + newFileName);

        model.contextPath = contextSavePath
        // 原文件名
        model.title = uploadFile.originalFilename!!
        // 获取文件size
        model.size = uploadFile.size.toInt()
        model.ext = suffix.replace(".", "")
        //文件MIME
        model.mime = getFileType(suffix)
        // 是图片的话 存宽&高
        var bufferedImage: BufferedImage? = ImageIO.read(uploadFile.getInputStream())
        if (bufferedImage == null) {
            model.image = false
        } else {
            model.image = true
            model.width = bufferedImage.getWidth()
            model.height = bufferedImage.getHeight()
        }
        var targetFile = java.io.File(fileSavePath, newFileName)
        if (!targetFile.parentFile.exists()) {
            targetFile.parentFile.mkdirs()
        }
        model.path = targetFile.path
        try {
            uploadFile.transferTo(targetFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /***
     * 筛选文件类型
     * @param fileName String
     * @return String
     */
    private fun getFileType(fileName: String): String {
        return when (fileName) {
            ".3gpp" -> "video/3gpp"
            ".png" -> "image/png"
            ".jpg" -> "image/jpeg"
            ".jpeg" -> "image/jpeg"
            ".doc" -> "application/msword"
            ".jpe" -> "image/jpeg"
            ".jp2" -> "image/jp2"
            ".pdf" -> "application/pdf"
            ".xls" -> "application/vnd.ms-excel"
            ".wps" -> "application/vnd.ms-works"
            ".xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            ".xml" -> " text/xml, application/xml"
            ".txt" -> "text/plain"
            ".dtd" -> "application/xml-dtd"
            ".json" -> "application/json"
            ".zip" -> "aplication/zip"
            ".ppt" -> "application/vnd.ms-powerpoint"
            else -> ""
        }
    }
}
