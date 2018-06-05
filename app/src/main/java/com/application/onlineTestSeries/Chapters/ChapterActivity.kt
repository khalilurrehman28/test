package com.application.onlineTestSeries.Chapters

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.application.onlineTestSeries.Chapters.Adapter.ChapterAdapter
import com.application.onlineTestSeries.Chapters.Models.ChapterData
import com.application.onlineTestSeries.Chapters.Models.ChapterResponse
import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration
import com.application.onlineTestSeries.Network.ApiClient
import com.application.onlineTestSeries.Network.ApiService
import com.application.onlineTestSeries.R
import com.application.onlineTestSeries.SectionActivity.sectionPreviewActivity
import com.application.onlineTestSeries.Utils.checkInternetState
import es.dmoral.toasty.Toasty
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.kotlin.createObject
import kotlinx.android.synthetic.main.activity_chapter.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ChapterActivity : AppCompatActivity(),ChapterAdapter.chapterAdapterListner {

    lateinit var chapterList:MutableList<ChapterData>
    lateinit var chapterList1:MutableList<ChapterData>

    lateinit var adapter:ChapterAdapter
    lateinit var ColorArray: MutableList<Int>
    lateinit var rand: Random
    lateinit var realm: Realm
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra("courseName")
        try {
            realm = Realm.getDefaultInstance()
        } catch (e: Exception) {
            // Get a Realm instance for this thread
            val config = RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build()
            realm = Realm.getInstance(config)
        }
        rand = Random()
        chapterList = ArrayList()
        chapterList1 = ArrayList()

        adapter = ChapterAdapter(this, chapterList,this)
        val mLayoutManager = GridLayoutManager(this, 1)
        ColorArray = ArrayList()

        chapterListRecycler.layoutManager= mLayoutManager
        chapterListRecycler.addItemDecoration(GridSpacingItemDecoration(1, dpToPx(1), true))
        chapterListRecycler.itemAnimator=DefaultItemAnimator()
        chapterListRecycler.adapter = adapter

        val MY_COLORS = intArrayOf(Color.rgb(192, 0, 0), Color.rgb(0, 229, 238), Color.rgb(255, 192, 0), Color.rgb(127, 127, 127), Color.rgb(146, 208, 80), Color.rgb(0, 176, 80), Color.rgb(79, 129, 189), Color.rgb(0, 128, 128), Color.rgb(0, 139, 69), Color.rgb(255, 215, 0), Color.rgb(255, 128, 0), Color.rgb(255, 106, 106))
        for (item in MY_COLORS) {
            ColorArray.add(item)
        }
        val results = realm.where(ChapterData::class.java).equalTo("cOURSEIDFK", intent.getStringExtra("courseID")).findAll()
        if (results.size>0){
            getDataFromRealmDataBase(results)
        }else{
            getDatafromServer(intent.getStringExtra("courseID"))
        }
    }

    override fun onContactSelected(ChapterData: ChapterData?) {
        val i = Intent(applicationContext, sectionPreviewActivity::class.java)
        i.putExtra("chapterID", "" + ChapterData?.chapterid)
        i.putExtra("courseName", "" + title)
        startActivity(i)
        val handler = Handler()
        handler.postDelayed({
            if (!searchView.isIconified) {
                searchView.onActionViewCollapsed()
            }
        }, 140)
    }

    private fun getDataFromRealmDataBase(results: RealmResults<ChapterData>) {
        if (results.size>0) {
            for (item in results) {
                var n = rand.nextInt(ColorArray.size)
                if (n == ColorArray.size) {
                    n -= 1
                }
                val data = ChapterData()
                data.chapterdate = item.chapterdate
                data.chapterid = item.chapterid
                data.chaptertitle = item.chaptertitle
                data.chaptersubheaD1 = item.chaptersubheaD1
                data.chaptersubheaD2 = item.chaptersubheaD2
                data.chaptersectiondesc = item.chaptersectiondesc
                data.colorCirlce = ColorArray[n]
                data.courseidfk = item.courseidfk
                chapterList.add(data)
                adapter.notifyDataSetChanged()
            }
        }else{
            chapterList.clear()
            adapter.notifyDataSetChanged()
            noChaptersFound.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_courses, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.search_action).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Search chapters..."
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.filter.filter(query.toLowerCase().trim())
                if (adapter.itemCount < 1) {
                    chapterListRecycler.visibility = View.GONE
                    noSearchResultFound.visibility = View.VISIBLE
                    noSearchResultFound.text = "No results found '${query.trim()}'"
                } else {
                    chapterListRecycler.visibility = View.VISIBLE
                    noSearchResultFound.visibility = View.GONE
                }
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                adapter.filter.filter(query.toLowerCase().trim())
                if (adapter.itemCount < 1) {
                    chapterListRecycler.visibility = View.GONE
                    noSearchResultFound.visibility = View.VISIBLE
                    noSearchResultFound.text = "No results found '" + query.trim() + "'"
                } else {
                    chapterListRecycler.visibility = View.VISIBLE
                    noSearchResultFound.visibility = View.GONE
                }
                return false
            }
        })
        searchView.setOnCloseListener {
            chapterListRecycler.visibility = View.VISIBLE
            noSearchResultFound.visibility = View.GONE
            false
        }
        return true
    }

    private fun getDatafromServer(courseID:String) {
        if (!checkInternetState.getInstance(this).isOnline) {
            showWarning("No Internet Connection")
        } else {
            loadData(courseID)
        }
    }

    private fun loadData(courseID: String) {
        progressBar.visibility= View.VISIBLE
        val service = ApiClient.client.create(ApiService::class.java)
        val userCall = service.get_chapter_list(courseID)
        userCall.enqueue(object : Callback<ChapterResponse> {
            override fun onResponse(call: Call<ChapterResponse>, response: Response<ChapterResponse>) {
                progressBar.visibility= View.GONE
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        chapterListRecycler.visibility = View.VISIBLE
                        val course = response.body()!!.data
                        realm.beginTransaction()
                        val results = realm.where(ChapterData::class.java).equalTo("cOURSEIDFK",courseID).findAll()
                        results.deleteAllFromRealm()
                        realm.commitTransaction()
                        for (item in course) {
                            var n = rand.nextInt(ColorArray.size)
                            if (n == ColorArray.size) {
                                n -= 1
                            }
                            println("color of item $n")
                            realm.beginTransaction()
                            val dataRealm: ChapterData = realm.createObject<ChapterData>(item.chapterid)
                            dataRealm.chapterdate = item.chapterdate
                            dataRealm.chaptertitle = item.chaptertitle
                            dataRealm.chaptersubheaD1 = item.chaptersubheaD1
                            dataRealm.chaptersubheaD2 = item.chaptersubheaD2
                            dataRealm.chaptersectiondesc = item.chaptersectiondesc
                            dataRealm.colorCirlce = ColorArray[n]
                            dataRealm.courseidfk = item.courseidfk

                            val dataServer= ChapterData()
                            dataServer.chapterid = item.chapterid
                            dataServer.chapterdate = item.chapterdate
                            dataServer.chaptertitle = item.chaptertitle
                            dataServer.chaptersubheaD1 = item.chaptersubheaD1
                            dataServer.chaptersubheaD2 = item.chaptersubheaD2
                            dataServer.chaptersectiondesc = item.chaptersectiondesc
                            dataServer.colorCirlce = ColorArray[n]
                            dataServer.courseidfk = item.courseidfk
                            realm.commitTransaction()
                            chapterList.add(dataServer)
                            chapterList1.add(dataRealm)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        noChaptersFound.visibility = View.VISIBLE
                        showWarning("No chapter available yet")
                    }
                } else {
                    showError("Something went wrong.")
                }
            }
            override fun onFailure(call: Call<ChapterResponse>, t: Throwable) {
                Log.d("onFailure", t.toString())
                progressBar.visibility= View.GONE
            }
        })
    }

    private fun showSuccess(s: String) {
        Toasty.success(this, s, Toast.LENGTH_LONG, true).show()
    }

    private fun showWarning(s: String) {
        Toasty.warning(this, s, Toast.LENGTH_LONG, true).show()
    }
    private fun showError(s: String) {
        Toasty.error(this, s, Toast.LENGTH_LONG, true).show()
    }

    private fun dpToPx(i: Int): Int {
        val r = resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i.toFloat(), r.displayMetrics))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if(id == R.id.refresh_data){
            chapterList.clear()
            chapterList1.clear()
            getDatafromServer(intent.getStringExtra("courseID"))
            chapterListRecycler.visibility = View.GONE
        }
        return super.onOptionsItemSelected(item)
    }
}