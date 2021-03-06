package chiachen.example.com.openglpractice;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GLSurfaceView glSurfaceView =
				(android.opengl.GLSurfaceView) findViewById(R.id.mGLSurfaceView);
		
		glSurfaceView.setEGLContextClientVersion(2);
		glSurfaceView.setRenderer(new MyRenderer());
		glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
	}
	static class MyRenderer implements GLSurfaceView.Renderer {
		// private static final String VERTEX_SHADER = "attribute vec4 vPosition;\n"
		// 		+ "void main() {\n"
		// 		+ "  gl_Position = vPosition;\n"
		// 		+ "}";
		private static final String VERTEX_SHADER = "attribute vec4 vPosition;\n"
				+ "uniform mat4 uMVPMatrix;\n"
				+ "void main() {\n"
				+ "  gl_Position = uMVPMatrix * vPosition;\n"
				+ "}";
		private static final String FRAGMENT_SHADER = "precision mediump float;\n"
				+ "void main() {\n"
				+ "  gl_FragColor = vec4(0.5,0,0,1);\n"
				+ "}";
		// private static final float[] VERTEX = {   // in counterclockwise order:
		// 		0, 1, 0.0f, // top
		// 		-0.5f, -1, 0.0f, // bottom left
		// 		1f, -1, 0.0f,  // bottom right
		// };
		//
		private static final float[] VERTEX = {   // in counterclockwise order:
				1, 1, 0,   // top right
				-1, 1, 0,  // top left
				-1, -1, 0, // bottom left
				1, -1, 0,  // bottom right
		};
		private static final short[] VERTEX_INDEX = { 0, 1, 2, 0, 2, 3 };
		private final ShortBuffer mVertexIndexBuffer;
		
		private final FloatBuffer mVertexBuffer;
		
		private int mProgram;
		private int mPositionHandle;
		
		//-----
		private int mMatrixHandle;
		private final float[] mProjectionMatrix = new float[16];
		private final float[] mCameraMatrix = new float[16];
		private final float[] mMVPMatrix = new float[16];
		
		MyRenderer() {
			// mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
			// 		.order(ByteOrder.nativeOrder())
			// 		.asFloatBuffer()
			// 		.put(VERTEX);
			// mVertexBuffer.position(0);
			mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer()
					.put(VERTEX);
			mVertexBuffer.position(0);
			
			mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
					.order(ByteOrder.nativeOrder())
					.asShortBuffer()
					.put(VERTEX_INDEX);
			mVertexIndexBuffer.position(0);
		}
		
		@Override
		public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		}
		
		@Override
		public void onSurfaceChanged(GL10 unused, int width, int height) {
			Log.e("test","onSurfaceChanged");
			mProgram = GLES20.glCreateProgram();
			int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
			int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
			GLES20.glAttachShader(mProgram, vertexShader);
			GLES20.glAttachShader(mProgram, fragmentShader);
			GLES20.glLinkProgram(mProgram);
			
			mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
			//---
			mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
			
			float ratio = (float) width / height;
			//public static void frustumM(float[] m, int offset,
			//float left, float right, float bottom, float top,
			//float near, float far)
			
			// Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
			Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -ratio, ratio, 3, 7);
			// public static void setLookAtM(float[] rm, int rmOffset,
			// float eyeX, float eyeY, float eyeZ,
			// float centerX, float centerY, float centerZ,
			// float upX, float upY,float upZ)
			Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 7, 0, 0, 0, 0,1,0 );
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mCameraMatrix, 0);
		}
		
		@Override
		public void onDrawFrame(GL10 unused) {
			// Log.e("test","onDrawFrame");
			// GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
			//
			// GLES20.glUseProgram(mProgram);
			//
			// GLES20.glEnableVertexAttribArray(mPositionHandle);
			//
			// GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);
			//
			// //----
			// GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
			// //----
			// GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
			//
			// GLES20.glDisableVertexAttribArray(mPositionHandle);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
			
			GLES20.glUseProgram(mProgram);
			
			GLES20.glEnableVertexAttribArray(mPositionHandle);
			GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0,
					mVertexBuffer);
			
			GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
			
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
					GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
			
			GLES20.glDisableVertexAttribArray(mPositionHandle);
		}
		
		static int loadShader(int type, String shaderCode) {
			int shader = GLES20.glCreateShader(type);
			GLES20.glShaderSource(shader, shaderCode);
			GLES20.glCompileShader(shader);
			return shader;
		}
		
	}
}
