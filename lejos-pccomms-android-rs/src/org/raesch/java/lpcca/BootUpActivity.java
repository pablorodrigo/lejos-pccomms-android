package org.raesch.java.lpcca;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.LayoutGameActivity;

import android.widget.Button;
import de.uni.kassel.distri.roboting.SharedRessource;
import de.uni.kassel.distri.roboting.StartScene;

public class BootUpActivity extends LayoutGameActivity {

	private Button startServiceButton;
	private Button requestConnectionButton;

	@Override
	public Engine onLoadEngine() {

		SharedRessource.mCamera = new Camera(0, 0,
				SharedRessource.CAMERA_WIDTH, SharedRessource.CAMERA_HEIGHT);

		return new Engine(
				new EngineOptions(true, ScreenOrientation.PORTRAIT,
						new RatioResolutionPolicy(SharedRessource.CAMERA_WIDTH,
								SharedRessource.CAMERA_HEIGHT),
						SharedRessource.mCamera));

	}

	@Override
	public void onLoadResources() {
		SharedRessource.mTexture = new Texture(32, 32,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		SharedRessource.mFaceTextureRegion = TextureRegionFactory
				.createFromAsset(SharedRessource.mTexture, this,
						"gfx/face_box.png", 0, 0);
		// this.pButtonTextureRegion = new TiledTextureRegion(mTexture, 40, 30,
		// 50, 50, 1, 1);
		// this.pButton = new ButtonBean(3,3,pButtonTextureRegion);

		this.mEngine.getTextureManager().loadTexture(SharedRessource.mTexture);
		SharedRessource.mOnScreenControlTexture = new Texture(256, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		SharedRessource.mOnScreenControlBaseTextureRegion = TextureRegionFactory
				.createFromAsset(SharedRessource.mOnScreenControlTexture, this,
						"gfx/onscreen_control_base.png", 0, 0);
		SharedRessource.mOnScreenControlKnobTextureRegion = TextureRegionFactory
				.createFromAsset(SharedRessource.mOnScreenControlTexture, this,
						"gfx/onscreen_control_knob.png", 128, 0);

		this.mEngine.getTextureManager().loadTextures(SharedRessource.mTexture,
				SharedRessource.mOnScreenControlTexture);

		startServiceButton = (Button) findViewById(R.id.button1);

		requestConnectionButton = (Button) findViewById(R.id.button2);

		SharedRessource.requestConnectionButton = requestConnectionButton;
		SharedRessource.startServiceButton = startServiceButton;

		this.mEngine.setScene(new StartScene(1, this));
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public Scene onLoadScene() {
		// TODO Auto-generated method stub
		return this.getEngine().getScene();
	}

	  @Override
      protected int getLayoutID() {
              return R.layout.main;
      }

      @Override
      protected int getRenderSurfaceViewID() {
              return R.id.xmllayoutexample_rendersurfaceview;
      }

}
