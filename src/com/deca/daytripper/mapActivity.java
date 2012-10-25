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
	// Menu����
	public static final int MENU_START = Menu.FIRST + 1;
	public static final int MENU_END = Menu.FIRST + 2;
	public static final int MENU_LOG = Menu.FIRST + 3;

	// ���� ���¸� �ӷ��ִ� ����
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

		// �������� ����
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);

		// �ʺ� Ŭ���� ���� ����� ��ġ�� �̵�
		mapView.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mapView.getController().animateTo(
							myLocationOverlay.getMyLocation());
				}
				return false;
			}

		});

		// ���� �� ��ġ�� ǥ���ϰ� ���󰣴�.
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(
						myLocationOverlay.getMyLocation());
			}
		});

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_START, Menu.NONE, "�������");
		menu.add(Menu.NONE, MENU_END, Menu.NONE, "���� ��");
		menu.add(Menu.NONE, MENU_LOG, Menu.NONE, "��� �����");
		return true;

	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		// trip
		case MENU_START: // �������
			if (tripStatus == true)
				Toast.makeText(this, "���� �������̿���", Toast.LENGTH_SHORT).show();
			else {
				startLatitude = myLocationOverlay.getMyLocation()
						.getLatitudeE6();
				startLongitude = myLocationOverlay.getMyLocation()
						.getLongitudeE6();
				startDialog();
				addMarker(startLatitude, startLongitude);

			}
			tripStatus = true;

			// ������ġ�� ����Ʈ�� �ְ�
			// ���� �� flag�� �־���Ѵ�;
			break;
		case MENU_END: // ���ೡ
			if (tripStatus == false)
				Toast.makeText(this, "������ ���۵��� �ʾҾ��", Toast.LENGTH_SHORT)
						.show();
			else {
				endLatitude = myLocationOverlay.getMyLocation().getLatitudeE6();
				endLongitude = myLocationOverlay.getMyLocation()
						.getLongitudeE6();
				tripStatus = false;

			}
			// �ѱ�°� text�����̳� root�� ������ ����
			// �������̽������

			break;
		case MENU_LOG:
			if (tripStatus == false)
				Toast.makeText(this, "������ ���۵��� �ʾҾ��", Toast.LENGTH_SHORT)
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
				.setTitle("�������")
				.setView(dialog_start)
				.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(mapActivity.this,
								trip_Title.getText().toString() + "�� ���۵Ǿ����ϴ�.",
								Toast.LENGTH_LONG).show();
						
					}
				})
				.setNegativeButton("���", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(mapActivity.this, "������ ��ҵǾ����ϴ�",
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

		new AlertDialog.Builder(this).setTitle("��ϳ����").setView(dialog_log)
				.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				})
				.setNegativeButton("���", new DialogInterface.OnClickListener() {

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
