package com.cwtsite.cwt.domain.core

import com.google.common.io.Files
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

object Unzip {

    fun unzipReplayFiles(inputStream: InputStream, destDir: File): Set<File> {
        if (!destDir.exists()) destDir.mkdirs()
        val buffer = ByteArray(1024)
        val res = mutableListOf<File>()
        val fis: InputStream = inputStream
        val zis = ZipInputStream(fis)
        var ze = zis.nextEntry

        while (ze != null) {
            @Suppress("UnstableApiUsage")
            if (!ze.isDirectory && Files.getFileExtension(ze.name) == "WAgame") {
                val fileName = ze.name
                val newFile = File(destDir.path + File.separator + fileName)
                res.add(newFile)
                //create directories for sub directories in zip
                File(newFile.parent).mkdirs()
                val fos = FileOutputStream(newFile)
                var len: Int
                while (zis.read(buffer).also { len = it } > 0) {
                    fos.write(buffer, 0, len)
                }
                fos.close()
                zis.closeEntry()
            }
            ze = zis.nextEntry
        }

        zis.closeEntry()
        zis.close()
        fis.close()

        return res.toSet()
    }
}
