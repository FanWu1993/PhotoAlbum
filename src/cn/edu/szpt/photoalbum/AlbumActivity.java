package cn.edu.szpt.photoalbum;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.szpt.photoalbum.customview.MyGallery;

import com.example.photoalbum.R;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ViewSwitcher.ViewFactory;

public class AlbumActivity extends Activity implements ViewFactory {
	private ImageSwitcher imgs;
	private Spinner mSpinner;
	private static final String PATH = "/sdcard/photo/";
	private String[] photo_folder;
	private ArrayAdapter<String> adapter;
	public String folder_name;
	MyGallery myGallery;
	public ImageView imgview;
	private List<String> arr = new ArrayList<String>();
	ImageAdapter ia;
	
	Spinner.OnItemSelectedListener spinner = new Spinner.OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (arg2 == photo_folder.length - 1) {
				folder_name = "";
				show();
				getPhotoList();
				ia.notifyDataSetChanged();
			} else {
				folder_name = photo_folder[arg2];
				show();
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.albummain);
		myGallery = (MyGallery) findViewById(R.id.myGallery1);
		imgs = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
		mSpinner = (Spinner) findViewById(R.id.spinner1);
		imgview = (ImageView) findViewById(R.id.imageView1);
		listPhotoFolder(PATH);
		imgs.setFactory(this);
		imgs.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.slide_in_left));
		imgs.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right));

	}

	public View makeView() {
		ImageView i = new ImageView(this);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return i;
	}

	public void show() {

		myGallery.setAdapter(ia=new ImageAdapter(this));
	}

	class ImageAdapter extends BaseAdapter {
		// 用来设置 ImageView的风格
		int mGalleryItemBackground;
		private Context context;
		ImageView is;
		int size;
		// 图片的资源 ID
		private LayoutInflater inflate;

		// 构造函数
		public ImageAdapter(Context context) {
			this.context = context;
			arr = new ArrayList<String>();
			listPhotos(folder_name);
			inflate = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			myGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					setImgs(arg2);
				}

				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}

		public void setImgs(int position) {
			Bitmap bm = BitmapFactory.decodeFile(arr.get(position));
			is = new ImageView(context);
			is.setImageBitmap(bm);
			imgs.setImageDrawable(is.getDrawable());
		}

		// 返回所有图片的个数
		public int getCount() {
			return arr.size();
		}

		// 返回图片在资源的位置
		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// 此方法是最主要的，他设置好的 ImageView对象返回给 Gallery
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(context);
			Bitmap bm = BitmapFactory.decodeFile(arr.get(position));
			imageView.setImageBitmap(bm);
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setLayoutParams(new Gallery.LayoutParams(120, 120));
			return imageView;
		}

		public void listPhotos(String folder_name) {
			if (folder_name .equals( "")) {
				return;
			}
			File file = new File(PATH + folder_name);
			File[] files = file.listFiles();
			size = files.length;
			for (int i = 0; i < size; i++) {
				arr.add(files[i].getPath());
			}
		}
	}

	public void getPhotoList() {
		Context context = this;
		ContentResolver resolver = context.getContentResolver();

		Cursor c = resolver.query(
				MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, null, null,
				null, null);
		int ss = c.getCount();
		Log.i("mediastor", ""+ss);
		for (int i = 0; c.moveToNext(); i++) {
			int index = c
					.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
			arr.add(c.getString(index));
		}
	}

	public void listPhotoFolder(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		if (files.length > 0) {
			int i = 0;
			photo_folder = new String[files.length + 1];
			for (File f : files) {
				if (f.isDirectory()) {
					photo_folder[i++] = f.getName();
				}
			}
			photo_folder[photo_folder.length - 1] = "MadiaStorephoto";
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, photo_folder);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinner.setAdapter(adapter);
			mSpinner.setOnItemSelectedListener(spinner);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}