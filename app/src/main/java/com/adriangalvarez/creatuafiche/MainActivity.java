package com.adriangalvarez.creatuafiche;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

	public static final int REQUEST_CODE = 101;
	private ImageView imgImage;
	private Button btnCamera;
	private Button btnShare;
	private Button btnReset;

	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		imgImage = findViewById( R.id.imgImage );
		btnCamera = findViewById( R.id.btnCamera );
		btnShare = findViewById( R.id.btnShare );
		btnReset = findViewById( R.id.btnReset );

		btnCamera.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View view ){
				Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
				startActivityForResult( intent, REQUEST_CODE );
			}
		} );

		btnShare.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View view ){
				Intent sendIntent = new Intent( Intent.ACTION_SEND );
				sendIntent.setDataAndType( takeScreenshot(), "image/jpeg" );
				startActivity( Intent.createChooser( sendIntent, getResources().getText( R.string.btnShare ) ) );
			}
		} );

		btnReset.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View view ){
				( ( TextView ) findViewById( R.id.txtVosAsk ) ).setText( "" );
				( ( TextView ) findViewById( R.id.txtProfesionAsk ) ).setText( "" );

				findViewById( R.id.layAsk ).setVisibility( View.VISIBLE );
				findViewById( R.id.relAfiche ).setVisibility( View.GONE );

				btnShare.setVisibility( View.GONE );
				btnReset.setVisibility( View.GONE );
			}
		} );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ){
		super.onActivityResult( requestCode, resultCode, data );

		Bundle bundle = data.getExtras();
		if( bundle != null ){
			findViewById( R.id.layAsk ).setVisibility( View.GONE );
			findViewById( R.id.relAfiche ).setVisibility( View.VISIBLE );

			btnShare.setVisibility( View.VISIBLE );
			btnReset.setVisibility( View.VISIBLE );

			Bitmap bitmap = (Bitmap) bundle.get( "data" );
			imgImage.setImageBitmap( bitmap );

			( ( TextView ) findViewById( R.id.txtVos ) ).setText( ( (TextView) findViewById( R.id.txtVosAsk ) ).getText() );
			( ( TextView ) findViewById( R.id.txtProfesion ) ).setText( ( (TextView) findViewById( R.id.txtProfesionAsk ) ).getText() );
		}
	}

	private Uri takeScreenshot(){
		try{
			String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "afiche.jpg";

			View v1 = findViewById( R.id.relAfiche ); //getWindow().getDecorView().getRootView();
			v1.setDrawingCacheEnabled( true );
			Bitmap bitmap = Bitmap.createBitmap( v1.getDrawingCache() );
			v1.setDrawingCacheEnabled( false );

			File imageFile = new File( mPath );

			if( !imageFile.exists() ){
				imageFile.createNewFile();
			}

			FileOutputStream outputStream = new FileOutputStream( imageFile );
			int quality = 100;
			bitmap.compress( Bitmap.CompressFormat.JPEG, quality, outputStream );
			outputStream.flush();
			outputStream.close();

			return Uri.fromFile( imageFile );
		}catch( Throwable e ){
			e.printStackTrace();
		}

		return null;
	}
}
