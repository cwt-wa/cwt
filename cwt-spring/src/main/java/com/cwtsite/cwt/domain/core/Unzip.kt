package com.cwtsite.cwt.domain.core

import com.google.common.io.Files
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

object Unzip {

    private lateinit var destDir: File

    fun unzipReplayFiles(inputStream: InputStream): WrappedCloseable<Set<File>> {
        val res = mutableSetOf<File>()
        destDir = createTempDir("cwt_", "_replay")
        val buffer = ByteArray(1024)
        val fis: InputStream = inputStream
        ZipInputStream(fis).use { zis ->
            var ze = zis.nextEntry
            while (ze != null) {
                try {
                    @Suppress("UnstableApiUsage")
                    if (!ze.isDirectory && Files.getFileExtension(ze.name) == "WAgame") {
                        val newFile = File(destDir.path + File.separator + ze.name)
                        res.add(newFile)
                        File(newFile.parent).mkdirs()
                        FileOutputStream(newFile).use { fos ->
                            var len: Int
                            while (zis.read(buffer).also { len = it } > 0) {
                                fos.write(buffer, 0, len)
                            }
                        }
                    }
                } finally {
                    zis.closeEntry()
                }
                ze = zis.nextEntry
            }
            zis.closeEntry()
        }

        return WrappedCloseable(res.toSet()) {
            destDir.deleteRecursively()
        }
    }
}
