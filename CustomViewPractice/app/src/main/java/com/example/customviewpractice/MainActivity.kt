package com.example.customviewpractice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.customviewpractice.Utils.IntentUtil.Companion.ACTION_CUSTOM_VIEW_SAMPLE
import com.example.customviewpractice.Utils.IntentUtil.Companion.CATEGORY_CUSTOM_VIEW_SAMPLE
import com.example.customviewpractice.Utils.IntentUtil.Companion.EXTRA_PREFIX_PATH

class MainActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPrefixPath:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mPrefixPath = intent.getStringExtra(EXTRA_PREFIX_PATH)?:"/"

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.adapter = EntryAdapter(getData())
        mRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getData(): List<Entry>{
        val intent = Intent(ACTION_CUSTOM_VIEW_SAMPLE, null)
        intent.addCategory(CATEGORY_CUSTOM_VIEW_SAMPLE)
        val infoList = packageManager.queryIntentActivities(intent, 0)

        val data = ArrayList<Entry>();

        val prefix = mPrefixPath
        val prefixPaths = mPrefixPath.split("/")

        for (info in infoList) {
            val labelSeq = info.loadLabel(packageManager)
            val label =  labelSeq?.toString()?:info.activityInfo.name
            val labelPaths = label.split("/")
            val nextLabel = labelPaths[prefixPaths.size - 1]

            if (!label.startsWith(prefix)) continue

            if (prefixPaths.size == labelPaths.size) {
                data.add(Entry(nextLabel, info.activityInfo.name,
                    activityIntent(info.activityInfo.applicationInfo.packageName, info.activityInfo.name)))
            } else {
                data.add(Entry(nextLabel, null, baseIntent("$prefix$nextLabel/")))
            }
        }
        return data
    }

    private fun baseIntent(prefixPath:String): Intent{
        val intent = Intent()
        intent.setComponent(componentName)
        intent.putExtra(EXTRA_PREFIX_PATH, prefixPath)
        return intent
    }

    private fun activityIntent(pkg :String, componentName: String) :Intent{
        val intent = Intent();
        intent.setClassName(pkg, componentName)
        return intent
    }

    private class Entry(val mName:String, val mContent:String?, val mIntent: Intent){
    }

    private class EntryAdapter(val mEntrys:List<Entry>):RecyclerView.Adapter<EntryView>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryView {
            return EntryView(LayoutInflater.from(parent.context).inflate(R.layout.entry_item, parent, false))
        }

        override fun getItemCount(): Int {
            return mEntrys.size
        }

        override fun onBindViewHolder(holder: EntryView, position: Int) {
            holder.mEntryName.text = mEntrys[position].mName
            holder.mEntryContent.text = mEntrys[position].mContent
            holder.mItemView.setOnClickListener {
                it.context.startActivity(mEntrys[position].mIntent)
            }
        }

    }

    private class EntryView(val mItemView: View):RecyclerView.ViewHolder(mItemView) {

        val mEntryName:TextView = mItemView.findViewById(R.id.entry_name)
        val mEntryContent:TextView = mItemView.findViewById(R.id.entry_content)

    }

}