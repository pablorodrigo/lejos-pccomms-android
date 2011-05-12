package de.uni.kassel.distri.roboting;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.raesch.java.lpcca.service.InterfaceLPCCARemoteService;

import android.widget.Button;

public class SharedRessource {
	public static final int CAMERA_WIDTH = 720;
	public static final int CAMERA_HEIGHT = 480;

	public static Camera mCamera;
	public static Texture mTexture;
	public static TextureRegion mFaceTextureRegion;
	public static Texture mOnScreenControlTexture;
	public static TextureRegion mOnScreenControlBaseTextureRegion;
	public static TextureRegion mOnScreenControlKnobTextureRegion;

	public static Button startServiceButton;
	public static Button requestConnectionButton;
	public static InterfaceLPCCARemoteService myRemoteService = null;

}
