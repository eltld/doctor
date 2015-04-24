package net.ememed.doctor2.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.Problem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProblemAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Problem> problems;

	public ProblemAdapter(Context context, ArrayList<Problem> problems) {
		if (problems == null) {
			problems = new ArrayList<Problem>();
		}
		this.problems = problems;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return problems.size();
	}

	@Override
	public Object getItem(int position) {
		return problems.get(position-1);

	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.problem_list_item, null);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_problem_titile);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Problem problem = problems.get(position);
		holder.tvTitle.setText(problem.getTITLE());

		return convertView;
	}

	class ViewHolder {
		TextView tvTitle;

	}

	public void change(List<Problem> historyQuarters) {
		if (historyQuarters == null) {
			historyQuarters = new ArrayList<Problem>();
		}
		this.problems = historyQuarters;
		notifyDataSetChanged();
	}

	public void add(List<Problem> historyQuarters) {
		if (this.problems == null) {
			this.problems = new ArrayList<Problem>();
		}
		this.problems.addAll(historyQuarters);
		notifyDataSetChanged();

	}

}
