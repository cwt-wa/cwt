package com.cwtsite.cwt.core

import org.springframework.web.multipart.MultipartFile
import java.io.File

object MultipartFileToFile {

     fun convertMultipartFileToFile(multipartFile: MultipartFile): File {
        val tempFile = createTempFile()
        multipartFile.transferTo(tempFile)
        return tempFile
    }
}
