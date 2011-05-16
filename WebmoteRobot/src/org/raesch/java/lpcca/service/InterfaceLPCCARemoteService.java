/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Volumes/macbook/Media/Documents/workspaces/AndroidLejosAdapter/lejos-pccomms-android/src/org/raesch/java/lpcca/service/InterfaceLPCCARemoteService.aidl
 */
package org.raesch.java.lpcca.service;
public interface InterfaceLPCCARemoteService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.raesch.java.lpcca.service.InterfaceLPCCARemoteService
{
private static final java.lang.String DESCRIPTOR = "org.raesch.java.lpcca.service.InterfaceLPCCARemoteService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.raesch.java.lpcca.service.InterfaceLPCCARemoteService interface,
 * generating a proxy if needed.
 */
public static org.raesch.java.lpcca.service.InterfaceLPCCARemoteService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.raesch.java.lpcca.service.InterfaceLPCCARemoteService))) {
return ((org.raesch.java.lpcca.service.InterfaceLPCCARemoteService)iin);
}
return new org.raesch.java.lpcca.service.InterfaceLPCCARemoteService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_requestConnectionToNXT:
{
data.enforceInterface(DESCRIPTOR);
this.requestConnectionToNXT();
reply.writeNoException();
return true;
}
case TRANSACTION_establishBTConnection:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.establishBTConnection(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_get:
{
data.enforceInterface(DESCRIPTOR);
org.raesch.java.lpcca.service.Navigator _result = this.get();
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.raesch.java.lpcca.service.InterfaceLPCCARemoteService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void requestConnectionToNXT() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_requestConnectionToNXT, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void establishBTConnection(java.lang.String deviceKey, java.lang.String deviceMac) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(deviceKey);
_data.writeString(deviceMac);
mRemote.transact(Stub.TRANSACTION_establishBTConnection, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public org.raesch.java.lpcca.service.Navigator get() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
org.raesch.java.lpcca.service.Navigator _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_get, _data, _reply, 0);
_reply.readException();
_result = org.raesch.java.lpcca.service.Navigator.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_requestConnectionToNXT = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_establishBTConnection = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_get = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void requestConnectionToNXT() throws android.os.RemoteException;
public void establishBTConnection(java.lang.String deviceKey, java.lang.String deviceMac) throws android.os.RemoteException;
public org.raesch.java.lpcca.service.Navigator get() throws android.os.RemoteException;
}
