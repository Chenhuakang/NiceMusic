package com.lzx.musiclibrary.aidl.source;

import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by xian on 2018/4/2.
 */

public interface IFileNameGenerator extends android.os.IInterface {

    public static abstract class Stub extends android.os.Binder implements IFileNameGenerator {
        private static final java.lang.String DESCRIPTOR = "IFileNameGenerator";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IFileNameGenerator asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IFileNameGenerator))) {
                return ((IFileNameGenerator) iin);
            }
            return new IFileNameGenerator.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_generate: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _result = this.generate(_arg0);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IFileNameGenerator {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public String generate(String url) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_generate, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_generate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    }

    String generate(String url) throws RemoteException;
}
