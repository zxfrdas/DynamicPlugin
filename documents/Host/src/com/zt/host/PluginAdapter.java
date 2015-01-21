package com.zt.host;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.konka.dynamicplugin.core.IAsyncListener;
import com.konka.dynamicplugin.core.IPluginManager;
import com.konka.dynamicplugin.core.PluginInfo;

public class PluginAdapter extends BaseAdapter implements OnClickListener {
	private WeakReference<Context> mContextRef;
	private LayoutInflater mInflater;
	private List<PluginInfo> mData;
	private IPluginManager mPluginManager;

	private static final class ViewHolder {
		public ImageView icon;
		public TextView info;
		public Switch enable;
		public Switch install;
		
		public void reset() {
			icon.setImageResource(0);
			info.setText("");
			enable.setChecked(false);
			install.setChecked(false);
		}
	}

	public PluginAdapter(Context context, IPluginManager pluginManager) {
		mContextRef = new WeakReference<Context>(context);
		mInflater = (LayoutInflater) mContextRef.get().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mData = new ArrayList<PluginInfo>();
		mPluginManager = pluginManager;
	}

	public void setData(List<PluginInfo> data) {
		mData = data;
	}
	
	public void addData(PluginInfo data) {
		mData.add(data);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public PluginInfo getItem(int position) {
		PluginInfo p = new PluginInfo();
		try {
			p = mData.get(position);
		} catch (Exception e) {
		}
		return p;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.plugininfo, null);
			viewHolder = createViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.reset();
		viewHolder.enable.setTag(position);
		viewHolder.install.setTag(position);
		bindView(viewHolder, position);
		return convertView;
	}

	private ViewHolder createViewHolder(View convertView) {
		ViewHolder mHolder = new ViewHolder();
		mHolder.icon = (ImageView) convertView.findViewById(R.id.plugin_icon);
		mHolder.info = (TextView) convertView.findViewById(R.id.plugin_info);
		mHolder.enable = (Switch) convertView.findViewById(R.id.plugin_enable);
		mHolder.install = (Switch) convertView.findViewById(R.id.plugin_install);
		mHolder.enable.setOnClickListener(this);
		mHolder.install.setOnClickListener(this);
		return mHolder;
	}
	
	private void bindView(ViewHolder viewHolder, int position) {
		PluginInfo data = getItem(position);
		if (null != data) {
			ViewHolder mHolder = viewHolder;
			mHolder.icon.setImageDrawable(data.getIcon());
			StringBuilder sb = new StringBuilder();
			sb.append("title = ").append(data.getTitle()).append("\n");
			sb.append("apkPath = ").append(data.getApkPath()).append("\n");
			sb.append("dexPath = ").append(data.getDexPath()).append("\n");
			sb.append("packageName = ").append(data.getPackageName()).append("\n");
			sb.append("entryClass = ").append(data.getEntryClass()).append("\n");
			sb.append("version = ").append(data.getVersion()).append("\n");
			sb.append("enableIndex = ").append(data.getEnableIndex()).append("\n");
			mHolder.info.setText(sb.toString());
			mHolder.install.setChecked(data.isInstalled());
			if (mHolder.install.isChecked()) {
				mHolder.enable.setEnabled(true);
			} else {
				mHolder.enable.setEnabled(false);
			}
			mHolder.enable.setChecked(data.isEnabled());
		}
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		final int position = (Integer) v.getTag();
		final PluginInfo pluginInfo = getItem(position);
		IAsyncListener listener = new IAsyncListener() {
			
			@Override
			public void success(List<PluginInfo> pluginInfos) {
				PluginInfo result = pluginInfos.get(0);
				pluginInfo.setInstalled(result.isInstalled());
				pluginInfo.setEnabled(result.isEnabled());
				pluginInfo.setEnableIndex(result.getEnableIndex());
				notifyDataSetChanged();
			}
			
			@Override
			public void fail(String reason) {
				Log.d("PluginManager", "fail, reason = " + reason);
			}
		};
		if (R.id.plugin_install == id) {
			if (pluginInfo.isInstalled()) {
				mPluginManager.asyncUninstallPlugin(mContextRef.get(), pluginInfo, listener);
			} else {
				mPluginManager.asyncInstallPlugin(mContextRef.get(), pluginInfo, listener);
			}
		} else if (R.id.plugin_enable == id) {
			if (pluginInfo.isEnabled()) {
				mPluginManager.asyncDisablePlugin(pluginInfo, listener);
			} else {
				mPluginManager.asyncEnablePlugin(pluginInfo, listener);
			}
		}
	}

}
