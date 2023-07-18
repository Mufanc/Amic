package xyz.mufanc.amic.utils

import android.content.Context
import android.os.Binder
import android.os.Parcel
import android.os.ServiceManager
import android.os.Process
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class XposedService : IXposedService.Stub() {
    companion object {
        private val HIJACK_TRANSACTION = "AMIC"
            .reversed()
            .mapIndexed { i, ch -> ch.code.shl(i * 8) }
            .sum()

        private lateinit var INSTANCE: IXposedService

        fun installHooks(classLoader: ClassLoader) {
            XposedHelpers.findAndHookMethod(
                "android.Content.IClipboard", classLoader,
                "onTransact",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        if (param.args[0] != HIJACK_TRANSACTION) return
                        if (Binder.getCallingUid() >= Process.SHELL_UID) return

                        val service = getService() ?: return
                        val resp = param.args[2] as Parcel

                        resp.writeStrongBinder(service.asBinder())
                    }
                }
            )
        }

        fun getService(): IXposedService? {
            if (Process.myUid() == Process.SYSTEM_UID) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = XposedService()
                }
                return INSTANCE
            }

            val ics = ServiceManager.getService(Context.CLIPBOARD_SERVICE)
            val req = Parcel.obtain()
            val res = Parcel.obtain()

            try {
                ics.transact(HIJACK_TRANSACTION, req, res, 0)
                return asInterface(res.readStrongBinder())
            } finally {
                req.recycle()
                req.recycle()
            }
        }
    }
}