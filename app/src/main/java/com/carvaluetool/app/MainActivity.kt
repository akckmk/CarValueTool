package com.carvaluetool.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carvaluetool.app.ui.CarValueTheme
import com.carvaluetool.app.ui.FormScreen
import com.carvaluetool.app.ui.ResultScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarValueTheme {
                val vm: ValuationViewModel = viewModel()
                val report = vm.report
                if (report == null) {
                    FormScreen(vm)
                } else {
                    BackHandler(onBack = vm::backToForm)
                    ResultScreen(report = report, onBack = vm::backToForm, onStartOver = vm::startOver)
                }
            }
        }
    }
}
