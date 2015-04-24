package net.ememed.doctor2.entity;

public class DoctorDetailInfoData {
	HospitalInfo HOSPITAL_INFO;
	DoctorInfoEntry DOCTOR_INFO;
	
	public HospitalInfo getHOSPITAL_INFO() {
		return HOSPITAL_INFO;
	}
	public void setHOSPITAL_INFO(HospitalInfo hOSPITAL_INFO) {
		HOSPITAL_INFO = hOSPITAL_INFO;
	}
	public DoctorInfoEntry getDOCTOR_INFO() {
		return DOCTOR_INFO;
	}
	public void setDOCTOR_INFO(DoctorInfoEntry dOCTOR_INFO) {
		DOCTOR_INFO = dOCTOR_INFO;
	}
}
