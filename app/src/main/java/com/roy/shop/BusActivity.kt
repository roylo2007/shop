package com.roy.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_bus.*
import kotlinx.android.synthetic.main.row_bus.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class BusActivity : AppCompatActivity(), AnkoLogger {

    lateinit var busses: Busses
    val retrofit = Retrofit.Builder()
        .baseUrl("https://data.tycg.gov.tw/opendata/datalist/datasetMeta/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus)
        doAsync {
            val busService = retrofit.create(BusService::class.java)
            busses = busService.listBuses().execute().body()!!
            busses.datas.forEach {
                info("${it.BusID} ${it.Speed}")
            }
            uiThread {
                recycler.layoutManager = LinearLayoutManager(it)
                recycler.setHasFixedSize(true)
                recycler.adapter = BusAdapter()
            }
        }
    }

    inner class BusAdapter():RecyclerView.Adapter<BusHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_bus, parent, false)
            return BusHolder(view)
        }

        override fun getItemCount(): Int {
            val size = busses.datas.size?:0
            return size
        }

        override fun onBindViewHolder(holder: BusHolder, position: Int) {
            holder.bindBus(busses.datas[position])
        }

    }

    inner class BusHolder(view: View): RecyclerView.ViewHolder(view){
        val idText = view.bus_id
        val speedText = view.bus_speed
        val routeidText = view.bus_routeid

        fun bindBus(bus: Bus){
            idText.text = bus.BusID
            speedText.text = bus.Speed
            routeidText.text = bus.RouteID
        }
    }
}

data class Busses(
    val datas: List<Bus>
)

data class Bus(
    val Azimuth: String,
    val BusID: String,
    val BusStatus: String,
    val DataTime: String,
    val DutyStatus: String,
    val GoBack: String,
    val Latitude: String,
    val Longitude: String,
    val ProviderID: String,
    val RouteID: String,
    val Speed: String,
    val ledstate: String,
    val sections: String
)

interface BusService{
    @GET("download?id=b3abedf0-aeae-4523-a804-6e807cbad589&rid=bf55b21a-2b7c-4ede-8048-f75420344aed")
    fun listBuses(): Call<Busses>
}