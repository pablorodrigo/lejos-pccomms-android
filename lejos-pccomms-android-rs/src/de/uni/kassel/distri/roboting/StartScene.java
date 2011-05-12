package de.uni.kassel.distri.roboting;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
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
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.util.Log;
import android.view.MotionEvent;

public class StartScene extends Scene {


	

	public StartScene(int pLayerCount) {
		super(pLayerCount);

		this.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));

		final int centerX = (SharedRessource.CAMERA_WIDTH - SharedRessource.mFaceTextureRegion.getWidth()) / 2;
		final int centerY = (SharedRessource.CAMERA_HEIGHT - SharedRessource.mFaceTextureRegion
				.getHeight()) / 2;

		/* Create the face and add it to the scene. */
		final Sprite face = new Sprite(centerX, centerY,
				SharedRessource.mFaceTextureRegion);
		this.getLastChild().attachChild(face);

		final PhysicsHandler physicsHandler = new PhysicsHandler(face);
		face.registerUpdateHandler(physicsHandler);

		this.getLastChild().attachChild(face);

		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(
				SharedRessource.CAMERA_WIDTH/2-SharedRessource.mOnScreenControlBaseTextureRegion.getWidth()/2, SharedRessource.CAMERA_HEIGHT/4*3
						- SharedRessource.mOnScreenControlBaseTextureRegion.getHeight(),
						SharedRessource.mCamera, SharedRessource.mOnScreenControlBaseTextureRegion,
						SharedRessource.mOnScreenControlKnobTextureRegion, 0.1f, 200,
				new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {
						physicsHandler
								.setVelocity(pValueX * 100, pValueY * 100);
					}

					@Override
					public void onControlClick(
							final AnalogOnScreenControl pAnalogOnScreenControl) {
						face.registerEntityModifier(new SequenceEntityModifier(
								new ScaleModifier(0.25f, 1, 1.5f),
								new ScaleModifier(0.25f, 1.5f, 1)));
					}
				});

		IOnAreaTouchListener ioAreaTouchListener = new IOnAreaTouchListener() {

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					ITouchArea pTouchArea, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
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
		this.registerTouchArea(face);

		analogOnScreenControl.getControlBase().setBlendFunction(
				GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.refreshControlKnobPosition();

		this.setChildScene(analogOnScreenControl);

	}

}
