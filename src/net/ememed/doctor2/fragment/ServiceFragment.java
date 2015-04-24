package net.ememed.doctor2.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.activity.WebViewThirdServiceActivity;
import net.ememed.doctor2.db.OtherServiceTable;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.ServiceListEntry;
import net.ememed.doctor2.entity.ServiceListInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UserPreferenceWrapper;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 服务tab
 * 
 * @author taro chyaohui@gmail.com edit by chenhj 更好动态逻辑并且与第三方服务的Activity相连
 */
public class ServiceFragment extends Fragment implements BasicUIEvent, Callback {
	private static final String TAG = ServiceFragment.class.getSimpleName();
	public static final int EXEU_GET_DATA = 0;

	private boolean isRefresh = false;

	private FrameLayout mContentView = null;
	private Handler mHandler = null;
	private MainActivity activity = null;
	private RefreshListView serviceListView = null;
	private ArrayList<ServiceListEntry> serviceContent = null;
	private boolean[] updatePos = new boolean[0]; // 记录服务项需要更新的位置，用于红点更新提示设置
	private ServiceAdapter adapter = null;

	private final String MYTAG = "chenhj,ServiceFragment";
	OtherServiceTable table;
	private String doctorID;

	public ServiceFragment() {
		this.activity = (MainActivity) getActivity();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mHandler = new Handler(this);
		Logger.dout(TAG + "onCreate");
		// getNewsType();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// fragment可见时执行加载数据或者进度条等
			Logger.dout(TAG + "isVisibleToUser");
		} else {
			// 不可见时不执行操作
			Logger.dout(TAG + "unVisibleToUser");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		doctorID = SharePrefUtil.getString(Conast.Doctor_ID);
		Logger.dout(TAG + "onCreateView");
		table = new OtherServiceTable();
		View view = inflater.inflate(R.layout.fragment_service_layout, null);
		ImageView btn_back = (ImageView) view.findViewById(R.id.btn_back);
		btn_back.setVisibility(View.INVISIBLE);
		TextView top_title = (TextView) view.findViewById(R.id.top_title);
		top_title.setText(getString(R.string.act_title_service));
		mContentView = (FrameLayout) view.findViewById(R.id.mainView);
		// setupView(view);

		serviceListView = (RefreshListView) view.findViewById(R.id.other_service_list);

		serviceListView.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				getServiceListFromNet();
			}

		});

		getServiceListFromDB();

		getServiceListFromNet();

		return view;
	}

	private void setOtherServiceListener() {
		serviceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ServiceListEntry entry = (ServiceListEntry) adapter.getItem(position - 1);
				View hintView = serviceListView.getChildAt(position).findViewById(R.id.other_service_hint);
				if (entry.getEXT() == null) {
					Toast.makeText(getActivity(), "没有内容", Toast.LENGTH_SHORT).show();
					return;
				}
				if (View.VISIBLE == hintView.getVisibility()) {
					hintView.setVisibility(View.INVISIBLE);
				}

				Intent intent;
				System.out.println("entry.getTYPE() = " + entry.getTYPE()+ "  entry.getEXT().getURL() = "+entry.getEXT().getURL());
				/* 根据服务项的TYPE来确定跳转类型 */
				switch (Integer.parseInt(entry.getTYPE())) {
				case 1: /* 内置HTML5, 即第三方服务，重点 */
					intent = new Intent(activity, WebViewThirdServiceActivity.class);
					if (UserPreferenceWrapper.isLogin()) 
						intent.putExtra("accesskey", UserPreferenceWrapper.getAccessKey());
					intent.putExtra("url", entry.getEXT().getURL());
					intent.putExtra("ServiceList", serviceContent);
					startActivity(intent);
					break;
				case 2: /* 外置HTML5 */
					intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					
					Uri content_url = Uri.parse(entry.getEXT().getURL());
					intent.setData(content_url);
					startActivity(intent);
					break;
				case 3: /* 薏米助理（电话） */
					try {
						
						
						
						intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + entry.getEXT().getPHONE_NUMBER()));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
				table.setServiceItemOnClick(doctorID, entry.getID(), false);
				if(updatePos.length>0){
					updatePos[position - 1] = false;
				}
				getImage();
			}
		});
	}

	private void getServiceListFromDB() {
		OtherServiceTable serviceTable = new OtherServiceTable();
		String doctorID = SharePrefUtil.getString(Conast.Doctor_ID);
		serviceContent = serviceTable.getServiceContent(doctorID);

		serviceListView.setAdapter(new ServiceAdapter(serviceContent));
		if (null != serviceContent && !serviceContent.isEmpty()) {
			adapter = new ServiceAdapter(serviceContent);
			serviceListView.setAdapter(adapter);
			setOtherServiceListener();
		}
	}

	public boolean getImage() {
		if (updatePos.length == 0) {
			activity.setServerRed(true);
			return true;
		} else {
			updatePos[0] = false;
			for (int i = 0; i < updatePos.length; i++) {
				if (updatePos[i]) {
					activity.setServerRed(true);
					return true;
				}
			}
			activity.setServerRed(false);
			return false;
		}
	}

	private void getServiceListFromNet() {
		if (NetWorkUtils.detect(activity)) {
			//activity.loading(null);
			try {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
				params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
				MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_other_service_list, ServiceListInfo.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						if (!isRefresh) {
							activity.destroyDialog();
						}
						if (null != response) {
							ServiceListInfo respEntry = (ServiceListInfo) response;
							if (respEntry.getSuccess() == 1) {
								List<ServiceListEntry> serList = respEntry.getData();

								/* 本地服务为空时直接网络加载 并保存到数据库 */
								if (null == serviceContent || serviceContent.isEmpty()) {
									updatePos = new boolean[serList.size()];
									int i = 0;
									for (ServiceListEntry serEntry : serList) {
										table.saveServiceContent(serEntry, doctorID);
										updatePos[i] = true;
										i++;
									}
									getImage();
									Message msg = mHandler.obtainMessage();
									msg.obj = response;
									msg.what = IResult.GET_OTHER_SERVICE;
									mHandler.sendMessage(msg);

								} else {// 有数据项需要检查本地和服务器之间的版本号，用来设置红点提示
									updatePos = new boolean[serList.size()];
									for (int i = 0; i < serList.size(); i++) {
										ServiceListEntry serEntry = serList.get(i);
										String versionInNet = serEntry.getVERSION() == null ? "0" : serEntry.getVERSION();
										String versionInDB = table.getServiceItemVersion(doctorID, serEntry.getID());
										boolean serviceItemOnClick = table.getServiceItemOnClick(doctorID, serEntry.getID());// true为未点击，false为已点击
										/* 本地有该项数据 */
										if (serviceItemOnClick) {
											updatePos[i] = true;
										} else {
											if (null != versionInDB) {
												/* version需要更新时 */
												try {
													if (Double.parseDouble(versionInNet) > Double.parseDouble(versionInDB)) {

														updatePos[i] = true;

													} else {
														updatePos[i] = false;

													}
												} catch (NumberFormatException e) {
													updatePos[i] = false;
													e.printStackTrace();
												} catch (Exception e) {
													e.printStackTrace();
												}
											} else {
												/* 无该项数据时 */
												updatePos[i] = false;
											}
										}

									}
									updatePos[0] = false;

									// table.clearTable();
									getImage();
									List<String> ids = new ArrayList<String>();
									for (ServiceListEntry serEntry : serList) {
										table.saveServiceContent(serEntry, doctorID);
										ids.add(serEntry.getID());
									}
									// 清理关闭的第三方服务
									List<String> serviceIds = table.getServiceIds();
									if (serviceIds != null && serviceIds.size() > 0) {
										for (String id : serviceIds) {
											boolean contains = ids.contains(id);
											if (!contains) {
												table.clearItem(id);
											}
										}
									}

									// 更新UI
									Message msg = mHandler.obtainMessage();
									msg.obj = response;
									msg.what = IResult.GET_OTHER_SERVICE;
									mHandler.sendMessage(msg);
								}
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						activity.destroyDialog();

						Message message = mHandler.obtainMessage();
						message.obj = error.getMessage();
						message.what = IResult.DATA_ERROR;
						mHandler.sendMessage(message);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.dout(TAG + "onResume");
	}

	@Override
	public void onPause() {
		super.onStop();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case IResult.GET_OTHER_SERVICE:
			serviceListView.onRefreshComplete();
			ServiceListInfo info = (ServiceListInfo) msg.obj;
			List<ServiceListEntry> list = info.getData();
			serviceContent = new ArrayList<ServiceListEntry>(list);

			adapter = new ServiceAdapter(list, updatePos);
			serviceListView.setAdapter(adapter);
			setOtherServiceListener();

			break;

		case IResult.DATA_ERROR:
			serviceListView.onRefreshComplete();
			// activity.showToast(IMessage.DATA_ERROR);
		case IResult.ERROR:
			serviceListView.onRefreshComplete();
			activity.showToast(IMessage.NET_ERROR);
			break;
		}
		return false;
	}

	@Override
	public void execute(int mes, Object obj) {
		switch (mes) {
		case EXEU_GET_DATA:

			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 用来设置服务项内容以及红点更新提示
	 * 
	 * @author chenhj
	 * 
	 */
	private class ServiceAdapter extends BaseAdapter {

		private List<ServiceListEntry> mList;
		private boolean[] updatePositions = new boolean[0];

		public ServiceAdapter(List<ServiceListEntry> list) {
			if (null == list) {
				mList = new ArrayList<ServiceListEntry>();
			} else {
				mList = list;
			}
		}

		public ServiceAdapter(List<ServiceListEntry> list, boolean[] positions) {
			this(list);
			updatePositions = positions;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			if (null != mList && 0 != mList.size()) {
				return mList.get(position);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				convertView = LayoutInflater.from(activity).inflate(R.layout.fragment_service_layout_item, null);
				holder = new ViewHolder();
				holder.serviceIcon = (ImageView) convertView.findViewById(R.id.other_service_icon);
				holder.serviceName = (TextView) convertView.findViewById(R.id.other_service_name);
				holder.serviceHint = (ImageView) convertView.findViewById(R.id.other_service_hint);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ServiceListEntry entry = mList.get(position);
			holder.serviceName.setText(entry.getSERVICE_NAME());

			activity.imageLoader.displayImage(entry.getICON(), holder.serviceIcon, Util.getOptions_pic());

			if (position != 0) {
				/* 设置自动红点提醒 */
				if (null != updatePositions && updatePositions.length != 0 && updatePositions[position]) {
					holder.serviceHint.setVisibility(View.VISIBLE);
				} else {
					holder.serviceHint.setVisibility(View.INVISIBLE);
				}
			} else {
				holder.serviceHint.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

	}

	private class ViewHolder {
		public ImageView unreadLabel;
		private ImageView serviceIcon;
		private TextView serviceName;
		private ImageView serviceHint;
	}

}
