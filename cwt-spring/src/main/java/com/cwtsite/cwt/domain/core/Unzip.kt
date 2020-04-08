package com.cwtsite.cwt.domain.core

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipInputStream

object Unzip {

    fun unzip(inputStream: InputStream, destDir: File): List<File> {
        // create output directory if it doesn't exist
        if (!destDir.exists()) destDir.mkdirs()
        val fis: InputStream
        //buffer for read and write data to file
        val buffer = ByteArray(1024)
        val res = mutableListOf<File>()
        try {
            fis = inputStream
            val zis = ZipInputStream(fis)
            var ze = zis.nextEntry
            while (ze != null) {
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
                //close this ZipEntry
                zis.closeEntry()
                ze = zis.nextEntry
            }
            //close last ZipEntry
            zis.closeEntry()
            zis.close()
            fis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return res.toList()
    }
}
