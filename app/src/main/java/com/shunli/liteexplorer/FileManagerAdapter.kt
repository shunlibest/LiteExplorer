package com.shunli.liteexplorer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shunli.liteexplorer.utils.FileUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileManagerAdapter(var context: Context?, private var files: List<File>) :
    RecyclerView.Adapter<FileManagerAdapter.ViewHolder>() {

    var projectPath: File? = null
    private var onClickListener: OnFileClickListener? = null

    fun setOnClickListener(click: OnFileClickListener?) {
        this.onClickListener = click
    }

    fun updateData(files: List<File>) {
        this.files = files
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.item_file_dir_list,
            viewGroup,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val file = files[i]
        viewHolder.item_name.text = file.name

        if (file.isFile) {
            viewHolder.item_icon.setImageResource(R.drawable.ic_file_generic)
            if (file.name.endsWith(".png") || file.name.endsWith(".jpg")) {
                Glide.with(context!!).load(file).into(viewHolder.item_icon)
            }

            if (file.name.endsWith(".txt")) {
                viewHolder.item_icon.setImageResource(R.drawable.ic_file_txt)
            }
            if (file.name.endsWith(".log")) {
                viewHolder.item_icon.setImageResource(R.drawable.ic_file_log)
            }
            val formatSize = FileUtils.getFormatSize(file)
            viewHolder.item_details.text = formatSize

        } else {
            viewHolder.item_icon.setImageResource(R.drawable.ic_folder_vector)
            val subFile = FileUtils.getAllFileDirByDir(file)
            val formatSize = FileUtils.getFormatSize(file)
            viewHolder.item_details.text = "${subFile.size}个项目 | $formatSize"
        }
        val lastModified = Date(file.lastModified())
        val strDateFormat = "yyyy/MM/dd HH:mm:ss"
        val sdf = SimpleDateFormat(strDateFormat, Locale.CHINA)
        val format = sdf.format(lastModified)
        viewHolder.item_date.text = format

        viewHolder.itemView.setOnClickListener {
            onClickListener?.onClick(i, file)
        }

        viewHolder.itemView.setOnLongClickListener {
            onClickListener?.onLongClick(i, file)
            true
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item_name: TextView
        var item_icon: ImageView
        var item_details: TextView
        var item_date: TextView

        init {
            item_name = itemView.findViewById(R.id.item_name)
            item_icon = itemView.findViewById(R.id.item_icon)
            item_details = itemView.findViewById(R.id.item_details)
            item_date = itemView.findViewById(R.id.item_date)
        }
    }

    interface OnFileClickListener {
        fun onClick(index: Int, file: File)
        fun onLongClick(index: Int, file: File)
    }
}

