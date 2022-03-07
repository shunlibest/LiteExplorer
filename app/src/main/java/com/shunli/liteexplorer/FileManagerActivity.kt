package com.shunli.liteexplorer

import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.shunli.liteexplorer.utils.FileUtils
import com.shunli.liteexplorer.utils.OpenFileUtil
import java.io.File

class FileManagerActivity : AppCompatActivity() {
    var adapter: FileManagerAdapter? = null

    lateinit var tv_current_file: TextView
    lateinit var rv_file_manager: RecyclerView

    var initFile: File? = null
    var currentFile: File? = null
        set(value) {
            field = value
            tv_current_file.text = value?.absolutePath
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)
        initViewId()
        val path = intent.getStringExtra("filePath")
        val filePath: String = if (path.isNullOrEmpty()) {
            FileUtils.getRootPath().absolutePath
        } else {
            path
        }
        currentFile = File(filePath)
        initFile = currentFile
        initRecyclerView()

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        requestPermission()
    }

    private fun initViewId() {
        tv_current_file = findViewById(R.id.tv_current_file)
        rv_file_manager = findViewById(R.id.rv_file_manager)
    }

    private fun initRecyclerView() {
        val files = FileUtils.getAllFileDirByDir(currentFile)
        val layoutManager = LinearLayoutManager(this)
        rv_file_manager.layoutManager = layoutManager
        adapter = FileManagerAdapter(this, files)
        rv_file_manager.adapter = adapter

        adapter?.setOnClickListener(object : FileManagerAdapter.OnFileClickListener {
            override fun onClick(index: Int, file: File) {

                if (file.isFile) {
                    if (file.name.endsWith(".jpg") || file.name.endsWith(".png")) {
                        val intent = OpenFileUtil.getImageFileIntent(file)
                        startActivity(intent);
                    }
                    if (file.name.endsWith(".txt") || file.name.endsWith(".log")) {
                        val intent = OpenFileUtil.getTextFileIntent(file)
                        startActivity(intent);
                    }

                    if (file.name.endsWith(".zip")) {
                        val intent = OpenFileUtil.getZipFileIntent(file)
                        startActivity(intent);
                    }

                } else {
                    val f = FileUtils.getAllFileDirByDir(file)
                    adapter?.updateData(f)
                    currentFile = file;
                }
            }

            override fun onLongClick(index: Int, file: File) {
                AlertDialog.Builder(this@FileManagerActivity).setTitle("删除项目?")
                    .setMessage("是否确定要删除当前项目:${file.name}")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        val res =
                            if (file.isDirectory) {
                                FileUtils.deleteDirectory(file)
                            } else {
                                FileUtils.deleteFile(file)
                            }
                        if (res) {
                            adapter?.notifyItemChanged(index)
                            Toast.makeText(this@FileManagerActivity, "删除成功", Toast.LENGTH_SHORT)
                                .show();
                        } else {
                            Toast.makeText(this@FileManagerActivity, "删除失败", Toast.LENGTH_SHORT)
                                .show();
                        }
                    }
                    ).show()
            }
        })
    }


    private fun requestPermission() {
        XXPermissions.with(this)
            // 申请单个权限
//            .permission(Permission.RECORD_AUDIO)
            // 申请多个权限
            .permission(Permission.Group.STORAGE)
            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    Toast.makeText(this@FileManagerActivity, "授权成功", Toast.LENGTH_SHORT)
                        .show();
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    Toast.makeText(this@FileManagerActivity, "授权失败", Toast.LENGTH_SHORT)
                        .show();
                }
            })
    }

    override fun onBackPressed() {
        if (initFile?.absolutePath == currentFile?.absolutePath) {
            super.onBackPressed()
        } else {
            val parentFile = currentFile?.parentFile
            val f = FileUtils.getAllFileDirByDir(parentFile)
            adapter?.updateData(f)
            currentFile = parentFile;
        }
    }

    fun _finish(view: View) {
        finish()
    }

}