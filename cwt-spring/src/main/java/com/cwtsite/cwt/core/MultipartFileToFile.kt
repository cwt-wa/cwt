package com.cwtsite.cwt.core

import org.springframework.web.multipart.MultipartFile
import java.io.File

object MultipartFileToFile {

     fun convertMultipartFileToFile(multipartFile: MultipartFile): File {
        val tempFile = File.createTempFile("cwt_", "_multipart")
        multipartFile.transferTo(tempFile)
        return tempFile
    }
}

