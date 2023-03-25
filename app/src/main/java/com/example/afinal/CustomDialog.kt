package com.example.afinal

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class CustomDialog(datas: AdditionalData?) : DialogFragment() {
    private var data: AdditionalData? = datas

    var cityView: TextView? = null
    var coordView: TextView? = null
    var tempView: TextView? = null
    var seemsView: TextView? = null
    var descView: TextView? = null
    var humidityView: TextView? = null
    var pressureView: TextView? = null
    var windView: TextView? = null
    var sunriseView: TextView? = null
    var sunsetView: TextView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val rootView = inflater.inflate(R.layout.dialog_layout, null)

        // сохраняем ссылки на TextView элементы
        cityView = rootView.findViewById(R.id.dialog_city)
        coordView = rootView.findViewById(R.id.dialog_cords)
        tempView = rootView.findViewById(R.id.dialog_temp)
        seemsView = rootView.findViewById(R.id.dialog_temp_seems)
        descView = rootView.findViewById(R.id.dialog_desc)
        humidityView = rootView.findViewById(R.id.dialog_humidity)
        pressureView = rootView.findViewById(R.id.dialog_pressure)
        windView = rootView.findViewById(R.id.dialog_wind)
        sunriseView = rootView.findViewById(R.id.dialog_sunrise)
        sunsetView = rootView.findViewById(R.id.dialog_sunset)

        builder.setView(rootView)
            .setPositiveButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }
        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_layout, container, false)

        cityView?.text = data?.dialog_city
        coordView?.text = data?.dialo_coord
        tempView?.text = data?.dialog_temp
        seemsView?.text = data?.dialog_seems
        descView?.text = data?.dialog_desc
        humidityView?.text = data?.dialog_humidity
        pressureView?.text = data?.dialog_pressure
        windView?.text = data?.dialog_wind
        sunriseView?.text = data?.dialog_sunrise
        sunsetView?.text = data?.dialog_sunset
        return rootView
    }
}
