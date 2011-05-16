/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Volumes/macbook/Media/Documents/workspaces/AndroidLejosAdapter/lejos-pccomms-android/src/org/raesch/java/lpcca/service/Navigator.aidl
 */
package org.raesch.java.lpcca.service;
public interface Navigator extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.raesch.java.lpcca.service.Navigator
{
private static final java.lang.String DESCRIPTOR = "org.raesch.java.lpcca.service.Navigator";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.raesch.java.lpcca.service.Navigator interface,
 * generating a proxy if needed.
 */
public static org.raesch.java.lpcca.service.Navigator asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.raesch.java.lpcca.service.Navigator))) {
return ((org.raesch.java.lpcca.service.Navigator)iin);
}
return new org.raesch.java.lpcca.service.Navigator.Stub.Proxy(obj);
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
case TRANSACTION_forward:
{
data.enforceInterface(DESCRIPTOR);
this.forward();
reply.writeNoException();
return true;
}
case TRANSACTION_left:
{
data.enforceInterface(DESCRIPTOR);
this.left();
reply.writeNoException();
return true;
}
case TRANSACTION_right:
{
data.enforceInterface(DESCRIPTOR);
this.right();
reply.writeNoException();
return true;
}
case TRANSACTION_stop:
{
data.enforceInterface(DESCRIPTOR);
this.stop();
reply.writeNoException();
return true;
}
case TRANSACTION_backward:
{
data.enforceInterface(DESCRIPTOR);
this.backward();
reply.writeNoException();
return true;
}
case TRANSACTION_connected:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.connected();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.raesch.java.lpcca.service.Navigator
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
public void forward() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_forward, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void left() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_left, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void right() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_right, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void backward() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_backward, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean connected() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_connected, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_forward = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_left = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_right = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_backward = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_connected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public void forward() throws android.os.RemoteException;
public void left() throws android.os.RemoteException;
public void right() throws android.os.RemoteException;
public void stop() throws android.os.RemoteException;
public void backward() throws android.os.RemoteException;
public boolean connected() throws android.os.RemoteException;
}
