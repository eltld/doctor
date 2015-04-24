package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.GroupPutImageAdapter;
import net.ememed.doctor2.activity.adapter.GroupPutImageAdapter.OnMemberDeteleListener;
import net.ememed.doctor2.baike.DoctorSimpleInfoActivity;
import net.ememed.doctor2.entity.ApplyToGroupEntity;
import net.ememed.doctor2.entity.BaseEntity;
import net.ememed.doctor2.entity.GroupDetailData;
import net.ememed.doctor2.entity.GroupDetailEntity;
import net.ememed.doctor2.entity.GroupMemberEntity;
import net.ememed.doctor2.entity.GroupMemberInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.MemberDetailEntry;
import net.ememed.doctor2.entity.SearchGroupEntity;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UserPreferenceWrapper;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.MyGridView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class GroupDetailActivity extends BasicActivity implements OnItemClickListener {

	private static final String TITLE = "群资料";
	public static final String GROUP_NUM = "group_num";
	public static final String TYPE = "type";

	private static final int[] TYPE_STYLES = { R.style.group_detail_button_apply,
			R.style.group_detail_button_dismiss, R.style.group_detail_button_exit };
	private static final String[] TYPE_TEXT = { "申请加入", "解散群组", "退出群组" };
	public static final int TYPE_APPLY = 0; // 申请者界面
	public static final int TYPE_OWNER = 1; // 拥有者界面
	public static final int TYPE_JOINER = 2; // 成员界面

	private final int AVATAR_NUM_ONE_LINE = 5;
	private String groupNum;
	private GroupDetailData groupDetail;

	private CircleImageView civ_photo;
	private CircleImageView civ_owner_photo;

	private MyGridView gridView;
	private GroupPutImageAdapter adapter;

	private TextView tv_group_name;
	private TextView tv_group_num;
	private TextView tv_group_tag;
	private TextView tv_owner_name;
	private TextView tv_owner_hospital;
	private TextView tv_group_limit;
	private TextView tv_group_person_num;
	private TextView tv_group_desc;
	private Button btn_submit;

	private View layout_groupOwner;
	private int mType;
	private int mDeletePosition;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_group_info);
		groupNum = getIntent().getStringExtra(GROUP_NUM);
		mType = getIntent().getIntExtra(TYPE, -1);
	}

	public static void startAction(Context context, String groupNum, int type) {
		Intent intent = new Intent(context, GroupDetailActivity.class);
		intent.putExtra(GROUP_NUM, groupNum);
		intent.putExtra(TYPE, type);
		context.startActivity(intent);
	}

	@Override
	protected void setupView() {
		super.setupView();

		TextView top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(TITLE);

		civ_photo = (CircleImageView) findViewById(R.id.civ_photo);
		civ_owner_photo = (CircleImageView) findViewById(R.id.civ_owner_photo);
		gridView = (MyGridView) findViewById(R.id.gridView);
		tv_group_name = (TextView) findViewById(R.id.tv_group_name);
		tv_group_num = (TextView) findViewById(R.id.tv_group_num);
		tv_group_tag = (TextView) findViewById(R.id.tv_group_tag);
		tv_owner_name = (TextView) findViewById(R.id.tv_owner_name);
		tv_owner_hospital = (TextView) findViewById(R.id.tv_owner_hospital);
		tv_group_limit = (TextView) findViewById(R.id.tv_group_limit);
		tv_group_person_num = (TextView) findViewById(R.id.tv_group_person_num);
		tv_group_desc = (TextView) findViewById(R.id.tv_group_desc);
		btn_submit = (Button) findViewById(R.id.btn_submit);

		layout_groupOwner = findViewById(R.id.layout_group_owner);

		updateView(mType);
		getGroupDetail();
	}

	@Override
	protected void addListener() {
		super.addListener();

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null == groupDetail)
					return;
				switch (mType) {
				case TYPE_APPLY:
					applyToGroup();
					break;
				case TYPE_OWNER:
					requestGroupExit();
					break;
				case TYPE_JOINER:
					requestGroupKickWithSelf();
					break;
				default:
					break;
				}
			}
		});

		civ_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GroupDetailActivity.this, ShowBigImage.class);
				intent.putExtra("remotepath", groupDetail.getLOGO());
				startActivity(intent);
			}
		});

		civ_owner_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String uri = groupDetail.getMember_list().getOwner().get(0).getMEMBERINFO()
						.getAVATAR();
				Intent intent = new Intent(GroupDetailActivity.this, ShowBigImage.class);
				intent.putExtra("remotepath", uri);
				startActivity(intent);
			}
		});

		layout_groupOwner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null == groupDetail) {
					return;
				}
				GroupMemberEntity memberInfo = groupDetail.getMember_list().getOwner().get(0);

				Intent intent = new Intent(GroupDetailActivity.this, DoctorSimpleInfoActivity.class);
				intent.putExtra("from", GroupDetailActivity.class.getSimpleName());
				intent.putExtra("name", memberInfo.getMEMBERINFO().getREALNAME());
				intent.putExtra("avatar", memberInfo.getMEMBERINFO().getAVATAR());
				intent.putExtra("professional", memberInfo.getMEMBERINFO().getPROFESSIONAL());
				intent.putExtra("zhuanye", memberInfo.getMEMBERINFO().getALLOWFREECONSULT());
				intent.putExtra("room", memberInfo.getMEMBERINFO().getROOMNAME());
				intent.putExtra("specially", memberInfo.getMEMBERINFO().getSPECIALITY());
				intent.putExtra("hospital", memberInfo.getHOSPITAL_INFO());
				startActivity(intent);
			}
		});
	}

	/**
	 * 获取群资料详情
	 */
	private void getGroupDetail() {
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("groupnum", groupNum);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_group_detail,
					GroupDetailEntity.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_GROUP_DETAIL;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/**
	 * 申请加入群
	 */
	private void applyToGroup() {
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("groupnum", groupNum);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.apply_to_group,
					ApplyToGroupEntity.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.APPLY_TO_GROUP;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/**
	 * 请求退出群组
	 */
	private void requestGroupKickWithSelf() {
		requestGroupKick(false, UserPreferenceWrapper.getMemberId());
	}

	/**
	 * 请求群组踢人
	 */
	private void requestGroupKickWithOther(String memberId) {
		requestGroupKick(true, memberId);
	}

	/**
	 * 请求群组踢人(管理员)/退出群组(自己)
	 * 
	 * @param memberId
	 *            医生Id
	 */
	private void requestGroupKick(boolean isOwner, String memberId) {
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", UserPreferenceWrapper.getToken());
		params.put("groupnum", groupNum);
		params.put("memberid", isOwner ? UserPreferenceWrapper.getMemberId() : "0");
		params.put("out_memberid", memberId);
		params.put("doctorid", memberId);

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.GROUP_KICK,
				ApplyToGroupEntity.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GROUP_KICK;
						handler.sendMessage(message);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Message message = new Message();
						message.obj = error.getMessage();
						message.what = IResult.DATA_ERROR;
						handler.sendMessage(message);
					}
				});
	}

	/**
	 * 请求解散群
	 */
	private void requestGroupExit() {
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}

		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", UserPreferenceWrapper.getToken());
		params.put("groupnum", groupNum);
		params.put("memberid", UserPreferenceWrapper.getMemberId());

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.GROUP_EXIT,
				ApplyToGroupEntity.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GROUP_EXIT;
						handler.sendMessage(message);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Message message = new Message();
						message.obj = error.getMessage();
						message.what = IResult.DATA_ERROR;
						handler.sendMessage(message);
					}
				});
	}

	@Override
	protected void onResult(Message msg) {
		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.GET_GROUP_DETAIL:
				GroupDetailEntity entry = (GroupDetailEntity) msg.obj;
				if (!entry.isSuccess()) {
					showToast(entry.getErrormsg());
					return;
				}

				if (entry.getData() == null) {
					return;
				}
				groupDetail = entry.getData();
				updateData();
				break;
			case IResult.APPLY_TO_GROUP:
				ApplyToGroupEntity entry2 = (ApplyToGroupEntity) msg.obj;
				if (null != entry2) {
					showToast(entry2.getErrormsg());
					sendBroadcast(new Intent(ActionFilter.REQUEST_DOCTOR_GROUP_LIST));
					startActivity(new Intent(GroupDetailActivity.this, MainActivity.class));
				}
				break;
			case IResult.GROUP_KICK:// 退出
				BaseEntity entry4 = (BaseEntity) msg.obj;
				showToast(entry4.getErrormsg());
				if (!entry4.isSuccess()) {
					return;
				}
				if (mType == TYPE_JOINER) {// 退群
					sendBroadcast(new Intent(ActionFilter.REQUEST_DOCTOR_GROUP_LIST));
					startActivity(new Intent(GroupDetailActivity.this, MainActivity.class));
				} else if (mType == TYPE_OWNER) {// 减员
					adapter.remove(mDeletePosition);
				}
				break;
			case IResult.GROUP_EXIT:// 解散
				BaseEntity entry3 = (BaseEntity) msg.obj;
				showToast(entry3.getErrormsg());
				if (!entry3.isSuccess()) {
					return;
				}
				sendBroadcast(new Intent(ActionFilter.REQUEST_DOCTOR_GROUP_LIST));
				startActivity(new Intent(GroupDetailActivity.this, MainActivity.class));
				break;
			case IResult.DATA_ERROR:
				showToast("数据异常");
				break;
			case IResult.NET_ERROR:
				showToast("网络异常");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			destroyDialog();
		}
		super.onResult(msg);
	}

	/**
	 * 获取成员列表
	 */
	private List<GroupMemberEntity> getMemberList(GroupDetailData detail) {
		List<GroupMemberEntity> list = new ArrayList<GroupMemberEntity>();
		list.add(detail.getMember_list().getOwner().get(0));
		if (detail.getMember_list().getMembers() != null) {
			list.addAll(detail.getMember_list().getMembers());
		}
		return list;
	}

	private GroupMemberInfo getOwner(GroupDetailData detail) {
		return detail.getMember_list().getOwner().get(0).getMEMBERINFO();
	}

	/**
	 * 更新界面数据
	 */
	private void updateData() {
		GroupMemberInfo owner = getOwner(groupDetail);
		List<GroupMemberEntity> memberLists = getMemberList(groupDetail);

		imageLoader.displayImage(groupDetail.getLOGO(), civ_photo, Util.getOptions_avatar());
		imageLoader.displayImage(owner.getAVATAR(), civ_owner_photo, Util.getOptions_avatar());

		tv_group_name.setText(groupDetail.getGROUPNAME());
		tv_group_num.setText(groupDetail.getGROUPNUM());
		tv_group_tag.setText(groupDetail.getTAGS());
		tv_group_desc.setText(groupDetail.getGROUPDESC());
		tv_owner_name.setText(owner.getREALNAME());
		tv_owner_hospital.setText(owner.getHOSPITALNAME());
		tv_group_limit.setText(String.format("%s人群", groupDetail.getMEMBER_LIMIT()));
		tv_group_person_num.setText(String.format("%s人", String.valueOf(memberLists.size())));

		adapter = new GroupPutImageAdapter(this, memberLists, mType);
		if (mType == TYPE_OWNER)
			adapter.setMemberDeteleListener(mDeteleListener);

		gridView.setNumColumns(AVATAR_NUM_ONE_LINE);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
	}

	/**
	 * 根据请求类型显示视图界面
	 * 
	 * @param type
	 */
	private void updateView(int type) {
		if (type == -1) {
			return;
		}
		btn_submit.setTextAppearance(this, TYPE_STYLES[type]);
		btn_submit.setText(TYPE_TEXT[type]);
		switch (type) {
		case TYPE_APPLY:
			break;
		case TYPE_OWNER:
			btn_submit.setTextAppearance(this, TYPE_STYLES[TYPE_OWNER]);
			layout_groupOwner.setVisibility(View.GONE);
			break;
		case TYPE_JOINER:
			btn_submit.setTextAppearance(this, TYPE_STYLES[TYPE_JOINER]);
			break;
		default:
			btn_submit.setVisibility(View.GONE);
			break;
		}
	}

	private GroupPutImageAdapter.OnMemberDeteleListener mDeteleListener = new OnMemberDeteleListener() {

		@Override
		public void onDelete(final int position) {
			final GroupMemberInfo groupMemberInfo = adapter.getItem(position).getMEMBERINFO();
			if (groupMemberInfo == null) {
				return;
			}
			new AlertDialog.Builder(GroupDetailActivity.this).setTitle("温馨提示")
					.setMessage(String.format("确定移除【 %s 】该成员", groupMemberInfo.getREALNAME())).setNegativeButton("取消", null)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mDeletePosition = position;
							requestGroupKickWithOther(groupMemberInfo.getMEMBERID());
						}
					}).show();
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int count = adapter.getCount();
		switch (mType) {
		case TYPE_APPLY:// 0
			break;
		case TYPE_OWNER:// 2
			if (position == count - 1) {
				//TODO:"点击添加"
				showToast("点击添加");
			}
			if (position == count - 2) {
				adapter.delete();
			}
			break;
		case TYPE_JOINER:// 1
			if (position == count - 1) {
				//TODO:点击添加
				showToast("点击添加");
			}
			break;
		default:
			break;
		}
	}

}
