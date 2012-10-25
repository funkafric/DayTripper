package com.deca.daytripper;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class mapActivity extends MapActivity {
	// Menu선언
	public static final int MENU_START = Menu.FIRST + 1;
	public static final int MENU_END = Menu.FIRST + 2;
	public static final int MENU_LOG = Menu.FIRST + 3;

	// 여행 상태를 앙려주는 변수
	boolean tripStatus = false;
	int log = 0;

	int startLatitude, startLongitude, endLatitude, endLongitude,
			currentLatitude, currentLongitude;

	LocationManager locManager;
	LocationListener locListener;

	MapView mapView = null;
	MyLocationOverlay myLocationOverlay;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mapView = (MapView) findViewById(R.id.map);
		mapView.getController().setZoom(16);

		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// 오버레이 생성
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);

		// 맵뷰 클릭시 현재 사용자 위치로 이동
		mapView.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mapView.getController().animateTo(
							myLocationOverlay.getMyLocation());
				}
				return false;
			}

		});

		// 현제 내 위치를 표시하고 따라간다.
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(
						myLocationOverlay.getMyLocation());
			}
		});

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_START, Menu.NONE, "여행시작");
		menu.add(Menu.NONE, MENU_END, Menu.NONE, "여행 끗");
		menu.add(Menu.NONE, MENU_LOG, Menu.NONE, "기록 남기기");
		return true;

	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		// trip
		case MENU_START: // 여행시작
			if (tripStatus == true)
				Toast.makeText(this, "지금 여행중이에요", Toast.LENGTH_SHORT).show();
			else {
				startLatitude = myLocationOverlay.getMyLocation()
						.getLatitudeE6();
				startLongitude = myLocationOverlay.getMyLocation()
						.getLongitudeE6();
				startDialog();
				addMarker(startLatitude, startLongitude);

			}
			tripStatus = true;

			// 현재위치를 리스트에 넣고
			// 시작 끝 flag가 있어야한다;
			break;
		case MENU_END: // 여행끝
			if (tripStatus == false)
				Toast.makeText(this, "여행이 시작되지 않았어요", Toast.LENGTH_SHORT)
						.show();
			else {
				endLatitude = myLocationOverlay.getMyLocation().getLatitudeE6();
				endLongitude = myLocationOverlay.getMyLocation()
						.getLongitudeE6();
				tripStatus = false;

			}
			// 넘기는거 text파일이나 root로 파일을 저장
			// 오버레이싹지우기

			break;
		case MENU_LOG:
			if (tripStatus == false)
				Toast.makeText(this, "여행이 시작되지 않았어요", Toast.LENGTH_SHORT)
						.show();
			else {

				currentLatitude = myLocationOverlay.getMyLocation()
						.getLatitudeE6();
				currentLongitude = myLocationOverlay.getMyLocation()
						.getLongitudeE6();

				GeoPoint geopoint = new GeoPoint((int) currentLatitude,
						(int) currentLongitude);
				addMarker(currentLatitude, currentLatitude);
				logDialog();
			}
			break;

		}

		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();

		myLocationOverlay.disableMyLocation();
	}

	private void startDialog() {
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout dialog_start = (LinearLayout) vi.inflate(
				R.layout.dialog_start, null);

		final EditText trip_Title = (EditText) dialog_start
				.findViewById(R.id.tripTitle);
		final EditText trip_Comment = (EditText) dialog_start
				.findViewById(R.id.tripCommnet);

		new AlertDialog.Builder(this)
				.setTitle("여행시작")
				.setView(dialog_start)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(mapActivity.this,
								trip_Title.getText().toString() + "가 시작되었습니다.",
								Toast.LENGTH_LONG).show();
						
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(mapActivity.this, "여행이 취소되엇습니다",
								Toast.LENGTH_LONG).show();

					}
				}).show();
	}

	private void logDialog() {
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout dialog_log = (LinearLayout) vi.inflate(
				R.layout.dialog_log, null);

		ImageView cameraButton = (ImageView) dialog_log
				.findViewById(R.id.camera);
		cameraButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		final EditText trip_context = (EditText) dialog_log
				.findViewById(R.id.tripContext);

		new AlertDialog.Builder(this).setTitle("기록남기기").setView(dialog_log)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();
		
	}
	private void addMarker(int markerLatitude, int markerLongitude){
		Drawable marker = getResources().getDrawable(R.drawable.ic_marker);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		
		mapView.getOverlays().add(new MyLocations(marker, markerLatitude, markerLongitude));
	}
		
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	class MyLocations extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> locations = new ArrayList<OverlayItem>();
		private Drawable marker;
		private OverlayItem mOverlayItem;

		public MyLocations(Drawable marker, int latitudeE6,
				int longitudeE6) {
			super(marker);
			this.marker = marker;

			GeoPoint myPlace = new GeoPoint(latitudeE6, longitudeE6);

			mOverlayItem = new OverlayItem(myPlace, "", "");
			locations.add(mOverlayItem);

			populate();

		}

		@Override
		protected OverlayItem createItem(int i) {
			return locations.get(i);
			// number of items in the array list
		}

		@Override
		public int size() {
			return locations.size();
		}
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow){
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}
	}
}
