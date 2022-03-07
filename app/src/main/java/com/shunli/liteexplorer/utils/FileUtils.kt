package com.shunli.liteexplorer.utils

import android.os.Environment
import android.util.Log
import java.io.*
import java.text.DecimalFormat
import java.util.*


object FileUtils {

    fun getFormatSize(file: File): String {
        val fileSize = if (file.isFile) {
            getFileSize(file)
        } else {
            getFolderSize(file)
        }
        return getFormatSize(fileSize)
    }


    /**
     * 获取指定文件大小
     * @param f
     * @return
     * @throws Exception
     */
    @Throws(java.lang.Exception::class)
    private fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            var fis: FileInputStream? = null
            fis = FileInputStream(file)
            size = fis.available().toLong()
        } else {
            file.createNewFile()
            Log.e("获取文件大小", "文件不存在!")
        }
        return size
    }


    /**
     * 获取文件夹大小
     * @param file File实例
     * @return long  单位 B
     */
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (i in fileList.indices) {
                size = if (fileList[i].isDirectory) {
                    size + getFolderSize(fileList[i])
                } else {
                    size + fileList[i].length()
                }
            }
        } catch (e: Exception) {
            Log.e("FileUtils", e.toString())
        }
        //return size/1048576;
        return size
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private fun getFormatSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        var fileSizeString = ""
        val wrongSize = "0B"
        if (fileS == 0L) {
            return wrongSize
        }
        fileSizeString = if (fileS < 1024) {
            df.format(fileS.toDouble()).toString() + "B"
        } else if (fileS < 1048576) {
            df.format(fileS.toDouble() / 1024).toString() + "KB"
        } else if (fileS < 1073741824) {
            df.format(fileS.toDouble() / 1048576).toString() + "MB"
        } else {
            df.format(fileS.toDouble() / 1073741824).toString() + "GB"
        }
        return fileSizeString
    }


    /**
     * 获取当前目录下所有文件/文件夹
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public fun getAllFileDirByDir(dir: File?): List<File> {
        val list: MutableList<File> = ArrayList()

        if (dir == null || !isDir(dir)) return list

        val files = dir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                list.add(file)
            }
        }
        return list
    }

    private fun isDir(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory
    }


    ////////////////////////////////////删除////////////////////////////////////////////
    /**
     * 只能删除文件, 不能删除文件夹.
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    fun deleteFile(filePath: String?): Boolean {
        Log.d("FileUtils", "delete FilePath: $filePath")
        return deleteFile(getFileByPath(filePath))
    }

    /**
     * 删除文件夹, 包括文件夹下的所有文件
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun deleteFileDir(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    fun deleteFileDir(filePath: String?): Boolean {
        Log.d("FileUtils", "delete FilePath: $filePath")
        return deleteFile(getFileByPath(filePath))
    }


    /**
     * 删除文件夹, 包括文件夹下的所有文件
     *
     * @param dir
     * @return
     */
    fun deleteDirectory(dir: String): Boolean {
        Log.d("FileUtils", "deleteDirectory $dir")
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        val temp = if (dir.endsWith(File.separator)) dir else dir + File.separator
        return deleteDirectory(File(temp))
    }

    public fun deleteDirectory(dirFile: File?): Boolean {
        if (dirFile == null) return false
        // dir doesn't exist then return true
        if (!dirFile.exists()) return true
        // 不是一个目录，则退出
        if (!dirFile.isDirectory) {
            Log.i("FileUtils", "删除目录 $dirFile")
            return false
        }

        // 删除文件夹中的所有文件包括子目录
        val files = dirFile.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) {
                        Log.i("FileUtils", "删除文件夹里的文件")
                        return false
                    }
                } else if (file.isDirectory) {
                    if (!deleteDirectory(file)) return false
                }
            }
        }
        return dirFile.delete();
    }


    /**
     * 根据文件路径字符串, 返回对应的文件列表
     * @param filePath The path of file.
     * @return the file
     */
    private fun getFileByPath(filePath: String?): File? {
        return if (filePath.isNullOrEmpty()) null else File(filePath)
    }


    fun getRootPath(): File {
        return Environment.getExternalStorageDirectory()
    }


}