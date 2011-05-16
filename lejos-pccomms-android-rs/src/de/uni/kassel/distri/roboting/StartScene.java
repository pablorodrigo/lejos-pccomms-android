package de.uni.kassel.distri.roboting;

import javax.microedition.khronos.opengles.GL10;

import lejos.nxt.Motor;

import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.raesch.java.lpcca.BootUpActivity;
import org.raesch.java.lpcca.service.InterfaceLPCCARemoteService;
import org.raesch.java.lpcca.service.LPCCARemoteService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class StartScene extends Scene {

	private BootUpActivity bootUpActivity;
	private Boolean connected = false;
	private Long currentTimeMillis;
	private Long nextTimeMillis;
	private long offset = 100;

	private final ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			SharedRessource.myRemoteService = InterfaceLPCCARemoteService.Stub.asInterface(service);
			if (SharedRessource.myRemoteService != null) {
				try {
					SharedRessource.myRemoteService.requestConnectionToNXT();
					connected = true;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	public StartScene(int pLayerCount, final BootUpActivity bootUpActivity) {
		super(pLayerCount);
		this.bootUpActivity = bootUpActivity;

		SharedRessource.startServiceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bootUpActivity.startService(new Intent(LPCCARemoteService.class.getName()));
				bootUpActivity.bindService(new Intent(LPCCARemoteService.class.getName()), serviceConnection, Context.BIND_AUTO_CREATE);

				Log.i("button", "start service clicked");
			}
		});
		SharedRessource.requestConnectionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("button", "request clicked");
			}
		});
		this.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));

		final int centerX = (SharedRessource.CAMERA_WIDTH - SharedRessource.mFaceTextureRegion.getWidth()) / 2;
		final int centerY = (SharedRessource.CAMERA_HEIGHT - SharedRessource.mFaceTextureRegion.getHeight()) / 2;


		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(SharedRessource.CAMERA_WIDTH / 2
				- SharedRessource.mOnScreenControlBaseTextureRegion.getWidth() / 2, SharedRessource.CAMERA_HEIGHT 
				- SharedRessource.mOnScreenControlBaseTextureRegion.getHeight(), SharedRessource.mCamera, SharedRessource.mOnScreenControlBaseTextureRegion,
				SharedRessource.mOnScreenControlKnobTextureRegion, 0.1f, 200, new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

						if (connected == true) {

							currentTimeMillis = System.currentTimeMillis();
							if (nextTimeMillis == null || currentTimeMillis > nextTimeMillis) {

								float mA = 0;
								float mB = 0;
								if (pValueX > 0) {
									mA =  0;
									mB = pValueX * 1000 / 2;
								} else {
									mA = Math.abs(pValueX) * 1000 / 2;
									mB = 0;

								}

								if (pValueY > 0) {
									mA += pValueY * 1000 / 2;
									mB += pValueY * 1000 / 2;
									Motor.A.setSpeed((int) mA);
									Motor.B.setSpeed((int) (mB));
									Motor.A.rotate(10000, true);
									Motor.B.rotate(10000, true);

								}
								if (pValueY < 0) {
									mA += Math.abs(pValueY) * 1000 / 2;
									mB += Math.abs(pValueY) * 1000 / 2;
									Motor.A.setSpeed((int) mA);
									Motor.B.setSpeed((int) (mB));
									Motor.A.rotate((-1) * 10000, true);
									Motor.B.rotate((-1) * 10000, true);

								}

								if (pValueX == 0 && pValueY == 0) {
									Motor.A.stop();
									Motor.B.stop();
								}

								nextTimeMillis = currentTimeMillis + offset;
								Log.i("Motor", "MotorA: " + mA);
								Log.i("Motor", "MotorB: " + mB);

							}

						}
					}

					@Override
					public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
						//face.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1)));
					}
				});

		IOnAreaTouchListener ioAreaTouchListener = new IOnAreaTouchListener() {

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch (pSceneTouchEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// pButton.press(1);
					Log.i("Button", "pressed");
					return true;

				case MotionEvent.ACTION_UP:
					// pButton.release(0);
					Log.i("Button", "released");
					return true;
				}

				return false;
			}
		};
		this.setOnAreaTouchListener(ioAreaTouchListener);
		// scene.getLastChild().attachChild(pButton);
		//this.registerTouchArea(face);

		analogOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.refreshControlKnobPosition();

		this.setChildScene(analogOnScreenControl);

	}

}
