package xyz.mufanc.amic.module.service

import android.os.Bundle
import android.os.ResultReceiver
import java.util.concurrent.Semaphore

class CommandResultReceiver(
    private val semaphore: Semaphore
) : ResultReceiver(null) {
    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        semaphore.release()
    }
}
