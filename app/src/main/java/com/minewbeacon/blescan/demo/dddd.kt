package com.minewbeacon.blescan.demo

class dddd {
    val eventHandler = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> Log.d("Info", "positive button")
            DialogInterface.BUTTON_NEGATIVE -> Log.d("Info", "negative button")
            DialogInterface.BUTTON_NEUTRAL -> Log.d("Info", "neutral button")
        }
    }

    button2.setOnClickListener {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setTitle("Hello")
            .setMessage("Bye")
            .setPositiveButton("Yes", eventHandler)
            .setNegativeButton("No", eventHandler)
            .setNeutralButton("More", eventHandler)
            .show()
    }
}