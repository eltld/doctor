package net.ememed.doctor2.baike.entity;

import net.ememed.doctor2.entity.BaseEntity;

public class ScoreExchangeResultEntry extends BaseEntity{
	ScoreExchangeResultInfo data;

	public ScoreExchangeResultInfo getData() {
		return data;
	}

	public void setData(ScoreExchangeResultInfo data) {
		this.data = data;
	}
}
